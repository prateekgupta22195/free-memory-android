package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.HomeUseCases
import com.pg.cloudcleaner.helper.ReadFileWorker
import com.pg.cloudcleaner.presentation.WorkerUIState
import com.pg.cloudcleaner.utils.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeVM : ViewModel() {

    private val storageHelper = StorageHelper()
    private val workManager = WorkManager.getInstance(App.instance)
    private val uniqueWorkName = "file reader"

    // Use the new sealed interface for UI state
    private val _uiState = MutableStateFlow<StorageUiState>(StorageUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isDatabaseEmpty = MutableStateFlow(true)
    val isDatabaseEmpty = _isDatabaseEmpty.asStateFlow()

    private val _scanUIStatus = MutableStateFlow<WorkerUIState?>(null)
    val scanUIStatus = _scanUIStatus.asStateFlow()

    private val homeUseCases by lazy { HomeUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao())) }

    init {
        fetchStorageDetails()
        observeScanWork()
    }

    private fun observeScanWork() {
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(uniqueWorkName).collect { workInfos ->
                val workInfo = workInfos.firstOrNull()
                if (workInfo != null) {
                    val progressMessage = workInfo.progress.getString(ReadFileWorker.KEY_PROGRESS_MESSAGE)
                    _scanUIStatus.value = when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            WorkerUIState.Success(progressMessage ?: "Scan finished successfully.")
                        }

                        WorkInfo.State.FAILED -> WorkerUIState.Failed("Scan failed.")
                        WorkInfo.State.CANCELLED -> WorkerUIState.Cancelled("Scan cancelled.")
                        else -> WorkerUIState.InProgress( progressMessage?: "Scan is in progress...")
                    }
                } else {
                    _scanUIStatus.value = null
                }
            }
        }
    }
    fun getAnyTwoDuplicateFiles(): Flow<Pair<LocalFile, LocalFile>?> {
        return homeUseCases.getAnyTwoDuplicates()
    }

    fun getVideoFile(): Flow<LocalFile?> {
        return homeUseCases.getVideoFile()
    }

    fun getVideoFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getVideoFiles()
    }

    fun getLargeFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getLargeFiles()
    }

    fun getImageFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getImageFiles()
    }

    fun getTotalSizeOfMimeType(mimeType: String): Flow<Long> {
        // returning size in kbs but we store size in mbs
        return homeUseCases.getTotalSizeOfMimeType(mimeType).map { size -> size * 1024 }
    }


    fun getTotalSizeOfLargeFiles(): Flow<Long> {
        // returning size in kbs but we store size in mbs
        return homeUseCases.getTotalSizeOfLargeFiles().map { size -> size * 1024 }
    }

    /**
     * Fetches storage details using the StorageHelper on a background thread
     * and updates the state flow.
     */

    private fun fetchStorageDetails() {
        // Set initial state to Loading
        _uiState.value = StorageUiState.Loading

        viewModelScope.launch {
            try {
                // Perform the file system check on an I/O-optimized thread
                val info = withContext(Dispatchers.IO) {
                    storageHelper.getTotalStorageInfo()
                }
                // On success, update the state
                _uiState.value = StorageUiState.Success(info)
            } catch (e: Exception) {
                // On failure, update the state with an error
                _uiState.value = StorageUiState.Error("Failed to calculate storage space.")
                // Optional: Log the actual exception for debugging
                // Log.e("HomeViewModel", "Storage calculation failed", e)
            }
        }
    }
}
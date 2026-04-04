package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.pg.cloudcleaner.app.App
import io.sentry.Sentry
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.HomeUseCases
import com.pg.cloudcleaner.helper.ReadFileWorker
import com.pg.cloudcleaner.presentation.WorkerUIState
import com.pg.cloudcleaner.utils.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _scanUIStatus = MutableStateFlow<WorkerUIState>(WorkerUIState.Initial)
    val scanUIStatus = _scanUIStatus.asStateFlow()

    // True while we are intentionally replacing a running scan (REPLACE policy cancels the old one)
    private var isRestarting = false

    private val homeUseCases by lazy { HomeUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao())) }

    val duplicatesCount: StateFlow<Int> by lazy {
        homeUseCases.getDuplicatesCount()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    }

    val imagesCount: StateFlow<Int> by lazy {
        homeUseCases.getLargeImagesCount()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    }

    val videosCount: StateFlow<Int> by lazy {
        homeUseCases.getVideosCount()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    }

    val largeFilesCount: StateFlow<Int> by lazy {
        homeUseCases.getLargeFilesCount()
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    }

    val duplicateThumbnails: StateFlow<List<LocalFile>> by lazy {
        homeUseCases.getAnyThreeDuplicateGroups()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    val previewImageFiles: StateFlow<List<LocalFile>> by lazy {
        homeUseCases.getLargeImageFiles(3)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    val previewVideoFiles: StateFlow<List<LocalFile>> by lazy {
        homeUseCases.getNVideoFiles(3)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    val previewLargeFiles: StateFlow<List<LocalFile>> by lazy {
        homeUseCases.getLargeFiles(3)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

    val imageSizeBytes: StateFlow<Long> by lazy {
        homeUseCases.getCustomTotalSize("image/%", 5000)
            .map { it * 1024 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

    val videoSizeBytes: StateFlow<Long> by lazy {
        homeUseCases.getTotalSizeOfMimeType("video/%")
            .map { it * 1024 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

    val largeSizeBytes: StateFlow<Long> by lazy {
        homeUseCases.getTotalSizeOfLargeFiles()
            .map { it * 1024 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

    val duplicateSizeBytes: StateFlow<Long> by lazy {
        homeUseCases.getTotalSizeOfDuplicates()
            .map { it * 1024 }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

    val totalFreeableBytes: StateFlow<Long> by lazy {
        combine(imageSizeBytes, videoSizeBytes, largeSizeBytes) { img, vid, large -> img + vid + large }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
    }

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
                    val progress = workInfo.progress.getInt(ReadFileWorker.KEY_PROGRESS, 0)
                    _scanUIStatus.value = when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            WorkerUIState.Success(progressMessage ?: "Scan finished successfully.")
                        }

                        WorkInfo.State.FAILED -> {
                            Sentry.captureException(Exception("ReadFileWorker failed. workId=${workInfo.id}"))
                            WorkerUIState.Failed("Scan failed.")
                        }
                        WorkInfo.State.CANCELLED -> {
                            if (!isRestarting) {
                                Sentry.captureException(Exception("ReadFileWorker cancelled unexpectedly. workId=${workInfo.id}"))
                            }
                            WorkerUIState.Cancelled("Scan cancelled.")
                        }
                        else -> {
                            if (workInfo.state == WorkInfo.State.RUNNING) isRestarting = false
                            WorkerUIState.InProgress(progressMessage ?: "Scan is in progress...", progress)
                        }
                    }
                } else {
                    _scanUIStatus.value = WorkerUIState.Initial
                }
            }
        }
    }
    fun getVideoFile(): Flow<LocalFile?> {
        return homeUseCases.getVideoFile()
    }

    fun getNVideoFiles(n: Int? = null): Flow<List<LocalFile>> {
        return homeUseCases.getNVideoFiles(limit = n)
    }

    fun getLargeFiles(limit: Int? = null): Flow<List<LocalFile>> {
        return homeUseCases.getLargeFiles(limit)
    }

    fun getNImageFiles(n: Int? = null): Flow<List<LocalFile>> {
        return homeUseCases.getImageFiles(n)
    }

    fun restartScan() {
        isRestarting = true
        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ReadFileWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("abc")
                .build()
        )
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
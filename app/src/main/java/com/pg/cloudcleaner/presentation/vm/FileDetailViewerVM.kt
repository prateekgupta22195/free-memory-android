package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import com.pg.cloudcleaner.utils.SavedMemoryTracker
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FileDetailViewerVM : ViewModel() {

    var infoPopUpVisible = false

    private val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    private val _categoryFiles = MutableStateFlow<List<LocalFile>?>(null)
    val categoryFiles: StateFlow<List<LocalFile>?> = _categoryFiles

    val showDeleteDialog = mutableStateOf(false)
    val isDeleting = mutableStateOf(false)

    private var filesJob: Job? = null

    fun loadFilesByCategory(category: String) {
        filesJob?.cancel()
        filesJob = viewModelScope.launch {
            fileUseCases.getFilesByCategory(category)
                .flowOn(Dispatchers.IO)
                .collect { _categoryFiles.value = it }
        }
    }

    fun loadFilesByMd5(md5: String) {
        filesJob?.cancel()
        filesJob = viewModelScope.launch {
            fileUseCases.getFilesByMd5(md5)
                .flowOn(Dispatchers.IO)
                .collect { _categoryFiles.value = it }
        }
    }


    suspend fun getFileById(fileId: String): LocalFile? {
        return fileUseCases.getFileById(fileId)
    }

    fun requestDelete() {
        showDeleteDialog.value = true
    }

    fun cancelDelete() {
        showDeleteDialog.value = false
    }

    fun confirmDelete(fileId: String, onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isDeleting.value = true }
            val sizeBytes = File(fileId).length()
            fileUseCases.deleteFile(fileId)
            File(fileId).apply { if (exists()) delete() }
            SavedMemoryTracker.addSavedBytes(sizeBytes)
            withContext(Dispatchers.Main) {
                isDeleting.value = false
                showDeleteDialog.value = false
                onDeleted()
            }
        }
    }


    fun getFileInfo(file: LocalFile): String {
        return fileUseCases.getFileInfo(file.id)
    }

}
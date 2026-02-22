package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

open class SelectableDeletableVM: ViewModel() {

    val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val selectedModeOn = mutableStateOf(false)

    val selectedFiles = mutableStateOf(setOf<String>())
    
    val showDeleteDialog = mutableStateOf(false)

    fun deleteFiles(ids: Set<String>) {
        showDeleteDialog.value = true
        pendingDeleteFiles = ids
    }

    fun confirmDeleteFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                fileUseCases.deleteFiles(pendingDeleteFiles.toList())
            }.join()

            launch {
                pendingDeleteFiles.forEach { filePath ->
                    File(filePath).apply {
                        if (exists()) delete()
                    }
                }
            }.join()
            selectedFiles.value -= pendingDeleteFiles
            showDeleteDialog.value = false
            selectedModeOn.value = false
        }
    }

    fun cancelDelete() {
        showDeleteDialog.value = false
        pendingDeleteFiles = emptySet()
    }

    private var pendingDeleteFiles = emptySet<String>()

}
package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import com.pg.cloudcleaner.utils.SavedMemoryTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

class FlatDuplicatesFileManagerVM : ViewModel() {

    val selectedFileIds = mutableStateOf(setOf<String>())
    val uncheckedFiles = mutableSetOf<String>()
    val showDeleteDialog = mutableStateOf(false)
    val isDeleting = mutableStateOf(false)

    private val fileUseCases =
        FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val duplicateFiles: StateFlow<Map<String, List<LocalFile>>?> =
        fileUseCases.getMediaFiles()
            .map { files ->
                files
                    .filter { it.md5CheckSum != null }
                    .groupBy { it.md5CheckSum!! }
                    .filter { it.value.size > 1 }
            }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectDuplicateFiles() {
        viewModelScope.launch {
            // Wait for first non-null emission — StateFlow starts as null
            val duplicateGroups = duplicateFiles.filterNotNull().first()
            val allFilesExceptFirst = duplicateGroups.values
                .flatMap { it.drop(1) }
                .map { it.id }
                .filter { it !in uncheckedFiles }
                .toSet()
            selectedFileIds.value = allFilesExceptFirst
        }
    }

    fun toggleGroupSelection(groupFiles: List<LocalFile>) {
        if (groupFiles.size <= 1) return
        val filesExceptFirst = groupFiles.drop(1)
        val ids = filesExceptFirst.map { it.id }.toSet()
        val allExceptFirstSelected = ids.all { selectedFileIds.value.contains(it) }
        if (allExceptFirstSelected) {
            selectedFileIds.value -= ids
            uncheckedFiles.addAll(ids)
        } else {
            uncheckedFiles.removeAll(ids)
            selectedFileIds.value += ids
        }
    }

    fun areAllExceptFirstSelected(groupFiles: List<LocalFile>): Boolean {
        if (groupFiles.size <= 1) return false
        
        val filesExceptFirst = groupFiles.drop(1)
        val currentSelection = selectedFileIds.value
        
        return filesExceptFirst.all { file ->
            currentSelection.contains(file.id)
        }
    }

    fun toggleAllGroups() {
        val duplicateGroups = duplicateFiles.value ?: return
        val currentSelection = selectedFileIds.value
        val allGroupsSelected = duplicateGroups.values.all { group ->
            if (group.size <= 1) return@all true
            group.drop(1).all { file -> currentSelection.contains(file.id) }
        }
        val allIds = duplicateGroups.values
            .filter { it.size > 1 }
            .flatMap { it.drop(1).map { f -> f.id } }
            .toSet()
        if (allGroupsSelected) {
            selectedFileIds.value -= allIds
            uncheckedFiles.addAll(allIds)
        } else {
            uncheckedFiles.removeAll(allIds)
            selectedFileIds.value += allIds
        }
    }


    fun deleteFile(localFile: LocalFile) {
        viewModelScope.launch(Dispatchers.IO) {
            val sizeBytes = File(localFile.id).length()
            launch { fileUseCases.deleteFile(localFile.id) }.join()
            File(localFile.id).apply { if (exists()) delete() }
            SavedMemoryTracker.addSavedBytes(sizeBytes)
            withContext(Dispatchers.Main) {
                selectedFileIds.value -= localFile.id
                uncheckedFiles.remove(localFile.id)
            }
        }
    }

    fun deleteFiles(ids: Set<String>) {
        showDeleteDialog.value = true
        pendingDeleteFiles = ids
    }

    fun confirmDeleteFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalBytes = pendingDeleteFiles.sumOf { File(it).length() }
            withContext(Dispatchers.Main) { isDeleting.value = true }
            val snapshotBeforeDelete = duplicateFiles.value
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
            SavedMemoryTracker.addSavedBytes(totalBytes)
            // Wait for Room to emit the updated list, with a 3s timeout as safety net
            val updatedGroups = withTimeoutOrNull(3000) {
                duplicateFiles.first { it != snapshotBeforeDelete }
            }
            val validIds = updatedGroups?.values?.flatten()?.map { it.id }?.toSet() ?: emptySet()
            delay(5000)
            withContext(Dispatchers.Main) {
                selectedFileIds.value = selectedFileIds.value.intersect(validIds)
                uncheckedFiles.removeAll(pendingDeleteFiles)
                showDeleteDialog.value = false
                pendingDeleteFiles = emptySet()
                isDeleting.value = false
            }
        }
    }

    fun cancelDelete() {
        showDeleteDialog.value = false
        pendingDeleteFiles = emptySet()
    }

    private var pendingDeleteFiles = emptySet<String>()

}

package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class
FlatDuplicatesFileManagerVM : ViewModel() {

     val selectedFileIds =  mutableStateOf(setOf<String>())
    val uncheckedFiles = mutableSetOf<String>()
    val showDeleteDialog = mutableStateOf(false)

    private val fileUseCases =
        FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    fun readFiles(): Flow<Map<String, List<LocalFile>>> {
        return fileUseCases.getMediaFiles().flowOn(Dispatchers.Default).map { files ->
            files
                .filter { it.md5CheckSum != null } // Don't group files without a checksum
                .groupBy { it.md5CheckSum!! } // Group by the non-null checksum
                .filter { it.value.size > 1 } // Only keep groups with more than one file
        }
    }

    suspend fun selectDuplicateFiles() {
        readFiles().collect { duplicateGroups ->
            val allFilesExceptFirst = mutableSetOf<String>()
            
            // Iterate through each duplicate group
            duplicateGroups.values.forEach { files ->
                // Add all files except the first one to the selection
                if (files.size > 1) {
                    files.drop(1).forEach { file ->
                        allFilesExceptFirst.add(file.id)
                    }
                }
            }
            
            // Filter out any unchecked files and update selection
            val finalSelection = allFilesExceptFirst.filter { !uncheckedFiles.contains(it) }
            selectedFileIds.value = finalSelection.toSet()
        }
    }

    fun toggleGroupSelection(groupFiles: List<LocalFile>) {
        if (groupFiles.size <= 1) return
        
        val filesExceptFirst = groupFiles.drop(1)
        val currentSelection = selectedFileIds.value
        val allExceptFirstSelected = filesExceptFirst.all { file -> 
            currentSelection.contains(file.id) 
        }
        
        if (allExceptFirstSelected) {
            // Deselect all files except first
            filesExceptFirst.forEach { file ->
                selectedFileIds.value -= file.id
                uncheckedFiles.add(file.id)
            }
        } else {
            // Select all files except first
            filesExceptFirst.forEach { file ->
                // Remove from uncheckedFiles and add to selection
                uncheckedFiles.remove(file.id)
                selectedFileIds.value += file.id
            }
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

    suspend fun toggleAllGroups() {
        readFiles().collect { duplicateGroups ->
            val allFilesExceptFirst = mutableSetOf<String>()
            val currentSelection = selectedFileIds.value
            
            // Check if all groups are currently selected
            val allGroupsSelected = duplicateGroups.values.all { group ->
                if (group.size <= 1) return@all true
                val filesExceptFirst = group.drop(1)
                filesExceptFirst.all { file -> currentSelection.contains(file.id) }
            }
            
            if (allGroupsSelected) {
                // Deselect all files except first in each group
                duplicateGroups.values.forEach { group ->
                    if (group.size > 1) {
                        group.drop(1).forEach { file ->
                            selectedFileIds.value -= file.id
                            uncheckedFiles.add(file.id)
                        }
                    }
                }
            } else {
                // Select all files except first in each group
                duplicateGroups.values.forEach { group ->
                    if (group.size > 1) {
                        group.drop(1).forEach { file ->
                            uncheckedFiles.remove(file.id)
                            selectedFileIds.value += file.id
                        }
                    }
                }
            }
        }
    }


    fun deleteFile(localFile: LocalFile) {
        viewModelScope.launch(Dispatchers.IO) {

//            deleting file from local storage table
            launch { fileUseCases.deleteFile(localFile.id) }.join()

//            deleting file from directory
            File(localFile.id).apply {
                if (exists()) delete()
            }
        }
    }

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
            selectedFileIds.value -= pendingDeleteFiles
            showDeleteDialog.value = false
        }
    }

    fun cancelDelete() {
        showDeleteDialog.value = false
        pendingDeleteFiles = emptySet()
    }

    private var pendingDeleteFiles = emptySet<String>()

}

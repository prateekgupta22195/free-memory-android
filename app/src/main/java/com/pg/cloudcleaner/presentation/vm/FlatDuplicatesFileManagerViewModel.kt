package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class
FlatDuplicatesFileManagerViewModel : ViewModel() {

     val selectedFileIds =  mutableStateOf(setOf<String>())
    val uncheckedFiles = mutableSetOf<String>()

    private val action: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    fun readFiles(): Flow<Map<String, List<LocalFile>>> {
        return action.getMediaFiles().flowOn(Dispatchers.Default).map { it ->
            it.groupBy { localFile ->
                localFile.md5CheckSum
            }.filter {
                it.value.size > 1
            }
        }
    }

    suspend fun selectDuplicateFiles() {
        action.getDuplicateFileIds().distinctUntilChanged().collect {
            it.filter {
                !uncheckedFiles.contains(it)
            }
            selectedFileIds.value += it
        }
    }


    fun deleteFile(localFile: LocalFile) {
        viewModelScope.launch(Dispatchers.IO) {

//            deleting file from local storage table
            launch { action.deleteFile(localFile.id) }.join()

//            deleting file from directory
            File(localFile.id).apply {
                if (exists()) delete()
            }
        }
    }

    suspend fun deleteFiles(ids: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                action.deleteFiles(ids.toList())
            }.join()

            launch {
                ids.forEach { filePath ->
                    File(filePath).apply {
                        if (exists()) delete()
                    }
                }
            }.join()
            selectedFileIds.value -= ids
        }
    }

}

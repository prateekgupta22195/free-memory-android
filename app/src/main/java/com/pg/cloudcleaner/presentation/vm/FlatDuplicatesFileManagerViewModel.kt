package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.File

class FlatDuplicatesFileManagerViewModel : ViewModel() {

    val selectedFileIds = mutableStateListOf<String>()

    private val action: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val mutex = Mutex()
    fun readFiles(): Flow<Map<String, List<LocalFile>>> {
        return action.getMediaFiles().flowOn(Dispatchers.Default).map { it ->
            it.groupBy { localFile ->
                localFile.md5CheckSum
            }.filter {
                it.value.size > 1
            }.
            onEachIndexed { _, entry ->
//                 This is to make sure that duplicate images remain selected
                Timber.d("inside filter")
//                mutex.withLock {
                selectedFileIds.addAll(
                    entry.value.filter {
                    it.duplicate }.map { it.id }
                )
            }
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

    suspend fun deleteFiles(ids: List<String>) {
        mutex.withLock {
            viewModelScope.launch(Dispatchers.IO) {

                launch {
                    action.deleteFiles(ids)
                }.join()

                launch {
                    ids.forEach { filePath ->
                        File(filePath).apply {
                            if (exists()) delete()
                        }
                    }
                }.join()
                selectedFileIds.clear()
            }
        }


    }
}

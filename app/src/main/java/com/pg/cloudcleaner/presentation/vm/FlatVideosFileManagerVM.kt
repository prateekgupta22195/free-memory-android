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

class FlatVideosFileManagerVM : ViewModel() {


    private val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val selectedModeOn = mutableStateOf(false)

    val selectedFiles = mutableStateOf(setOf<String>())

    fun getVideoFiles() = fileUseCases.getVideoFiles()
    suspend fun deleteFiles(ids: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                fileUseCases.deleteFiles(ids.toList())
            }.join()

            launch {
                ids.forEach { filePath ->
                    File(filePath).apply {
                        if (exists()) delete()
                    }
                }
            }.join()
            selectedFiles.value -= ids
        }
    }

}
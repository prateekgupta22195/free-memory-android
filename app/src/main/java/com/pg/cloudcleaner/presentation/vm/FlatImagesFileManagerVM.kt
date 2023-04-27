package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FlatImagesFileManagerVM : ViewModel() {

    private val action: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val selectedModeOn = mutableStateOf(false)

    val selectedFiles = mutableStateOf(setOf<String>())

    fun getImageFiles() = action.getImageFiles()
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
            selectedFiles.value -= ids
        }
    }

}
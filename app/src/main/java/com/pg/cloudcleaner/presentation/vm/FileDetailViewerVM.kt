package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class FileDetailViewerVM : ViewModel() {

    var infoPopUpVisible = false

    private val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    private val _categoryFiles = MutableStateFlow<List<LocalFile>>(emptyList())
    val categoryFiles: StateFlow<List<LocalFile>> = _categoryFiles

    fun loadFilesByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val files = fileUseCases.getFilesByCategory(category)
            _categoryFiles.value = files
        }
    }

    fun loadFilesByMd5(md5: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val files = fileUseCases.getFilesByMd5(md5)
            _categoryFiles.value = files
        }
    }


    suspend fun getFileById(fileId: String): LocalFile? {
        return fileUseCases.getFileById(fileId)
    }

    fun deleteFile(fileId: String) {

        viewModelScope.launch(Dispatchers.IO) {
//            deleting file from local storage table
            launch { fileUseCases.deleteFile(fileId) }.join()

//            deleting file from directory
            File(fileId).apply {
                if (exists()) delete()
            }
        }
    }


    fun getFileInfo(file: LocalFile): String {
        return fileUseCases.getFileInfo(file.id)
    }

}
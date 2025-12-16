package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileDetailViewerVM : ViewModel() {


    var infoPopUpVisible = false

    private val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))


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
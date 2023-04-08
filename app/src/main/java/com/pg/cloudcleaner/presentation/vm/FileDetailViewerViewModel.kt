package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import java.util.*
import kotlin.math.round

class FileDetailViewerViewModel : ViewModel() {


    var infoPopUpVisible = false

    private val interactor: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))


    suspend fun getFileById(fileId: String): LocalFile? {
        return interactor.getFileById(fileId)
    }


    fun getFileInfo(file: LocalFile): String {
        val fileSize: String =
            if (file.size > 1000) {
                "File size : ${(round((file.size / 1000).toDouble()))} MB\""
            } else {
                "File size : ${(round((file.size).toDouble()))} KB\""
            }
        return fileSize + "\nFile Type : ${file.fileType}" + "\nFile Name : ${file.fileName}" + "\nLast Modified : ${
            file.modifiedTime?.let {
                Date(it)
            } ?: '-'
        }"
    }

}
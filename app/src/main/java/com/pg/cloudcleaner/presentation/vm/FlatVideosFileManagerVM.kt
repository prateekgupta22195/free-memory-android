package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl

class FlatVideosFileManagerVM : ViewModel() {


    private val action: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))


    fun getVideoFiles() = action.getVideoFiles()

}
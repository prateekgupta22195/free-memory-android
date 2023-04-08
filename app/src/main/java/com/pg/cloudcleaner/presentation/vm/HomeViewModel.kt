package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.HomeInteractor
import com.pg.cloudcleaner.domain.interactors.HomeInteractorImpl
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {

    private val interactor: HomeInteractor =
        HomeInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    fun getAnyTwoDuplicateFiles(): Flow<Pair<LocalFile, LocalFile>?> {
        return interactor.getAnyTwoDuplicates()
    }
    fun getVideoFile(): Flow<LocalFile?> {
        return interactor.getVideoFile()
    }
    fun getVideoFiles(): Flow<List<LocalFile>> {
        return interactor.getVideoFiles()
    }
    fun getImageFiles(): Flow<List<LocalFile>> {
        return interactor.getImageFiles()
    }


}
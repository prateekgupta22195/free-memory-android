package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.HomeUseCases
import kotlinx.coroutines.flow.Flow

class HomeVM : ViewModel() {

    private val homeUseCases = HomeUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    fun getAnyTwoDuplicateFiles(): Flow<Pair<LocalFile, LocalFile>?> {
        return homeUseCases.getAnyTwoDuplicates()
    }

    fun getVideoFile(): Flow<LocalFile?> {
        return homeUseCases.getVideoFile()
    }

    fun getVideoFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getVideoFiles()
    }

    fun getLargeFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getLargeFiles()
    }

    fun getImageFiles(): Flow<List<LocalFile>> {
        return homeUseCases.getImageFiles()
    }


}
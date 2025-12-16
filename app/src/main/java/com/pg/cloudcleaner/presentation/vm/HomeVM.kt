package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import coil.size.Size
import com.google.android.exoplayer2.util.MimeTypes
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.HomeUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

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

    fun getTotalSizeOfMimeType(mimeType: String): Flow<Long> {
        // returning size in kbs but we store size in mbs
        return homeUseCases.getTotalSizeOfMimeType(mimeType).map { size -> size * 1024 }
    }


    fun getTotalSizeOfLargeFiles(): Flow<Long> {
        // returning size in kbs but we store size in mbs
        return homeUseCases.getTotalSizeOfLargeFiles().map { size -> size * 1024 }
    }






}
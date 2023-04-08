package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow

interface FileActionInteractor {

    suspend fun syncAllFilesToDb(directoryName: String)

    fun getAllFiles(): Flow<List<LocalFile>>

    suspend fun getFileById(fileId: String): LocalFile?

    fun deleteFile(fileIdentity: String)

    fun getMediaFiles(): Flow<List<LocalFile>>

}
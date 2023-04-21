package com.pg.cloudcleaner.domain.repository

import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow

interface LocalFilesRepo {

    suspend fun insertFile(localFile: LocalFile)

    suspend fun insertAll(localFiles: List<LocalFile>)

    fun getAllFiles(): Flow<List<LocalFile>>

    fun deleteFile(id: String)

    fun deleteFiles(ids: List<String>)

    fun getFilesViaQuery(query: String): Flow<List<LocalFile>>

    fun getFilesIDLike(id: String): Flow<List<LocalFile>>

    fun fileAlreadyExists(md5: String) : Boolean

    suspend fun getFileById(id: String): LocalFile?

    fun getDuplicateFileIds() : Flow<List<String>>

}
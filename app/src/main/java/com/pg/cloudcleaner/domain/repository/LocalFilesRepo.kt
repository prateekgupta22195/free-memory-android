package com.pg.cloudcleaner.domain.repository

import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Pattern

interface LocalFilesRepo {

    suspend fun insertFile(localFile: LocalFile)

    suspend fun insertAll(localFiles: List<LocalFile>)

    fun getAllFiles(): Flow<List<LocalFile>>

    suspend fun deleteFile(id: String)

    suspend fun deleteFiles(ids: List<String>)

    fun getFilesViaQuery(query: String): Flow<List<LocalFile>>

    fun fileAlreadyExists(md5: String) : Boolean

    suspend fun getFileById(id: String): LocalFile?

    fun getDuplicateFileIds() : Flow<List<String>>

    fun getDuplicateMediaFiles(): Flow<List<LocalFile>>

    fun getDuplicateCopies(): Flow<List<LocalFile>>

    fun getFilesSizeSumViaQuery(query: String): Flow<Long>

    suspend fun updateFileSize(id: String, sizeKb: Long)
    suspend fun markFileAsOptimised(id: String)
}
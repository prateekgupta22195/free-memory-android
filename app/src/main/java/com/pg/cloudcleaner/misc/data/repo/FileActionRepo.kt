package com.pg.cloudcleaner.misc.data.repo

import com.pg.cloudcleaner.misc.model.DriveFile
import kotlinx.coroutines.flow.Flow

interface FileActionRepo {

    suspend fun getAllFiles(): Flow<List<DriveFile>?>

    suspend fun getAllFilesOwnedByMe(): Flow<List<DriveFile>?>

    suspend fun deleteFile(fileID: String)

    suspend fun deleteFile(driveFile: DriveFile)

    suspend fun getFile(fileID: String): Flow<DriveFile?>

    suspend fun syncDBFiles(accessToken: String, pageToken: String? = null)
}

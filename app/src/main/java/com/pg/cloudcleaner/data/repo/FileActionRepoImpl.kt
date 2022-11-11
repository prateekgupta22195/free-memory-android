package com.pg.cloudcleaner.data.repo

import android.content.Context
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.data.db.AppDatabase
import com.pg.cloudcleaner.data.remote.FileActionRemoteImpl
import com.pg.cloudcleaner.model.DriveFile
import com.pg.cloudcleaner.utils.getGoogleAccessToken
import kotlinx.coroutines.flow.Flow

class FileActionRepoImpl(val context: Context) : FileActionRepo {

    private val database: AppDatabase = AppDatabase.getDatabase(context = context)

    private val fileActionRemote by lazy {
        FileActionRemoteImpl(apiKey = context.getString(R.string.google_api_key))
    }

    override suspend fun getAllFiles(): Flow<List<DriveFile>?> {
        return database.driveFileDao().getAll()
    }

    override suspend fun getAllFilesOwnedByMe(): Flow<List<DriveFile>?> {
        return database.driveFileDao().getFilesOwnedByMe()
    }

    override suspend fun deleteFile(fileID: String) {
        val accessToken = getGoogleAccessToken(context = context)
        val response = fileActionRemote.deleteFile(accessToken, fileID)
        if (response.isSuccessful)
            database.driveFileDao().delete(fileID)
    }

    override suspend fun deleteFile(driveFile: DriveFile) {
        val accessToken = getGoogleAccessToken(context = context)
        val response = fileActionRemote.deleteFile(accessToken, driveFile.id)
        if (response.isSuccessful)
            database.driveFileDao().delete(driveFile)
    }

    override suspend fun getFile(fileID: String): Flow<DriveFile?> {
        return database.driveFileDao().get(fileID)
    }

    override suspend fun syncDBFiles(accessToken: String, pageToken: String?) {

        val response = fileActionRemote.getFiles(accessToken, pageToken).execute()

        val tempFiles = response.body()?.files

        if (tempFiles != null) {
            database.driveFileDao().insertAll(files = tempFiles)
        }
        val tempNextToken = response.body()?.nextTokenPage
        if (tempNextToken != null)
            syncDBFiles(accessToken, tempNextToken)
    }
}

package com.pg.cloudcleaner.misc.data.remote

import com.pg.cloudcleaner.misc.network.retrofit
import com.pg.cloudcleaner.misc.service.DriveAPIResponse
import com.pg.cloudcleaner.misc.service.GoogleDriveApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class FileActionRemoteImpl(
    private val googleDriveApiService: GoogleDriveApiService = retrofit.create(GoogleDriveApiService::class.java),
    private val apiKey: String
) :
    FileActionRemote {

    override fun getFiles(accessToken: String, pageToken: String?): Call<DriveAPIResponse> {
        return googleDriveApiService.filesData(
            "Bearer $accessToken",
            fields = "nextPageToken,files(createdTime,modifiedTime,size,viewedByMeTime,id,mimeType,originalFilename,thumbnailLink,iconLink,ownedByMe)",
            pageSize = 500, pageToken = pageToken, key = apiKey
        )
    }

    override suspend fun deleteFile(accessToken: String, fileId: String): Response<ResponseBody> {
        return googleDriveApiService.deleteFile(fileId = fileId, auth = "Bearer $accessToken", key = apiKey)
    }
}

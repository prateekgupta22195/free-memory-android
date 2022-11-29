package com.pg.cloudcleaner.misc.data.remote

import com.pg.cloudcleaner.misc.service.DriveAPIResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface FileActionRemote {

    fun getFiles(accessToken: String, pageToken: String?): Call<DriveAPIResponse>

    suspend fun deleteFile(accessToken: String, fileId: String): Response<ResponseBody>
}

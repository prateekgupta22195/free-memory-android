package com.pg.cloudcleaner.misc.service

import com.google.gson.annotations.SerializedName
import com.pg.cloudcleaner.misc.model.DriveFile
import com.pg.cloudcleaner.misc.model.MediaItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface GoogleDriveApiService {

    @GET("/drive/v3/files")
    fun filesData(
        @Header("Authorization") auth: String,
        @Query("key") key: String,
        @Query("fields") fields: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageToken") pageToken: String? = null,
    ): Call<DriveAPIResponse>

    @GET("/v1/mediaItems")
    fun getPhotos(
        @Query("pageToken") pageToken: String? = null,
    ): Call<PhotoAPIResponse>

    @DELETE("/drive/v3/files/{fileId}")
    suspend fun deleteFile(
        @Path("fileId") fileId: String,
        @Header("Authorization") auth: String,
        @Query("key") key: String,
    ): Response<ResponseBody>
}

data class DriveAPIResponse(
    @SerializedName("nextPageToken") val nextTokenPage: String?,
    @SerializedName("files") val files: List<DriveFile>,
)

data class PhotoAPIResponse(
    @SerializedName("nextPageToken") val nextTokenPage: String?,
    @SerializedName("mediaItems") val mediaItems: List<MediaItem>,
)

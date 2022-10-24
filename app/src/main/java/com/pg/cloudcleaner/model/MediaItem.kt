package com.pg.cloudcleaner.model

import com.google.gson.annotations.SerializedName

data class MediaItem(
    @SerializedName("id") val id: String,
    @SerializedName("mimeType") val mimeType: String,
    @SerializedName("filename") val filename: String,
    @SerializedName("baseUrl") val baseUrl: String,
)

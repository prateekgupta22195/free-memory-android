package com.pg.cloudcleaner.misc.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DriveFile(
    @ColumnInfo(name = "mimeType") @SerializedName("mimeType") val fileType: String?,
    @ColumnInfo(name = "viewedByMeTime") @SerializedName("viewedByMeTime") val lastViewedTime: String?,
    @ColumnInfo(name = "modifiedTime") @SerializedName("modifiedTime") val modifiedTime: String?,
    @ColumnInfo(name = "originalFilename") @SerializedName("originalFilename") val fileName: String?,
    @ColumnInfo(name = "size") @SerializedName("size") val size: Long,
    @ColumnInfo(name = "thumbnailLink") @SerializedName("thumbnailLink") val thumbnailLink: String?,
    @ColumnInfo(name = "iconLink") @SerializedName("iconLink") val iconLink: String?,
    @ColumnInfo(name = "ownedByMe") @SerializedName("ownedByMe") val ownedByMe: Boolean,
    @PrimaryKey @SerializedName("id") val id: String,
)

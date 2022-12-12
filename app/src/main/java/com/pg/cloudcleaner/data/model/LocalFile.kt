package com.pg.cloudcleaner.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.size
import java.io.File
import java.security.MessageDigest
import java.util.*

@Entity
data class LocalFile(
    @ColumnInfo(name = "mimeType") @SerializedName("mimeType") val fileType: String?,
    @ColumnInfo(name = "modifiedTime") @SerializedName("modifiedTime") val modifiedTime: Long?,
    @ColumnInfo(name = "originalFilename") @SerializedName("originalFilename") val fileName: String?,
    @ColumnInfo(name = "size") @SerializedName("size") val size: Long,
    @PrimaryKey
    @SerializedName("id") val id: String,
)

fun File.toLocalFile(): LocalFile {
    return LocalFile(
        getMimeType(absolutePath),
        lastModified(),
        name,
        size(),
        absolutePath
    )
}
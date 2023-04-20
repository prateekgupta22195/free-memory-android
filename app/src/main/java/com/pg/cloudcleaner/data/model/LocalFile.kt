package com.pg.cloudcleaner.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.size
import java.io.File
import java.util.*

@Entity(indices = [Index(value = ["md5"])])
open class LocalFile(
    @ColumnInfo(name = "mimeType") @SerializedName("mimeType") val fileType: String?,
    @ColumnInfo(name = "modifiedTime") @SerializedName("modifiedTime") val modifiedTime: Long?,
    @ColumnInfo(name = "originalFilename") @SerializedName("originalFilename") val fileName: String?,
    @ColumnInfo(name = "size") @SerializedName("size") val size: Long,
    @ColumnInfo(name = "md5") @SerializedName("md5") val md5CheckSum: String,
    @PrimaryKey
    @SerializedName("id") val id: String,
    @ColumnInfo(name = "duplicate") @SerializedName("duplicate") val duplicate: Boolean,
)

fun File.toLocalFile(duplicate: Boolean, md5: String): LocalFile {
    return LocalFile(
        getMimeType(absolutePath),
        lastModified(),
        name,
        size(),
        md5,
        absolutePath,
        duplicate
    )
}
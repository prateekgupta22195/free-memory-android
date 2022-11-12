package com.pg.cloudcleaner.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore.Video.Thumbnails
import android.util.Size
import android.util.TypedValue
import android.webkit.MimeTypeMap
import java.io.File

fun getBitmapThumbnail(file: File): Bitmap? {
    val mimeType = getMimeType(file.absolutePath)
    return when {
        mimeType?.contains("image") == true -> {
            getImageBitmapThumbnail(file)
        }
        mimeType?.contains("video") == true -> {
            getVideoBitmapThumbnail(file)
        }
        mimeType?.contains("pdf") == true -> {
            getPDFBitmapThumbnail(file)
        }
        else -> {
            null
        }
    }
}

private fun getPDFBitmapThumbnail(file: File): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(file.absolutePath, Thumbnails.MINI_KIND)
}

private fun getImageBitmapThumbnail(file: File, size: Size = Size(128.toPx.toInt(), 128.toPx.toInt())): Bitmap {
    return ThumbnailUtils.extractThumbnail(
        BitmapFactory.decodeFile(file.absolutePath),
        size.width,
        size.height
    )
}

private fun getVideoBitmapThumbnail(file: File, size: Size = Size(128.toPx.toInt(), 128.toPx.toInt()), thumbnailKind: Int = Thumbnails.MINI_KIND): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(file.absolutePath, thumbnailKind)
}

fun getMimeType(path: String): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

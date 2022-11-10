package com.pg.cloudcleaner.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.TypedValue
import android.webkit.MimeTypeMap
import java.io.File

fun getBitmap(file: File): Bitmap? {

    val mimeType = getMimeType(file.absolutePath)
    return when {
        mimeType?.contains("image") == true -> {
            getImageBitmap(file)
        }
        mimeType?.contains("video") == true -> {
            getVideoBitmap(file)
        }
        mimeType?.contains("pdf") == true -> {
            getPDFBitmap(file)
        }
        else -> {
            null
        }
    }
}

private fun getPDFBitmap(file: File): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
}

private fun getImageBitmap(file: File): Bitmap {
    return ThumbnailUtils.extractThumbnail(
        BitmapFactory.decodeFile(file.absolutePath),
        128.toPx.toInt(),
        128.toPx.toInt()
    )
}

private fun getVideoBitmap(file: File): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
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

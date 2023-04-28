package com.pg.cloudcleaner.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore.Video.Thumbnails
import android.util.Size
import android.util.TypedValue
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

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

private fun getImageBitmapThumbnail(
    file: File, size: Size = Size(128.toPx.toInt(), 128.toPx.toInt())
): Bitmap {
    return ThumbnailUtils.extractThumbnail(
        BitmapFactory.decodeFile(file.absolutePath), size.width, size.height
    )
}

private fun getVideoBitmapThumbnail(
    file: File,
    size: Size = Size(128.toPx.toInt(), 128.toPx.toInt()),
    thumbnailKind: Int = Thumbnails.MINI_KIND
): Bitmap? {
    return ThumbnailUtils.createVideoThumbnail(file.absolutePath, thumbnailKind)
}

fun getMimeType(path: String): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

// return file size in mb
fun File.size(): Long {
    return length() / 1024
}


fun Uri.open(context: Context) {
    Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setDataAndType(this@open, context.contentResolver.getType(this@open))
    }.also {
        context.startActivity(it)
    }
}


fun File.md5(): String {
    val md: MessageDigest = MessageDigest.getInstance("MD5")
    val fis = FileInputStream(this)
    val buffer = ByteArray(8192)
    var read: Int
    while (fis.read(buffer).also { read = it } != -1) {
        md.update(buffer, 0, read)
    }
    val hashBytes: ByteArray = md.digest()
    val sb = StringBuilder()
    for (hashByte in hashBytes) {
        sb.append(((hashByte.toInt() and 0xff) + 0x100).toString(16).substring(1))
    }
    fis.close()
    return sb.toString()
}


val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    )


fun isFileImage(mimeType: String?) = mimeType?.contains("image", ignoreCase = true) == true
fun isFileVideo(mimeType: String?) = mimeType?.contains("video", ignoreCase = true) == true


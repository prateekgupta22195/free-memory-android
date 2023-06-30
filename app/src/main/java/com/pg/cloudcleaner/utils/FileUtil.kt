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

fun getMimeType(path: String): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

// return file size in mb
fun File.size(): Long {
    return length() / 1024
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
fun isFileImage(mimeType: String?) = mimeType?.contains("image", ignoreCase = true) == true
fun isFileVideo(mimeType: String?) = mimeType?.contains("video", ignoreCase = true) == true

fun isPDFFile(mimeType: String?) = mimeType?.contains("pdf", ignoreCase = true) == true

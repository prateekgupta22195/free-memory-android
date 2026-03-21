package com.pg.cloudcleaner.utils

import android.webkit.MimeTypeMap
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

fun getMimeType(path: String): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

// return file size in mb
fun File.size(): Long {
    return length() / 1024
}

fun File.md5(): String? {
    if (!exists()) return null
    return try {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(this).use { fis ->
            val buffer = ByteArray(8192)
            var read: Int
            while (fis.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        val hashBytes = md.digest()
        val sb = StringBuilder()
        for (hashByte in hashBytes) {
            sb.append(((hashByte.toInt() and 0xff) + 0x100).toString(16).substring(1))
        }
        sb.toString()
    } catch (e: IOException) {
        Timber.w(e, "Could not compute md5 for $absolutePath")
        null
    }
}

fun isFileImage(mimeType: String?) = mimeType?.contains("image", ignoreCase = true) == true
fun isFileVideo(mimeType: String?) = mimeType?.contains("video", ignoreCase = true) == true

fun isVideo(path: String): Boolean {
    val extension = path.substringAfterLast('.', "").lowercase()
    return extension in listOf("mp4", "mkv", "webm", "avi", "mov")
}

fun isImage(path: String): Boolean {
    val extension = path.substringAfterLast('.', "").lowercase()
    return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
}
package com.pg.cloudcleaner.utils

import android.webkit.MimeTypeMap
import androidx.exifinterface.media.ExifInterface
import com.pg.cloudcleaner.utils.ImageOptimizer
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

fun File.readIsOptimised(): Boolean {
    val ext = extension.lowercase()
    if (ext != "jpg" && ext != "jpeg") return false
    return try {
        ExifInterface(absolutePath).getAttribute(ExifInterface.TAG_USER_COMMENT) == ImageOptimizer.EXIF_MARKER
    } catch (_: Exception) {
        false
    }
}

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
            val buffer = ByteArray(65536)
            var read: Int
            while (fis.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        val sb = StringBuilder(32)
        for (hashByte in md.digest()) {
            sb.append("%02x".format(hashByte.toInt() and 0xff))
        }
        sb.toString()
    } catch (e: IOException) {
        Timber.w(e, "Could not compute md5 for $absolutePath")
        null
    }
}

/**
 * Computes MD5 over a partial read of the file and appends the file size as
 * "<hash>_<sizeInBytes>", so two files with identical partial content but
 * different sizes always produce different checksum strings.
 *
 * Strategy:
 *  - Files ≤ 2 × [bytesFromEachEnd]: entire file is hashed (no edge cases).
 *  - Larger files: first [bytesFromEachEnd] bytes + last [bytesFromEachEnd] bytes.
 *    The tail is reached via FileChannel.position() which is an exact seek,
 *    unlike InputStream.skip() which is not guaranteed to advance the full amount.
 *
 * Reading both head and tail catches files that share a common header but differ
 * at the end (e.g. a PDF with extra pages appended).
 */
fun File.partialMd5(bytesFromEachEnd: Int = 4096): String? {
    if (!exists()) return null
    val fileSize = length()
    return try {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(this).use { fis ->
            if (fileSize <= bytesFromEachEnd * 2L) {
                // Small file: hash everything in one pass
                val buffer = ByteArray(fileSize.toInt())
                val read = fis.read(buffer)
                if (read > 0) md.update(buffer, 0, read)
            } else {
                // Large file: hash head
                val headBuffer = ByteArray(bytesFromEachEnd)
                val headRead = fis.read(headBuffer)
                if (headRead > 0) md.update(headBuffer, 0, headRead)

                // Seek to tail via channel (exact, unlike skip())
                fis.channel.position(fileSize - bytesFromEachEnd)
                val tailBuffer = ByteArray(bytesFromEachEnd)
                val tailRead = fis.read(tailBuffer)
                if (tailRead > 0) md.update(tailBuffer, 0, tailRead)
            }
        }
        val sb = StringBuilder(32)
        for (hashByte in md.digest()) {
            sb.append("%02x".format(hashByte.toInt() and 0xff))
        }
        "${sb}_$fileSize"
    } catch (e: IOException) {
        Timber.w(e, "Could not compute partial md5 for $absolutePath")
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
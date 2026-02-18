package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt

@Composable
fun OtherFileThumbnailCompose(
    modifier: Modifier = Modifier,
    filePath: String? = null
) {

    val extension = remember(filePath) {
        filePath?.substringAfterLast('.', "") ?: ""
    }
    if (extension.isNotBlank()) {
        FileTypeIcon(extension = extension, modifier = modifier)
    } else {
        // Fallback for when we don't have an extension
        Icon(
            imageVector = Icons.Filled.InsertDriveFile,
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
@Composable
fun FileTypeIcon(extension: String, modifier: Modifier = Modifier) {
    val backgroundColor = fileTypeColor(extension)
    Box(
        modifier = modifier
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = extension.uppercase(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

fun fileTypeColor(extension: String): Color {
    val colorInt = when (extension.lowercase()) {
        "pdf" -> "#E53935".toColorInt()
        "doc", "docx" -> "#1E88E5".toColorInt()
        "xls", "xlsx" -> "#43A047".toColorInt()
        "ppt", "pptx" -> "#FB8C00".toColorInt()
        "mp3", "wav" -> "#8E24AA".toColorInt()
        "mp4", "mkv" -> "#3949AB".toColorInt()
        "apk" -> "#2E7D32".toColorInt()
        "zip", "rar" -> "#6D4C41".toColorInt()
        else -> "#757575".toColorInt()
    }
    return Color(colorInt)
}

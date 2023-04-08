package com.pg.cloudcleaner.presentation.ui.components.thumbnail

import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.runtime.Composable

@Composable
fun OtherFileThumbnail(mimeType: String?) {
    val icon = if (mimeType?.contains("pdf", true) == true) Icons.Filled.PictureAsPdf
    else if (mimeType?.contains("doc", true) == true || mimeType?.contains(
            "docx",
            true
        ) == true
    ) Icons.Filled.Description
    else if (mimeType?.contains("xls", true) == true || mimeType?.contains(
            "xlsx", true
        ) == true
    ) Icons.Filled.InsertChart
    else Icons.Filled.InsertDriveFile

    Icon(
        imageVector = icon, contentDescription = null, tint = MaterialTheme.colors.surface
    )
}
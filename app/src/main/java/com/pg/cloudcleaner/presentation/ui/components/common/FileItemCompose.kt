package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.common.thumbnail.FileThumbnailCompose

@Composable
fun FileItemCompose(
    file: LocalFile,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    key(file.id) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(thumbnailSize)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline)
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() },
                )
        ) {
            FileThumbnailCompose(mimeType = file.fileType, model = file.id)
        }
    }
}
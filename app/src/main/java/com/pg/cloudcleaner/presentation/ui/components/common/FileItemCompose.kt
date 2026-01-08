package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.common.thumbnail.FileThumbnailCompose
import java.io.File

@Composable
fun FileItemCompose(
    file: LocalFile,
    thumbnailSize: Dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    key(file.id) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(thumbnailSize)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                .background(MaterialTheme.colorScheme.surfaceDim)
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = { onLongClick?.invoke() },
                )
        ) {
            FileThumbnailCompose(model = file.id)
        }
    }
}
package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.thumbnail.FileThumbnail

@Composable
fun FileItem(
    file: LocalFile,
    onClick: (() -> Unit)? = null,
) {
    key(file.id) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(thumbnailSize)) {
            Card(modifier = if (onClick != null) Modifier.clickable {
                    onClick()
//                    val navController = App.instance.navController()
//                    val fileUrl = Uri.encode(file.id)
//                    navController.navigate(Routes.FILE_DETAIL_VIEWER + "?url=$fileUrl")

                } else Modifier,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)) {
                FileThumbnail(mimeType = file.fileType, model = file.id)
            }
        }
    }

}
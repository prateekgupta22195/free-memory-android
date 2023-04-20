package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.thumbnail.FileThumbnail

@Composable
fun FileItem(
    file: LocalFile,
    onClick: (() -> Unit)? = null,
) {
    key(file.id) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier) {
            Card(
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
                    .padding(4.dp)

                    .clickable {
                        onClick?.invoke()
//                    val navController = App.instance.navController()
//                    val fileUrl = Uri.encode(file.id)
//                    navController.navigate(Routes.FILE_DETAIL_VIEWER + "?url=$fileUrl")

                    }, border = BorderStroke(width = 1.dp, color = Color.LightGray)
            ) {
                FileThumbnail(mimeType = file.fileType, model = file.id)
            }
        }
    }

}
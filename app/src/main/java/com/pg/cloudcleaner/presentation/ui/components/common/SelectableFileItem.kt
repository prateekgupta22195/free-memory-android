package com.pg.cloudcleaner.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.ui.IconModifier
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.common.FileItemCompose

@Composable
fun SelectableFileItem(
    file: LocalFile,
    thumbnailSize: Dp,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    showInfo: (() -> Unit)? = null,
    onCheckedChangeListener: ((Boolean) -> Unit)? = null,
    onLongClickOnItem: (() -> Unit)? = null,
    category: String = "",
) {
    Box(contentAlignment = Alignment.Center) {
        FileItemCompose(
            file = file,
            thumbnailSize = thumbnailSize,
            onClick = {
                // Always navigate on item click, regardless of enabled state
                val navController = App.instance.navController()
                val fileUrl = Uri.encode(file.id)
                val encodedCategory = Uri.encode(category)
                
                // For duplicate files, pass the MD5 hash to show the entire duplicate group
                val duplicateParam = if (category == "category_duplicates" && file.md5CheckSum != null) {
                    "&md5=${Uri.encode(file.md5CheckSum)}"
                } else {
                    ""
                }
                
                navController.navigate(Routes.FILE_DETAIL_VIEWER + "?url=$fileUrl&category=$encodedCategory$duplicateParam")
            }, onLongClick = onLongClickOnItem )
        if (enabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable {
                        onCheckedChangeListener?.invoke(!isSelected)
                    }
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null // We handle the click through the Box's clickable modifier
                )
            }
        }
        if (showInfo != null) Icon(Icons.Filled.Info, "info", modifier = IconModifier
            .clickable {
                showInfo()
            }
            .align(Alignment.BottomEnd))
    }
}
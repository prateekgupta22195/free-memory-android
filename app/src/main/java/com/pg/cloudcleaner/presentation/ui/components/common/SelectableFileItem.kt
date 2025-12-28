package com.pg.cloudcleaner.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.ui.IconModifier
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.common.FileItemCompose

@Composable
fun SelectableFileItem(
    file: LocalFile,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    showInfo: (() -> Unit)? = null,
    onCheckedChangeListener: ((Boolean) -> Unit)? = null,
    onLongClickOnItem: (() -> Unit)? = null
) {
    Box(contentAlignment = Alignment.Center) {
        FileItemCompose(file = file, onClick = {
            if(!enabled) {
                val navController = App.instance.navController()
                val fileUrl = Uri.encode(file.id)
                navController.navigate(Routes.FILE_DETAIL_VIEWER + "?url=$fileUrl")
            } else {
                onCheckedChangeListener?.invoke(!isSelected)
            }
        }, onLongClick = onLongClickOnItem )
        if (enabled) Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChangeListener,
            modifier = Modifier.align(Alignment.TopEnd),
        )
        if (showInfo != null) Icon(Icons.Filled.Info, "info", modifier = IconModifier
            .clickable {
                showInfo()
            }
            .align(Alignment.BottomEnd))
    }
}
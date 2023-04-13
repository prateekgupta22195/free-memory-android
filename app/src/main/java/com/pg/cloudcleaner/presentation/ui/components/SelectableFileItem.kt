package com.pg.cloudcleaner.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.data.model.LocalFile

@Composable
fun SelectableFileItem(
    file: LocalFile,
    isSelected: Boolean = false,
    onCheckedChangeListener: ((Boolean) -> Unit)? = null
) {
    Box {
        FileItem(file = file, onClick = {
            val navController = App.instance.navController()
            val fileUrl = Uri.encode(file.id)
            navController.navigate(Routes.FILE_DETAIL_VIEWER + "?url=$fileUrl")
        })

        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChangeListener,
            modifier = Modifier.align(Alignment.TopEnd),
        )

    }

}
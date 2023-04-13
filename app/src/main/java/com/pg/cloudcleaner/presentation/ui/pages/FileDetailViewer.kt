package com.pg.cloudcleaner.presentation.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.ui.IconModifier
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIcon
import com.pg.cloudcleaner.presentation.ui.components.ImageViewer
import com.pg.cloudcleaner.presentation.ui.components.Popup
import com.pg.cloudcleaner.presentation.ui.components.VideoPlayer
import com.pg.cloudcleaner.presentation.ui.components.thumbnail.OtherFileThumbnail
import com.pg.cloudcleaner.presentation.vm.FileDetailViewerViewModel
import com.pg.cloudcleaner.utils.isFileImage
import com.pg.cloudcleaner.utils.isFileVideo

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FileDetailViewer(
    filePath: String, vm: FileDetailViewerViewModel = viewModel()
) {
    val file: MutableState<LocalFile?> = remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = filePath) {
        file.value = vm.getFileById(filePath)
    }


    val infoPopUpVisibility = remember {
        mutableStateOf(vm.infoPopUpVisible)
    }



    Scaffold(topBar = {
        TopAppBar(
            title = {},
            navigationIcon = { BackNavigationIcon() },
            actions = {
                if (file.value != null) {
                    Icon(Icons.Filled.Info, "info", modifier = IconModifier.clickable {
                        infoPopUpVisibility.value = !infoPopUpVisibility.value
                    })
                }

                Icon(Icons.Filled.Delete, "delete", modifier = IconModifier.clickable {})
            },
        )
    }, content = fileDetailBody(file.value))


    Popup(show = infoPopUpVisibility.value, onBackPress = {},) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val dialogWidth = (screenWidth * 0.8).dp
        Box {
            Card(
                elevation = 8.dp,
                modifier = Modifier
                    .width(dialogWidth)
                    .height(100.dp)
                    .align(alignment = Alignment.Center)
            ) {
                Text(
                    text = vm.getFileInfo(file.value!!),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    fontSize = 18.sp,
                )
            }
        }

    }

}


@Composable
fun fileDetailBody(
    file: LocalFile?
): @Composable (a: PaddingValues) -> Unit {

    if (file != null) return {
        if (isFileImage(file.fileType)) ImageViewer(file.id)
        else if (isFileVideo(file.fileType)) VideoPlayer(file.id)
        else OtherFileThumbnail(mimeType = file.fileType)
    }
    return {}
}

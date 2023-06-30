package com.pg.cloudcleaner.presentation.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.ui.IconModifier
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.VideoPlayer
import com.pg.cloudcleaner.presentation.ui.components.common.ImageViewer
import com.pg.cloudcleaner.presentation.ui.components.common.PDFViewer
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.ui.components.common.thumbnail.OtherFileThumbnailCompose
import com.pg.cloudcleaner.presentation.vm.FileDetailViewerVM
import com.pg.cloudcleaner.utils.isFileImage
import com.pg.cloudcleaner.utils.isFileVideo
import com.pg.cloudcleaner.utils.isPDFFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FileDetailViewerCompose(
    filePath: String, vm: FileDetailViewerVM = viewModel()
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

    val snackbarHostState = remember { SnackbarHostState() }


    val scope = rememberCoroutineScope()
    val navigator = remember {
        App.instance.navController()
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, topBar = {
        TopAppBar(
            title = {},
            navigationIcon = { BackNavigationIconCompose() },
            actions = {
                if (file.value != null) {
                    Icon(Icons.Filled.Info, "info", modifier = IconModifier.clickable {
                        infoPopUpVisibility.value = !infoPopUpVisibility.value
                    })
                }

                Icon(Icons.Filled.Delete, "delete", modifier = IconModifier.clickable {
                    scope.launch {
                        vm.deleteFile(filePath)
                        snackbarHostState.showSnackbar(
                            "File deleted successfully!"
                        )
                        navigator.navigateUp()
                    }
                })
            },
        )
    }, content = {


        Box(
            modifier = Modifier
                .padding(it)
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
        ) {

            file.value?.let { file ->
                if (isFileImage(file.fileType)) ImageViewer(file.id)
                else if (isFileVideo(file.fileType)) VideoPlayer(file.id)
                else if (isPDFFile(file.fileType))
                    PDFViewer(file.id)
                else OtherFileThumbnailCompose(mimeType = file.fileType)
            }

            PopupCompose(show = infoPopUpVisibility.value, onPopupDismissed = {
                infoPopUpVisibility.value = false
            }) {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp
                val dialogWidth = (screenWidth * 0.8).dp
                Box {
                    Card(
                        modifier = Modifier
                            .width(dialogWidth)
                            .align(alignment = Alignment.Center)
                    ) {
                        Text(
                            text = vm.getFileInfo(file.value!!),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    })
}


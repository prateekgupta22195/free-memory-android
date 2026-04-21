package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.VideoPlayer
import com.pg.cloudcleaner.presentation.ui.components.common.ImageViewer
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.ui.components.common.thumbnail.OtherFileThumbnailCompose
import com.pg.cloudcleaner.presentation.vm.FileDetailViewerVM
import com.pg.cloudcleaner.utils.isFileImage
import com.pg.cloudcleaner.utils.isFileVideo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailViewerCompose(
    filePath: String,
    category: String,
    md5: String? = null,
    vm: FileDetailViewerVM = viewModel()
) {
    LaunchedEffect(category, md5) {
        if (category == "category_duplicates" && md5 != null) {
            vm.loadFilesByMd5(md5)
        } else {
            vm.loadFilesByCategory(category)
        }
    }

    val files by vm.categoryFiles.collectAsState()
    val currentFiles = files

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { currentFiles?.size ?: 0 }
    )

    LaunchedEffect(currentFiles, filePath) {
        if (!currentFiles.isNullOrEmpty()) {
            val index = currentFiles.indexOfFirst { it.id == filePath }
            if (index != -1 && index != pagerState.currentPage) {
                pagerState.animateScrollToPage(index)
            }
        }
    }

    val infoPopUpVisibility = remember { mutableStateOf(false) }
    val navigator = remember { App.instance.navController() }
    val showDeleteDialog = remember { vm.showDeleteDialog }
    val isDeleting = remember { vm.isDeleting }

    val currentFile = currentFiles?.getOrNull(pagerState.currentPage)

    if (showDeleteDialog.value || isDeleting.value) {
        PopupCompose(show = true, onPopupDismissed = { if (!isDeleting.value) vm.cancelDelete() }) {
            AlertDialog(
                onDismissRequest = { if (!isDeleting.value) vm.cancelDelete() },
                title = { Text(if (isDeleting.value) "Deleting…" else "Delete File") },
                text = {
                    if (isDeleting.value) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Text("Please wait…")
                        }
                    } else {
                        Text("Are you sure you want to delete ${currentFile?.fileName}? You will not be able to recover it.")
                    }
                },
                confirmButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = {
                            currentFile?.let { file ->
                                vm.confirmDelete(file.id) {
                                    if ((currentFiles?.size ?: 0) <= 1) navigator.navigateUp()
                                }
                            }
                        }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                    }
                },
                dismissButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = { vm.cancelDelete() }) { Text("Cancel") }
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentFile?.fileName ?: "", fontSize = 14.sp) },
                navigationIcon = { BackNavigationIconCompose() },
                actions = {
                    if (currentFile != null) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "info",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { infoPopUpVisibility.value = true }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "delete",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { vm.requestDelete() }
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (currentFiles == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp
            ) { pageIndex ->
                val file = currentFiles.getOrNull(pageIndex) ?: return@HorizontalPager

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when {
                        isFileImage(file.fileType) -> ImageViewer(file.id)
                        isFileVideo(file.fileType) -> VideoPlayer(file.id)
                        else -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                OtherFileThumbnailCompose(filePath = file.fileName)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = vm.getFileInfo(file),
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 18.sp,
                                )
                            }
                        }
                    }
                }
            }

            if (infoPopUpVisibility.value && currentFile != null) {
                PopupCompose(show = true, onPopupDismissed = { infoPopUpVisibility.value = false }) {
                    Card(modifier = Modifier.width(300.dp)) {
                        Text(
                            text = vm.getFileInfo(currentFile),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

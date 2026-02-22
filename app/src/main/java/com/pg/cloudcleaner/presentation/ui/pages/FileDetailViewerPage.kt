package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch

// ... (keep existing imports)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailViewerCompose(
    filePath: String,
    category: String, // Add category as a parameter
    md5: String? = null, // Add optional MD5 parameter
    vm: FileDetailViewerVM = viewModel()
) {
    // 1. Load the list of files for this category or duplicate group
    LaunchedEffect(category, md5) {
        if (category == "category_duplicates" && md5 != null) {
            vm.loadFilesByMd5(md5)
        } else {
            vm.loadFilesByCategory(category)
        }
    }

    val files by vm.categoryFiles.collectAsState()

    // 2. Setup Pager State
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { files.size }
    )

    // Update pager to the correct page when files are loaded
    LaunchedEffect(files, filePath) {
        if (files.isNotEmpty()) {
            val index = files.indexOfFirst { it.id == filePath }
            if (index != -1 && index != pagerState.currentPage) {
                pagerState.animateScrollToPage(index)
            }
        }
    }

    val infoPopUpVisibility = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navigator = remember { App.instance.navController() }
    val showDeleteDialog = remember { mutableStateOf(false) }

    // Logic for deleting the CURRENT file in view
    val currentFile = if (files.isNotEmpty()) files[pagerState.currentPage] else null

    if (showDeleteDialog.value && currentFile != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete File") },
            text = { Text("Are you sure you want to delete ${currentFile.fileName}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog.value = false
                    scope.launch {
                        vm.deleteFile(currentFile.id)
                        snackbarHostState.showSnackbar("Deleted!")
                        // If it was the last file, go back
                        if (files.size <= 1) navigator.navigateUp()
                    }
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog.value = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                                .clickable { showDeleteDialog.value = true }
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // 3. The Pager implementation
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp // Visual gap between files
            ) { pageIndex ->
                val file = files[pageIndex]

                // Display content based on file type (Extracted logic)
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

            // Info Popup for the specific file currently visible
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

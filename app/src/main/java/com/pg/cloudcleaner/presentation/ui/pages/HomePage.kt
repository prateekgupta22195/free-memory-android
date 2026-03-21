package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.presentation.WorkerUIState
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_DUPLICATES
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_IMAGES
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_LARGE_FILES
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_VIDEOS
import com.pg.cloudcleaner.presentation.ui.components.home.AnimatedStorageMeter
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryDuplicateFilesCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryImagesCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryLargeFileCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryVideosCompose
import com.pg.cloudcleaner.presentation.vm.HomeVM
import com.pg.cloudcleaner.presentation.vm.StorageUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComposable(viewModel: HomeVM = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scanStatus by viewModel.scanUIStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = R.drawable.logo,
                            contentDescription = "App Logo",
                            modifier = Modifier.size(48.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(text = "FreeMemory")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                // ── Scanning in progress ──────────────────────────────────
                scanStatus is WorkerUIState.InProgress -> {
                    val state = scanStatus as WorkerUIState.InProgress
                    ScanningComposable(
                        progress = state.progress,
                        message = state.message,
                        vm = viewModel,
                    )
                }

                // ── Scan finished ─────────────────────────────────────────
                scanStatus is WorkerUIState.Success -> {
                    ScanResultComposable(vm = viewModel)
                }

                // ── Normal home ───────────────────────────────────────────
                else -> {
                    val lazyState = rememberLazyListState()
                    LazyColumn(
                        state = lazyState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (uiState !is StorageUiState.Error) {
                            item(key = "storage_meter") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val isLoading = uiState is StorageUiState.Loading
                                    val storageInfo = (uiState as? StorageUiState.Success)?.info
                                    AnimatedStorageMeter(
                                        usedSpaceGB = storageInfo?.usedSpaceGB ?: 0f,
                                        totalSpaceGB = storageInfo?.totalSpaceGB ?: 0f,
                                        isLoading = isLoading
                                    )
                                }
                            }
                        }

                        item(key = CATEGORY_DUPLICATES) { CategoryDuplicateFilesCompose() }
                        item(key = CATEGORY_IMAGES) { CategoryImagesCompose() }
                        item(key = CATEGORY_VIDEOS) { CategoryVideosCompose() }
                        item(key = CATEGORY_LARGE_FILES) { CategoryLargeFileCompose() }
                    }
                }
            }
        }
    }
}

package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.presentation.WorkerUIState
import com.pg.cloudcleaner.presentation.ui.components.home.AnimatedStorageMeter
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryDuplicateFilesCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryImagesCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryLargeFileCompose
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryVideosCompose
import com.pg.cloudcleaner.presentation.vm.HomeVM
import com.pg.cloudcleaner.presentation.vm.StorageUiState

@Composable
fun HomeComposable(viewModel: HomeVM = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scanStatus by viewModel.scanUIStatus.collectAsState()

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val lazyState = rememberLazyListState()

            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                contentPadding = PaddingValues(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                if (uiState !is StorageUiState.Error) {
                    item(key = "storage_meter") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Determine loading state and data based on the UI state
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

                if (scanStatus != null && scanStatus is WorkerUIState.InProgress) {
                    item(key = "scan_status") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.LightGray
                            )
                            Text(
                                fontSize = 12.sp,
                                text = (scanStatus as WorkerUIState.InProgress).message,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item(key = "category_duplicates") {
                    CategoryDuplicateFilesCompose()
                }

                item(key = "category_images") {
                    CategoryImagesCompose()
                }

                item(key = "category_videos") {
                    CategoryVideosCompose()
                }

                item(key = "category_large_files") {
                    CategoryLargeFileCompose()
                }

            }

        }
    }

}

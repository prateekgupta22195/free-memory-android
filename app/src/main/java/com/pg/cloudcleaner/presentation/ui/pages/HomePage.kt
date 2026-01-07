package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    MaterialTheme {
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
                item {
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
            item {
                CategoryDuplicateFilesCompose()
            }

            item {
                CategoryImagesCompose()
            }

            item {
                CategoryVideosCompose()
            }

            item {
                CategoryLargeFileCompose()
            }

        }

    }

}











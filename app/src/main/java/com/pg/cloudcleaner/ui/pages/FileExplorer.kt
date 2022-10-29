package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pg.cloudcleaner.ui.components.fileexplorer.FileListView
import com.pg.cloudcleaner.utils.LogCompositions
import com.pg.cloudcleaner.vm.FileExplorerViewModel

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun FileExplorer() {
    LogCompositions(msg = "FileExplorer")
    val viewModel = ViewModelProvider(LocalViewModelStoreOwner.current!!)[FileExplorerViewModel::class.java]

    val refreshing by remember {
        viewModel.refreshing
    }

    val state = rememberSwipeRefreshState(refreshing)
    val files = viewModel.filesMutable.collectAsState(initial = null)

    SwipeRefresh(
        state = state, onRefresh = {
            viewModel.refresh()
        },
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
    ) {
        if (files.value != null)
            FileListView(driveFiles = files.value!!)
    }
}

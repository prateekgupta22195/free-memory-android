package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pg.cloudcleaner.misc.ui.components.fileexplorer.FileListView
import com.pg.cloudcleaner.misc.vm.FileExplorerViewModel
import com.pg.cloudcleaner.utils.LogCompositions

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun FileExplorer(vm: FileExplorerViewModel = viewModel()) {
    LogCompositions(msg = "FileExplorer")
    val refreshing by remember {
        vm.refreshing
    }

    val state = rememberSwipeRefreshState(refreshing)
    val files = vm.filesMutable.collectAsState(initial = null)

    Column {
        TopAppBar(title = {
            Text(text = "File Explorer")
        })
        SwipeRefresh(
            state = state, onRefresh = {
                vm.refresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            if (files.value != null)
                FileListView(driveFiles = files.value!!)
        }
    }
}

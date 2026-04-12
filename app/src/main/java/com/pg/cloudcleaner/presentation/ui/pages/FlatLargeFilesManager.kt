package com.pg.cloudcleaner.presentation.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_LARGE_FILES
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerContent
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerDeleteComposable
import com.pg.cloudcleaner.presentation.vm.FlatLargeFileManagerVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatLargeFilesManager(vm: FlatLargeFileManagerVM = viewModel()) {

    val selectedModeOn = remember { vm.selectedModeOn }

    val files = vm.getLargeFiles().collectAsState(initial = null)

    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 6
    val thumbnailSize = configuration.screenWidthDp.dp/columns

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(R.string.category_large_files))
        }, actions = {

            if (!selectedModeOn.value) TextButton(onClick = {
                selectedModeOn.value = true
            }) {
                Text(stringResource(R.string.action_select))
            }
            else TextButton(onClick = {
                selectedModeOn.value = false
            }) {
                Text(stringResource(R.string.action_cancel))
            }
        }, navigationIcon = { BackNavigationIconCompose() })
    }, floatingActionButton = {
        if (selectedModeOn.value) {
            val selectedSize = (files.value?.filter { it.id in vm.selectedFiles.value }?.sumOf { it.size } ?: 0L) * 1024
            FlatFileManagerDeleteComposable(vm, selectedSize)
        }
    }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            FlatFileManagerContent(files.value, columns, thumbnailSize, vm, category = CATEGORY_LARGE_FILES)
        }
    }
}
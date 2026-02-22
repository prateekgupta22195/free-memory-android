package com.pg.cloudcleaner.presentation.ui.pages

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_IMAGES
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerContent
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerDeleteComposable
import com.pg.cloudcleaner.presentation.vm.FlatImagesFileManagerVM
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatImagesFileManager(vm: FlatImagesFileManagerVM = viewModel()) {

    val selectedModeOn = remember { vm.selectedModeOn }

    val files = vm.getImageFiles().collectAsState(initial = listOf())

    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 6
    val thumbnailSize = configuration.screenWidthDp.dp/columns

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Images")
        }, actions = {

            if (!selectedModeOn.value) TextButton(onClick = {
                selectedModeOn.value = true
            }) {
                Text("Select")
            }
            else TextButton(onClick = {
                selectedModeOn.value = false
            }) {
                Text("Cancel")
            }
        }, navigationIcon = { BackNavigationIconCompose() })
    }, floatingActionButton = {
        if (selectedModeOn.value) FlatFileManagerDeleteComposable(vm)
    }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            FlatFileManagerContent(files.value, columns, thumbnailSize, vm, category = CATEGORY_IMAGES)
        }
    }
}
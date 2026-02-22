package com.pg.cloudcleaner.presentation.ui.pages

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.CATEGORY_VIDEOS
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerContent
import com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager.FlatFileManagerDeleteComposable
import com.pg.cloudcleaner.presentation.vm.FlatVideosFileManagerVM
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatVideosFileManager(vm: FlatVideosFileManagerVM = viewModel()) {

    val selectedModeOn = remember { vm.selectedModeOn }

    val files = vm.getVideoFiles().collectAsState(initial = listOf())

    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 6
    val thumbnailSize = configuration.screenWidthDp.dp/columns


    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Videos")
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
    }, bottomBar = {
        if (selectedModeOn.value) FlatFileManagerDeleteComposable(vm)
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            FlatFileManagerContent(files.value, columns, thumbnailSize, vm, category = CATEGORY_VIDEOS)
        }
    }
}



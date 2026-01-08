package com.pg.cloudcleaner.presentation.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.presentation.ui.components.common.FileItemCompose
import com.pg.cloudcleaner.presentation.vm.HomeVM


@Composable
fun CategoryDuplicateFilesCompose(vm: HomeVM = viewModel()) {
    val list = vm.getAnyTwoDuplicateFiles().collectAsState(initial = null)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                App.instance
                    .navController()
                    .navigate(Routes.FLAT_DUPLICATES_FILE_MANAGER)
            }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Duplicate Files", modifier = Modifier.padding(bottom = 16.dp), fontWeight = FontWeight.Bold)
            if (list.value != null) {
                Row(
                    modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    FileItemCompose(list.value!!.first, thumbnailSize = thumbnailSize)
                    FileItemCompose(list.value!!.second, thumbnailSize = thumbnailSize)
                }
            } else {
                Text("No Duplicate files found!")
            }
        }
    }

}
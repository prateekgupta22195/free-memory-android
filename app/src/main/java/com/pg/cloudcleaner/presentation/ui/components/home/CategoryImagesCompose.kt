package com.pg.cloudcleaner.presentation.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.presentation.ui.components.common.FileItemCompose
import com.pg.cloudcleaner.presentation.vm.HomeVM

@Composable
fun CategoryImagesCompose(vm: HomeVM = viewModel()) {
    val videoFile = vm.getImageFiles().collectAsState(initial = null)
    if (!videoFile.value.isNullOrEmpty()) {
        Card(modifier = Modifier
            .fillMaxSize()
            .clickable {
                val navController = App.instance.navController()
                navController.navigate(Routes.FLAT_IMAGES_FILE_MANAGER)
            }) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Image Files", modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items(videoFile.value!!.size) {
                        val file = videoFile.value!![it]
                        key(file.id) {
                            Row {
                                FileItemCompose(videoFile.value!![it])
                            }

                        }
                    }
                }
            }
        }
    }
}
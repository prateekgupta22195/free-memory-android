package com.pg.cloudcleaner.presentation.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.HomeVM

@Composable
fun CategoryImagesCompose(vm: HomeVM = viewModel()) {
    val videoFile = vm.getNImageFiles(5).collectAsState(initial = null)

    CategoryWrapperCompose(
        title = "Image Files",
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val navController = App.instance.navController()
            navController.navigate(Routes.FLAT_IMAGES_FILE_MANAGER)
        },
        trailing = { CategorySizeComposable(mimeType = "%image%") },
    ) {
        Column {
            if (!videoFile.value.isNullOrEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items(videoFile.value!!.size) {
                        val file = videoFile.value!![it]
                        key(file.id) {
                            SelectableFileItem(
                                file = videoFile.value!![it], 
                                thumbnailSize = thumbnailSize,
                                enabled = false,
                                category = "category_images"
                            )
                        }
                    }
                }
            } else {
                Text("No images found!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
package com.pg.cloudcleaner.presentation.ui.components.home

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.HomeVM

@Composable
fun CategoryVideosCompose(vm: HomeVM = viewModel()) {
    val videoFile = vm.getNVideoFiles(5).collectAsState(initial = null)

    CategoryWrapperCompose(
        title = "Video Files",
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val navController = App.instance.navController()
            navController.navigate(Routes.FLAT_VIDEOS_FILE_MANAGER)
        },
        trailing = { CategorySizeComposable(mimeType = "%video%") },
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
                                category = "category_videos"
                            )
                        }
                    }
                }
            } else {
                Text("No videos found!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
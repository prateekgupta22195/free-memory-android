package com.pg.cloudcleaner.presentation.ui.components.home

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.HomeVM


@Composable
fun CategoryLargeFileCompose(vm: HomeVM = viewModel()) {
    val videoFile = vm.getLargeFiles().collectAsState(initial = null)

    CategoryWrapperCompose(
        title = "Large Files",
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            val navController = App.instance.navController()
            navController.navigate(Routes.FLAT_LARGE_FILE_MANAGER)
        },
        trailing = { LargeCategorySizeComposable() },
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
                                category = "category_large_files"
                            )
                        }
                    }
                }
            } else {
                Text("No large files found!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun LargeCategorySizeComposable(
    modifier: Modifier = Modifier,
    viewModel: HomeVM = viewModel()
) {
    val context = LocalContext.current

    // Collect the flow from the ViewModel as state
    // Initial value is 0L until the DB query returns
    val totalSizeBytes by viewModel.getTotalSizeOfLargeFiles().collectAsState(initial = 0L)

    // Format the bytes to human readable string (e.g. "12.5 MB")
    val formattedSize = Formatter.formatFileSize(context, totalSizeBytes)

    Text(
        text = formattedSize,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}


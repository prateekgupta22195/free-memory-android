package com.pg.cloudcleaner.presentation.ui.components.home

import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.presentation.ui.components.common.FileItemCompose
import com.pg.cloudcleaner.presentation.vm.HomeVM


@Composable
fun CategoryLargeFileCompose(vm: HomeVM = viewModel()) {
    val videoFile = vm.getLargeFiles().collectAsState(initial = null)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val navController = App.instance.navController()
                navController.navigate(Routes.FLAT_VIDEOS_FILE_MANAGER)
            }) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Large Files", modifier = Modifier.padding(bottom = 16.dp), fontWeight = FontWeight.Bold)
                LargeCategorySizeComposable()
            }
            if (!videoFile.value.isNullOrEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items(videoFile.value!!.size) {
                        val file = videoFile.value!![it]
                        key(file.id) {
                            FileItemCompose(
                                videoFile.value!![it],
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


package com.pg.cloudcleaner.presentation.ui.components.home

import android.text.format.Formatter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.presentation.vm.HomeVM

@Composable
fun CategorySizeComposable(
    mimeType: String,
    modifier: Modifier = Modifier,
    viewModel: HomeVM = viewModel()
) {
    val context = LocalContext.current

    // Collect the flow from the ViewModel as state
    // Initial value is 0L until the DB query returns
    val totalSizeBytes by viewModel.getTotalSizeOfMimeType(mimeType).collectAsState(initial = 0L)

    // Format the bytes to human readable string (e.g. "12.5 MB")
    val formattedSize = Formatter.formatFileSize(context, totalSizeBytes)

    Text(
        text = formattedSize,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

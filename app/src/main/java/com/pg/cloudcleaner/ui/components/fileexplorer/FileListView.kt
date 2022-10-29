package com.pg.cloudcleaner.ui.components.fileexplorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.model.DriveFile
import com.pg.cloudcleaner.utils.LogCompositions

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun FileListView(driveFiles: List<DriveFile>) {
    LogCompositions(msg = "FileListView")
    val context = LocalContext.current
    val fileRepo = FileActionRepoImpl(context = context)
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(driveFiles.size) { index ->
            DriveFileItem(driveFiles[index], fileRepo)
        }
    }
}

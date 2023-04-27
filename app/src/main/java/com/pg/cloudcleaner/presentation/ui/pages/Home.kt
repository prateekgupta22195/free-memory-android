package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryDuplicateFiles
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryImages
import com.pg.cloudcleaner.presentation.ui.components.home.CategoryVideos


@Composable
fun DataCategories() {
    MaterialTheme {
        val lazyState = rememberLazyListState()
        LazyColumn(
            state = lazyState,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            item {
                CategoryDuplicateFiles()
            }

            item {
                CategoryImages()
            }

            item {
                CategoryVideos()
            }

        }

    }

}











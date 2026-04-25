package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App

@Composable
fun ImageViewer(imagePath: String) {
    AsyncImage(
        model = imagePath,
        imageLoader = App.instance.imageLoader,
        contentDescription = "",
        contentScale = ContentScale.Fit,
        error = painterResource(id = R.drawable.ic_file),
        modifier = Modifier.fillMaxSize()
    )
}

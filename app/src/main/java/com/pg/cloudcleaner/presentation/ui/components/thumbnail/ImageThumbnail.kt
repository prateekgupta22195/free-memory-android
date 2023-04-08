package com.pg.cloudcleaner.presentation.ui.components.thumbnail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App


@Composable
fun ImageThumbnail(imagePath: String) {
    val painter = rememberAsyncImagePainter(
        model = imagePath,
        imageLoader = App.instance.imageLoader,
        error = painterResource(id = R.drawable.ic_file)
    )


    Image(
        painter = painter,
        contentDescription = "Image Thumbnail",
        contentScale = if (painter.state is AsyncImagePainter.State.Success) ContentScale.Crop else ContentScale.None,
        modifier = Modifier.fillMaxSize()
    )
}
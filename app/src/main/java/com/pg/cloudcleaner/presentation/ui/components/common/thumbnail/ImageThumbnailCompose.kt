package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Precision
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App

@Composable
fun ImageThumbnailCompose(model: Any?) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(model)
            .size(512)
            .precision(Precision.INEXACT) // 2. Don't let Coil downsample aggressively
            .build(),
        imageLoader = App.instance.imageLoader,
        error = painterResource(id = R.drawable.ic_file),
        fallback = painterResource(id = R.drawable.ic_file)
    )
    val imageStatus by painter.state.collectAsState()
    Image(
        painter = painter,
        contentDescription = "Image Thumbnail",
        modifier = Modifier.fillMaxSize(),
        contentScale = if (imageStatus is AsyncImagePainter.State.Success) ContentScale.Crop else ContentScale.None,
    )
}
package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App

@Composable
fun ImageThumbnailCompose(model: Any?) {

    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(model)
            .size(coil.size.Size.ORIGINAL)
            .precision(Precision.EXACT) // 2. Don't let Coil downsample aggressively
            .build(),
        imageLoader = App.instance.imageLoader,
        error = painterResource(id = R.drawable.ic_file),
        fallback = painterResource(id = R.drawable.ic_file)
    )

    Image(
        painter = painter,
        contentDescription = "Image Thumbnail",
        modifier = Modifier.fillMaxSize(),
        contentScale = if (painter.state is AsyncImagePainter.State.Success) ContentScale.Crop else ContentScale.None,
    )
//    GlideImage(model, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alignment = Alignment.CenterEnd)
}
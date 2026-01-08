package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Precision
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App
import androidx.compose.foundation.Image


@Composable
fun VideoThumbnailCompose(
    model: Any?,
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(model.toString())
            .size(512)
            .precision(Precision.EXACT) // 2. Don't let Coil downsample aggressively
            .build(),
        imageLoader = App.instance.imageLoader,
        error = painterResource(id = R.drawable.ic_file)
    )
    val imageStatus by painter.state.collectAsState()
    val isSuccess = imageStatus is AsyncImagePainter.State.Success
    Box {
        Image(
            painter = painter,
            contentDescription = "video thumbnail",
            alignment = Alignment.Center,
            colorFilter = if (isSuccess) ColorFilter.tint(
                color = Color.DarkGray, blendMode = BlendMode.Overlay
            ) else ColorFilter.tint(
                color = MaterialTheme.colorScheme.onSurface, blendMode = BlendMode.SrcIn
            ),
            modifier = Modifier.fillMaxSize(),
            contentScale = if (isSuccess) ContentScale.Crop else ContentScale.None
        )

        if (isSuccess) Icon(
            Icons.Rounded.PlayArrow,
            contentDescription = "Play icon",
            tint = Color.White,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}
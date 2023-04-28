package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App


@Composable
fun VideoThumbnailCompose(
    model: Any?,
) {
    val painter = rememberAsyncImagePainter(
        model = model.toString(),
        imageLoader = App.instance.imageLoader,
        error = painterResource(id = R.drawable.ic_file)
    )
    val isSuccess = painter.state is AsyncImagePainter.State.Success
    Box {
        androidx.compose.foundation.Image(
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
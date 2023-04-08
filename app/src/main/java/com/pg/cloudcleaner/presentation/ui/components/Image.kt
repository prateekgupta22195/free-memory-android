package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.ktx.InternalGlideApi
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.presentation.ui.components.thumbnail.VideoThumbnail

/**
 * This is sort of adapter class to render Image composable.
 * If later on, we think that we should replace Glide image library with any other one,
 * then it will save us from making changes everywhere.
 */
@OptIn(ExperimentalGlideComposeApi::class, InternalGlideApi::class)
@Composable
fun Image(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.None,
    error: Painter =
        painterResource(
            id = R.drawable.ic_file,
        ),
    placeholder: Painter = painterResource(
        id = R.drawable.ic_file,
    ),
) {


    VideoThumbnail(model = model)
    
}


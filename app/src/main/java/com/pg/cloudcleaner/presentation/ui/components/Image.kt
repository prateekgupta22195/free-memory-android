package com.pg.cloudcleaner.presentation.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pg.cloudcleaner.R

/**
 * This is sort of adapter class to render Image composable.
 * If later on, we think that we should replace Glide image library with any other one,
 * then it will save us from making changes everywhere.
 */
@Composable
fun Image(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.None,
    error: Painter = painterResource(
        id = R.drawable.ic_file,
    ),
    placeholder: Painter = painterResource(
        id = R.drawable.ic_file,
    ),
) {


    AsyncImage(
        model = model,
        placeholder = placeholder,
        error = error,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
    )


}

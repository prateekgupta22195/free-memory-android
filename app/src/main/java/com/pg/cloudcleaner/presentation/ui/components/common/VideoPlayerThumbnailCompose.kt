package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import timber.log.Timber

@Composable
fun VideoPlayerThumbnailCompose(videoUrl: String) {
    var player by remember {
        mutableStateOf<ExoPlayer?>(null)
    }

    AndroidView(factory = { context ->
        StyledPlayerView(context).apply {
            // Create a new SimpleExoPlayer instance
            player = ExoPlayer.Builder(context).build()
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

            // Set the player to the PlayerView
            this.player = player

            // Create a MediaItem for the video
            val mediaItem = MediaItem.fromUri(videoUrl.toUri())

            // Set the MediaItem to the player and prepare it
            player?.setMediaItem(mediaItem)
            player?.prepare()
            hideController()
            player?.volume = 0f
            player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            player?.repeatMode = Player.REPEAT_MODE_ALL
            player?.playWhenReady = true
        }
    }, modifier = Modifier.fillMaxSize())

    DisposableEffect(Unit) {
        onDispose {
            // Release the player when the composable is disposed
            Timber.d("delete ")
            player?.release()
        }
    }
}
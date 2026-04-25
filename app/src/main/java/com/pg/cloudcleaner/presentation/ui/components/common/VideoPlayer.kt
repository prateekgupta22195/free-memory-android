package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current

    val player = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            prepare()
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.stop()
            player.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                this.player = player
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

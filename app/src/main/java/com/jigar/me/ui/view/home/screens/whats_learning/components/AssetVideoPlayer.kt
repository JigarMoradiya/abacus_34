package com.jigar.me.ui.view.home.screens.whats_learning.components

import com.jigar.me.ui.view.home.theme.AppDimens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.AssetDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.jigar.me.R

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun AssetVideoPlayer(
    assetFileName: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(assetFileName) {

        val dataSourceFactory = DataSource.Factory {
            AssetDataSource(context)
        }

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(
                MediaItem.Builder()
                    .setUri("asset:///$assetFileName".toUri())
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build()
            )

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaSource(mediaSource)
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false

            addListener(object : Player.Listener {
                override fun onIsLoadingChanged(loading: Boolean) {
                    isLoading = loading
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        isLoading = false
                    }
                }
            })

            prepare()
        }

        onDispose {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    LaunchedEffect(isPlaying) {
        exoPlayer?.playWhenReady = isPlaying
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.Dimens12),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens4)
    ) {
        Box {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1920f / 960f),
                factory = {
                    PlayerView(it).apply {
                        useController = false
                        setKeepContentOnPlayerReset(true)
                        defaultArtwork = ContextCompat.getDrawable(it, R.drawable.placeholder)
                        player = exoPlayer
                    }
                },
                update = {
                    it.player = exoPlayer
                }
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

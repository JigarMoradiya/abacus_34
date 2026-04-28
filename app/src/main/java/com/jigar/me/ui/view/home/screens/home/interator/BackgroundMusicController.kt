package com.jigar.me.ui.view.home.screens.home.interator

import android.content.Context
import android.media.MediaPlayer
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound

class BackgroundMusicController(
    private val context: Context,
    private val prefs: AppPreferencesHelper
) {

    private var mediaPlayer: MediaPlayer? = null

    fun playIfNeeded() {
        val volume = prefs.getCustomParamInt(
            AppConstants.Settings.Setting_bg_music_volume,
            AppConstants.Settings.Setting_bg_music_volume_default
        )

        if (volume <= 0) return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                val afd = context.assets.openFd(PlaySound.background_music)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = true
                prepare()
            }
        }

        setVolumeInternal(volume)

        if (mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    fun updateVolume(volume: Int) {
        prefs.setCustomParamInt(
            AppConstants.Settings.Setting_bg_music_volume,
            volume
        )

        if (volume <= 0) {
            mediaPlayer?.pause()
            return
        }

        playIfNeeded()           // ✅ only entry point
        setVolumeInternal(volume)
    }

    private fun setVolumeInternal(volume: Int) {
        val vol = volume / 100f
        mediaPlayer?.setVolume(vol, vol)
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying != true) {
            mediaPlayer?.start()
        }
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}


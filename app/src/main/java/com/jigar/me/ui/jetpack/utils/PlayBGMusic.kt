package com.jigar.me.ui.jetpack.utils

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

object PlayBGMusic {
    const val background_music = "bg_music_sand_castle.mp3"
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, fileName: String) {
        stop() // stop & release previous if any
        try {
            val afd = context.assets.openFd(fileName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener {
                    stop() // auto release when done
                }
                setOnErrorListener { _, _, _ ->
                    stop()
                    true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            stop()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (_: Exception) {}
            it.release()
        }
        mediaPlayer = null
    }
}
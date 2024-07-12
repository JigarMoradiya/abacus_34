package com.jigar.me.utils

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

object PlaySound {
    const val abacus_click = "click.wav"
    const val clap_click = "clap.wav"
    const val number_puzzle_win = "number_puzzle_win.wav"
    const val swap_sound = "number_puzzle_click.wav"
//    const val background_music = "bg_music_piano.mp3"
    const val background_music = "bg_music_sand_castle.mp3"

    private val player = MediaPlayer()
    fun playClickSound(context: Context) {
        play(context, abacus_click)
    }

    fun playClapSound(context: Context) {
        play(context, clap_click)
    }

    fun play(context: Context, fileName: String) {
//        mediaPrefs = context.getSharedPreferences(Constants.MediaPrefs, Context.MODE_PRIVATE)
//        if (mediaPrefs?.getString(Constants.Sound, "Y")!!.equals("Y", ignoreCase = true)) {
            try {
                val afd = context.assets.openFd(fileName)

                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                player.prepare()
                player.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

//        }

    }
}
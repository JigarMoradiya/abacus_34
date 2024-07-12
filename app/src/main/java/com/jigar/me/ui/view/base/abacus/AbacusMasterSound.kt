package com.jigar.me.ui.view.base.abacus

import android.content.Context
import android.media.MediaPlayer
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import java.io.IOException

object AbacusMasterSound {
    const val abacus_click = "click.wav"
    const val clap_click = "clap.wav"
    const val number_puzzle_win = "number_puzzle_win.wav"
    const val swap_sound = "number_puzzle_click.wav"
    const val tap_sound = "button_tap.wav"
    const val reset_sound = "reset.wav"

    private val player = MediaPlayer()
    fun playClickSound(context: Context?) {
        play(context!!, abacus_click)
    }

    fun playResetSound(context: Context?) {
        play(context!!, reset_sound)
    }

    fun playClapSound(context: Context) {
        play(context, clap_click)
    }
    fun playTap(context: Context) {
        playNoCondition(context, tap_sound)
    }

    private fun play(context: Context, fileName: String) {
        if (AppPreferencesHelper(context, AppConstants.PREF_NAME)
                .getCustomParamBoolean(AppConstants.Settings.Setting_sound, true)
        ) {
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
        }
    }

    private fun playNoCondition(context: Context, fileName: String) {
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

    }
}
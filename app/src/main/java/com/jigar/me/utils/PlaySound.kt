package com.jigar.me.utils

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

object PlaySound {
    const val abacus_bead_click = "click.wav"
    const val abacus_bead_reset = "reset.wav"
    const val play_win = "play_win.wav"
    const val play_swap = "play_swip.wav"
    const val background_music = "bg_music_sand_castle.mp3"
    const val btn_clear = "btn_clear.mp3"
    const val btn_click = "btn_click.mp3"
    const val btn_tap = "btn_tap.wav"
    const val clap = "clap.wav"
    const val btn_hint = "btn_hint.mp3"
    const val btn_wrong = "btn_wrong.mp3"

    fun playBeadClick(context: Context) {
        play(context, abacus_bead_click)
    }
    fun playBeadReset(context: Context) {
        play(context, abacus_bead_reset)
    }
    fun playClear(context: Context) {
        play(context, btn_clear)
    }
    fun playTap(context: Context) {
        play(context, btn_tap)
    }
    fun playClick(context: Context) {
        play(context, btn_click)
    }
    fun playHint(context: Context) {
        play(context, btn_hint)
    }
    fun playWrong(context: Context) {
        play(context, btn_wrong)
    }
    fun playSwip(context: Context) {
        play(context, play_swap)
    }
    fun playWin(context: Context) {
        play(context, play_win)
    }
    fun playClap(context: Context) {
        play(context, clap)
    }
    fun play(context: Context, fileName: String) {
        try {
            val player = MediaPlayer()
            val afd = context.assets.openFd(fileName)
            player.reset()
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            player.prepare()
            player.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
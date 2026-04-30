package com.jigar.me.ui.jetpack.utils

import android.content.Context
import android.media.SoundPool

object AudioPlayerManager {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()

    fun init(context: Context) {
        if (soundPool != null) return

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .build()

        soundMap["abacus_move"] = load(context, "abacus_move.mp3")
        soundMap["abacus_reset"] = load(context, "abacus_reset.mp3")
        soundMap["ans_correct"] = load(context, "ans_correct.mp3")
        soundMap["ans_wrong"] = load(context, "ans_wrong.mp3")
        soundMap["back_click"] = load(context, "back_click.mp3")
        soundMap["btn_click"] = load(context, "btn_click.mp3")
        soundMap["btn_hint"] = load(context, "btn_hint.mp3")
        soundMap["clap"] = load(context, "clap.mp3")
        soundMap["play_swip"] = load(context, "play_swip.mp3")
        soundMap["play_win"] = load(context, "play_win.mp3")
    }

    private fun load(context: Context, fileName: String): Int {
        val afd = context.assets.openFd(fileName)
        return soundPool!!.load(afd, 1)
    }


    fun playAbacusMove() {
        play("abacus_move")
    }
    fun playAbacusReset() {
        play("abacus_reset")
    }
    fun playSoundAnsCorrect() {
        play("ans_correct")
    }
    fun playSoundAnsWrong() {
        play("ans_wrong")
    }
    fun playSoundBtnBack() {
        play("back_click")
    }
    fun playSoundBtnClick() {
        play("btn_click")
    }
    fun playSoundHintClick() {
        play("btn_hint")
    }
    fun playSoundClap() {
        play("clap")
    }
    fun playSoundSwip() {
        play("play_swip")
    }
    fun playSoundWin() {
        play("play_win")
    }

    private fun play(key: String) {
        val soundId = soundMap[key] ?: return
        soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun stop() {
        soundPool?.autoPause()
    }
}
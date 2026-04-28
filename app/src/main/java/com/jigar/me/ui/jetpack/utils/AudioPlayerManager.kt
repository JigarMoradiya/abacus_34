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

        soundMap["wrong_ans"] = load(context, "btn_wrong.mp3")
        soundMap["menu_click"] = load(context, "click.mp3")
        soundMap["back_click"] = load(context, "back_click.mp3")
        soundMap["game_complete"] = load(context, "play_win.wav")
        soundMap["clap"] = load(context, "clap.wav")
    }

    private fun load(context: Context, fileName: String): Int {
        val afd = context.assets.openFd(fileName)
        return soundPool!!.load(afd, 1)
    }


    fun playSoundWrongAnswer() {
        play("wrong_ans")
    }

    fun playSoundMenuClick() {
        play("menu_click")
    }

    fun playSoundBtnBack() {
        play("back_click")
    }

    fun playSoundGameComplete() {
        play("game_complete")
    }

    fun playSoundClap() {
        play("clap")
    }

    private fun play(key: String) {
        val soundId = soundMap[key] ?: return
        soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun stop() {
        soundPool?.autoPause()
    }
}
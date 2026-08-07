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
        soundMap["ans_wrong"] = load(context, "ans_wrong.mp3")
        soundMap["back_click"] = load(context, "back_click.mp3")
        soundMap["btn_click"] = load(context, "btn_click.mp3")
        soundMap["btn_hint"] = load(context, "btn_hint.mp3")
        soundMap["clap"] = load(context, "clap.mp3")
        soundMap["play_swip"] = load(context, "play_swip.mp3")
        soundMap["play_win"] = load(context, "play_win.mp3")
        soundMap["option_correct_ans"] = load(context, "option_correct_ans.mp3")
        soundMap["option_wrong_ans"] = load(context, "option_wrong_ans.mp3")
        // Game SFX (synthesized pack — see math game zone)
        soundMap["sfx_balloon_pop"] = load(context, "sfx_balloon_pop.wav")
        soundMap["sfx_wrong_soft"] = load(context, "sfx_wrong_soft.wav")
        soundMap["sfx_correct_ding"] = load(context, "sfx_correct_ding.wav")
        soundMap["sfx_sparkle"] = load(context, "sfx_sparkle.wav")
        soundMap["sfx_card_flip"] = load(context, "sfx_card_flip.wav")
        soundMap["sfx_tile_place"] = load(context, "sfx_tile_place.wav")
        soundMap["sfx_merge_pop"] = load(context, "sfx_merge_pop.wav")
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
    fun playSoundCorrectAns() {
        play("option_correct_ans")
    }
    fun playSoundOptionWrong() {
        play("option_wrong_ans")
    }

    // Game SFX (synthesized pack — see math game zone)
    fun playSoundBalloonPop() {
        play("sfx_balloon_pop")
    }
    fun playSoundWrongSoft() {
        play("sfx_wrong_soft")
    }
    fun playSoundDing() {
        play("sfx_correct_ding")
    }
    fun playSoundSparkle() {
        play("sfx_sparkle")
    }
    fun playSoundCardFlip() {
        play("sfx_card_flip")
    }
    fun playSoundTilePlace() {
        play("sfx_tile_place")
    }
    fun playSoundMergePop() {
        play("sfx_merge_pop")
    }

    private fun play(key: String) {
        val soundId = soundMap[key] ?: return
        soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun stop() {
        soundPool?.autoPause()
    }
}
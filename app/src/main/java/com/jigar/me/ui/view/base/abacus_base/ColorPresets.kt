package com.jigar.me.ui.view.base.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object ColorPresets {

    fun getMixColorListOfPoligon(): List<Color> {
        fun hex(h: String) = Color("#$h".toColorInt())
        return listOf(
            hex("9C27B0"),
            hex("6334B4"),
            hex("2196F3"),
            hex("03AC13"),
            hex("FFC107"),
            hex("FF7F00"),
            hex("F44336"),
            hex("FF7F00"),
            hex("FFC107"),
            hex("03AC13"),
            hex("2196F3"),
            hex("6334B4"),
            hex("9C27B0")
        )
    }

    // if you need unique list:
    fun getMixColorListOfPoligonUnique(): List<Color> {
        return listOf(
            hex("9C27B0"),
            hex("501EA8"),
            hex("2196F3"),
            hex("03AC13"),
            hex("FFC107"),
            hex("FF7F00"),
            hex("F44336"),
        )
    }

    // "Duo-Tone" theme: whole rod alternates between these 2 colors (indexed by rod/column number).
    fun getDuoToneColorList(): List<Color> = listOf(hex("2E86AB"), hex("F26D6D"))

    // "Candy" theme: 5 distinct colors, one per bead position, same sequence repeated on every rod.
    fun getCandyColorList(): List<Color> = listOf(
        hex("E91E8C"), hex("8E24AA"), hex("00ACC1"), hex("66BB6A"), hex("FF7043")
    )

    // "Heaven & Earth" theme: the heaven bead (upper) vs earth beads (lower) get different colors,
    // same on every rod -- mirrors a traditional two-tone soroban.
    val heavenBeadColor: Color get() = hex("F5B400")
    val earthBeadColor: Color get() = hex("0B4F6C")



    // helper: convert hex string to Color
    fun hex(hex: String): Color {
        return Color("#$hex".toColorInt())
    }
}
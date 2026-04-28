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



    // helper: convert hex string to Color
    fun hex(hex: String): Color {
        return Color("#$hex".toColorInt())
    }
}
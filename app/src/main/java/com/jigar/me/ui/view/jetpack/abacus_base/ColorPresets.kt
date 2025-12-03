package com.jigar.me.ui.view.jetpack.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object ColorPresets {

    fun getMixColorListOfPoligon(): List<Color> {
        fun hex(h: String) = Color("#$h".toColorInt())
        return listOf(
            hex("9C27B0"),
            hex("5F00A2"),
            hex("2196F3"),
            hex("03AC13"),
            hex("FFC107"),
            hex("FF7F00"),
            hex("F44336"),
            hex("FF7F00"),
            hex("FFC107"),
            hex("03AC13"),
            hex("2196F3"),
            hex("5F00A2"),
            hex("9C27B0")
        )
    }

    // if you need unique list:
    fun getMixColorListOfPoligonUnique(): List<Color> {
        return listOf(
            hex("9C27B0"),
            hex("4B0082").copy(alpha = 0.4f),
            hex("2196F3"),
            hex("03AC13"),
            hex("FFC107"),
            hex("FF7F00"),
            hex("F44336"),
        )
    }

    // helper: convert hex string to Color
    private fun hex(hex: String): Color {
        return Color("#$hex".toColorInt())
    }
}
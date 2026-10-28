package com.jigar.me.ui.view.base.abacus_base

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object ColorPresets {

    // Every solid-colour bead theme has a second, Dark-mode palette: the same colour family, but brighter and more
    // luminous so it glows on the navy panel instead of looking like the light one on a dark background.
    val darkBeadHex: Map<String, String> = mapOf(
        "poligon_purple" to "B66DFF",
        "poligon_blue" to "7B8CFF",
        "poligon_skyblue" to "4DC3FF",
        "poligon_red" to "FF5D6C",
        "poligon_green" to "3DDC84",
        "poligon_orange" to "FFA23A",
        "poligon_cyan" to "2DE2E6",
        "poligon_pink" to "FF58A8",
        "poligon_yellow" to "FFE45C",
        "poligon_silver" to "C3CFDB",
        "poligon_brown" to "D9A066",
        "poligon_black" to "B4B4D4",
    )

    /** Every bead theme id the picker offers. A stored id outside this set is replaced by the default. */
    val knownThemeIds: Set<String> = setOf(
        "poligon_rainbow", "poligon_black", "poligon_blue", "poligon_purple", "poligon_skyblue", "poligon_red", "poligon_green",
        "poligon_orange", "poligon_cyan", "poligon_pink", "poligon_yellow", "poligon_silver", "poligon_brown", "poligon_duotone",
        "poligon_candy", "poligon_heaven_earth", "poligon_hex_horizontal", "poligon_rectangle", "poligon_real_bead",
        "poligon_circle",
    )

    fun isKnownTheme(id: String?): Boolean = id != null && id in knownThemeIds

    /** What a theme is called in the picker while Dark mode is on (null = the usual name). */
    fun darkDisplayName(id: String): String? = if (id == "poligon_black") "Pearl" else null

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
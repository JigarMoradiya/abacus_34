package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components

import androidx.compose.ui.graphics.Color
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

data class Merge2048Tile(
    val id: Long,
    val value: Int,
    val row: Int,
    val col: Int,
    val isNew: Boolean = false,
    val justMerged: Boolean = false
)

enum class Merge2048Direction { UP, DOWN, LEFT, RIGHT }

data class Merge2048Config(val size: Int, val target: Int) {
    companion object {
        fun forDifficulty(d: CommonDifficulty4): Merge2048Config = when (d) {
            CommonDifficulty4.easy -> Merge2048Config(4, 64)
            CommonDifficulty4.medium -> Merge2048Config(5, 128)
            CommonDifficulty4.hard -> Merge2048Config(6, 256)
            CommonDifficulty4.veryHard -> Merge2048Config(7, 512)
        }
    }
}

data class Merge2048UiState(
    val size: Int = 4,
    val tiles: List<Merge2048Tile> = emptyList(),
    val score: Int = 0,
    val target: Int = 0,               // current goal; doubles each time it's reached
    val milestoneValue: Int? = null,   // brief banner when a target is reached
    val isGameOver: Boolean = false,
    val didWin: Boolean = false
)

object Merge2048Palette {
    fun bg(value: Int): Color = when (value) {
        2 -> Color(0xFFFFE0B2)
        4 -> Color(0xFFFFCC80)
        8 -> Color(0xFFFFB74D)
        16 -> Color(0xFFFF8A65)
        32 -> Color(0xFFFF7043)
        64 -> Color(0xFFEF5350)
        128 -> Color(0xFFAB47BC)
        256 -> Color(0xFF7E57C2)
        512 -> Color(0xFF5C6BC0)
        1024 -> Color(0xFF42A5F5)
        // Every higher tier gets its own distinct, kid-friendly color.
        2048 -> Color(0xFF0097A7)      // cyan
        4096 -> Color(0xFF00796B)      // teal
        8192 -> Color(0xFF2E7D32)      // green
        16384 -> Color(0xFF558B2F)     // light green
        32768 -> Color(0xFF827717)     // olive
        65536 -> Color(0xFFEF6C00)     // orange
        131072 -> Color(0xFFD84315)    // deep orange
        262144 -> Color(0xFFC2185B)    // pink
        524288 -> Color(0xFF6A1B9A)    // purple
        1048576 -> Color(0xFF4527A0)   // deep purple
        2097152 -> Color(0xFF283593)   // indigo
        4194304 -> Color(0xFF1565C0)   // blue
        else -> Color(0xFF37474F)      // slate (8M+)
    }
    fun fg(value: Int): Color = if (value <= 4) Color(0xFF5D4037) else Color.White

    // Up to 4 digits show the full number; bigger values switch to K / M
    // (binary, ÷1024) so a tile never shows more than 4 characters. e.g. 65536 -> "64K".
    fun label(value: Int): String = when {
        value < 10000 -> "$value"
        value < 1_048_576 -> "${value / 1024}K"
        else -> "${value / 1_048_576}M"
    }
}

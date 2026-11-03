package com.jigar.me.ui.view.home.screens.levels.level3

import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import androidx.annotation.StringRes
import com.jigar.me.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource

enum class L3Mode(
    val emoji:      String,
    @StringRes val titleRes:    Int,
    @StringRes val subtitleRes: Int,
    val startColor: Color,
    val endColor:   Color,
) {
    GUIDED("🧮", R.string.l3_mode_guided_title, R.string.l3_mode_guided_sub, Color(0xFF6A1B9A), Color(0xFF9C27B0)),
    SEMI_ANZAN("👁", R.string.l3_mode_semi_title, R.string.l3_mode_semi_sub, Color(0xFF00695C), Color(0xFF00897B)),
    FULL_ANZAN("🧠", R.string.l3_mode_full_title, R.string.l3_mode_full_sub, Color(0xFFE65100), Color(0xFFFF6F00)),
    SPEED_DRILL("⚡", R.string.l3_mode_speed_title, R.string.l3_mode_speed_sub, Color(0xFFB71C1C), Color(0xFFE53935)),
    FLASH("🔥", R.string.l3_mode_flash_title, R.string.l3_mode_flash_sub, Color(0xFF1565C0), Color(0xFF1976D2)),
}

data class L3Config(
    val mode:          L3Mode,
    val terms:         Int     = 5,
    val digits:        Int     = 1,
    val flashMs:       Int     = 1200,
    val autoAbacus:    Boolean = false,
    val timeLimitSecs: Int     = 60,
)

data class L3FlashDifficulty(
    @StringRes val labelRes: Int,
    val emoji:      String,
    val terms:      Int,
    val digits:     Int,
    val flashMs:    Int,
    val startColor: Color,
    val endColor:   Color,
)

val l3FlashDifficulties = listOf(
    L3FlashDifficulty(R.string.l3_diff_starter, "🌱", 3, 1, 1600, Color(0xFF2E7D32), Color(0xFF43A047)),
    L3FlashDifficulty(R.string.l3_diff_explorer, "🚀", 5, 1, 1200, Color(0xFF1565C0), Color(0xFF1E88E5)),
    L3FlashDifficulty(R.string.l3_diff_expert, "🦅", 7, 2, 800, Color(0xFFE65100), Color(0xFFFF7043)),
    L3FlashDifficulty(R.string.l3_diff_master, "💎", 10, 2, 500, Color(0xFF4A148C), Color(0xFF7B1FA2)),
)

/** "Slow" / "Normal" / "Fast" / "Blazing": how long each flashed number stays, in the app language. */
@Composable
fun l3SpeedLabel(flashMs: Int): String = stringResource(when (flashMs) {
    1600 -> R.string.l3_speed_slow
    1200 -> R.string.l3_speed_normal
    800  -> R.string.l3_speed_fast
    else -> R.string.l3_speed_blazing
})

/** "5 numbers · 1-digit · Normal", built from the numbers of the difficulty, in the app language. */
@Composable
fun l3FlashDesc(d: L3FlashDifficulty): String = stringResource(
    R.string.l3_flash_desc,
    pluralStringResource(R.plurals.l3_n_numbers, d.terms, d.terms),
    pluralStringResource(R.plurals.l3_n_digit, d.digits, d.digits),
    l3SpeedLabel(d.flashMs),
)

data class L3Term(val sign: String, val value: Int)   // sign: "" first | "+" | "−"
data class L3Session(val terms: List<L3Term>, val answer: Int)

fun generateL3Session(termCount: Int, maxDigits: Int): L3Session {
    val maxV = if (maxDigits >= 2) 49 else 9
    val minV = if (maxDigits >= 2) 10 else 1

    val first = (minV..maxV).random()
    val terms = mutableListOf(L3Term("", first))
    var running = first

    repeat(termCount - 1) {
        val subMax  = minOf(maxV, running - 1)
        val addMax  = minOf(maxV, 99 - running)
        val canSub  = subMax >= minV
        val canAdd  = addMax >= minV

        val isAdd = when {
            canAdd && canSub -> Random.nextBoolean()
            canAdd           -> true
            else             -> false
        }

        if (isAdd) {
            val v = (minV..addMax.coerceAtLeast(minV)).random()
            terms.add(L3Term("+", v))
            running += v
        } else {
            val v = (minV..subMax).random()
            terms.add(L3Term("−", v))
            running -= v
        }
    }
    return L3Session(terms, running)
}

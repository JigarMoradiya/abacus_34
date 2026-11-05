package com.jigar.me.ui.view.home.screens.levels.level3

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

enum class L3Mode(
    val emoji:      String,
    val title:      String,
    val subtitle:   String,
    val startColor: Color,
    val endColor:   Color,
) {
    GUIDED(      "🧮", "Guided Series",   "Abacus step by step",    Color(0xFF6A1B9A), Color(0xFF9C27B0)),
    SEMI_ANZAN(  "👁", "Semi-Anzan",      "Watch the abacus",       Color(0xFF00695C), Color(0xFF00897B)),
    FULL_ANZAN(  "🧠", "Full Anzan",      "Pure mental math",       Color(0xFFE65100), Color(0xFFFF6F00)),
    SPEED_DRILL( "⚡", "Speed Drill",     "Race the clock",         Color(0xFFB71C1C), Color(0xFFE53935)),
    FLASH(       "🔥", "Flash Challenge", "Rapid-fire numbers",     Color(0xFF1565C0), Color(0xFF1976D2)),
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
    val label:      String,
    val emoji:      String,
    val terms:      Int,
    val digits:     Int,
    val flashMs:    Int,
    val desc:       String,
    val startColor: Color,
    val endColor:   Color,
)

val l3FlashDifficulties = listOf(
    L3FlashDifficulty("Starter",  "🌱",  3, 1, 1600, "3 numbers · 1-digit · Slow",     Color(0xFF2E7D32), Color(0xFF43A047)),
    L3FlashDifficulty("Explorer", "🚀",  5, 1, 1200, "5 numbers · 1-digit · Normal",   Color(0xFF1565C0), Color(0xFF1E88E5)),
    L3FlashDifficulty("Expert",   "🦅",  7, 2,  800, "7 numbers · 2-digit · Fast",     Color(0xFFE65100), Color(0xFFFF7043)),
    L3FlashDifficulty("Master",   "💎", 10, 2,  500, "10 numbers · 2-digit · Blazing", Color(0xFF4A148C), Color(0xFF7B1FA2)),
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

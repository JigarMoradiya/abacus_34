package com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.components

import androidx.compose.ui.graphics.Color
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4

enum class EqMatchCardState { DOWN, UP, MATCHED }

data class EqMatchCard(
    val id: Int,
    val pairId: Int,     // both cards of a matching pair share this
    val text: String,    // "3×4", "12", "6+6" …
    val state: EqMatchCardState = EqMatchCardState.DOWN
)

enum class EqOp { ADD, SUB, MUL, DIV }

data class EquationMatchConfig(
    val pairs: Int,
    val cols: Int,
    val rows: Int,
    val ops: List<EqOp>,
    val maxOperand: Int,
    val eqEqChance: Double   // chance a pair is equation ↔ equation (else equation ↔ answer)
) {
    companion object {
        fun forDifficulty(d: CommonDifficulty4): EquationMatchConfig = when (d) {
            CommonDifficulty4.easy     -> EquationMatchConfig(6,  4, 3, listOf(EqOp.ADD, EqOp.SUB), 10, 0.0)
            CommonDifficulty4.medium   -> EquationMatchConfig(8,  4, 4, listOf(EqOp.ADD, EqOp.SUB, EqOp.MUL), 10, 0.25)
            CommonDifficulty4.hard     -> EquationMatchConfig(10, 5, 4, listOf(EqOp.ADD, EqOp.SUB, EqOp.MUL, EqOp.DIV), 12, 0.45)
            CommonDifficulty4.veryHard -> EquationMatchConfig(12, 6, 4, listOf(EqOp.ADD, EqOp.SUB, EqOp.MUL, EqOp.DIV), 12, 0.6)
        }
    }
}

data class EquationMatchUiState(
    val cards: List<EqMatchCard> = emptyList(),
    val score: Int = 0,
    val multiplier: Int = 1,
    val matchesFound: Int = 0,
    val mismatches: Int = 0,
    val isGameOver: Boolean = false,
    val pulse: Boolean = false      // brief mascot pop on a match
)

// A cheerful color per pair so a matched pair reads as "belonging together".
object EqMatchPalette {
    val backTop = Color(0xFF5C6BC0)
    val backBottom = Color(0xFF3949AB)
    val matched = Color(0xFF2E7D32)

    // One color per pair. No blues (the face-down back is blue), all dark enough
    // for white text, ordered so consecutive pairs are maximally distinct — the
    // colors used in Easy/Medium/Hard never look alike.
    private val faces = listOf(
        0xFFE53935, 0xFF00897B, 0xFFEF6C00, 0xFF6A1B9A, 0xFF2E7D32, 0xFFC2185B,
        0xFF827717, 0xFF8E24AA, 0xFF5D4037, 0xFFAD1457, 0xFF00695C, 0xFFF4511E
    )
    fun face(pairId: Int): Color = Color(faces[pairId % faces.size])
}

package com.jigar.me.utils

object FreemiumManager {

    enum class GateResult {
        ALLOW,
        REQUIRE_LOGIN,
        REQUIRE_LOGIN_THEN_PAYWALL,
        REQUIRE_PAYWALL
    }

    // Level 1: all free. Level 2: first 20 pages free. Level 3+: fully locked.
    // Abacus practice is local — no login required, purely subscription-based.
    fun levelPageGate(isSubscribed: Boolean, levelName: String, pageIndex: Int): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        return when (levelName) {
            "level1" -> GateResult.ALLOW
            "level2" -> if (pageIndex < 20) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
            else -> GateResult.REQUIRE_PAYWALL
        }
    }

    // Exam: addition-only + beginner = free (but saves to server, so needs login).
    fun examGate(
        isLoggedIn: Boolean,
        isSubscribed: Boolean,
        isAddition: Boolean,
        isSubtraction: Boolean,
        isMultiplication: Boolean,
        isDivision: Boolean,
        difficulty: String
    ): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        val onlyAddition = isAddition && !isSubtraction && !isMultiplication && !isDivision
        val isBeginner = difficulty == AppConstants.EXAM.examDifficultyBeginner
        val isFreeConfig = onlyAddition && isBeginner
        return when {
            isFreeConfig && isLoggedIn -> GateResult.ALLOW
            isFreeConfig && !isLoggedIn -> GateResult.REQUIRE_LOGIN
            else -> GateResult.REQUIRE_PAYWALL
        }
    }

    // Exercise: page 0, items 0 and 1 are free (accessible without subscription).
    // Login is required to save results; guest can proceed without saving.
    fun exerciseGate(
        isLoggedIn: Boolean,
        isSubscribed: Boolean,
        page: Int,
        itemIndex: Int
    ): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        val isFreeItem = page == 0 && itemIndex < 2
        return when {
            isFreeItem && isLoggedIn -> GateResult.ALLOW
            isFreeItem && !isLoggedIn -> GateResult.REQUIRE_LOGIN
            else -> GateResult.REQUIRE_PAYWALL
        }
    }

    // Math games: the easiest variant of EVERY game is free (same rule as the
    // newer zone games, where Easy is free and higher difficulties are premium).

    // Number Sequence: 3x3 free (local). 4x4 and 5x5 locked.
    fun numberSequenceGate(isSubscribed: Boolean, gridSize: Int): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        return if (gridSize == 3) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
    }

    // Sudoku: 4x4 + easiest difficulty free.
    fun sudokuGate(isSubscribed: Boolean, grid: Int, isEasiestDifficulty: Boolean): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        return if (grid == 4 && isEasiestDifficulty) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
    }

    // Math Pyramid: 2 levels + easy free.
    fun mathPyramidGate(isSubscribed: Boolean, levels: Int, isEasiestDifficulty: Boolean): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        return if (levels <= 2 && isEasiestDifficulty) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
    }

    // Target Number: level 1 + easy free.
    fun targetNumberGate(isSubscribed: Boolean, level: Int, isEasiestDifficulty: Boolean): GateResult {
        if (isSubscribed) return GateResult.ALLOW
        return if (level == 1 && isEasiestDifficulty) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
    }

    // CCM: fully locked.
    fun gameGate(isSubscribed: Boolean): GateResult {
        return if (isSubscribed) GateResult.ALLOW else GateResult.REQUIRE_PAYWALL
    }
}

package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.ui.graphics.Color

// ─── Abacus bead state ────────────────────────────────────────────────────

internal data class RodState(
    val heavenDown: Boolean,  // true = heaven bead touching beam = 5
    val earthUp: Int,          // 0..4 earth beads touching beam
) {
    val value: Int get() = (if (heavenDown) 5 else 0) + earthUp
}

internal data class AbacusDisplayState(
    val rods: List<RodState>,  // index 0 = leftmost (highest place value)
) {
    val value: Int get() {
        var result = 0
        var multiplier = 1
        for (i in rods.indices.reversed()) {
            result += rods[i].value * multiplier
            multiplier *= 10
        }
        return result
    }

    companion object {
        fun ones(heavenDown: Boolean, earthUp: Int) =
            AbacusDisplayState(listOf(RodState(heavenDown, earthUp)))

        fun tensOnes(
            tensHeavenDown: Boolean, tensEarthUp: Int,
            onesHeavenDown: Boolean, onesEarthUp: Int,
        ) = AbacusDisplayState(listOf(
            RodState(tensHeavenDown, tensEarthUp),
            RodState(onesHeavenDown, onesEarthUp),
        ))
    }
}

// ─── Content types ────────────────────────────────────────────────────────

internal data class LearnStep(
    val emoji: String,
    val title: String,
    val body: String,
    val abacusState: AbacusDisplayState? = null,
    val showFrameOnly: Boolean = false,
)

internal data class PracticeProblem(
    val targetNumber: Int,
    val abacusState: AbacusDisplayState,
    val instruction: String,
)

internal data class QuizQuestion(
    val abacusState: AbacusDisplayState,
    val choices: List<Int>,
    val correctAnswer: Int,
)

internal data class LessonFullContent(
    val learnSteps: List<LearnStep>,
)

// ─── Lesson 1: Meet the Abacus ────────────────────────────────────────────

private val lesson1Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("🔢", "What is an Abacus?",
            "An abacus is a counting frame with beads on rods. People used it long before calculators existed!"),
        LearnStep("🪵", "The Frame & Beam",
            "The outer frame holds everything together. The horizontal bar in the middle is called the BEAM — it divides top from bottom!",
            showFrameOnly = true),
        LearnStep("✨", "The Heaven Bead",
            "Above the beam sits 1 special bead. Push it DOWN to touch the beam and it counts as FIVE! ✋",
            AbacusDisplayState.ones(true, 0)),
        LearnStep("🌍", "The Earth Beads",
            "Below the beam are 4 beads. Each one you push UP to touch the beam counts as ONE! 1️⃣",
            AbacusDisplayState.ones(false, 4)),
        LearnStep("🎉", "Reading the Abacus!",
            "Heaven bead down = 5. Count earth beads touching the beam. Add them! Here: heaven(5) + 3 earth = 8! 🌟",
            AbacusDisplayState.ones(true, 3)),
    ),
)

// ─── Lesson 2: Numbers 1–4 ───────────────────────────────────────────────

private val lesson2Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("🈳", "Zero",
            "All beads away from the beam = ZERO. Nothing touching the beam = nothing to count!",
            AbacusDisplayState.ones(false, 0)),
        LearnStep("1️⃣", "One",
            "Push exactly 1 earth bead UP to touch the beam. The rod now shows the number 1!",
            AbacusDisplayState.ones(false, 1)),
        LearnStep("2️⃣", "Two",
            "Push 2 earth beads UP. Count them: one... two! The rod shows 2! 🎊",
            AbacusDisplayState.ones(false, 2)),
        LearnStep("3️⃣", "Three",
            "Three earth beads touching the beam. Count: 1, 2, 3! The rod shows 3! 🌈",
            AbacusDisplayState.ones(false, 3)),
        LearnStep("4️⃣", "Four",
            "Push ALL 4 earth beads up to the beam! Count: 1, 2, 3, 4! Maximum using only earth beads!",
            AbacusDisplayState.ones(false, 4)),
    ),
)

// ─── Lesson 3: Number 5 & Beyond ─────────────────────────────────────────

private val lesson3Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("✋", "Five!",
            "Push the HEAVEN bead DOWN to touch the beam. Just that ONE bead = FIVE! It's a super shortcut! 🌟",
            AbacusDisplayState.ones(true, 0)),
        LearnStep("6️⃣", "Six",
            "Heaven bead down (=5) PLUS 1 earth bead up (=1). 5 + 1 = SIX! Adding magic! ✨",
            AbacusDisplayState.ones(true, 1)),
        LearnStep("7️⃣", "Seven",
            "Heaven bead down (=5) + 2 earth beads up (=2). 5 + 2 = SEVEN! 🎯",
            AbacusDisplayState.ones(true, 2)),
        LearnStep("8️⃣", "Eight",
            "Heaven bead down (=5) + 3 earth beads up (=3). 5 + 3 = EIGHT! Almost there! 🎈",
            AbacusDisplayState.ones(true, 3)),
        LearnStep("9️⃣", "Nine — the Maximum!",
            "Heaven bead down (=5) + 4 earth beads up (=4). 5 + 4 = NINE! That's the most 1 rod can show! 🏆",
            AbacusDisplayState.ones(true, 4)),
    ),
)

// ─── Lesson 4: Place Value ────────────────────────────────────────────────

private val lesson4Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("🏛️", "Two Rods!",
            "An abacus has many rods! The RIGHT rod shows ONES (1–9). The LEFT rod shows TENS (10, 20, ...)! 🎯",
            AbacusDisplayState.tensOnes(false, 0, false, 0)),
        LearnStep("🔟", "The Number 10",
            "Push 1 earth bead up on the LEFT (tens) rod. The right rod stays at zero. One bead on the tens rod = TEN!",
            AbacusDisplayState.tensOnes(false, 1, false, 0)),
        LearnStep("✌️", "The Number 23",
            "LEFT: 2 earth beads up = 2 tens = 20. RIGHT: 3 earth beads up = 3 ones. 20 + 3 = 23! 🌟",
            AbacusDisplayState.tensOnes(false, 2, false, 3)),
        LearnStep("🖐️", "The Number 56",
            "LEFT: heaven bead down = 5 tens = 50. RIGHT: heaven + 1 earth = 5+1 = 6 ones. 50 + 6 = 56! 🎊",
            AbacusDisplayState.tensOnes(true, 0, true, 1)),
        LearnStep("📖", "Read Any 2-Digit Number!",
            "Read LEFT rod first (tens × 10), then add RIGHT rod (ones). Here: 4 tens + 7 ones = 47! 🏆",
            AbacusDisplayState.tensOnes(false, 4, true, 2)),
    ),
)

// ─── Lesson 5: Numbers to 99 ─────────────────────────────────────────────

private val lesson5Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("💯", "Any Number 0–99!",
            "With 2 rods you can show ANY number from 0 to 99! That's 100 different numbers you can read! 🎉",
            AbacusDisplayState.tensOnes(false, 0, false, 0)),
        LearnStep("🌟", "The Number 67",
            "TENS rod: heaven(5) + 1 earth(1) = 6 tens = 60. ONES rod: heaven(5) + 2 earth(2) = 7. 60 + 7 = 67! 🎯",
            AbacusDisplayState.tensOnes(true, 1, true, 2)),
        LearnStep("✋", "The Number 50",
            "TENS rod: only the heaven bead down = 5 tens = 50. ONES rod: nothing = 0. Just one heaven bead = 50! 🌟",
            AbacusDisplayState.tensOnes(true, 0, false, 0)),
        LearnStep("🏆", "The Number 99 — Maximum!",
            "TENS: heaven(5)+4 earth = 9 tens = 90. ONES: heaven(5)+4 earth = 9 ones. 90+9 = 99! The max! 🌈",
            AbacusDisplayState.tensOnes(true, 4, true, 4)),
        LearnStep("🎓", "You're a Master!",
            "Read the LEFT rod (×10) then add the RIGHT rod. What's below? Left=3, Right=8 → 38! You did it! 🌈",
            AbacusDisplayState.tensOnes(false, 3, true, 3)),
    ),
)

// ─── Dynamic quiz generator ───────────────────────────────────────────────

private fun abacusStateFor(value: Int): AbacusDisplayState =
    if (value <= 9) AbacusDisplayState.ones(value >= 5, value % 5)
    else { val t = value / 10; val o = value % 10; AbacusDisplayState.tensOnes(t >= 5, t % 5, o >= 5, o % 5) }

private fun buildChoices(correct: Int, min: Int, max: Int): List<Int> {
    val wrong = mutableListOf<Int>()
    for (d in listOf(1, -1, 2, -2, 3, -3, 10, -10, 11, -11, 9, -9)) {
        val c = correct + d
        if (c in min..max && c != correct && c !in wrong) wrong.add(c)
        if (wrong.size >= 3) break
    }
    if (wrong.size < 3) {
        (min..max).filter { it != correct && it !in wrong }.shuffled()
            .take(3 - wrong.size).forEach { wrong.add(it) }
    }
    return (wrong.take(3) + correct).sorted()
}

private fun rodDesc(digit: Int, label: String): String = when {
    digit == 0 -> "$label: all beads down"
    digit < 5  -> "$label: $digit earth bead${if (digit == 1) "" else "s"} up"
    digit == 5 -> "$label: heaven bead down only"
    else       -> "$label: heaven + ${digit - 5} earth bead${if (digit - 5 == 1) "" else "s"} up"
}

private fun instructionFor(value: Int): String = if (value <= 9) when {
    value == 0 -> "Keep ALL beads away from the beam"
    value < 5  -> "Push $value earth bead${if (value == 1) "" else "s"} UP to the beam"
    value == 5 -> "Push ONLY the heaven bead DOWN to the beam"
    else       -> "Heaven bead down + push ${value - 5} earth bead${if (value - 5 == 1) "" else "s"} up"
} else "${rodDesc(value / 10, "Tens")} · ${rodDesc(value % 10, "Ones")}"

internal fun generatePracticeProblems(lessonId: Int, count: Int = 5): List<PracticeProblem> {
    val (min, max) = when (lessonId) {
        1, 2 -> 0 to 4
        3    -> 5 to 9
        else -> 0 to 99
    }
    val pool = (min..max).toList().shuffled()
    return List(count) { i ->
        val value = pool[i % pool.size]
        PracticeProblem(value, abacusStateFor(value), instructionFor(value))
    }
}

internal fun generateQuizQuestions(lessonId: Int, count: Int = 10): List<QuizQuestion> {
    val (min, max) = when (lessonId) {
        1, 2 -> 0 to 4
        3    -> 5 to 9
        else -> 0 to 99
    }
    val pool = (min..max).toList().shuffled()
    return List(count) { i ->
        val correct = pool[i % pool.size]
        QuizQuestion(abacusStateFor(correct), buildChoices(correct, min, max), correct)
    }
}

// ─── Registry ─────────────────────────────────────────────────────────────

internal val allLessonContent: Map<Int, LessonFullContent> = mapOf(
    1 to lesson1Content,
    2 to lesson2Content,
    3 to lesson3Content,
    4 to lesson4Content,
    5 to lesson5Content,
)

package com.jigar.me.ui.view.home.screens.levels.level1

import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import com.jigar.me.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource

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
    @StringRes val title: Int,
    @StringRes val body: Int,
    val abacusState: AbacusDisplayState? = null,
    val showFrameOnly: Boolean = false,
    val fromValue: Int? = null,
)

internal data class PracticeProblem(
    val targetNumber: Int,
    val abacusState: AbacusDisplayState,
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
        LearnStep("🔢", R.string.l1_lesson1_step1_title,
            R.string.l1_lesson1_step1_body),
        LearnStep("🪵", R.string.l1_lesson1_step2_title,
            R.string.l1_lesson1_step2_body,
            showFrameOnly = true),
        LearnStep("✨", R.string.l1_lesson1_step3_title,
            R.string.l1_lesson1_step3_body,
            AbacusDisplayState.ones(true, 0)),
        LearnStep("🌍", R.string.l1_lesson1_step4_title,
            R.string.l1_lesson1_step4_body,
            AbacusDisplayState.ones(false, 4)),
        LearnStep("🎉", R.string.l1_lesson1_step5_title,
            R.string.l1_lesson1_step5_body,
            AbacusDisplayState.ones(true, 3)),
    ),
)

// ─── Lesson 2: Numbers 1–4 ───────────────────────────────────────────────

private val lesson2Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("🈳", R.string.l1_lesson2_step1_title,
            R.string.l1_lesson2_step1_body,
            AbacusDisplayState.ones(false, 0)),
        LearnStep("1️⃣", R.string.l1_lesson2_step2_title,
            R.string.l1_lesson2_step2_body,
            AbacusDisplayState.ones(false, 1)),
        LearnStep("2️⃣", R.string.l1_lesson2_step3_title,
            R.string.l1_lesson2_step3_body,
            AbacusDisplayState.ones(false, 2)),
        LearnStep("3️⃣", R.string.l1_lesson2_step4_title,
            R.string.l1_lesson2_step4_body,
            AbacusDisplayState.ones(false, 3)),
        LearnStep("4️⃣", R.string.l1_lesson2_step5_title,
            R.string.l1_lesson2_step5_body,
            AbacusDisplayState.ones(false, 4)),
    ),
)

// ─── Lesson 3: Number 5 & Beyond ─────────────────────────────────────────

private val lesson3Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("✋", R.string.l1_lesson3_step1_title,
            R.string.l1_lesson3_step1_body,
            AbacusDisplayState.ones(true, 0)),
        LearnStep("6️⃣", R.string.l1_lesson3_step2_title,
            R.string.l1_lesson3_step2_body,
            AbacusDisplayState.ones(true, 1)),
        LearnStep("7️⃣", R.string.l1_lesson3_step3_title,
            R.string.l1_lesson3_step3_body,
            AbacusDisplayState.ones(true, 2)),
        LearnStep("8️⃣", R.string.l1_lesson3_step4_title,
            R.string.l1_lesson3_step4_body,
            AbacusDisplayState.ones(true, 3)),
        LearnStep("9️⃣", R.string.l1_lesson3_step5_title,
            R.string.l1_lesson3_step5_body,
            AbacusDisplayState.ones(true, 4)),
    ),
)

// ─── Lesson 4: Place Value ────────────────────────────────────────────────

private val lesson4Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("🏛️", R.string.l1_lesson4_step1_title,
            R.string.l1_lesson4_step1_body,
            AbacusDisplayState.tensOnes(false, 0, false, 0)),
        LearnStep("🔟", R.string.l1_lesson4_step2_title,
            R.string.l1_lesson4_step2_body,
            AbacusDisplayState.tensOnes(false, 1, false, 0)),
        LearnStep("✌️", R.string.l1_lesson4_step3_title,
            R.string.l1_lesson4_step3_body,
            AbacusDisplayState.tensOnes(false, 2, false, 3)),
        LearnStep("🖐️", R.string.l1_lesson4_step4_title,
            R.string.l1_lesson4_step4_body,
            AbacusDisplayState.tensOnes(true, 0, true, 1)),
        LearnStep("📖", R.string.l1_lesson4_step5_title,
            R.string.l1_lesson4_step5_body,
            AbacusDisplayState.tensOnes(false, 4, true, 2)),
    ),
)

// ─── Lesson 5: Numbers to 99 ─────────────────────────────────────────────

private val lesson5Content = LessonFullContent(
    learnSteps = listOf(
        LearnStep("💯", R.string.l1_lesson5_step1_title,
            R.string.l1_lesson5_step1_body,
            AbacusDisplayState.tensOnes(false, 0, false, 0)),
        LearnStep("🌟", R.string.l1_lesson5_step2_title,
            R.string.l1_lesson5_step2_body,
            AbacusDisplayState.tensOnes(true, 1, true, 2)),
        LearnStep("✋", R.string.l1_lesson5_step3_title,
            R.string.l1_lesson5_step3_body,
            AbacusDisplayState.tensOnes(true, 0, false, 0)),
        LearnStep("🏆", R.string.l1_lesson5_step4_title,
            R.string.l1_lesson5_step4_body,
            AbacusDisplayState.tensOnes(true, 4, true, 4)),
        LearnStep("🎓", R.string.l1_lesson5_step5_title,
            R.string.l1_lesson5_step5_body,
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

/** One rod in words ("Tens: 2 earth beads up"), in the app language. */
@Composable
private fun rodDesc(digit: Int, label: String): String = when {
    digit == 0 -> stringResource(R.string.l1_rod_zero, label)
    digit < 5  -> pluralStringResource(R.plurals.l1_rod_earth, digit, label, digit)
    digit == 5 -> stringResource(R.string.l1_rod_heaven, label)
    else       -> pluralStringResource(R.plurals.l1_rod_heaven_earth, digit - 5, label, digit - 5)
}

/** What to do on the abacus to show [value], in the app language. */
@Composable
internal fun practiceInstruction(value: Int): String = if (value <= 9) when {
    value == 0 -> stringResource(R.string.l1_instr_zero)
    value < 5  -> pluralStringResource(R.plurals.l1_instr_earth, value, value)
    value == 5 -> stringResource(R.string.l1_instr_heaven)
    else       -> pluralStringResource(R.plurals.l1_instr_heaven_earth, value - 5, value - 5)
} else stringResource(R.string.l1_instr_two_digits, rodDesc(value / 10, stringResource(R.string.place_tens)), rodDesc(value % 10, stringResource(R.string.place_ones)))

internal fun generatePracticeProblems(lessonId: Int, count: Int = 5): List<PracticeProblem> {
    val (min, max) = when (lessonId) {
        1, 2 -> 0 to 4
        3    -> 5 to 9
        else -> 0 to 99
    }
    val pool = (min..max).toList().shuffled()
    return List(count) { i ->
        val value = pool[i % pool.size]
        PracticeProblem(value, abacusStateFor(value))
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

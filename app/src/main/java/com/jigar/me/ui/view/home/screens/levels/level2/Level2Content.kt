package com.jigar.me.ui.view.home.screens.levels.level2

import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.home.screens.levels.level1.AbacusDisplayState
import androidx.annotation.StringRes
import com.jigar.me.R

internal data class LearnStep(
    val emoji: String,
    @StringRes val title: Int,
    @StringRes val body: Int,
    val abacusState: AbacusDisplayState? = null,
    val showFrameOnly: Boolean = false,
    val fromValue: Int? = null,
)

internal data class LessonFullContent(
    val learnSteps: List<LearnStep>,
)

// ─── Shared animation helpers ─────────────────────────────────────────────────

internal fun l2ComputeMovement(from: Int, to: Int): Movement {
    val fH = from >= 5; val fE = from % 5
    val tH = to   >= 5; val tE = to   % 5
    return Movement(
        upperDown     = tH && !fH,
        upperUp       = !tH && fH,
        lowerUp       = if (tE > fE) tE - fE else 0,
        lowerDown     = if (fE > tE) fE - tE else 0,
        lowerOldValue = fE
    )
}

internal fun l2FormulaRodMovements(from: Int, to: Int, columns: Int): List<RodMovement> {
    if (columns == 1) return listOf(RodMovement(0, l2ComputeMovement(from, to)))
    val fromTens = from / 10; val fromOnes = from % 10
    val toTens   = to   / 10; val toOnes   = to   % 10
    return buildList {
        if (fromTens != toTens) add(RodMovement(0, l2ComputeMovement(fromTens, toTens)))
        if (fromOnes != toOnes) add(RodMovement(1, l2ComputeMovement(fromOnes, toOnes)))
    }
}

// ─── Level 2 data structures ─────────────────────────────────────────────────

internal data class Level2PracticeProblem(
    val a: Int,
    val op: String,    // "+" or "−"
    val b: Int,
    val result: Int,
    val abacusState: AbacusDisplayState,
)

internal data class Level2QuizQuestion(
    val a: Int,
    val op: String,
    val b: Int,
    val op2: String? = null,
    val c: Int? = null,
    val result: Int,
    val abacusState: AbacusDisplayState,
    val choices: List<Int>,
)

// ─── Helpers (local to Level2 — can't access private fns from Level1Content) ──

private fun l2AbacusStateFor(value: Int): AbacusDisplayState = when {
    value <= 9 -> AbacusDisplayState.ones(value >= 5, value % 5)
    else       -> { val t = value / 10; val o = value % 10
                    AbacusDisplayState.tensOnes(t >= 5, t % 5, o >= 5, o % 5) }
}

private fun buildL2Choices(result: Int): List<Int> {
    val maxVal = if (result > 9) 18 else 9
    val wrong = mutableListOf<Int>()
    for (d in listOf(1, -1, 2, -2, 3, -3, 4, -4)) {
        val c = result + d
        if (c in 0..maxVal && c != result && c !in wrong) wrong.add(c)
        if (wrong.size >= 3) break
    }
    if (wrong.size < 3) {
        (0..maxVal).filter { it != result && it !in wrong }.shuffled()
            .take(3 - wrong.size).forEach { wrong.add(it) }
    }
    return (wrong.take(3) + result).sorted()
}

private data class Op(val a: Int, val op: String, val b: Int, val result: Int)
private data class Op3(val a: Int, val op1: String, val b1: Int, val op2: String, val b2: Int, val result: Int)

private fun buildBeforePool(lessonId: Int): List<Op> {
    val basic = addPairs(1..9) + subPairs(1..9)
    return when (lessonId) {
        in 1..5 -> basic
        6       -> basic + smallFriendAddPairs()
        7       -> basic + smallFriendAddPairs() + smallFriendSubPairs()
        8       -> basic + smallFriendAddPairs() + smallFriendSubPairs() + bigFriendAddPairs()
        9       -> basic + smallFriendAddPairs() + smallFriendSubPairs() + bigFriendAddPairs() + bigFriendSubPairs()
        else    -> basic + smallFriendAddPairs() + smallFriendSubPairs() + bigFriendAddPairs() + bigFriendSubPairs() + familyAddPairs() + familySubPairs()
    }
}

private fun buildFormulaChains(lessonId: Int): List<Op3> {
    val primary = buildPool(lessonId).toSet()
    val allPool = buildBeforePool(lessonId)
    val byA     = allPool.groupBy { it.a }   // look up op2 by starting value
    val seen    = mutableSetOf<String>()
    return allPool.flatMap { op1 ->
        (byA[op1.result] ?: emptyList()).mapNotNull { op2 ->
            val key = "${op1.a}${op1.op}${op1.b}${op2.op}${op2.b}"
            val practicesFormula = op1 in primary || op2 in primary
            if (practicesFormula && op2.result != op1.a && seen.add(key))
                Op3(op1.a, op1.op, op1.b, op2.op, op2.b, op2.result)
            else null
        }
    }
}

private fun addPairs(resultRange: IntRange): List<Op> =
    (1..8).flatMap { a -> (1..8).mapNotNull { b -> val r = a + b; if (r in resultRange) Op(a, "+", b, r) else null } }

private fun subPairs(startRange: IntRange): List<Op> =
    startRange.flatMap { a -> (1..a).map { b -> Op(a, "−", b, a - b) } }

private fun smallFriendAddPairs(): List<Op> = listOf(
    Op(4, "+", 1, 5), Op(3, "+", 2, 5), Op(2, "+", 3, 5), Op(1, "+", 4, 5),
    Op(4, "+", 2, 6), Op(3, "+", 3, 6), Op(2, "+", 4, 6),
    Op(4, "+", 3, 7), Op(3, "+", 4, 7), Op(4, "+", 4, 8),
)

private fun smallFriendSubPairs(): List<Op> = listOf(
    Op(5, "−", 1, 4), Op(5, "−", 2, 3), Op(5, "−", 3, 2), Op(5, "−", 4, 1),
    Op(6, "−", 2, 4), Op(6, "−", 3, 3), Op(6, "−", 4, 2),
    Op(7, "−", 3, 4), Op(7, "−", 4, 3), Op(8, "−", 4, 4),
)

private fun bigFriendAddPairs(): List<Op> =
    (1..9).flatMap { b -> (1..9).mapNotNull { a ->
        val r = a + b
        val isFamily = a >= 5 && b >= 6 && (a % 5) < (10 - b)
        if (r in 10..18 && !isFamily) Op(a, "+", b, r) else null
    } }

private fun bigFriendSubPairs(): List<Op> =
    (10..18).flatMap { a -> (1..9).mapNotNull { b ->
        val r = a - b
        val isFamily = a < 15 && b >= 6 && r >= 5
        if (r in 0..9 && !isFamily) Op(a, "−", b, r) else null
    } }

private fun familyAddPairs(): List<Op> = listOf(
    Op(5, "+", 6, 11), Op(6, "+", 6, 12), Op(7, "+", 6, 13), Op(8, "+", 6, 14),
    Op(5, "+", 7, 12), Op(6, "+", 7, 13), Op(7, "+", 7, 14),
    Op(5, "+", 8, 13), Op(6, "+", 8, 14),
    Op(5, "+", 9, 14),
)

private fun familySubPairs(): List<Op> = listOf(
    Op(11, "−", 6, 5), Op(12, "−", 6, 6), Op(13, "−", 6, 7), Op(14, "−", 6, 8),
    Op(12, "−", 7, 5), Op(13, "−", 7, 6), Op(14, "−", 7, 7),
    Op(13, "−", 8, 5), Op(14, "−", 8, 6),
    Op(14, "−", 9, 5),
)

private fun buildPool(lessonId: Int): List<Op> = when (lessonId) {
    1    -> addPairs(1..4)
    2    -> addPairs(5..9).filter { it.a >= 5 || it.b >= 5 }
    3    -> subPairs(1..4) + subPairs(6..9).filter { it.result >= 5 }
    4    -> subPairs(5..9).filter { it.result < 5 && it.b >= 5 }
    6    -> smallFriendAddPairs()
    7    -> smallFriendSubPairs()
    8    -> bigFriendAddPairs()
    9    -> bigFriendSubPairs()
    10   -> familyAddPairs() + familySubPairs()
    else -> addPairs(2..9) + subPairs(1..9)
}

internal fun generateLevel2PracticeProblems(lessonId: Int, count: Int = 5): List<Level2PracticeProblem> {
    val pool = buildPool(lessonId).shuffled()
    return List(count) { i ->
        val op = pool[i % pool.size]
        Level2PracticeProblem(op.a, op.op, op.b, op.result, l2AbacusStateFor(op.result))
    }
}

internal fun generateLevel2QuizQuestions(lessonId: Int, count: Int = 10): List<Level2QuizQuestion> {
    val pool   = buildPool(lessonId).shuffled()
    val chains = buildFormulaChains(lessonId).shuffled()
    val threeCount = if (chains.isNotEmpty()) count / 3 else 0
    val twoCount   = count - threeCount
    val twoStep = List(twoCount) { i ->
        val op = pool[i % pool.size]
        Level2QuizQuestion(op.a, op.op, op.b, null, null, op.result, l2AbacusStateFor(op.result), buildL2Choices(op.result))
    }
    val threeStep = List(threeCount) { i ->
        val ch = chains[i % chains.size]
        Level2QuizQuestion(ch.a, ch.op1, ch.b1, ch.op2, ch.b2, ch.result, l2AbacusStateFor(ch.result), buildL2Choices(ch.result))
    }
    return (twoStep + threeStep).shuffled()
}

// ─── Learn steps ─────────────────────────────────────────────────────────────

private val lesson1LearnSteps = listOf(
    LearnStep("➕", R.string.l2_lesson1_step1_title,
        R.string.l2_lesson1_step1_body),
    LearnStep("2️⃣", R.string.l2_lesson1_step2_title,
        R.string.l2_lesson1_step2_body,
        AbacusDisplayState.ones(false, 2)),
    LearnStep("➕", R.string.l2_lesson1_step3_title,
        R.string.l2_lesson1_step3_body,
        AbacusDisplayState.ones(false, 3), fromValue = 2),
    LearnStep("🌟", R.string.l2_lesson1_step4_title,
        R.string.l2_lesson1_step4_body,
        AbacusDisplayState.ones(true, 0), fromValue = 2),
    LearnStep("⭐", R.string.l2_lesson1_step5_title,
        R.string.l2_lesson1_step5_body,
        AbacusDisplayState.ones(false, 4)),
)

private val lesson2LearnSteps = listOf(
    LearnStep("🌟", R.string.l2_lesson2_step1_title,
        R.string.l2_lesson2_step1_body),
    LearnStep("6️⃣", R.string.l2_lesson2_step2_title,
        R.string.l2_lesson2_step2_body,
        AbacusDisplayState.ones(true, 1), fromValue = 4),
    LearnStep("7️⃣", R.string.l2_lesson2_step3_title,
        R.string.l2_lesson2_step3_body,
        AbacusDisplayState.ones(true, 2), fromValue = 3),
    LearnStep("8️⃣", R.string.l2_lesson2_step4_title,
        R.string.l2_lesson2_step4_body,
        AbacusDisplayState.ones(true, 3), fromValue = 5),
    LearnStep("9️⃣", R.string.l2_lesson2_step5_title,
        R.string.l2_lesson2_step5_body,
        AbacusDisplayState.ones(true, 4), fromValue = 5),
)

private val lesson3LearnSteps = listOf(
    LearnStep("➖", R.string.l2_lesson3_step1_title,
        R.string.l2_lesson3_step1_body),
    LearnStep("4️⃣", R.string.l2_lesson3_step2_title,
        R.string.l2_lesson3_step2_body,
        AbacusDisplayState.ones(false, 4)),
    LearnStep("2️⃣", R.string.l2_lesson3_step3_title,
        R.string.l2_lesson3_step3_body,
        AbacusDisplayState.ones(false, 2), fromValue = 4),
    LearnStep("5️⃣", R.string.l2_lesson3_step4_title,
        R.string.l2_lesson3_step4_body,
        AbacusDisplayState.ones(true, 0), fromValue = 8),
    LearnStep("🎉", R.string.l2_lesson3_step5_title,
        R.string.l2_lesson3_step5_body,
        AbacusDisplayState.ones(false, 3)),
)

private val lesson4LearnSteps = listOf(
    LearnStep("🔽", R.string.l2_lesson4_step1_title,
        R.string.l2_lesson4_step1_body),
    LearnStep("0️⃣", R.string.l2_lesson4_step2_title,
        R.string.l2_lesson4_step2_body,
        AbacusDisplayState.ones(false, 0), fromValue = 5),
    LearnStep("2️⃣", R.string.l2_lesson4_step3_title,
        R.string.l2_lesson4_step3_body,
        AbacusDisplayState.ones(false, 2), fromValue = 7),
    LearnStep("1️⃣", R.string.l2_lesson4_step4_title,
        R.string.l2_lesson4_step4_body,
        AbacusDisplayState.ones(false, 1), fromValue = 9),
    LearnStep("🏆", R.string.l2_lesson4_step5_title,
        R.string.l2_lesson4_step5_body,
        AbacusDisplayState.ones(false, 1)),
)

private val lesson5LearnSteps = listOf(
    LearnStep("🔀", R.string.l2_lesson5_step1_title,
        R.string.l2_lesson5_step1_body),
    LearnStep("➕", R.string.l2_lesson5_step2_title,
        R.string.l2_lesson5_step2_body,
        AbacusDisplayState.ones(true, 3), fromValue = 3),
    LearnStep("➖", R.string.l2_lesson5_step3_title,
        R.string.l2_lesson5_step3_body,
        AbacusDisplayState.ones(true, 1), fromValue = 8),
    LearnStep("🔢", R.string.l2_lesson5_step4_title,
        R.string.l2_lesson5_step4_body,
        AbacusDisplayState.ones(false, 3), fromValue = 5),
    LearnStep("🌟", R.string.l2_lesson5_step5_title,
        R.string.l2_lesson5_step5_body),
)

private val lesson6LearnSteps = listOf(
    LearnStep("🤝", R.string.l2_lesson6_step1_title,
        R.string.l2_lesson6_step1_body),
    LearnStep("5️⃣", R.string.l2_lesson6_step2_title,
        R.string.l2_lesson6_step2_body,
        AbacusDisplayState.ones(true, 0), fromValue = 1),
    LearnStep("5️⃣", R.string.l2_lesson6_step3_title,
        R.string.l2_lesson6_step3_body,
        AbacusDisplayState.ones(true, 0), fromValue = 2),
    LearnStep("7️⃣", R.string.l2_lesson6_step4_title,
        R.string.l2_lesson6_step4_body,
        AbacusDisplayState.ones(true, 2), fromValue = 3),
    LearnStep("🌟", R.string.l2_lesson6_step5_title,
        R.string.l2_lesson6_step5_body,
        AbacusDisplayState.ones(true, 2)),
)

private val lesson7LearnSteps = listOf(
    LearnStep("🤝", R.string.l2_lesson7_step1_title,
        R.string.l2_lesson7_step1_body),
    LearnStep("4️⃣", R.string.l2_lesson7_step2_title,
        R.string.l2_lesson7_step2_body,
        AbacusDisplayState.ones(false, 4), fromValue = 5),
    LearnStep("3️⃣", R.string.l2_lesson7_step3_title,
        R.string.l2_lesson7_step3_body,
        AbacusDisplayState.ones(false, 3), fromValue = 5),
    LearnStep("3️⃣", R.string.l2_lesson7_step4_title,
        R.string.l2_lesson7_step4_body,
        AbacusDisplayState.ones(false, 3), fromValue = 7),
    LearnStep("🌟", R.string.l2_lesson7_step5_title,
        R.string.l2_lesson7_step5_body,
        AbacusDisplayState.ones(false, 3)),
)

private val lesson8LearnSteps = listOf(
    LearnStep("🔢", R.string.l2_lesson8_step1_title,
        R.string.l2_lesson8_step1_body),
    LearnStep("🔟", R.string.l2_lesson8_step2_title,
        R.string.l2_lesson8_step2_body,
        AbacusDisplayState.tensOnes(false, 1, false, 0), fromValue = 9),
    LearnStep("1️⃣1️⃣", R.string.l2_lesson8_step3_title,
        R.string.l2_lesson8_step3_body,
        AbacusDisplayState.tensOnes(false, 1, false, 1), fromValue = 5),
    LearnStep("1️⃣4️⃣", R.string.l2_lesson8_step4_title,
        R.string.l2_lesson8_step4_body,
        AbacusDisplayState.tensOnes(false, 1, false, 4), fromValue = 5),
    LearnStep("🏆", R.string.l2_lesson8_step5_title,
        R.string.l2_lesson8_step5_body,
        AbacusDisplayState.tensOnes(false, 1, true, 4)),
)

private val lesson9LearnSteps = listOf(
    LearnStep("🔢", R.string.l2_lesson9_step1_title,
        R.string.l2_lesson9_step1_body),
    LearnStep("9️⃣", R.string.l2_lesson9_step2_title,
        R.string.l2_lesson9_step2_body,
        AbacusDisplayState.ones(true, 4), fromValue = 10),
    LearnStep("8️⃣", R.string.l2_lesson9_step3_title,
        R.string.l2_lesson9_step3_body,
        AbacusDisplayState.ones(true, 3), fromValue = 10),
    LearnStep("5️⃣", R.string.l2_lesson9_step4_title,
        R.string.l2_lesson9_step4_body,
        AbacusDisplayState.ones(true, 0), fromValue = 11),
    LearnStep("🏆", R.string.l2_lesson9_step5_title,
        R.string.l2_lesson9_step5_body,
        AbacusDisplayState.ones(true, 0)),
)

private val lesson10LearnSteps = listOf(
    LearnStep("👨‍👩‍👧‍👦", R.string.l2_lesson10_step1_title,
        R.string.l2_lesson10_step1_body),
    LearnStep("1️⃣3️⃣", R.string.l2_lesson10_step2_title,
        R.string.l2_lesson10_step2_body,
        AbacusDisplayState.tensOnes(false, 1, false, 3), fromValue = 7),
    LearnStep("1️⃣5️⃣", R.string.l2_lesson10_step3_title,
        R.string.l2_lesson10_step3_body,
        AbacusDisplayState.tensOnes(false, 1, true, 0), fromValue = 6),
    LearnStep("5️⃣", R.string.l2_lesson10_step4_title,
        R.string.l2_lesson10_step4_body,
        AbacusDisplayState.ones(true, 0), fromValue = 11),
    LearnStep("🏆", R.string.l2_lesson10_step5_title,
        R.string.l2_lesson10_step5_body,
        AbacusDisplayState.ones(true, 0)),
)

// ─── Registry ─────────────────────────────────────────────────────────────────

internal val allLevel2LearnContent: Map<Int, LessonFullContent> = mapOf(
    1 to LessonFullContent(lesson1LearnSteps),
    2 to LessonFullContent(lesson2LearnSteps),
    3 to LessonFullContent(lesson3LearnSteps),
    4 to LessonFullContent(lesson4LearnSteps),
    5 to LessonFullContent(lesson5LearnSteps),
    6 to LessonFullContent(lesson6LearnSteps),
    7 to LessonFullContent(lesson7LearnSteps),
    8 to LessonFullContent(lesson8LearnSteps),
    9 to LessonFullContent(lesson9LearnSteps),
    10 to LessonFullContent(lesson10LearnSteps),
)

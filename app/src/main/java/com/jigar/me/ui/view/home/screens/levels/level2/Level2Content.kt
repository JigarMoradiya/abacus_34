package com.jigar.me.ui.view.home.screens.levels.level2

import com.jigar.me.data.local.data.Movement
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.home.screens.levels.level1.LearnStep
import com.jigar.me.ui.view.home.screens.levels.level1.LessonFullContent
import com.jigar.me.ui.view.home.screens.levels.level1.AbacusDisplayState

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
    val instruction: String,
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

private fun l2InstructionFor(value: Int): String = when {
    value == 0 -> "Keep ALL beads away from the beam"
    value < 5  -> "Push $value earth bead${if (value == 1) "" else "s"} UP to the beam"
    value == 5 -> "Push ONLY the heaven bead DOWN to the beam"
    else       -> "Heaven bead down + push ${value - 5} earth bead${if (value - 5 == 1) "" else "s"} up"
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
    1    -> addPairs(2..5)
    2    -> addPairs(6..9)
    3    -> subPairs(1..5)
    4    -> subPairs(6..9)
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
        Level2PracticeProblem(op.a, op.op, op.b, op.result, l2AbacusStateFor(op.result), l2InstructionFor(op.result))
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
    LearnStep("➕", "Adding on the Abacus!",
        "When you ADD, push MORE beads UP toward the beam. Start at 2, add 1 → push 1 more bead up = 3! More beads = bigger number!"),
    LearnStep("2️⃣", "Start with 2",
        "Push 2 earth beads up to the beam. Your abacus shows 2. This is your starting number!",
        AbacusDisplayState.ones(false, 2)),
    LearnStep("➕", "Add 1 → Get 3",
        "Watch the bead move! Start at 2, push 1 more earth bead UP → 3! 🎊",
        AbacusDisplayState.ones(false, 3), fromValue = 2),
    LearnStep("🌟", "2 + 3 = 5",
        "Start at 2, add 3 more beads. At 5 you swap to the heaven bead! ✨",
        AbacusDisplayState.ones(true, 0), fromValue = 2),
    LearnStep("⭐", "Adding = Push UP!",
        "Remember: adding always means pushing MORE beads toward the beam. Your number goes UP! Try: 1 + 3 = ? (push 3 more beads!)",
        AbacusDisplayState.ones(false, 4)),
)

private val lesson2LearnSteps = listOf(
    LearnStep("🌟", "Adding Past 5!",
        "When your total reaches 5 or more, swap 5 earth beads for the HEAVEN bead! Push all earth DOWN + heaven DOWN = 5!"),
    LearnStep("6️⃣", "4 + 2 = 6",
        "Watch! Start at 4, add 2 → swap to heaven bead + 1 earth = 6! ✨",
        AbacusDisplayState.ones(true, 1), fromValue = 4),
    LearnStep("7️⃣", "3 + 4 = 7",
        "Watch! Start at 3, add 4 → swap to heaven + 2 earth = 7! 🎯",
        AbacusDisplayState.ones(true, 2), fromValue = 3),
    LearnStep("8️⃣", "5 + 3 = 8",
        "Watch! Start at 5 (heaven), add 3 earth beads up → heaven + 3 earth = 8! 🎈",
        AbacusDisplayState.ones(true, 3), fromValue = 5),
    LearnStep("9️⃣", "5 + 4 = 9",
        "Watch! Start at 5 (heaven), add 4 earth beads → heaven + 4 earth = 9! 🏆",
        AbacusDisplayState.ones(true, 4), fromValue = 5),
)

private val lesson3LearnSteps = listOf(
    LearnStep("➖", "Subtracting on the Abacus!",
        "When you SUBTRACT, push beads DOWN (away from the beam). Start at 4, subtract 2 → push 2 earth beads DOWN = 2!"),
    LearnStep("4️⃣", "Start at 4",
        "Push 4 earth beads up to the beam. Your abacus shows 4! This is your starting number.",
        AbacusDisplayState.ones(false, 4)),
    LearnStep("2️⃣", "4 − 2 = 2",
        "Watch! Start at 4, push 2 earth beads DOWN → 2! ✅",
        AbacusDisplayState.ones(false, 2), fromValue = 4),
    LearnStep("✋", "5 − 3 = 2",
        "Watch! Start at 5 (heaven), push heaven UP and 2 earth UP → 2! 🎯",
        AbacusDisplayState.ones(false, 2), fromValue = 5),
    LearnStep("🎉", "Subtracting = Push DOWN!",
        "Subtracting means pushing beads AWAY from the beam. Your number goes DOWN! Try: 3 − 1 = ? (push 1 bead down!)",
        AbacusDisplayState.ones(false, 3)),
)

private val lesson4LearnSteps = listOf(
    LearnStep("🔢", "Bigger Subtractions!",
        "Now let's subtract from bigger numbers like 7, 8, and 9! Same rule: push beads DOWN to subtract. Ready? 🚀"),
    LearnStep("8️⃣", "Show 8",
        "Heaven bead down (=5) + 3 earth beads up (=3). 5 + 3 = 8! This is your starting number.",
        AbacusDisplayState.ones(true, 3)),
    LearnStep("5️⃣", "8 − 3 = 5",
        "Watch! Start at 8, push 3 earth beads DOWN → just heaven = 5! ✨",
        AbacusDisplayState.ones(true, 0), fromValue = 8),
    LearnStep("9️⃣", "9 − 4 = 5",
        "Watch! Start at 9, push 4 earth beads DOWN → just heaven = 5! 🌟",
        AbacusDisplayState.ones(true, 0), fromValue = 9),
    LearnStep("🏆", "You're Amazing!",
        "7 = heaven + 2 earth. 7 − 2 = 5 (push 2 earth down). You can subtract any number from 1–9 now! 🎊",
        AbacusDisplayState.ones(true, 0)),
)

private val lesson5LearnSteps = listOf(
    LearnStep("🔀", "Mix It Up!",
        "Real math mixes adding AND subtracting! You already know both separately. Now let's use them together! 🚀"),
    LearnStep("➕", "Add First: 3 + 5 = 8",
        "Watch! Start at 3, add 5 → swap to heaven + 3 earth = 8! 🎯",
        AbacusDisplayState.ones(true, 3), fromValue = 3),
    LearnStep("➖", "Then Subtract: 8 − 2 = 6",
        "Watch! Start at 8, push 2 earth beads DOWN → heaven + 1 earth = 6! ✨",
        AbacusDisplayState.ones(true, 1), fromValue = 8),
    LearnStep("🔢", "Chain: 5 − 2 = 3",
        "Watch! Start at 5 (heaven), push heaven UP and 3 earth UP → 3! 🌟",
        AbacusDisplayState.ones(false, 3), fromValue = 5),
    LearnStep("🌟", "You're a Math Master!",
        "Adding pushes beads TOWARD the beam. Subtracting pushes beads AWAY from the beam. Mix them freely! 🎊 Level 2 Complete! 🏆"),
)

private val lesson6LearnSteps = listOf(
    LearnStep("🤝", "Small Friend +",
        "When adding would overflow the earth beads but result stays ≤ 9, use the Small Friend formula: remove earth beads + push heaven DOWN."),
    LearnStep("5️⃣", "+4 = −1+5",
        "Try 1+4. Can't just add 4 earth (only 3 free!). Formula: remove 1 earth DOWN, push heaven DOWN. 1−1+5 = 5! ✨",
        AbacusDisplayState.ones(true, 0), fromValue = 1),
    LearnStep("5️⃣", "+3 = −2+5",
        "Try 2+3. Formula: remove 2 earth DOWN, push heaven DOWN. 2−2+5 = 5! Same net result!",
        AbacusDisplayState.ones(true, 0), fromValue = 2),
    LearnStep("7️⃣", "+4 = −1+5 (mid-range)",
        "Try 3+4. Formula: remove 1 earth DOWN, push heaven DOWN. 3−1+5 = 7! 🎯",
        AbacusDisplayState.ones(true, 2), fromValue = 3),
    LearnStep("🌟", "Small Friend + Pattern",
        "+1=−4+5 | +2=−3+5 | +3=−2+5 | +4=−1+5. The two numbers always add to 5! 🤝 That's the 5-complement trick!",
        AbacusDisplayState.ones(true, 2)),
)

private val lesson7LearnSteps = listOf(
    LearnStep("🤝", "Small Friend −",
        "If your number is 5, 6, 7 or 8, the heaven bead is already touching the beam! When you need to subtract but there aren't enough earth beads — push heaven UP and move earth beads UP to the beam instead! That's the Small Friend swap! 🤝"),
    LearnStep("4️⃣", "−1 = −5+4",
        "Try 5−1. Heaven is DOWN. Can't remove 1 earth! Formula: push heaven UP (−5), add 4 earth UP (+4). 5−5+4 = 4! ✅",
        AbacusDisplayState.ones(false, 4), fromValue = 5),
    LearnStep("3️⃣", "−2 = −5+3",
        "Try 5−2. Push heaven UP (−5), add 3 earth UP (+3). 5−5+3 = 3! 🎯",
        AbacusDisplayState.ones(false, 3), fromValue = 5),
    LearnStep("3️⃣", "−4 = −5+1 (mid-range)",
        "Try 7−4. Heaven + 2 earth = 7. Push heaven UP (7→2), add 1 earth UP. 7−5+1 = 3! 🌟",
        AbacusDisplayState.ones(false, 3), fromValue = 7),
    LearnStep("🌟", "Small Friend − Pattern",
        "−1=−5+4 | −2=−5+3 | −3=−5+2 | −4=−5+1. Heaven bead swaps with earth beads! The two numbers always add to 5. 🤝",
        AbacusDisplayState.ones(false, 3)),
)

private val lesson8LearnSteps = listOf(
    LearnStep("🔢", "Big Friend +",
        "When ones column is full and result goes to 10+, carry a bead to the TENS column! Formula: +N = +10 − (10−N). Watch the tens rod light up!"),
    LearnStep("🔟", "+1 = +10−9",
        "Try 9+1. Ones is full (heaven+4earth=9). Carry: add 1 to TENS, remove 9 from ones. 9+1=10 → tens=1, ones=0! 🎉",
        AbacusDisplayState.tensOnes(false, 1, false, 0), fromValue = 9),
    LearnStep("1️⃣1️⃣", "+6 = +10−4",
        "Try 5+6. Ones shows 5 (heaven). Carry: add 1 to TENS, remove 4 from ones. 5+6=11 → tens=1, ones=1! ✨",
        AbacusDisplayState.tensOnes(false, 1, false, 1), fromValue = 5),
    LearnStep("1️⃣4️⃣", "+9 = +10−1",
        "Try 5+9. Heaven down (=5). Carry: add 1 to TENS, heaven stays + 4 earth. 5+9=14 → tens=1, ones=4! 🌟",
        AbacusDisplayState.tensOnes(false, 1, true, 4), fromValue = 5),
    LearnStep("🏆", "Big Friend + Pattern",
        "+1=+10−9, +2=+10−8 … +9=+10−1. Always: add 10 to tens, subtract the complement from ones. Pairs add to 10! 🔢",
        AbacusDisplayState.tensOnes(false, 1, true, 4)),
)

private val lesson9LearnSteps = listOf(
    LearnStep("🔢", "Big Friend −",
        "When ones column doesn't have enough beads to subtract, BORROW from the tens column! Formula: −N = −10 + (10−N)."),
    LearnStep("9️⃣", "−1 = −10+9",
        "Try 10−1. Ones is 0! Borrow: remove 1 from TENS, add 9 to ones. 10−1=9 → tens=0, ones=9! ✅",
        AbacusDisplayState.ones(true, 4), fromValue = 10),
    LearnStep("8️⃣", "−2 = −10+8",
        "Try 10−2. Borrow: remove 1 from TENS, add 8 to ones. 10−2=8! ✨",
        AbacusDisplayState.ones(true, 3), fromValue = 10),
    LearnStep("5️⃣", "−6 = −10+4",
        "Try 11−6. Ones has only 1 bead — can't remove 6! Borrow: −1 from TENS, +4 to ones. 11−6=5! 🎯",
        AbacusDisplayState.ones(true, 0), fromValue = 11),
    LearnStep("🏆", "Big Friend − Pattern",
        "−1=−10+9, −2=−10+8 … −9=−10+1. Always: borrow 10 from tens, add the complement to ones. Pairs add to 10! 🔢",
        AbacusDisplayState.ones(true, 0)),
)

private val lesson10LearnSteps = listOf(
    LearnStep("👨‍👩‍👧‍👦", "Family Formulas!",
        "Sometimes you need BOTH a heaven-bead swap AND a tens carry in one step. This 'Family' formula combines Small Friend + Big Friend together!"),
    LearnStep("1️⃣3️⃣", "+6 = −5+10+1",
        "Try 7+6. Heaven is down (7=heaven+2earth). Family: heaven UP (7→2), carry to tens, add 1 earth. 7+6=13! ✨",
        AbacusDisplayState.tensOnes(false, 1, false, 3), fromValue = 7),
    LearnStep("1️⃣5️⃣", "+9 = −5+10+4",
        "Try 6+9. Heaven UP (6→1), carry to tens, add 4 earth. 6+9=15! 🎯",
        AbacusDisplayState.tensOnes(false, 1, true, 0), fromValue = 6),
    LearnStep("5️⃣", "−6 = +5−10−1",
        "Try 11−6. Tens=1, ones=1. Borrow from tens (−10), push heaven DOWN (+5), remove 1 earth (−1). 11−6=5! 🌟",
        AbacusDisplayState.ones(true, 0), fromValue = 11),
    LearnStep("🏆", "Family Complete!",
        "+6=−5+10+1 … +9=−5+10+4 | −6=+5−10−1 … −9=+5−10−4. Level 2 done! 🎊",
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

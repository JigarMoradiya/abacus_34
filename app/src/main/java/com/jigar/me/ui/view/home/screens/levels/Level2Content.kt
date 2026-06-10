package com.jigar.me.ui.view.home.screens.levels

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
    val result: Int,
    val abacusState: AbacusDisplayState,
    val choices: List<Int>,
)

// ─── Helpers (local to Level2 — can't access private fns from Level1Content) ──

private fun l2AbacusStateFor(value: Int): AbacusDisplayState =
    AbacusDisplayState.ones(value >= 5, value % 5)

private fun l2InstructionFor(value: Int): String = when {
    value == 0 -> "Keep ALL beads away from the beam"
    value < 5  -> "Push $value earth bead${if (value == 1) "" else "s"} UP to the beam"
    value == 5 -> "Push ONLY the heaven bead DOWN to the beam"
    else       -> "Heaven bead down + push ${value - 5} earth bead${if (value - 5 == 1) "" else "s"} up"
}

private fun buildL2Choices(result: Int): List<Int> {
    val wrong = mutableListOf<Int>()
    for (d in listOf(1, -1, 2, -2, 3, -3)) {
        val c = result + d
        if (c in 0..9 && c != result && c !in wrong) wrong.add(c)
        if (wrong.size >= 3) break
    }
    if (wrong.size < 3) {
        (0..9).filter { it != result && it !in wrong }.shuffled()
            .take(3 - wrong.size).forEach { wrong.add(it) }
    }
    return (wrong.take(3) + result).sorted()
}

private data class Op(val a: Int, val op: String, val b: Int, val result: Int)

private fun addPairs(resultRange: IntRange): List<Op> =
    (1..8).flatMap { a -> (1..8).mapNotNull { b -> val r = a + b; if (r in resultRange) Op(a, "+", b, r) else null } }

private fun subPairs(startRange: IntRange): List<Op> =
    startRange.flatMap { a -> (1..a).map { b -> Op(a, "−", b, a - b) } }

private fun buildPool(lessonId: Int): List<Op> = when (lessonId) {
    1    -> addPairs(2..5)
    2    -> addPairs(6..9)
    3    -> subPairs(1..5)
    4    -> subPairs(6..9)
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
    val pool = buildPool(lessonId).shuffled()
    return List(count) { i ->
        val op = pool[i % pool.size]
        Level2QuizQuestion(op.a, op.op, op.b, op.result, l2AbacusStateFor(op.result), buildL2Choices(op.result))
    }
}

// ─── Learn steps ─────────────────────────────────────────────────────────────

private val lesson1LearnSteps = listOf(
    LearnStep("➕", "Adding on the Abacus!",
        "When you ADD, push MORE beads UP toward the beam. Start at 2, add 1 → push 1 more bead up = 3! More beads = bigger number!"),
    LearnStep("2️⃣", "Start with 2",
        "Push 2 earth beads up to the beam. Your abacus shows 2. This is your starting number!",
        AbacusDisplayState.ones(false, 2)),
    LearnStep("➕", "Add 1 → Get 3",
        "Push 1 more earth bead UP. Count all beads at the beam: 1, 2, 3! You added 1 to make 3! 🎊",
        AbacusDisplayState.ones(false, 3)),
    LearnStep("🌟", "2 + 3 = 5",
        "Start at 2 (2 earth beads). Add 3 more earth beads. Now you have 5! That's the heaven bead! ✨",
        AbacusDisplayState.ones(true, 0)),
    LearnStep("⭐", "Adding = Push UP!",
        "Remember: adding always means pushing MORE beads toward the beam. Your number goes UP! Try: 1 + 4 = ? (push 4 more beads!)",
        AbacusDisplayState.ones(false, 4)),
)

private val lesson2LearnSteps = listOf(
    LearnStep("🌟", "Adding Past 5!",
        "When your total reaches 5 or more, swap 5 earth beads for the HEAVEN bead! Push all earth DOWN + heaven DOWN = 5!"),
    LearnStep("6️⃣", "4 + 2 = 6",
        "Set 4 earth beads. Add 2: +1 makes 5 (swap: all earth down, heaven down!) then +1 earth up = 6! ✨",
        AbacusDisplayState.ones(true, 1)),
    LearnStep("7️⃣", "3 + 4 = 7",
        "Set 3 earth beads. Add 4: reach 5 (use heaven!), then 2 more earth = 7! Heaven + 2 earth = 7! 🎯",
        AbacusDisplayState.ones(true, 2)),
    LearnStep("8️⃣", "5 + 3 = 8",
        "Set heaven bead only (=5). Now add 3 earth beads up. Heaven + 3 earth = 8! Simple! 🎈",
        AbacusDisplayState.ones(true, 3)),
    LearnStep("9️⃣", "5 + 4 = 9",
        "Heaven bead + 4 earth beads = 9! That's the maximum for one rod! You can add any numbers now! 🏆",
        AbacusDisplayState.ones(true, 4)),
)

private val lesson3LearnSteps = listOf(
    LearnStep("➖", "Subtracting on the Abacus!",
        "When you SUBTRACT, push beads DOWN (away from the beam). Start at 4, subtract 2 → push 2 earth beads DOWN = 2!"),
    LearnStep("4️⃣", "Start at 4",
        "Push 4 earth beads up to the beam. Your abacus shows 4! This is your starting number.",
        AbacusDisplayState.ones(false, 4)),
    LearnStep("2️⃣", "4 − 2 = 2",
        "Take away 2! Push 2 earth beads DOWN. Count beads at beam: 1, 2. 4 − 2 = 2! ✅",
        AbacusDisplayState.ones(false, 2)),
    LearnStep("✋", "5 − 3 = 2",
        "Show 5 (heaven bead down). Subtract 3: push heaven UP (−5), then push 2 earth beads UP (add back 2). 5 − 3 = 2! 🎯",
        AbacusDisplayState.ones(false, 2)),
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
        "From 8, push 3 earth beads DOWN. Heaven bead stays! 8 − 3 = 5! Just the heaven bead left! ✨",
        AbacusDisplayState.ones(true, 0)),
    LearnStep("9️⃣", "9 − 4 = 5",
        "Show 9 (heaven + 4 earth). Take away 4 earth beads (push them DOWN). Heaven stays = 5! 🌟",
        AbacusDisplayState.ones(true, 0)),
    LearnStep("🏆", "You're Amazing!",
        "7 = heaven + 2 earth. 7 − 2 = 5 (push 2 earth down). You can subtract any number from 1–9 now! 🎊",
        AbacusDisplayState.ones(true, 0)),
)

private val lesson5LearnSteps = listOf(
    LearnStep("🔀", "Mix It Up!",
        "Real math mixes adding AND subtracting! You already know both separately. Now let's use them together! 🚀"),
    LearnStep("➕", "Add First: 3 + 5 = 8",
        "Set 3 earth beads. Add 5: when you reach 5 use the heaven bead! Then add 3 more earth = 8! 🎯",
        AbacusDisplayState.ones(true, 3)),
    LearnStep("➖", "Then Subtract: 8 − 2 = 6",
        "From 8 (heaven + 3 earth), push 2 earth beads DOWN. 8 − 2 = 6! Heaven + 1 earth = 6! ✨",
        AbacusDisplayState.ones(true, 1)),
    LearnStep("🔢", "Chain: 2 + 3 = 5, then 5 − 2 = 3",
        "First 2 + 3 = 5 (set heaven bead). Then 5 − 2 = 3: push heaven UP (−5), push 3 earth UP (+3) = 3! 🌟",
        AbacusDisplayState.ones(false, 3)),
    LearnStep("🌟", "You're a Math Master!",
        "Adding pushes beads TOWARD the beam. Subtracting pushes beads AWAY from the beam. Mix them freely! 🎊 Level 2 Complete! 🏆"),
)

// ─── Registry ─────────────────────────────────────────────────────────────────

internal val allLevel2LearnContent: Map<Int, LessonFullContent> = mapOf(
    1 to LessonFullContent(lesson1LearnSteps),
    2 to LessonFullContent(lesson2LearnSteps),
    3 to LessonFullContent(lesson3LearnSteps),
    4 to LessonFullContent(lesson4LearnSteps),
    5 to LessonFullContent(lesson5LearnSteps),
)

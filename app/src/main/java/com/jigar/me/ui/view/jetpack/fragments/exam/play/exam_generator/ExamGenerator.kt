package com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator

import com.jigar.me.utils.AppConstants

object ExamGenerator {

    fun generateExamPaperNew(examLevel : String,list : ArrayList<String>) : List<ExamMathQuestion>{
        val totalQuestion = 20
        val paperList : ArrayList<ExamMathQuestion> = arrayListOf()
        val generators: List<() -> ExamMathQuestion> =
            if (list.isNotEmpty()) {
                list.mapNotNull { type ->
                    when (type) {
                        AppConstants.EXAM.isAdditionSelected -> { { generateAddition(examLevel) } }
                        AppConstants.EXAM.isSubtractionSelected -> { { generateAddSubQuestion(examLevel) } }
                        AppConstants.EXAM.isMultiplicationSelected -> { { generateMultiplication(examLevel) } }
                        AppConstants.EXAM.isDivisionSelected -> { { generateDivision(examLevel) } }
                        else -> null
                    }
                }
            } else {
                listOf { generateNumberQuestion(examLevel) }
            }

        repeat(totalQuestion) {
            paperList.add(generators.random().invoke())
        }
        return paperList
    }

    // ---------------- Number ----------------

    private fun generateNumberQuestion(level: String): ExamMathQuestion {
        val number = when (level) {
            AppConstants.EXAM.examDifficultyBeginner -> (1..100).random()
            AppConstants.EXAM.examDifficultyIntermediate -> (11..999).random()
            AppConstants.EXAM.examDifficultyExpert -> (111..9999).random()
            else -> (1..20).random()
        }

        return createQuestion(ExamQuestionType.Number.name,number.toString(), number)
    }

    // ---------------- Addition ----------------

    private fun generateAddition(level: String): ExamMathQuestion {
        var numbers: List<Int>
        var answer: Int

        do {
            numbers = when (level) {
                AppConstants.EXAM.examDifficultyBeginner -> when ((0..3).random()) {
                    0 -> listOf((1..9).random(), (1..9).random())
                    1 -> listOf((2..9).random(), (11..19).random(), (3..14).random())
                    2 -> listOf((11..19).random(), (15..24).random(), (6..14).random())
                    else -> listOf((7..14).random(), (11..30).random(), (11..19).random())
                }

                AppConstants.EXAM.examDifficultyIntermediate -> when ((0..7).random()) {
                    0 -> listOf((6..19).random(), (11..29).random())
                    1 -> listOf((15..29).random(), (6..39).random(), (11..69).random())
                    2 -> listOf((15..49).random(), (25..59).random(), (6..69).random(), (20..99).random())
                    3 -> listOf((11..49).random(), (21..99).random(), (31..99).random(), (41..99).random())
                    4 -> listOf((39..399).random(), (49..299).random(), (19..399).random())
                    5 -> listOf((39..399).random(), (49..299).random(), (19..399).random(), (79..499).random())
                    6 -> listOf((201..399).random(), (79..299).random(), (500..999).random(), (301..799).random())
                    else -> listOf((111..999).random(), (222..555).random(), (555..999).random(), (59..399).random())
                }

                AppConstants.EXAM.examDifficultyExpert -> when ((0..6).random()) {
                    0 -> listOf((49..299).random(), (79..399).random(), (151..399).random())
                    1 -> listOf((199..499).random(), (99..799).random(), (500..999).random())
                    2 -> listOf((500..999).random(), (199..499).random(), (99..799).random(), (700..1999).random())
                    3 -> listOf((25..5999).random(), (1999..7999).random(), (2999..9999).random())
                    4 -> List(4) { (25..9999).random() }
                    5 -> List(4) { (25..99999).random() }
                    else -> List(2) { (1000..99999).random() }
                }

                else -> listOf((1..9).random(), (1..9).random())
            }.shuffled()

            answer = numbers.sum()
        } while (answer == 0)

        return createQuestion(ExamQuestionType.Addition.name, numbers.joinToString(" + "), answer)
    }

    // ---------------- Add / Sub ----------------

    private fun generateAddSubQuestion(level: String): ExamMathQuestion {
        var numbers: List<Int>
        var ops: MutableList<String>
        var answer: Int

        do {
            numbers = when (level) {
                AppConstants.EXAM.examDifficultyBeginner -> when ((0..3).random()) {
                    0 -> listOf((1..9).random(), (1..9).random())
                    1 -> listOf((11..20).random(), (1..9).random(), (5..15).random())
                    2 -> listOf((11..30).random(), (4..15).random(), (1..24).random())
                    else -> listOf((1..20).random(), (1..20).random(), (8..20).random(), (1..9).random())
                }

                AppConstants.EXAM.examDifficultyIntermediate -> when ((0..9).random()) {
                    0 -> listOf((2..19).random(), (1..15).random())
                    1 -> listOf((2..29).random(), (2..19).random(), (11..39).random())
                    2 -> listOf((11..29).random(), (4..9).random(), (6..39).random(), (11..49).random())
                    3 -> listOf((11..49).random(), (11..79).random())
                    4 -> listOf((11..99).random(), (25..99).random(), (51..99).random())
                    5 -> List(4) { (11..99).random() }
                    6 -> listOf((25..399).random(), (11..399).random(), (25..499).random())
                    else -> listOf((111..999).random(), (41..599).random(), (70..799).random(), (111..999).random())
                }

                AppConstants.EXAM.examDifficultyExpert -> when ((0..6).random()) {
                    0 -> listOf((49..599).random(), (201..399).random(), (49..599).random())
                    1 -> listOf((29..499).random(), (49..399).random(), (49..799).random())
                    2 -> listOf((111..999).random(), (49..999).random(), (111..999).random(), (30..999).random())
                    3 -> List(5) { (25..9999).random() }
                    4 -> List(5) { (25..99999).random() }
                    5 -> List(2) { (25..99999).random() }
                    else -> List(3) { (25..99999).random() }
                }

                else -> listOf((1..9).random(), (1..9).random())
            }.shuffled()

            ops = mutableListOf()
            var running = numbers[0]

            for (i in 1 until numbers.size) {
                val n = numbers[i]
                val op = if (n <= running) listOf("+", "-").random() else "+"
                ops.add(op)
                running = if (op == "+") running + n else running - n
            }

            answer = numbers[0]
            for (i in ops.indices) {
                answer = if (ops[i] == "+") answer + numbers[i + 1] else answer - numbers[i + 1]
            }

        } while (answer == 0)

        val questionText = buildString {
            append(numbers[0])
            for (i in ops.indices) {
                append(" ${ops[i]} ${numbers[i + 1]}")
            }
        }

        return createQuestion(ExamQuestionType.Subtraction.name, questionText, answer)
    }

    // ---------------- Multiplication ----------------

    private fun generateMultiplication(level: String): ExamMathQuestion {
        val (a, b) = when (level) {
            AppConstants.EXAM.examDifficultyBeginner -> (1..50).random() to (2..10).random()
            AppConstants.EXAM.examDifficultyIntermediate -> when ((0..2).random()) {
                0 -> (21..99).random() to (11..99).random()
                1 -> (101..399).random() to (21..49).random()
                else -> (1001..99999).random() to (2..10).random()
            }
            AppConstants.EXAM.examDifficultyExpert -> when ((0..2).random()) {
                0 -> (211..999).random() to (21..99).random()
                1 -> (111..999).random() to (101..999).random()
                else -> (1001..9999).random() to (12..999).random()
            }
            else -> (1..20).random() to (2..10).random()
        }

        return createQuestion(
            ExamQuestionType.Multiplication.name, "$a x $b", a * b)
    }

    // ---------------- Division ----------------

    private fun generateDivision(level: String): ExamMathQuestion {
        var a: Int
        var b: Int
        var ans: Int

        do {
            b = when (level) {
                AppConstants.EXAM.examDifficultyBeginner -> (2..20).random()
                AppConstants.EXAM.examDifficultyIntermediate -> (2..99).random()
                AppConstants.EXAM.examDifficultyExpert -> (2..800).random()
                else -> (2..20).random()
            }

            a = when (level) {
                AppConstants.EXAM.examDifficultyBeginner -> (2..400).random()
                AppConstants.EXAM.examDifficultyIntermediate -> (10..9999).random()
                else -> (100..999_999).random()
            }

            ans = a / b
        } while (a % b != 0 || ans == 1)

        return createQuestion(ExamQuestionType.Division.name, "$a ÷ $b", ans)
    }

    // ---------------- Options ----------------

    private fun createQuestion(
        questionType: String,
        questionText: String,
        answer: Int
    ): ExamMathQuestion {

        val options = mutableSetOf(answer)

        while (options.size < 4) {
            val wrong = when ((0..2).random()) {

                // smaller than answer
                0 -> {
                    val start = maxOf(1, answer / 2)
                    val end = maxOf(1, answer - 1)
                    (start..end).random()
                }

                // slightly smaller or bigger (±30%)
                1 -> {
                    val offsetRangeStart = (-answer * 0.3).toInt()
                    val offsetRangeEnd = (answer * 0.3).toInt()
                    val offset = (offsetRangeStart..offsetRangeEnd).random()
                    maxOf(1, answer + offset)
                }

                // bigger than answer
                else -> {
                    val start = answer + 1
                    val end = maxOf(answer * 2, answer + 10)
                    (start..end).random()
                }
            }

            options.add(wrong)
        }

        val shuffled = options.shuffled()

        return ExamMathQuestion(
            questionType,
            questionText,
            answer,
            shuffled[0],
            shuffled[1],
            shuffled[2],
            shuffled[3]
        )
    }

}
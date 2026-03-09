package com.jigar.me.data.local.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.jigar.me.R
import java.util.Random
import kotlin.random.Random.Default.nextInt

data class DivisionQuestion(
    val dividend: Int,
    val divisor: Int,
    val quotient: Int
)

object DataProvider {
    fun getVideoPreviewList(context: Context): List<VideoTutorial>{
        return with(context){
            listOf(
                VideoTutorial("video_free_mode.mp4","What Kid's Learn in Abacus Free Mode",
                    arrayListOf("• Understanding the <b>structure and part<b> of the abacus.",
                        "• Easy abacus learning for <b>beginners</b>.",
                        "• Recognize <b>bead types</b> and <b>values</b>.",
                        "• <b>Finger techniques</b> for moving beads correctly.",
                        "• Learning to <b>read, represent and set numbers</b> on the abacus.")),
                VideoTutorial("video_abacus_practice.mp4","What Kids Learn in Abacus Practice Module",
                    arrayListOf("• <b>Step-by-Step</b> problem solving with <b>correct bead directions</b>.",
                        "• <b>Visualize abacus formula</b> at every step (including multiplication & division).",
                        "• Learn to enter only <b>final answers</b> support.",
                        "• <b>Practice formal exam mode</b> with correct & incorrect answer tracking.",
                        "• Build speed & accuracy with <b>timed sets</b>.",
                        "• Understand <b>wrong bead movement</b> with direction hints.",
                        "• <b>Hide/Show</b> bead direction & formula display from <b>settings</b>.",
                        "• <b>Supports left & right hand use</b> - kids practice comfortably with their natural hand.",
                    )),
                VideoTutorial("video_exercise.mp4","What Kid's do in Exercise Module",
                    arrayListOf("• <b>Start</b> Addition, Subtraction, Multiplication, and Division challenges <b>based on their learning level</b>.",
                        "• <b>Practice</b> with different sets of exercises and <b>time limits to improve speed and focus</b>.",
                        "• <b>Build</b> exam-like stamina with structured question sets and <b>countdown timers</b>.",
                        "• <b>Improve</b> concentration, memory retention, and mental agility <b>through flow-based drills</b>.")),
                VideoTutorial("video_exam.mp4","What Kid's do in Exam Module",
                    arrayListOf(
                        "• <b>Simulated Test Environment –</b> Practice under real exam-like conditions to build accuracy and confidence.",
                        "• <b>Math Types Supported –</b> Addition, Subtraction, Multiplication, and Division.",
                        "• <b>Difficulty Levels –</b> Choose from Easy, Medium, or Hard for progressive learning.",
                        "• <b>Multiple-Choice Format –</b> Each question comes with 4 options to develop decision-making skills.",
                        "• <b>Timer Display –</b> Track how long you take without strict time pressure.",
                        "• <b>Competition-Ready –</b> Perfect for UCMAS exams, school tests, math competitions, and abacus contests.",
                        "• <b>Focused Training –</b> Builds exam temperament, speed, and logical thinking.")),
                VideoTutorial("video_ccm.mp4","What Kid's do in Custom Challenge Mode Module",
                    arrayListOf("• <b>Full Customization –</b> Control number of questions, question length (min/max digits), and time gap between questions.",
                        "• <b>Flexible Presentation –</b> Choose how questions appear: Numbers, Words, or Voice.",
                        "• <b>Timed Practice –</b> Add intervals between questions to simulate pressure and boost focus.",
                        "• <b>Oral & Visual Training –</b> Perfect for listening-based, visualization-only, or mixed learning styles.",
                        "• <b>Versatile Usage –</b> Suitable for home practice, classroom activities, and custom coaching sessions.",
                        "• <b>Skill Development –</b> Enhances listening, memory power, concentration, visualization, and calculation speed.",
                        "• <b>Benefit –</b> Builds adaptability, improves decision-making, and prepares students for diverse test formats.")),
            )
        }
    }
    fun getFaqsList(context: Context,emailId : String): List<FAQs>{
        return with(context){
            listOf(FAQs(getString(R.string.faq_que_1), getString(R.string.faq_ans_1)),
                FAQs(getString(R.string.faq_que_2), getString(R.string.faq_ans_2)),
                FAQs(getString(R.string.faq_que_3), getString(R.string.faq_ans_3)),
                FAQs(getString(R.string.faq_que_4), getString(R.string.faq_ans_4)),
                FAQs(getString(R.string.faq_que_41), getString(R.string.faq_ans_41)),
                FAQs(getString(R.string.faq_que_42), String.format(getString(R.string.faq_ans_42), emailId)),
                FAQs(getString(R.string.faq_que_5), getString(R.string.faq_ans_5)),
                FAQs(getString(R.string.faq_que_6), getString(R.string.faq_ans_6)),
                FAQs(getString(R.string.faq_que_7), getString(R.string.faq_ans_7)),
                FAQs(getString(R.string.faq_que_8), getString(R.string.faq_ans_8)),
                FAQs(getString(R.string.faq_que_9), getString(R.string.faq_ans_9)),
                FAQs(getString(R.string.faq_que_10), getString(R.string.faq_ans_10)),
                FAQs(String.format(getString(R.string.faq_que_support), emailId),""))
        }
    }

    private fun getMultipleDimensions(abacusBeadType: AbacusBeadType = AbacusBeadType.None) : Float{
        return when (abacusBeadType) {
            AbacusBeadType.ExamResult -> {
                0.35f
            }
            AbacusBeadType.Exam -> {
                0.5f
            }
            AbacusBeadType.SettingPreview -> {
                0.7f
            }
            AbacusBeadType.AbacusPreciseStepByStep -> {
                0.78f // still need to down
            }
            AbacusBeadType.AbacusPrecise, AbacusBeadType.CustomeChallenge, AbacusBeadType.Exercise -> {
                0.92f
            }
            AbacusBeadType.FullMode -> {
                1f
            }
            else -> { // AbacusBeadType.FreeMode
                1f
            }
        }
    }

    fun generateSingleDigit(min: Int, max: Int): Int {// min = to
        return  Random().nextInt(max - min + 1) + min
    }

    fun generateIndex(endNumber : Int = 2): Int {
        return nextInt(0, endNumber)
    }

    private fun generateTotalMinusSign(max : Int = 5): Int {
        return nextInt(1, max)
    }

    fun generateMixQuestions() {
        val listMain : ArrayList<String> = arrayListOf()
        (0 until 20).forEach { j ->
            val type : Int = generateSingleDigit(0,1)
            val list : List<String> = when (type) {
                0 -> {
                    generateDivisionQuestions(1)
                }
                1 -> {
                    generateMultiplication3_Temp(1)
                }
                else -> {
                    generateAdditionSubExerciseTemp(1)
                }
            }
            listMain.addAll(list)
        }
        listMain.shuffle()
        Log.e("jigarGenerateSetDiv","listQue = "+ Gson().toJson(listMain))
        Log.e("jigarGenerateSetDiv","joinToString = "+ listMain.joinToString(","))
    }
    fun generateDivisionQuestions(count : Int = 20): List<String> {
        val set = mutableSetOf<String>()
        val result = mutableListOf<DivisionQuestion>()
        val stringList = mutableListOf<String>()

        while (result.size < count) {
            val q = generateDivisionQuestion()
            val key = "${q.dividend}-${q.divisor}"

            if (set.add(key)) {
                result.add(q)
                stringList.add("${q.dividend}/${q.divisor}")
            }
        }
        Log.e("jigarGenerateSetDiv","listQue = "+ Gson().toJson(stringList))
        Log.e("jigarGenerateSetDiv","joinToString = "+ stringList.joinToString(","))
        return stringList
    }


    fun generateDivisionQuestion(): DivisionQuestion {
//        val divisor = (11..50).random()
        val divisor = (51..99) // odd / even numbers
//            .filter { it % 2 == 0 }
            .random()

//        val index = generateIndex()
//        val divisor = if (index == 0){
//            (21..30) // odd / even numbers
//                .filter { it % 2 == 0 }
//                .random()
//        }else{
//            (11..20).random()
//        }

        // Find valid quotient range so dividend stays between range
        val minQuotient = kotlin.math.ceil(9000.0 / divisor).toInt()
        val value = 99999
//        val value = if (divisor > 30){
//            6999
//        }else{
//            4999
//        }
        val maxQuotient = value / divisor

        val quotient = (minQuotient..maxQuotient).random()
        val dividend = divisor * quotient

        return DivisionQuestion(
            dividend = dividend,
            divisor = divisor,
            quotient = quotient
        )
    }


    fun generateDivisionTemp(): MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        val totalQue = 20
        (0 until totalQue).forEach { j ->
//            val que2 : Int = 10
            val que2 : Int = generateSingleDigit(2,7)
            val que1 : Int = if (que2 == 5 || que2 == 6 || que2 == 7){
                generateSingleDigit(100,299)
            }else{
                generateSingleDigit(100,499)
            }
//            val que1 : Int = generateSingleDigit(100,499)

//            val que2 : Int = if (j < 10){
//                generateSingleDigit(2,9)
//            }else{
//                generateSingleDigit(5,9)
//            }
//            val que1 : Int = if (j < 10){
//                generateSingleDigit(2,50)
//            }else{
//                generateSingleDigit(2,50)
//            }
//            val que1 : Int = if (j < 6){
//                generateSingleDigit(5,10)
//            }else{
//                generateSingleDigit(10,35)
//            }

            var answer = 0
            var question = ""
//            if (que2 > que1){
//                val answerTemp = que2 * que1
//                question = "${answerTemp}/$que1"
//                answer = answerTemp / que1
//            }else{
            val answerTemp =  que1 * que2
            question = "${answerTemp}/$que2"
            answer = answerTemp / que2
//            }

            listExercise.add(ExerciseList(question,answer.toString()))
        }
        listExercise.shuffle()
        val listnew = listExercise
        val listQue: MutableList<String> = arrayListOf()
        listnew.map {
            listQue.add(it.question)
        }
        Log.e("jigarGenerateSetDiv","listExercise = "+ Gson().toJson(listnew))
        Log.e("jigarGenerateSetDiv","listQue = "+ Gson().toJson(listQue))
        Log.e("jigarGenerateSetDiv","joinToString = "+ listQue.joinToString(","))
        return listExercise
    }
    private fun generateDivision(child: ExerciseLevelDetail): MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        (0 until child.totalQue).forEach { j ->
            val que1 = if (child.digits == 3){
                generateSingleDigit(2,9)
            }else if (child.digits == 4){
                if (j < 2){
                    generateSingleDigit(2,19)
                }else{
                    generateSingleDigit(2,99)
                }
            }else if (child.digits == 5){
                if (j < 2){
                    generateSingleDigit(2,19)
                }else{
                    generateSingleDigit(2,299)
                }
            }else { // 6 digit
                if (child.totalQue > 5){
                    if (j < 5){
                        generateSingleDigit(2,299)
                    }else{
                        generateSingleDigit(300,999)
                    }
                }else{
                    if (j < 2){
                        generateSingleDigit(2,19)
                    }else{
                        generateSingleDigit(2,399)
                    }
                }

            }

            val que22 = if (child.digits == 3){
                val min = 100 / que1
                val max : Int = 999 / que1
                generateSingleDigit(min,max)
            }else if (child.digits == 4){
                val min = 1000 / que1
                val max : Int = 9999 / que1
                generateSingleDigit(min,max)
            }else if (child.digits == 5){
                val min = 10000 / que1
                val max : Int = 99999 / que1
                generateSingleDigit(min,max)
            }else { // 6 digit
                val min = 100000 / que1
                val max : Int = 999999 / que1
                generateSingleDigit(min,max)
            }
            val que2 : Int = que22
            var answer = 0
            var question = ""
            if (que2 > que1){
                val answerTemp = que2 * que1
                question = "${answerTemp}/$que1"
                answer = answerTemp / que1
            }else{
                val answerTemp = que1 * que2
                question = "${answerTemp}/$que2"
                answer = answerTemp / que2
            }

            listExercise.add(ExerciseList(question,answer.toString()))
        }
        listExercise.shuffle()
        return listExercise
    }

    private fun generateMultiplication3(child: ExerciseLevelDetail): MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        (0 until child.totalQue).forEach { j ->
            val que1 = if (child.digits == 3){
                generateSingleDigit(2,99)
            }else if (child.digits == 4){
                generateSingleDigit(2,999)
            }else if (child.digits == 5){
                generateSingleDigit(2,9999)
            }else if (child.digits == 6){
                generateSingleDigit(2,99999)
            }else { // 7 digit
                generateSingleDigit(2,999999)
            }

            val que22 = if (child.digits == 3){
                val min = 100 / que1
                val max : Int = 999 / que1
                generateSingleDigit(min,max)
            }else if (child.digits == 4){
                val min = 1000 / que1
                val max : Int = 9999 / que1
                generateSingleDigit(min,max)
            }else if (child.digits == 5){
                val min = 10000 / que1
                val max : Int = 99999 / que1
                generateSingleDigit(min,max)
            }else if (child.digits == 6){
                val min = 100000 / que1
                val max : Int = 999999 / que1
                generateSingleDigit(min,max)
            }else { // 7 digit
                val min = 1000000 / que1
                val max : Int = 9999999 / que1
                generateSingleDigit(min,max)
            }
            val que2 : Int = que22
            val isInvert = generateIndex()
            var answer = 0
            var question = ""
            if (isInvert == 0){
                answer = que2 * que1
                question = "${que2}x$que1"
            }else{
                answer = que1 * que2
                question = "${que1}x$que2"
            }
            listExercise.add(ExerciseList(question,answer.toString()))
        }

        return listExercise
    }
    fun generateMultiplication3_Temp(count : Int = 20): MutableList<String>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        (0 until count).forEach { j ->
//            val index = generateSingleDigit(0,list.lastIndex)
//            val que1 : Int = list[index]
            val index = generateIndex()
//            val que1 = if (index == 0){
//                (101..999) // odd / even numbers
////                    .filter { it % 2 == 0 }
//                    .random()
//            }else{
////                generateSingleDigit(51,99)
//                (49..99) // odd / even numbers
////                    .filter { it % 2 == 0 }
//                    .random()
////                (11..99) // odd / even numbers
////                .filter { it % 2 != 0 }
////                .random()
//            }
            val que1 = generateSingleDigit(301,999)
//            val que22 = generateSingleDigit(11,49)
//            val que22 =  if (que1 < 50){
//                generateSingleDigit(25,69)
//            }else{
//                generateSingleDigit(11,79)
//            }
//            val que1 = (101..999) // odd / even numbers
//                .filter { it % 2 == 0 }
//                .random()
            val que22 = (51..99) // odd / even numbers
//                .filter { it % 2 == 0 }
                .random()

//            val index = generateSingleDigit(1,4)

//            val index2 = generateSingleDigit(0,list2.lastIndex)
//            val que22 : Int = list2[index2]
            // 2578 3469
            // 2489 3567

//            val que22 = if (index == 1){3}else if (index == 2){5}else if (index == 3){7}else if (index == 4){9}else{1}
            // 257 468 139 25784 36915

            val que2 : Int = que22
//            val isInvert = generateIndex()
            val isInvert = -1
            var answer = 0
            var question = ""
            if (isInvert == 0){
                answer = que2 * que1
                question = "${que2}*$que1"
            }else{
                answer = que1 * que2
                question = "${que1}*$que2"
            }
            listExercise.add(ExerciseList(question,answer.toString()))
        }
        val listnew = listExercise.shuffled().shuffled()
        val listQue: MutableList<String> = arrayListOf()
        listnew.map {
            listQue.add(it.question)
        }
        Log.e("jigarGenerateSetMul","listExercise = "+ Gson().toJson(listnew))
        Log.e("jigarGenerateSetMul","listQue = "+ Gson().toJson(listQue))
        Log.e("jigarGenerateSetMul","joinToString = "+ listQue.joinToString(","))
        return listQue
    }

    private fun generateMulDigit3Que5(child: ExerciseLevelDetail) : MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        var isLongNotDone2 = true
        var isLongNotDone3 = true
        var isLongNotDone4 = true
        var isLongNotDone5 = true
        var isLongNotDone6 = true
        var isLongNotDone7 = true
        var isLongNotDone8 = true
        var isLongNotDone9 = true
        for (j in 0 until child.totalQue){
            val que1 = generateSingleDigit(2,9)
            val que2 = if (que1 == 2){
                val index = generateIndex(3)
                if (isLongNotDone2 && index == 0){
                    isLongNotDone2 = false
                    generateSingleDigit(100,499)
                }else{
                    generateSingleDigit(50,99)
                }
            }else if (que1 == 3){
                val index = generateIndex(3)
                if (isLongNotDone3 && index == 0){
                    isLongNotDone3 = false
                    generateSingleDigit(100,333)
                }else{
                    generateSingleDigit(34,99)
                }
            }else if (que1 == 4){
                val index = generateIndex(3)
                if (isLongNotDone4 && index == 0){
                    isLongNotDone4 = false
                    generateSingleDigit(100,249)
                }else{
                    generateSingleDigit(25,99)
                }
            }else if (que1 == 5){
                val index = generateIndex(3)
                if (isLongNotDone5 && index == 0){
                    isLongNotDone5 = false
                    generateSingleDigit(100,199)
                }else{
                    generateSingleDigit(20,99)
                }
            }else if (que1 == 6){
                val index = generateIndex(3)
                if (isLongNotDone6 && index == 0){
                    isLongNotDone6 = false
                    generateSingleDigit(100,166)
                }else{
                    generateSingleDigit(17,99)
                }
            }else if (que1 == 7){
                val index = generateIndex(3)
                if (isLongNotDone7 && index == 0){
                    isLongNotDone7 = false
                    generateSingleDigit(100,142)
                }else{
                    generateSingleDigit(15,99)
                }
            }else if (que1 == 8){
                val index = generateIndex(3)
                if (isLongNotDone8 && index == 0){
                    isLongNotDone8 = false
                    generateSingleDigit(100,124)
                }else{
                    generateSingleDigit(13,99)
                }
            }else { // if (que1 == 9)
                val index = generateIndex(3)
                if (isLongNotDone9 && index == 0){
                    isLongNotDone9 = false
                    generateSingleDigit(100,111)
                }else{
                    generateSingleDigit(12,99)
                }
            }

            val isInvert = generateIndex()
            var answer = 0
            var question = ""
            if (isInvert == 0){
                answer = que2 * que1
                question = "${que2}x$que1"
            }else{
                answer = que1 * que2
                question = "${que1}x$que2"
            }
            listExercise.add(ExerciseList(question,answer.toString()))
        }

        return listExercise
    }

    fun generateChallengeModeQuestion(totalQuestion : Int, minNumber: Int, maxNumber : Int) : CustomChallengeData{
        val listQuestion: MutableList<CustomChallengeQuestion> = arrayListOf()
        val max = when (maxNumber) {
            2 -> {
                99
            }
            3 -> {
                999
            }
            4 -> {
                9999
            }
            5 -> {
                99999
            }
            6 -> {
                999999
            }
            7 -> {
                9999999
            }
            else -> {
                9
            }
        }
        val min = when (minNumber) {
            2 -> {
                10
            }
            3 -> {
                100
            }
            4 -> {
                1000
            }
            5 -> {
                10000
            }
            6 -> {
                100000
            }
            7 -> {
                1000000
            }
            else -> {
                1
            }
        }

        var maxMinusSignCount = 2

        if (totalQuestion > 10){
            maxMinusSignCount = generateTotalMinusSign(totalQuestion/2)
        }
        var answer = 0
        var minusSignCount = 0
        var question = ""
        for (i in 0 until totalQuestion){
            if (i == 0){
                answer = generateSingleDigit(min, max)
                question = answer.toString()
                listQuestion.add(CustomChallengeQuestion("",answer))
            }else{
                if (minusSignCount == maxMinusSignCount){
                    val nextValues = generateSingleDigit(min, max)
                    val tempAnswer = answer + nextValues
                    if (tempAnswer > 999999){
                        listQuestion.add(CustomChallengeQuestion("-",nextValues))
                        question = "$question-$nextValues"
                        answer -= nextValues
                    }else{
                        listQuestion.add(CustomChallengeQuestion("+",nextValues))
                        question = "$question+$nextValues"
                        answer += nextValues
                    }

                }else{
                    val index = generateIndex()
                    if (index == 0 || answer < min) { // 0 = add +
                        val nextValues = generateSingleDigit(min, max)
                        val tempAnswer = answer + nextValues
                        if (tempAnswer > 999999){
                            listQuestion.add(CustomChallengeQuestion("-",nextValues))
                            question = "$question-$nextValues"
                            answer -= nextValues
                        }else{
                            listQuestion.add(CustomChallengeQuestion("+",nextValues))
                            question = "$question+$nextValues"
                            answer += nextValues
                        }

                    }else{ // minus -
                        minusSignCount++
                        val nextValues = if ((answer + 1) > max){
                            nextInt(min, max)
                        }else{
                            nextInt(min, answer + 1)
                        }

                        val temp = answer - nextValues
                        if (i == (totalQuestion -1) && temp == 0){
                            val nextValuesTemp = nextValues - 1
                            listQuestion.add(CustomChallengeQuestion("-",nextValuesTemp))
                            question = "$question-$nextValuesTemp"
                            answer -= nextValuesTemp
                        }else{
                            listQuestion.add(CustomChallengeQuestion("-",nextValues))
                            question = "$question-$nextValues"
                            answer -= nextValues
                        }

                    }
                }

            }
        }
        return CustomChallengeData(listQuestion,question,answer)
    }

    fun generateAdditionSubExerciseTemp(count : Int = 20) : MutableList<String>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        var min = 100
        var max = 999

        var queLines = 5
        for (j in 0 until count){
//             min = 1
//             max = 20

            var minusSignCount = 0
            var maxMinusSignCount = 10
            val indexLines = generateSingleDigit(0,2)
            if (indexLines == 0){
                queLines = 3
            }else if (indexLines == 1){
                queLines = 4
            }else if (indexLines == 2){
                queLines = 5
            }
            val index1 = generateIndex()
//            maxMinusSignCount = if (index1 == 0){
//                1
//            }else{
//                val index2 = generateIndex()
//                if (index2 == 0){
//                    0
//                }else{
//                    2
//                }
//            }

            var answer = 0
            var question = ""
            var isTwoLineDone = false
            var isThreeLineDone = false
            var isOneLineDone = false
//            var isOtherQueDone = false
            for (i in 0 until queLines){

                val index : Int = generateSingleDigit(0,3)
                when (index) {
                    0 -> {
                        min = 49
                        max = 999
                    }
                    1 -> {
                        min = 999
                        max = 2999
                    }
                    2 -> {
                        min = 2999
                        max = 7999
                    }
                    3 -> {
                        min = 4999
                        max = 9999
                    }
                }



//                val index1 = generateIndex()
//                val index2 = generateIndex()
//                if (!isTwoLineDone && index1 == 0){
//                    isTwoLineDone = true
//                    min = 31
//                    max = 99
//                }
//                else if (!isOneLineDone && index2 == 1){
//                    isOneLineDone = true
//                    min = 41
//                    max = 99
//                }
//                else{
////                    isThreeLineDone = true
//                    min = 111
//                    max = 999
//                }
                if (i == 0){
                    answer = generateSingleDigit(min, max)
                    question = answer.toString()
                }else{
                    if (minusSignCount == maxMinusSignCount) {
                        val nextValues = generateSingleDigit(min, max)
                        question = "$question+$nextValues"
                        answer += nextValues
                    }else{
                        val index = generateIndex()
                        if (index == 0 || answer < min) { // 0 = add +
                            val nextValues = generateSingleDigit(min, max)
                            question = "$question+$nextValues"
                            answer += nextValues
                        }else{ // minus -
                            minusSignCount++
                            val nextValues = if ((answer + 1) > max){
                                nextInt(min, max)
                            }else{
                                nextInt(min, answer + 1)
                            }

                            val temp = answer - nextValues
                            if (i == (queLines -1) && temp == 0){
                                val nextValuesTemp = nextValues - 1
                                question = "$question-$nextValuesTemp"
                                answer -= nextValuesTemp
                            }else{
                                question = "$question-$nextValues"
                                answer -= nextValues
                            }

                        }
                    }
                }
            }
            listExercise.add(ExerciseList(question,answer.toString()))
        }
        val listnew = listExercise.shuffled().shuffled()
        val listQue: MutableList<String> = arrayListOf()
        listnew.map {
            listQue.add(it.question)
        }
        Log.e("jigarGenerateSetAddSub","listExercise = "+ Gson().toJson(listnew))
        Log.e("jigarGenerateSetAddSub","listQue = "+ Gson().toJson(listQue))
        Log.e("jigarGenerateSetAddSub","joinToString = "+ listQue.joinToString(","))
        return listQue
    }

    fun getTensList(context: Context) : java.util.ArrayList<String> {
        val tens : java.util.ArrayList<String> = arrayListOf()
        tens.add("")
        tens.add("")
        tens.add(context.resources.getString(R.string.Twenty))
        tens.add(context.resources.getString(R.string.Thirty))
        tens.add(context.resources.getString(R.string.Forty))
        tens.add(context.resources.getString(R.string.Fifty))
        tens.add(context.resources.getString(R.string.Sixty))
        tens.add(context.resources.getString(R.string.Seventy))
        tens.add(context.resources.getString(R.string.Eighty))
        tens.add(context.resources.getString(R.string.Ninety))
        return tens
    }
    fun getUnitsList(context: Context) : java.util.ArrayList<String> {
        val units : java.util.ArrayList<String> = arrayListOf()
        units.add("")
        units.add(context.resources.getString(R.string.One))
        units.add(context.resources.getString(R.string.Two))
        units.add(context.resources.getString(R.string.Three))
        units.add(context.resources.getString(R.string.Four))
        units.add(context.resources.getString(R.string.Five))
        units.add(context.resources.getString(R.string.Six))
        units.add(context.resources.getString(R.string.Seven))
        units.add(context.resources.getString(R.string.Eight))
        units.add(context.resources.getString(R.string.Nine))
        units.add(context.resources.getString(R.string.Ten))
        units.add(context.resources.getString(R.string.Eleven))
        units.add(context.resources.getString(R.string.Twelve))
        units.add(context.resources.getString(R.string.Thirteen))
        units.add(context.resources.getString(R.string.Fourteen))
        units.add(context.resources.getString(R.string.Fifteen))
        units.add(context.resources.getString(R.string.Sixteen))
        units.add(context.resources.getString(R.string.Seventeen))
        units.add(context.resources.getString(R.string.Eighteen))
        units.add(context.resources.getString(R.string.Nineteen))
        return units
    }

}
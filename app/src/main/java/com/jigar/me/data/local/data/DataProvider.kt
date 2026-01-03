package com.jigar.me.data.local.data

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.pages.*
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.dpToPx
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt


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

    fun getAvatarList() : ArrayList<AvatarImages>{
        val list = ArrayList<AvatarImages>()
        with(list){
            add(AvatarImages(1,R.drawable.ic_avatar_man_01))
            add(AvatarImages(2,R.drawable.ic_avatar_girl_02))
            add(AvatarImages(3,R.drawable.ic_avatar_girl_03))
            add(AvatarImages(4,R.drawable.ic_avatar_girl_04))
            add(AvatarImages(5,R.drawable.ic_avatar_girl_05))
            add(AvatarImages(6,R.drawable.ic_avatar_girl_06))
            add(AvatarImages(1001,R.drawable.ic_avatar_girl_01))
            add(AvatarImages(1002,R.drawable.ic_avatar_man_02))
            add(AvatarImages(1003,R.drawable.ic_avatar_man_03))
            add(AvatarImages(1004,R.drawable.ic_avatar_man_04))
            add(AvatarImages(1005,R.drawable.ic_avatar_man_05))
            add(AvatarImages(1006,R.drawable.ic_avatar_man_06))
            add(AvatarImages(1007,R.drawable.ic_avatar_man_07))
            add(AvatarImages(1008,R.drawable.ic_avatar_man_08))
        }
        return list
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
    fun getAbacusThemeFreeTypeList(context: Context,abacusBeadType: AbacusBeadType) : ArrayList<AbacusContent>{
        val list = ArrayList<AbacusContent>()
        val multiply = getMultipleDimensions(abacusBeadType)

        val prefManager = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        val screenWidthDp = prefManager.getCustomParamInt(AppConstants.screenWidthDp,0)
        val rectWidth = context.resources.getDimension(R.dimen.padding_radius_abacus_frame_large) * 2
        val colSpace = context.resources.getDimension(R.dimen.poligon_space) * 14
        val extraPadding = context.resources.getDimension(R.dimen.activity_padding10) * 2
        val remainSpace = screenWidthDp - colSpace - rectWidth - extraPadding
        val beadWidth = remainSpace / 13
        val beadHeight : Double = ((5 * beadWidth) / 9).toDouble()  // 9 : 5

        val height = context.dpToPx((beadHeight * multiply).toFloat()).toInt()
        val width = context.dpToPx((beadWidth * multiply)).toInt()

        val space = (context.resources.getDimension(R.dimen.poligon_space) * multiply).toInt()
        with(list){
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Rainbow,R.drawable.poligon_pink,R.drawable.bg_abacus_frame_large_pink,R.drawable.bg_abacus_frame_large_pink_exam,R.color.abacus_rod_pink,R.color.abacus_rod_pink_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_pink,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#216869", answerWindowLine = "#49a078", answerWindowBtnBgLine = "#f06292"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_default,R.drawable.poligon_black,R.drawable.bg_abacus_frame_large_black,R.drawable.bg_abacus_frame_large_black_exam,R.color.abacus_rod_black,R.color.abacus_rod_black_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_black,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#bb4430", answerWindowLine = "#f6ae29", answerWindowBtnBgLine = "#bdbdbd"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Blue,R.drawable.poligon_blue,R.drawable.bg_abacus_frame_large_blue,R.drawable.bg_abacus_frame_large_blue_exam,R.color.abacus_rod_blue,R.color.abacus_rod_blue_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_blue,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#7f2ccb", answerWindowLine = "#ffa9e7", answerWindowBtnBgLine = "#7986cb"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Purple,R.drawable.poligon_purple,R.drawable.bg_abacus_frame_large_purple,R.drawable.bg_abacus_frame_large_purple_exam,R.color.abacus_rod_purple,R.color.abacus_rod_purple_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_purple,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#a23a06", answerWindowLine = "#f6ae29", answerWindowBtnBgLine = "#ba68cb"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Blue_Sky,R.drawable.poligon_blue_sky,R.drawable.bg_abacus_frame_large_blue_sky,R.drawable.bg_abacus_frame_large_blue_sky_exam,R.color.abacus_rod_blue_sky,R.color.abacus_rod_blue_sky_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_blue_sky,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#401e5b", answerWindowLine = "#ff8552", answerWindowBtnBgLine = "#64b5f6"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Orange,R.drawable.poligon_orange,R.drawable.bg_abacus_frame_large_orange,R.drawable.bg_abacus_frame_large_orange_exam,R.color.abacus_rod_orange,R.color.abacus_rod_orange_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_orange,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#731500", answerWindowLine = "#fcdc4d", answerWindowBtnBgLine = "#ffb74d"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Green,R.drawable.poligon_green,R.drawable.bg_abacus_frame_large_green,R.drawable.bg_abacus_frame_large_green_exam,R.color.abacus_rod_green,R.color.abacus_rod_green_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_green,arrayListOf(),arrayListOf(),R.color.abacus_rod_green_txt, unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#932826", answerWindowLine = "#fb9648", answerWindowBtnBgLine = "#81c784"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Red,R.drawable.poligon_red,R.drawable.bg_abacus_frame_large_red,R.drawable.bg_abacus_frame_large_red_exam,R.color.abacus_rod_red,R.color.abacus_rod_red_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_red,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#0D1764", answerWindowLine = "#9199E2", answerWindowBtnBgLine = "#e57373"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Tint,R.drawable.poligon_tint,R.drawable.bg_abacus_frame_large_tint,R.drawable.bg_abacus_frame_large_tint_exam,R.color.abacus_rod_tint,R.color.abacus_rod_tint_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_tint,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#5f0f40", answerWindowLine = "#da7422", answerWindowBtnBgLine = "#4dd0e1"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Pink,R.drawable.poligon_pink,R.drawable.bg_abacus_frame_large_pink,R.drawable.bg_abacus_frame_large_pink_exam,R.color.abacus_rod_pink,R.color.abacus_rod_pink_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_pink,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#216869", answerWindowLine = "#49a078", answerWindowBtnBgLine = "#f06292"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Yellow,R.drawable.poligon_yellow,R.drawable.bg_abacus_frame_large_yellow,R.drawable.bg_abacus_frame_large_yellow_exam,R.color.abacus_rod_yellow,R.color.abacus_rod_yellow_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_yellow,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#388659", answerWindowLine = "#33ca7f", answerWindowBtnBgLine = "#ffd54f"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Silver,R.drawable.poligon_silver,R.drawable.bg_abacus_frame_large_silver,R.drawable.bg_abacus_frame_large_silver_exam,R.color.abacus_rod_silver,R.color.abacus_rod_silver_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_silver,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#235789", answerWindowLine = "#e1bc29", answerWindowBtnBgLine = "#90a4ae"))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Brown,R.drawable.poligon_brown,R.drawable.bg_abacus_frame_large_brown,R.drawable.bg_abacus_frame_large_brown_dark,R.color.abacus_rod_brown,R.color.abacus_rod_brown_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_brown,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light,answerWindowBG = "#932826", answerWindowLine = "#ff8841", answerWindowBtnBgLine = "#a1887f"))
        }
        return list
    }

    private val abacusThemeList = ArrayList<AbacusContent>()
    fun getAllAbacusThemeTypeList(context: Context, abacusBeadType: AbacusBeadType) : ArrayList<AbacusContent>{
        val list = ArrayList<AbacusContent>()
        list.addAll(getAbacusThemeFreeTypeList(context,abacusBeadType))
        abacusThemeList.clear()
        abacusThemeList.addAll(list)
        list.shuffle()
        return list
    }
    fun findAbacusThemeType(context: Context, theme : String, abacusBeadType: AbacusBeadType) : AbacusContent{
        getAllAbacusThemeTypeList(context,abacusBeadType = abacusBeadType)
        val content : AbacusContent? = abacusThemeList.find { it.type == theme }
        return content ?: abacusThemeList.first()
    }

    /* exercise */
    fun getExerciseList(context: Context) : ArrayList<ExerciseLevel>{
        val list = ArrayList<ExerciseLevel>()
        val listAddition = ArrayList<ExerciseLevelDetail>()
        with(listAddition){
            add(ExerciseLevelDetail("1",5,5,1,3))
            add(ExerciseLevelDetail("2",10,10,1,5))
            add(ExerciseLevelDetail("3",5,5,2,4))
            add(ExerciseLevelDetail("4",10,10,2,8))
            add(ExerciseLevelDetail("5",5,5,3,4))
            add(ExerciseLevelDetail("6",10,10,3,8))
            add(ExerciseLevelDetail("7",5,5,4,5))
            add(ExerciseLevelDetail("8",10,10,4,10))
            add(ExerciseLevelDetail("9",5,5,5,5))
            add(ExerciseLevelDetail("10",10,10,5,10))
            add(ExerciseLevelDetail("11",5,5,6,5))
            add(ExerciseLevelDetail("12",10,10,6,10))
        }
        val listMultiplication = ArrayList<ExerciseLevelDetail>()
        with(listMultiplication){
            add(ExerciseLevelDetail("13",5,0,3,3))
            add(ExerciseLevelDetail("14",10,0,3,5))
            add(ExerciseLevelDetail("15",5,0,4,3))
            add(ExerciseLevelDetail("16",10,0,4,5))
            add(ExerciseLevelDetail("17",5,0,5,3))
            add(ExerciseLevelDetail("18",10,0,5,5))
            add(ExerciseLevelDetail("19",5,0,6,3))
            add(ExerciseLevelDetail("20",10,0,6,5))
            add(ExerciseLevelDetail("21",5,0,7,3))
            add(ExerciseLevelDetail("22",10,0,7,5))
        }
        val listDivision = ArrayList<ExerciseLevelDetail>()
        with(listDivision){
            add(ExerciseLevelDetail("33",5,0,3,3))
            add(ExerciseLevelDetail("34",5,0,3,2))

            add(ExerciseLevelDetail("23",5,0,4,3))
            add(ExerciseLevelDetail("24",5,0,4,2))
            add(ExerciseLevelDetail("25",10,0,4,6))
            add(ExerciseLevelDetail("26",10,0,4,4))
            add(ExerciseLevelDetail("27",5,0,5,3))
            add(ExerciseLevelDetail("28",5,0,5,2))
            add(ExerciseLevelDetail("29",10,0,5,6))
            add(ExerciseLevelDetail("30",10,0,5,4))
            add(ExerciseLevelDetail("31",10,0,6,6))
            add(ExerciseLevelDetail("32",10,0,6,4))
        }
        with(list) {
            add(ExerciseLevel("1",context.getString(R.string.AdditionSubtraction),listAddition))
            add(ExerciseLevel("2",context.getString(R.string.Multiplication),listMultiplication))
            add(ExerciseLevel("3",context.getString(R.string.Division),listDivision))
        }
        return list
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
    fun generateDivisionExercise(child: ExerciseLevelDetail) : MutableList<ExerciseList>{
        return generateDivision(child)
    }
    fun generateMultiplicationExercise(child: ExerciseLevelDetail) : MutableList<ExerciseList>{
        var listExercise: MutableList<ExerciseList> = arrayListOf()
        if (child.digits == 3 && child.totalQue == 5){
            listExercise.addAll(generateMulDigit3Que5(child))
        }else{
            listExercise = generateMultiplication3(child)
        }
        return listExercise
    }

    fun generateDivisionTemp(child: ExerciseLevelDetail): MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        val totalQue = 20
        (0 until totalQue).forEach { j ->
//            val que2 : Int = 10
            val que2 : Int = generateSingleDigit(4,9)
            val que1 : Int = generateSingleDigit(100,600)

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
    fun generateMultiplication3_Temp(child: ExerciseLevelDetail): MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
//        val list : ArrayList<Int> = arrayListOf()
//        val endDigit = 0
//        (1 until 6).forEach { k ->
//            list.add(("$k"+endDigit).toInt())
//        }
//        (10 until 100).forEach { k ->
//            if (k%2 == 0){
//                list.add(k)
//            }else{
//                list.add(k)
//            }
//        }
//        val list2 : ArrayList<Int> = arrayListOf()
//        (50 until 90).forEach { k ->
//            if (k%2 == 0){
//                list2.add(k)
//            }else{
//                list2.add(k)
//            }
//        }
        (0 until 20).forEach { j ->
//            val que1 = if (child.digits == 3){
//                generateSingleDigit(2,99)
//            }else if (child.digits == 4){
//                generateSingleDigit(2,999)
//            }else if (child.digits == 5){
//                generateSingleDigit(2,9999)
//            }else if (child.digits == 6){
//                generateSingleDigit(2,99999)
//            }else { // 7 digit
//                generateSingleDigit(2,999999)
//            }
//
//            val que22 = if (child.digits == 3){
//                val min = 100 / que1
//                val max : Int = 999 / que1
//                generateSingleDigit(min,max)
//            }else if (child.digits == 4){
//                val min = 1000 / que1
//                val max : Int = 9999 / que1
//                generateSingleDigit(min,max)
//            }else if (child.digits == 5){
//                val min = 10000 / que1
//                val max : Int = 99999 / que1
//                generateSingleDigit(min,max)
//            }else if (child.digits == 6){
//                val min = 100000 / que1
//                val max : Int = 999999 / que1
//                generateSingleDigit(min,max)
//            }else { // 7 digit
//                val min = 1000000 / que1
//                val max : Int = 9999999 / que1
//                generateSingleDigit(min,max)
//            }
            var min = 300
            var max = 999
//            if (j < 4){
//                min = 5
//                max = 9
//            }else{
//                 min = 10
//                 max = 49
//            }

//            val index = generateSingleDigit(0,list.lastIndex)
//            val que1 : Int = list[index]
            val que1 = generateSingleDigit(min,max)
//            val que22 = 9
            val que22 = generateSingleDigit(50,99)
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

        return listExercise
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
    fun generateAdditionSubExercise(child: ExerciseLevelDetail) : MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        val max = if (child.digits == 2){
            99
        }else if (child.digits == 3){
            999
        }else if (child.digits == 4){
            9999
        }else if (child.digits == 5){
            99999
        }else if (child.digits == 6){
            999999
        }else{
            9
        }
        val min = if (child.digits == 2){
            10
        }else if (child.digits == 3){
            100
        }else if (child.digits == 4){
            1000
        }else if (child.digits == 5){
            10000
        }else if (child.digits == 6){
            100000
        }else{
            1
        }

        for (j in 0 until child.totalQue){
            var maxMinusSignCount = 2
            if (child.queLines > 5){
                maxMinusSignCount = generateTotalMinusSign()
            }else{
                val index = generateIndex()
                if (index == 0){
                    maxMinusSignCount = 1
                }
            }
            var answer = 0
            var minusSignCount = 0
            var question = ""
            for (i in 0 until child.queLines){
                if (i == 0){
                    answer = generateSingleDigit(min, max)
                    question = answer.toString()
                }else{
                    if (minusSignCount == maxMinusSignCount){
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
                            if (i == (child.queLines -1) && temp == 0){
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
        return listExercise
    }

    fun  generateAdditionSubExerciseTemp(child: ExerciseLevelDetail) : MutableList<ExerciseList>{
        val listExercise: MutableList<ExerciseList> = arrayListOf()
        var min = 1000
        var max = 9999

        var queLines = 5
        for (j in 0 until 20){
//             min = 1
//             max = 20

            var minusSignCount = 0
            var maxMinusSignCount = 1
            val index1 = generateIndex()
            if (index1 == 0){
                maxMinusSignCount = 1
            }else{
//                maxMinusSignCount = 2
                val index2 = generateIndex()
                if (index2 == 0){
                    maxMinusSignCount = 2
                }else{
                    maxMinusSignCount = 0
                }
            }

            var singleDigitCount = 0
            var maxSingleDigitCount = 0
            var threeDigitCount = 0
            var maxThreeDigitCount = 6
            var twoDigitCount = 0
            var maxTwoDigitCount = 2

            val index = generateIndex()
//            if (index == 0){
////                maxSingleDigitCount = 1
//                maxTwoDigitCount = 1
//                maxThreeDigitCount = 4
//            }else{
////                maxSingleDigitCount = 0
//                maxTwoDigitCount = 0
//                maxThreeDigitCount = 5
//            }

//            queLines = generateSingleDigit(5, 6)
//            if (queLines != 4){
//                maxMinusSignCount = 2
//            }
            var answer = 0
            var question = ""
            var isTwoLineDone = false
            var isThreeLineDone = false
            var isOneLineDone = false
//            var isOtherQueDone = false
            for (i in 0 until queLines){
//                if (i == 0){
//                    min = 1000
//                    max = 9999
//                }else{
                    val index1 = generateIndex()
                    val index2 = generateIndex()
//                    if (!isTwoLineDone && index1 == 0){
//                        isTwoLineDone = true
//                        min = 39
//                        max = 99
//                    }else
                    if (!isThreeLineDone && index2 == 1){
                        isThreeLineDone = true
                        min = 500
                        max = 999
                    }else{
                        min = 2000
                        max = 9999
                    }
//                }

//                if (i == 0){
//                    min = 10
//                    max = 29
//                }else if (i == 1){
//                    min = 15
//                    max = 59
//                }else if (i == 2){
//                    min = 45
//                    max = 79
//                }else{
//                    min = 70
//                    max = 99
//                }
//                val index1 = generateIndex()
//                if (index1 == 0 && !isThreeLineDone) {
//                    isThreeLineDone = true
////                    min = 100
////                    max = 399
//                    min = 1
//                    max = 9
//                } else {
////                    min = 10
////                    max = 99
//
//                    val index2 = generateIndex()
//                    if ((index2 == 0 && !isTwoLineDone) || isOneLineDone){
//                        isTwoLineDone = true
//                        min = 10
//                        max = 99
//
////                        min = 100
////                        max = 399
//                    }else{
//                        if (!isThreeLineDone) {
//                            isThreeLineDone = true
//                            min = 10
//                            max = 79
//                        }else{
//                            isOneLineDone = true
//                            min = 10
//                            max = 99
//                        }
//                    }
////
////                    //                    val index = generateIndex()
////                    //                    if (index == 0){
////                    //                        min = 10
////                    //                        max = 99
////                    //                    }else{
////                    //                        min = 1
////                    //                        max = 9
////                    //                    }
//                }

//                else if (i == 1){
//                    min = 10
//                    max = 99
//                }
//                else{
//                    min = 3
//                    max = 9
//                }

//                val index4 = generateIndex()
//                if (index4 == 0 && threeDigitCount != maxThreeDigitCount){
//                    min = 100
//                    max = 499
//                    threeDigitCount++
//                }else {
//                    val index5 = generateIndex()
//                    if (index5 == 1 && twoDigitCount != maxTwoDigitCount){
//                        min = 50
//                        max = 99
//                        twoDigitCount++
//                    }else{
//                        if (singleDigitCount != maxSingleDigitCount){
//                            min = 4
//                            max = 9
//                            singleDigitCount++
//                        }else if (threeDigitCount != maxThreeDigitCount){
//                            min = 300
//                            max = 999
//                            threeDigitCount++
//                        }else if (twoDigitCount != maxTwoDigitCount){
//                            min = 50
//                            max = 99
//                            twoDigitCount++
//                        }
//
//                    }
//                }


//                if (i == 0){
//                    min = 14
//                    max = 79
//                }else if (i == 1){
//                    val index = generateIndex()
//                    if (index == 0 || maxSingleDigitCount == singleDigitCount){
//                        min = 14
//                        max = 79
//                    }else{
//                        singleDigitCount++
//                        min = 1
//                        max = 9
//                    }
//                }else if (i == 2){
//                    if (singleDigitCount == 0){
//                        min = 1
//                        max = 9
//                    }else{
//                        min = 14
//                        max = 79
//                    }
//                }

//                if (i == 0){
//                     min = 20
//                     max = 99
//                }else{
//                    min = 3
//                    max = 9
//                }
//                if (j < 10){
//                    max = 400
//                    min = 100
//                    if (i == 2){
//                        max = 300
//                    }
//                }else{
//                    max = 800
//                    min = 100
//                }


//                if (i == 0){
//                    max = 500
//                    min = 100
//                }else if (i == 1){
//                    max = 300
//                    min = 100
//                }else{
//                    max = 99
//                    min = 20
//                }

//                if (i == 0 || i == 1){
//                    if (j < 5){
//                        min = 10
//                        max = 49
//                    }else{
//                        min = 10
//                        max = 99
//                    }
//                }else{
//                    if (j < 8){
//                        min = 2
//                    }else{
//                        min = 4
//                    }
//                    max = 10
//                }
                if (i == 0){
                    answer = generateSingleDigit(min, max)
//                    if (answer > 10){
//                        max = 9
//                    }
                    question = answer.toString()
                }else{
                    if (minusSignCount == maxMinusSignCount) {
                        val nextValues = generateSingleDigit(min, max)
//                        if (nextValues > 10){
//                            max = 9
//                        }
                        question = "$question+$nextValues"
                        answer += nextValues
                    }else{
                        val index = generateIndex()
                        if (index == 0 || answer < min) { // 0 = add +
                            val nextValues = generateSingleDigit(min, max)
//                            if (nextValues > 10){
//                                max = 9
//                            }
                            question = "$question+$nextValues"
                            answer += nextValues
                        }else{ // minus -
                            minusSignCount++
                            val nextValues = if ((answer + 1) > max){
                                nextInt(min, max)
                            }else{
                                nextInt(min, answer + 1)
                            }
//                            if (nextValues > 10){
//                                max = 9
//                            }

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
//        val listnew = listExercise.shuffled().shuffled()
        val listnew = listExercise
        val listQue: MutableList<String> = arrayListOf()
        listnew.map {
            listQue.add(it.question)
        }
        return listExercise
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
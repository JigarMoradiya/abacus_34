package com.jigar.me.data.local.data

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.jigar.me.R
import com.jigar.me.data.model.pages.*
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt


object DataProvider {
    fun getAppPaidFeatureList(context: Context): List<String>{
        return with(context){
            listOf(
                getString(R.string.txt_purchase1),
                getString(R.string.txt_purchase2),
                getString(R.string.txt_purchase3),
                getString(R.string.txt_purchase4),
                getString(R.string.txt_purchase5),
                getString(R.string.txt_purchase6),
                getString(R.string.txt_purchase7),
                getString(R.string.txt_purchase8),
                getString(R.string.txt_purchase9),
                getString(R.string.txt_purchase10),
            )
        }
    }
    fun getAppFreeFeatureList(context: Context): List<String>{
        return with(context){
            listOf(
                getString(R.string.txt_free1),
                getString(R.string.txt_free2),
                getString(R.string.txt_free3),
                getString(R.string.txt_free4),
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
    fun getBannerList(context : Context) : ArrayList<HomeBanner>{
        val bannerListData: ArrayList<HomeBanner> = arrayListOf()
        with(context){
            bannerListData.add(HomeBanner(
//                getString(R.string.need_application_desc)
                Constants.banner_bulk_login,getString(R.string.need_application),"",getString(R.string.contact_us_now),
                ContextCompat.getDrawable(this,R.drawable.gradient_banner_bg_bulk_login)))
            bannerListData.add(HomeBanner(
                Constants.banner_rate_us,getString(R.string.we_need_your_help),getString(R.string.kindly_support_us_by_rating_an_application),getString(R.string.rate_now),
                ContextCompat.getDrawable(this,R.drawable.gradient_banner_bg_rate_now)))
            bannerListData.add(HomeBanner(
                Constants.banner_share,getString(R.string.we_need_your_help),getString(R.string.kindly_support_us_by_sharing_an_application),getString(R.string.share_now_),
                ContextCompat.getDrawable(this,R.drawable.gradient_banner_bg_share_now)))
        }
        return bannerListData
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
    fun getHomeMenuRandomIntro(prefManager: AppPreferencesHelper): HomeMenuIntroType{
        val list = ArrayList<HomeMenuIntroType>()
        with(list){
            add(HomeMenuIntroType.freeMode)
            add(HomeMenuIntroType.videoTutorial)
            add(HomeMenuIntroType.exercise)
            add(HomeMenuIntroType.exam)
            add(HomeMenuIntroType.numberPuzzle)
            add(HomeMenuIntroType.ccm)
            add(HomeMenuIntroType.setting)
            add(HomeMenuIntroType.material)
            if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All, "") != "Y"
                && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Toddler_Single_digit_level1, "") != "Y"
                && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Add_Sub_level2, "") != "Y"
                && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Mul_Div_level3, "") != "Y"){
                add(HomeMenuIntroType.purchase)
            }

//            if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All, "") != "Y"
//                && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "") != "Y"
//                && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "") != "Y"){
//
//            }
        }
        list.shuffle()
        return list.first()
//        return HomeMenuIntroType.purchase
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
            AbacusBeadType.AbacusPrecise,AbacusBeadType.Exercise,AbacusBeadType.CustomeChallenge -> {
                0.8f
            }
            AbacusBeadType.FreeMode,AbacusBeadType.FullMode -> {
                0.85f
            }
            else -> {
                1f
            }
        }
    }
    fun getAbacusThemeFreeTypeList(context: Context,abacusBeadType: AbacusBeadType) : ArrayList<AbacusContent>{
        val list = ArrayList<AbacusContent>()
        val multiply = getMultipleDimensions(abacusBeadType)
        val height = (context.resources.getDimension(R.dimen.poligon_height) * multiply).toInt()
        val width = (context.resources.getDimension(R.dimen.poligon_width) * multiply).toInt()
        val space = (context.resources.getDimension(R.dimen.poligon_space) * multiply).toInt()
        with(list){
            add(AbacusContent(AppConstants.Settings.theam_Poligon_default,R.drawable.poligon_black,R.drawable.bg_abacus_frame_large_black,R.drawable.bg_abacus_frame_large_black_exam,R.color.abacus_rod_black,R.color.abacus_rod_black_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_black,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Blue,R.drawable.poligon_blue,R.drawable.bg_abacus_frame_large_blue,R.drawable.bg_abacus_frame_large_blue_exam,R.color.abacus_rod_blue,R.color.abacus_rod_blue_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_blue,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Purple,R.drawable.poligon_purple,R.drawable.bg_abacus_frame_large_purple,R.drawable.bg_abacus_frame_large_purple_exam,R.color.abacus_rod_purple,R.color.abacus_rod_purple_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_purple,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Blue_Sky,R.drawable.poligon_blue_sky,R.drawable.bg_abacus_frame_large_blue_sky,R.drawable.bg_abacus_frame_large_blue_sky_exam,R.color.abacus_rod_blue_sky,R.color.abacus_rod_blue_sky_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_blue_sky,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Orange,R.drawable.poligon_orange,R.drawable.bg_abacus_frame_large_orange,R.drawable.bg_abacus_frame_large_orange_exam,R.color.abacus_rod_orange,R.color.abacus_rod_orange_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_orange,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Green,R.drawable.poligon_green,R.drawable.bg_abacus_frame_large_green,R.drawable.bg_abacus_frame_large_green_exam,R.color.abacus_rod_green,R.color.abacus_rod_green_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_green,arrayListOf(),arrayListOf(),R.color.abacus_rod_green_txt, unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Red,R.drawable.poligon_red,R.drawable.bg_abacus_frame_large_red,R.drawable.bg_abacus_frame_large_red_exam,R.color.abacus_rod_red,R.color.abacus_rod_red_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_red,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Tint,R.drawable.poligon_tint,R.drawable.bg_abacus_frame_large_tint,R.drawable.bg_abacus_frame_large_tint_exam,R.color.abacus_rod_tint,R.color.abacus_rod_tint_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_tint,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Pink,R.drawable.poligon_pink,R.drawable.bg_abacus_frame_large_pink,R.drawable.bg_abacus_frame_large_pink_exam,R.color.abacus_rod_pink,R.color.abacus_rod_pink_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_pink,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Yellow,R.drawable.poligon_yellow,R.drawable.bg_abacus_frame_large_yellow,R.drawable.bg_abacus_frame_large_yellow_exam,R.color.abacus_rod_yellow,R.color.abacus_rod_yellow_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_yellow,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Silver,R.drawable.poligon_silver,R.drawable.bg_abacus_frame_large_silver,R.drawable.bg_abacus_frame_large_silver_exam,R.color.abacus_rod_silver,R.color.abacus_rod_silver_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_silver,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
            add(AbacusContent(AppConstants.Settings.theam_Poligon_Brown,R.drawable.poligon_brown,R.drawable.bg_abacus_frame_large_brown,R.drawable.bg_abacus_frame_large_brown_dark,R.color.abacus_rod_brown,R.color.abacus_rod_brown_dark,height,width,space,R.drawable.poligon_gray,R.drawable.poligon_brown,arrayListOf(),arrayListOf(), unUsedBeads = R.drawable.poligon_gray_light))
        }
        return list
    }

    fun getAbacusThemePaidTypeList(context: Context,abacusBeadType: AbacusBeadType,isAddSelected : Boolean = false) : ArrayList<AbacusContent>{
        val list = ArrayList<AbacusContent>()
        val multiply = getMultipleDimensions(abacusBeadType)
        val faceCloseList = if (abacusBeadType == AbacusBeadType.Exam || abacusBeadType == AbacusBeadType.ExamResult){
            arrayListOf(R.drawable.face_gray_close,R.drawable.face_gray_close,R.drawable.face_gray_close,R.drawable.face_gray_close)
        }else{
            arrayListOf(R.drawable.face_pink_close,R.drawable.face_orange_close,R.drawable.face_blue_close,R.drawable.face_green_close)
        }

        val faceCloseTop = if (abacusBeadType == AbacusBeadType.Exam || abacusBeadType == AbacusBeadType.ExamResult){
            R.drawable.face_gray_close
        }else{
            R.drawable.face_red_close
        }
        val faceOpenList = arrayListOf(R.drawable.face_pink_open,R.drawable.face_orange_open,R.drawable.face_blue_open,R.drawable.face_green_open)
        val starCloseList = arrayListOf(R.drawable.star_gray_close,R.drawable.star_gray_close,R.drawable.star_gray_close,R.drawable.star_gray_close)
        val starOpenList = arrayListOf(R.drawable.star_yellow_open,R.drawable.star_blue_open,R.drawable.star_orange_open,R.drawable.star_green_open)
        val diamondCloseList = arrayListOf(R.drawable.diamond_gray,R.drawable.diamond_gray,R.drawable.diamond_gray,R.drawable.diamond_gray)
        val diamondOpenList = arrayListOf(R.drawable.diamond_purple,R.drawable.diamond_yellow,R.drawable.diamond_blue,R.drawable.diamond_green)
        val garnetCloseList = arrayListOf(R.drawable.garnet_gray,R.drawable.garnet_gray,R.drawable.garnet_gray,R.drawable.garnet_gray)
        val garnetOpenList = arrayListOf(R.drawable.garnet_purple,R.drawable.garnet_orange,R.drawable.garnet_blue,R.drawable.garnet_green)
        val shapeCloseList = arrayListOf(R.drawable.shape_stone_gray,R.drawable.shape_triangle_gray,R.drawable.shape_circle_gray,R.drawable.shape_hexagon_gray)
        val shapeOpenList = arrayListOf(R.drawable.shape_stone,R.drawable.shape_triangle,R.drawable.shape_circle,R.drawable.shape_hexagon)
        val eggCloseList = arrayListOf(R.drawable.egg,R.drawable.egg,R.drawable.egg,R.drawable.egg)
        val eggOpenList = arrayListOf(R.drawable.egg1,R.drawable.egg4,R.drawable.egg2,R.drawable.egg3)
        with(list){
            add(AbacusContent(AppConstants.Settings.theam_face,R.drawable.face_red_open,R.drawable.bg_abacus_frame_large_red_eye,R.drawable.bg_abacus_frame_large_red_eye_exam,R.color.abacus_rod_red,R.color.abacus_rod_red_dark
                ,(context.resources.getDimension(R.dimen.face_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.face_width) * multiply).toInt(),(context.resources.getDimension(R.dimen.face_space) * multiply).toInt(),faceCloseTop,R.drawable.face_red_open,faceCloseList,faceOpenList, unUsedBeads = R.drawable.face_gray_close))
            add(AbacusContent(AppConstants.Settings.theam_Star,R.drawable.star_red_open,R.drawable.bg_abacus_frame_large_gray,R.drawable.bg_abacus_frame_large_gray_exam,R.color.abacus_rod_gray,R.color.abacus_rod_gray_dark
                ,(context.resources.getDimension(R.dimen.star_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.star_width) * multiply).toInt(),(context.resources.getDimension(R.dimen.star_space) * multiply).toInt(),R.drawable.star_gray_close,R.drawable.star_red_open,starCloseList,starOpenList, unUsedBeads = R.drawable.star_gray_close))
            if (!isAddSelected){
                add(AbacusContent(AppConstants.Settings.theam_diamond,R.drawable.diamond_red,R.drawable.bg_abacus_frame_large_silver,R.drawable.bg_abacus_frame_large_silver_exam,R.color.abacus_rod_silver,R.color.abacus_rod_silver_dark
                    ,(context.resources.getDimension(R.dimen.diamond_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.diamond_width) * multiply).toInt(),(context.resources.getDimension(R.dimen.diamond_space) * multiply).toInt(),R.drawable.diamond_gray,R.drawable.diamond_red,diamondCloseList,diamondOpenList, unUsedBeads = R.drawable.diamond_gray))
                add(AbacusContent(AppConstants.Settings.theam_garnet,R.drawable.garnet_red,R.drawable.bg_abacus_frame_large_gray,R.drawable.bg_abacus_frame_large_gray_exam,R.color.abacus_rod_gray,R.color.abacus_rod_gray_dark
                    ,(context.resources.getDimension(R.dimen.garnet_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.garnet_width) * multiply).toInt(),(context.resources.getDimension(R.dimen.garnet_space) * multiply).toInt(),R.drawable.garnet_gray,R.drawable.garnet_red,garnetCloseList,garnetOpenList, unUsedBeads = R.drawable.garnet_gray))
                add(AbacusContent(AppConstants.Settings.theam_shape,R.drawable.shape_stone,R.drawable.bg_abacus_frame_large_gray,R.drawable.bg_abacus_frame_large_gray_exam,R.color.abacus_rod_gray,R.color.abacus_rod_gray_dark
                    ,(context.resources.getDimension(R.dimen.square_width_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.square_width_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.square_space) * multiply).toInt(),R.drawable.shape_square_gray,R.drawable.shape_square,shapeCloseList,shapeOpenList, unUsedBeads = R.drawable.poligon_gray_light))
                add(AbacusContent(AppConstants.Settings.theam_Egg,R.drawable.egg0,R.drawable.bg_abacus_frame_large_gray,R.drawable.bg_abacus_frame_large_gray_exam,R.color.abacus_rod_gray,R.color.abacus_rod_gray_dark
                    ,(context.resources.getDimension(R.dimen.square_width_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.square_width_height) * multiply).toInt(),(context.resources.getDimension(R.dimen.square_space) * multiply).toInt(),R.drawable.egg,R.drawable.egg0,eggCloseList,eggOpenList, unUsedBeads = R.drawable.egg))
            }

        }
        return list
    }

    private val abacusThemeList = ArrayList<AbacusContent>()
    fun getAllAbacusThemeTypeList(context: Context, isPaidThemeAdd : Boolean = true,abacusBeadType: AbacusBeadType,isAddSelected : Boolean = false) : ArrayList<AbacusContent>{
        val list = ArrayList<AbacusContent>()
        list.addAll(getAbacusThemeFreeTypeList(context,abacusBeadType))
        if (isPaidThemeAdd){
            list.addAll(getAbacusThemePaidTypeList(context,abacusBeadType,isAddSelected))
        }
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
    fun getHomeMenuList(context: Context) : ArrayList<HomeMenu>{
        val list = ArrayList<HomeMenu>()
        with(list){
            add(HomeMenu(AppConstants.HomeClicks.Menu_Starter,R.drawable.home_menu_starter,context.getString(R.string.beginner)))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Number,R.drawable.home_menu_number))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Addition_Subtraction,R.drawable.home_menu_addition_subtraction))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Formulas,R.drawable.home_menu_formula))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Multiplication,R.drawable.home_menu_multiplication))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Division,R.drawable.home_menu_division))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Exercise,R.drawable.home_menu_exercise,context.getString(R.string.most_liked)))
            add(HomeMenu(AppConstants.HomeClicks.Menu_DailyExam,R.drawable.home_menu_exam,context.getString(R.string.favourite)))
            add(HomeMenu(AppConstants.HomeClicks.Menu_CustomChallengeMode,R.drawable.home_menu_custom_challenge,context.getString(R.string.popular)))
            add(HomeMenu(AppConstants.HomeClicks.Menu_PractiseMaterial,R.drawable.home_menu_material))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Number_Puzzle,R.drawable.home_menu_number_sequence))
            add(HomeMenu(AppConstants.HomeClicks.Menu_Click_Youtube,R.drawable.home_menu_tutorial,context.getString(R.string.useful)))
//            add(HomeMenu(AppConstants.HomeClicks.Menu_Purchase,R.drawable.home_menu_purchase))
        }
        return list
    }
    fun getOtherAppList(): ArrayList<OtherApps> {
        val list = ArrayList<OtherApps>()
        with(list) {
            add(OtherApps(AppConstants.HomeClicks.OtherApp_Number, R.drawable.logo_number,"Number learning with abacus","https://play.google.com/store/apps/details?id=com.abacus.soroban&hl=en&utm_source=ref-abacus&utm_medium=related-app&utm_campaign=app"))
            add(OtherApps(AppConstants.HomeClicks.OtherApp_Sudoku, R.drawable.logo_sudoku,"Sudoku Puzzle","https://play.google.com/store/apps/details?id=com.sudoku.puzzle.maths.number&hl=en&utm_source=ref-abacus&utm_medium=related-app&utm_campaign=app"))
        }
        return list
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

    val listColorRandom: MutableList<Int> = arrayListOf()
    fun getRandomColor() : Int{
        if (listColorRandom.isEmpty()){
            listColorRandom.addAll(ColorProvider.getColorList())
        }
        var color = Color.parseColor("#FFEBEE")
        if (listColorRandom.isNotEmpty()){
//            listColorRandom.shuffle()
            color = listColorRandom.first()
            listColorRandom.removeAt(0)
        }
        return color
    }
    // TODO SingleDigit Pages
    fun getSingleDigitPages(context: Context): MutableList<CategoryPages>{
        val listCategory: MutableList<CategoryPages> = arrayListOf()
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_1_50),
            pages = listOf(Pages("1", from = 1, to = 10),
                Pages("2",from = 11, to = 20),
                Pages("3",from = 21, to = 30),
                Pages("4",from = 31, to = 40),
                Pages("5",from = 41, to = 50),
                Pages("6",from = 1, to = 50, type_random = true, page_name = context.getString(R.string.random))
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_51_100),
            pages = listOf(Pages("7",from = 51, to = 60),
                Pages("8",from = 61, to = 70),
                Pages("9",from = 71, to = 80),
                Pages("10",from = 81,to = 90),
                Pages("11",from = 91, to = 100),
                Pages("12",from = 51, to = 100, type_random = true, page_name = context.getString(R.string.random))
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_101_200),
            pages = listOf(Pages("14",from = 101, to = 150),
                Pages("15",from = 151, to = 200),
                Pages("37",from = 101, to = 200, type_random = true,page_name = context.getString(R.string.random)) // new
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.random_numbers),
            pages = listOf(Pages("13",from = 1, to = 100, type_random = true),
                Pages("38",from = 101, to = 200, type_random = true), // new
                Pages("16",from = 1, to = 200, type_random = true)
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_201_300),
            pages = listOf(Pages("17",from = 201, to = 250),
                Pages("18",from = 251, to = 300),
                Pages("39",from = 201, to = 300, type_random = true,page_name = context.getString(R.string.random)), // new
                Pages("19",from = 101, to = 300, type_random = true,page_name = context.getString(R.string.random))
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_301_500),
            pages = listOf(Pages("20",from = 301, to = 350),
                Pages("21",from = 351, to = 400),
                Pages("22",from = 401, to = 450),
                Pages("23",from = 451, to = 500)
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.random_numbers),
            pages = listOf(Pages("24",from = 301, to = 500, type_random = true),
                Pages("25",from = 101, to = 500, type_random = true),
                Pages("26",from = 1, to = 500, type_random = true)
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.number_501_1000),
            pages = listOf(Pages("27",from = 501, to = 600),
                Pages("28",from = 601, to = 700),
                Pages("29",from = 701, to = 800),
                Pages("30",from = 501, to = 800, type_random = true, page_name = context.getString(R.string.random)),
                Pages("31",from = 801, to = 900),
                Pages("32",from = 901, to = 1000)
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.random_numbers),
            pages = listOf(Pages("33",from = 501, to = 700, type_random = true),
                Pages("34",from = 701, to = 900, type_random = true),
                Pages("40",from = 801, to = 1000, type_random = true),
                Pages("35",from = 501, to = 1000, type_random = true),
                Pages("41",from = 301, to = 800, type_random = true),
                Pages("36",from = 1, to = 1000, type_random = true)
            )))
        return listCategory
    }
    // TODO Multiplication Pages
    fun getMultiplicationPages(context: Context): MutableList<CategoryPages>{
        val listCategory: MutableList<CategoryPages> = arrayListOf()
        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_2d_1d),
            pages = listOf(Pages(page_name = context.getString(R.string.Multiplicationpage1), que2_str = "2", que2_type = "", que1_digit_type = 2, page_id = "1"),
                Pages(page_name = context.getString(R.string.Multiplicationpage2), que2_str = "3", que2_type = "", que1_digit_type = 2,page_id = "2"),
                Pages(page_name = context.getString(R.string.Multiplicationpage3), que2_str = "4", que2_type = "", que1_digit_type = 2,page_id = "3"),
                Pages(page_name = context.getString(R.string.Multiplicationpage4), que2_str = "", que2_type = "234", que1_digit_type = 2,page_id = "4"),
                Pages(page_name = context.getString(R.string.Multiplicationpage5), que2_str = "5", que2_type = "", que1_digit_type = 2,page_id = "5"),
                Pages(page_name = context.getString(R.string.Multiplicationpage6), que2_str = "6", que2_type = "", que1_digit_type = 2,page_id = "6"),
                Pages(page_name = context.getString(R.string.Multiplicationpage7), que2_str = "7", que2_type = "", que1_digit_type = 2,page_id = "7"),
                Pages(page_name = context.getString(R.string.Multiplicationpage8), que2_str = "", que2_type = "567", que1_digit_type = 2,page_id = "8"),
                Pages(page_name = context.getString(R.string.Multiplicationpage9), que2_str = "8", que2_type = "", que1_digit_type = 2,page_id = "9"),
                Pages(page_name = context.getString(R.string.Multiplicationpage10), que2_str = "9", que2_type = "", que1_digit_type = 2,page_id = "10"),
                Pages(page_name = context.getString(R.string.Multiplicationpage11),que2_str =  "", que2_type = "89", que1_digit_type = 2,page_id = "11"),
                Pages(page_name = context.getString(R.string.Multiplicationpage12), que2_str = "", que2_type = "1..9", que1_digit_type = 2,page_id = "12")
            )))

        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_3d_1d),
            pages = listOf(Pages(page_name = context.getString(R.string.Multiplicationpage13), que2_str = "2", que2_type = "", que1_digit_type = 3, page_id = "13"),
                Pages(page_name = context.getString(R.string.Multiplicationpage14), que2_str = "3", que2_type = "", que1_digit_type = 3,page_id = "14"),
                Pages(page_name = context.getString(R.string.Multiplicationpage15), que2_str = "4", que2_type = "", que1_digit_type = 3,page_id = "15"),
                Pages(page_name = context.getString(R.string.Multiplicationpage16), que2_str = "", que2_type = "234", que1_digit_type = 3,page_id = "16"),
                Pages(page_name = context.getString(R.string.Multiplicationpage17), que2_str = "5", que2_type = "", que1_digit_type = 3,page_id = "17"),
                Pages(page_name = context.getString(R.string.Multiplicationpage18), que2_str = "6", que2_type = "", que1_digit_type = 3,page_id = "18"),
                Pages(page_name = context.getString(R.string.Multiplicationpage19), que2_str = "7", que2_type = "", que1_digit_type = 3,page_id = "19"),
                Pages(page_name = context.getString(R.string.Multiplicationpage20), que2_str = "", que2_type = "567", que1_digit_type = 3,page_id = "20"),
                Pages(page_name = context.getString(R.string.Multiplicationpage21), que2_str = "8", que2_type = "", que1_digit_type = 3,page_id = "21"),
                Pages(page_name = context.getString(R.string.Multiplicationpage22), que2_str = "9", que2_type = "", que1_digit_type = 3,page_id = "22"),
                Pages(page_name = context.getString(R.string.Multiplicationpage23), que2_str = "", que2_type = "89", que1_digit_type = 3,page_id = "23"),
                Pages(page_name = context.getString(R.string.Multiplicationpage24), que2_str = "", que2_type = "1..9", que1_digit_type = 3,page_id = "24")
            )))

        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_2d_2d),
            pages = listOf(Pages(page_name = context.getString(R.string.Multiplicationpage25), que2_str = "", que2_type = "02", que1_digit_type = 2,page_id = "25"),
                Pages(page_name = context.getString(R.string.Multiplicationpage26), que2_str = "", que2_type = "03", que1_digit_type = 2,page_id = "26"),
                Pages(page_name = context.getString(R.string.Multiplicationpage27), que2_str = "", que2_type = "04", que1_digit_type = 2,page_id = "27"),
                Pages(page_name = context.getString(R.string.Multiplicationpage28), que2_str = "", que2_type = "05", que1_digit_type = 2,page_id = "28"),
                Pages(page_name = context.getString(R.string.Multiplicationpage29), que2_str = "", que2_type = "06", que1_digit_type = 2,page_id = "29"),
                Pages(page_name = context.getString(R.string.Multiplicationpage30), que2_str = "", que2_type = "07", que1_digit_type = 2,page_id = "30"),
                Pages(page_name = context.getString(R.string.Multiplicationpage31), que2_str = "", que2_type = "08", que1_digit_type = 2,page_id = "31"),
                Pages(page_name = context.getString(R.string.Multiplicationpage32), que2_str = "", que2_type = "09", que1_digit_type = 2,page_id = "32"),
                Pages(page_name = context.getString(R.string.Multiplicationpage33), que2_str = "ran2", que2_type = "", que1_digit_type = 2,page_id = "33")
            )))

        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_3d_2d),
            pages = listOf(Pages(page_name = context.getString(R.string.Multiplicationpage34), que2_str = "", que2_type = "02", que1_digit_type = 3,page_id = "34"),
                Pages(page_name = context.getString(R.string.Multiplicationpage35), que2_str = "", que2_type = "03", que1_digit_type = 3,page_id = "35"),
                Pages(page_name = context.getString(R.string.Multiplicationpage36), que2_str = "", que2_type = "04", que1_digit_type = 3,page_id = "36"),
                Pages(page_name = context.getString(R.string.Multiplicationpage37), que2_str = "", que2_type = "05", que1_digit_type = 3,page_id = "37"),
                Pages(page_name = context.getString(R.string.Multiplicationpage38), que2_str = "", que2_type = "06", que1_digit_type = 3,page_id = "38"),
                Pages(page_name = context.getString(R.string.Multiplicationpage39), que2_str = "", que2_type = "07", que1_digit_type = 3,page_id = "39"),
                Pages(page_name = context.getString(R.string.Multiplicationpage40), que2_str = "", que2_type = "08", que1_digit_type = 3,page_id = "40"),
                Pages(page_name = context.getString(R.string.Multiplicationpage41), que2_str = "", que2_type = "09", que1_digit_type = 3,page_id = "41"),
                Pages(page_name = context.getString(R.string.Multiplicationpage42), que2_str = "ran2", que2_type = "", que1_digit_type = 3,page_id = "42")
            )))

        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_3d_3d),
            pages = listOf(
                Pages(page_name = context.getString(R.string.Multiplicationpage43), que2_str = "ran3_1", que2_type = "", que1_digit_type = 30,page_id = "43"),
                Pages(page_name = context.getString(R.string.Multiplicationpage44), que2_str = "ran3_2", que2_type = "", que1_digit_type = 300,page_id = "44"),
                Pages(page_name = context.getString(R.string.Multiplicationpage45), que2_str = "ran3", que2_type = "", que1_digit_type = 3,page_id = "45")
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_4d_2d),
            pages = listOf(
                Pages(page_name = context.getString(R.string.Multiplicationpage46), que2_str = "ran2_1", que2_type = "", que1_digit_type = 40,page_id = "46"),
                Pages(page_name = context.getString(R.string.Multiplicationpage47), que2_str = "ran2_2", que2_type = "", que1_digit_type = 400,page_id = "47"),
                Pages(page_name = context.getString(R.string.Multiplicationpage48), que2_str = "ran2", que2_type = "", que1_digit_type = 4,page_id = "48")
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_4d_3d),
            pages = listOf(
                Pages(page_name = context.getString(R.string.Multiplicationpage49), que2_str = "ran3_1", que2_type = "", que1_digit_type = 40,page_id = "49"),
                Pages(page_name = context.getString(R.string.Multiplicationpage50), que2_str = "ran3_2", que2_type = "", que1_digit_type = 400,page_id = "50"),
                Pages(page_name = context.getString(R.string.Multiplicationpage51), que2_str = "ran3", que2_type = "", que1_digit_type = 4,page_id = "51")
            )))
        listCategory.add(CategoryPages(category_name = context.getString(R.string.multiplications_4d_4d),
            pages = listOf(
                Pages(page_name = context.getString(R.string.Multiplicationpage52), que2_str = "ran4_1", que2_type = "", que1_digit_type = 40,page_id = "52"),
                Pages(page_name = context.getString(R.string.Multiplicationpage53), que2_str = "ran4_2", que2_type = "", que1_digit_type = 400,page_id = "53"),
                Pages(page_name = context.getString(R.string.Multiplicationpage54), que2_str = "ran4", que2_type = "", que1_digit_type = 4,page_id = "54")
            )))

        return listCategory
    }
    // TODO Division Pages
    fun getDivisionPages(context: Context): MutableList<CategoryPages>{
        val listCategory: MutableList<CategoryPages> = arrayListOf()
        listCategory.add(CategoryPages(category_name = context.getString(R.string.divide_by_single_digit),
            pages = listOf(Pages(page_name = context.getString(R.string.Devidepage1), que2_str = "2", que2_type = "", page_id = "1"),
                Pages(page_name = context.getString(R.string.Devidepage2), que2_str = "3", que2_type = "",page_id = "2"),
                Pages(page_name = context.getString(R.string.Devidepage3), que2_str = "4", que2_type = "",page_id = "3"),
                Pages(page_name = context.getString(R.string.Devidepage4), que2_str = "", que2_type = "234",page_id = "4"),
                Pages(page_name = context.getString(R.string.Devidepage5), que2_str = "5", que2_type = "",page_id = "5"),
                Pages(page_name = context.getString(R.string.Devidepage6), que2_str = "6", que2_type = "",page_id = "6"),
                Pages(page_name = context.getString(R.string.Devidepage7), que2_str = "7", que2_type = "",page_id = "7"),
                Pages(page_name = context.getString(R.string.Devidepage8), que2_str = "", que2_type = "567",page_id = "8"),
                Pages(page_name = context.getString(R.string.Devidepage9), que2_str = "8", que2_type = "",page_id = "9"),
                Pages(page_name = context.getString(R.string.Devidepage10), que2_str = "9", que2_type = "",page_id = "10"),
                Pages(page_name = context.getString(R.string.Devidepage11), que2_str = "", que2_type = "89",page_id = "11"),
                Pages(page_name = context.getString(R.string.Devidepage12), que2_str = "", que2_type = "1..9",page_id = "12")
            )))

        listCategory.add(CategoryPages(category_name = context.getString(R.string.divide_by_two_digit),
            pages = listOf(
                Pages(page_name = context.getString(R.string.Devidepage22), que2_str = "", que2_type = "00",page_id = "22"),
                Pages(page_name = context.getString(R.string.Devidepage23), que2_str = "", que2_type = "01",page_id = "23"),
                Pages(page_name = context.getString(R.string.Devidepage13), que2_str = "", que2_type = "02",page_id = "13"),
                Pages(page_name = context.getString(R.string.Devidepage14), que2_str = "", que2_type = "03",page_id = "14"),
                Pages(page_name = context.getString(R.string.Devidepage15), que2_str = "", que2_type = "04",page_id = "15"),
                Pages(page_name = context.getString(R.string.Devidepage16), que2_str = "", que2_type = "05",page_id = "16"),
                Pages(page_name = context.getString(R.string.Devidepage17), que2_str = "", que2_type = "06",page_id = "17"),
                Pages(page_name = context.getString(R.string.Devidepage18), que2_str = "", que2_type = "07",page_id = "18"),
                Pages(page_name = context.getString(R.string.Devidepage19), que2_str = "", que2_type = "08",page_id = "19"),
                Pages(page_name = context.getString(R.string.Devidepage20), que2_str = "", que2_type = "09",page_id = "20"),
                Pages(page_name = context.getString(R.string.Devidepage21), que2_str = "ran2", que2_type = "",page_id = "21"),
                Pages(page_name = context.getString(R.string.Devidepage21), que2_str = "ran2_1", que2_type = "",page_id = "24"),
            )))

        return listCategory
    }

    fun generateSingleDigit(min: Int, max: Int): Int {// min = to
        return  Random().nextInt(max - min + 1) + min
//        val rand = kotlin.random.Random(System.nanoTime())
//        return (min..max).random(rand)
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

            listExercise.add(ExerciseList(question,answer))
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
            listExercise.add(ExerciseList(question,answer))
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
            listExercise.add(ExerciseList(question,answer))
        }

        return listExercise
    }

    fun generateChallengeModeQuestion(totalQuestion : Int, minNumber: Int, maxNumber : Int) : CustomChallengeData{
        val listQuestion: MutableList<CustomChallengeQuestion> = arrayListOf()
        val max = if (maxNumber == 2){
            99
        }else if (maxNumber == 3){
            999
        }else if (maxNumber == 4){
            9999
        }else if (maxNumber == 5){
            99999
        }else if (maxNumber == 6){
            999999
        }else if (maxNumber == 7){
            9999999
        }else{
            9
        }
        val min = if (minNumber == 2){
            10
        }else if (minNumber == 3){
            100
        }else if (minNumber == 4){
            1000
        }else if (minNumber == 5){
            10000
        }else if (minNumber == 6){
            100000
        }else if (minNumber == 7){
            1000000
        }else{
            1
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
            listExercise.add(ExerciseList(question,answer))
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
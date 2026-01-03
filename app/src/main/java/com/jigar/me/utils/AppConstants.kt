package com.jigar.me.utils

object AppConstants {
    internal const val DB_NAME = "kotlin_basic.db"
    internal const val DB_NAME_NEW = "VedaaviAbacus.db"
    internal const val PREF_NAME = "kotlin_basic_pref"
    internal const val YOUTUBE_URL = "https://www.youtube.com/channel/UC9MSzIbLkuzffqepgOqBLhw"

    const val BLINK_ICON_ANIMATION_DURATION: Long = 600
    const val BLINK_ICON_ANIMATION_ALPHA: Float = 0.1F

    const val screenWidthDp = "screenWidthDp"
    const val HAS_NOTCH = "has_notch"
    const val NOTCH_HEIGHT = "notch_height"
    const val BOTTOM_NAV_HEIGHT = "bottom_nav_height"

    const val NUMBER_PUZZLE_SAVE = "number_puzzle_save"
    const val NUMBER_PUZZLE_CURRENT_SCORE = "number_puzzle_current_score"
    const val NUMBER_PUZZLE_BEST_SCORE = "number_puzzle_best_score"
    const val PAGINATION_RECORDS = 20

    const val PREF_KEY_APP_VERSION_CODE = "pref_previous_version_code"
    annotation class ExamType {
        companion object {
            var exam_Type_Number = "Number"
            var exam_Type_Addition = "Addition"
            var exam_Type_Subtraction = "Subtraction"
            var exam_Type_Multiplication = "Multiplication"
            var exam_Type_Division = "Division"

            val exam_Level_Beginner = "Beginner"
            val exam_Level_Intermediate = "Intermediate"
            val exam_Level_Expert = "Expert"

            val exam_Que_type_question = "question"
            val exam_Que_type_object = "object"
            val exam_Que_type_abacus = "abacus"

            val type_Exam = "Exam"
            val type_Exercise = "Exercise"
            val type_CCM = "CCM"
            val type_CustomChallengeMode = "Custom Challenge Mode"
            val type_Practice_Set = "Practice Set of Formal Exam" // user only display name // formal_answer
        }
    }
    annotation class LoginData {
        companion object {
            var LoginTypesStudent = "student"
            var LoginCompleteStep2 = "STEP_2"
            var LoginCompleteStep1 = "STEP_1"
            var LoginCountry_IN = "IN"
        }
    }
    annotation class OTPScreen {
        companion object {
            var forgotPassword = "FORGOT_PASSWORD"
            var signupStep = "SIGN_UP"
        }
    }
    annotation class APP_PLAN_DATA {
        companion object {
            var Currency_INR = "INR"
            var Currency_USD = "USD"
            var Symbol_INR = "₹"
            var Symbol_USD = "$"
            var PLAN_TERM_MONTH_1 = "1_month"
            var PLAN_TERM_MONTH_3 = "3_month"
            var PLAN_TERM_MONTH_6 = "6_month"
            var PLAN_TERM_MONTH_12 = "12_month"
        }
    }
    annotation class CCM {
        companion object {
            var totalQuestion = "totalQuestion"
            var questionGap = "questionGap"
            var questionMinLength = "questionMinLength"
            var questionMaxLength = "questionMaxLength"
            var isQuestionSpeak = "isQuestionSpeak"
            var isQuestionShowNumber = "isQuestionShowNumber"
            var isQuestionShowWord = "isQuestionShowWord"
        }
    }

    annotation class FirebaseEvents {
        companion object {
            var appInstallFrom = "app_install_from"
        }
    }

    annotation class RemoteConfig {
        companion object {
            var videoList = "video"
            var displayPlanList = "display_plan"
            var displayMenuList = "display_menu"
            var supportEmail = "supportEmail"
            var newVersionNotes = "newVersionNotes"
            var bulkLogin = "bulkLogin"
            var privacyPolicyUrl = "privacyPolicyUrl"
            var versionCode = "versionCode"
            var discountPer = "discount_per"
            var discountPerLifeTime = "discount_per_lifetime"
        }
    }

    annotation class AbacusScreen {
        companion object {
            const val screenTypeFreeMode = "FreeMode"
            const val screenTypeAbacusPractice = "AbacusPractice"
            const val screenTypeSettingPreview = "SettingPreview"
            const val screenTypeCCM = "CCM"



            const val isFreeMode = "isFreeMode"
            const val isResetEveryTime = "isResetEveryTime"
            const val isRandomNumber = "isRandomNumber"
            const val fromNumber = "fromNumber"
            const val toNumber = "toNumber"
            const val currentReachNumber = "currentReachNumber"
        }
    }
    annotation class Settings {
        companion object {
            const val Setting_direction = "Setting_direction"
            const val Setting_sound = "Setting_sound"
            const val Setting_NumberPuzzleVolume = "Setting_NumberPuzzleVolume"
            const val Setting__hint_sound = "Setting_hint_sound"
            const val Setting_display_abacus_number = "Setting_display_abacus_number"
            const val Setting_display_help_message = "Setting_display_help_message"
            const val Setting_left_hand = "Setting_left_hand"
            const val Setting_bg_music_volume = "Setting_bg_music_volume"
            const val Setting_bg_music_volume_default = 5

            var Theam = "Theam"
            var TheamTempView = "TheamTempView"
            const val theam_Poligon_default = "Poligon" // black
            const val theam_Poligon_Rainbow = "poligon_rainbow"
            const val theam_Poligon_Blue = "poligon_blue"
            const val theam_Poligon_Blue_Sky = "poligon_blue_sky"
            const val theam_Poligon_Orange = "poligon_orange"
            const val theam_Poligon_Purple = "poligon_purple"
            const val theam_Poligon_Pink = "poligon_pink"
            const val theam_Poligon_Yellow = "poligon_yellow"
            const val theam_Poligon_Red = "poligon_red"
            const val theam_Poligon_Green = "poligon_green"
            const val theam_Poligon_Tint = "poligon_tint"
            const val theam_Poligon_Silver = "poligon_silver"
            const val theam_Poligon_Brown = "poligon_brown"
            const val theam_Default = theam_Poligon_Rainbow
        }

    }

    annotation class HomeClicks {
        companion object {
            const val Menu_Math_Game = "math_game"
            const val Menu_Abacus_Free_Mode = "abacus_free_mode"
            const val Menu_Practice_Abacus = "practice_abacus"
            const val Menu_My_Account = "my_account"
            const val Menu_Abacus_Exercise = "abacus_exercise"
            const val Menu_Exam = "exam"
            const val Menu_CCM = "ccm"
            const val Menu_Video_Tutorial = "video_tutorial"
            const val Menu_Purchase_Store = "purchase_store"
            const val Menu_Settings = "setting"
        }
    }

    annotation class extras_Comman {
        companion object {
            var FROM = "from"
            var Title = "Title"
            val data = "data"
            var AbacusTypeNumber = "Number"
            var AbacusTypeAdditionSubtraction = "AdditionSubtraction"
            var AbacusTypeMultiplication = "Multiplication"
            var AbacusTypeDivision = "Division"

            var From = "From"

            var examGivenCount = "examGivenCount"

            var examResult = "ExamResult"
            var examAbacusType = "ExamAbacusType"

            var type = "type"
            var order = "order"

            var tour = "tour"

            var Level = "LevelNews" // level page store
            var examLevel = "examLevels" // exam store

            var typeBulkLogin = "Need Login"
            var typeNeedHelp = " "
        }
    }

    annotation class APIStatus {
        companion object {
            var SUCCESS = "SUCCESS"
            var ERROR = "ERROR"
            var ERROR_CODE_USER_NOT_VERIFIED = "USER_NOT_VERIFIED"
            var ERROR_CODE_OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER = "OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER"
            var ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER = "THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER"
        }
    }


    // TODO api param


    interface apiParams {
        companion object {
            const val hint = "hint"
            const val file = "file"
            const val level = "level"
            const val type = "type"
            const val answerStepByStep = "step_by_step_answer"
            const val answerFinalAnswer = "final_answer"
            const val answerFormalExam = "formal_answer"


        }
    }
    interface DBParam {
        companion object {
            const val table_level = "level"
            const val table_category = "category"
            const val table_pages = "pages"
            const val table_sets = "sets"
            const val table_set_progress = "setProgress"
            const val table_abacus = "abacus"
        }
    }


}
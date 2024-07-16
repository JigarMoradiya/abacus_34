package com.jigar.me.utils

object AppConstants {
    internal const val DB_NAME = "kotlin_basic.db"
    internal const val PREF_NAME = "kotlin_basic_pref"
    internal const val YOUTUBE_URL = "https://www.youtube.com/channel/UC9MSzIbLkuzffqepgOqBLhw"
    internal const val TEMP_BASE_URL = "https://abacuspro.in/backend/index.php/Api/"
//    internal const val TEMP_BASE_URL = "https://www.sdd-production.com/jigar/backend/index.php/Api/"

    const val BLINK_ICON_ANIMATION_DURATION: Long = 700
    const val BLINK_ICON_ANIMATION_ALPHA: Float = 0.2F

    const val NUMBER_PUZZLE_SAVE = "number_puzzle_save"
    const val NUMBER_PUZZLE_CURRENT_SCORE = "number_puzzle_current_score"
    const val NUMBER_PUZZLE_BEST_SCORE = "number_puzzle_best_score"
    const val PAGINATION_RECORDS = 20
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
            val type_Practise = "Practise"
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
            var MaterialDownloaded = "abacus_material_download"

            var InAppPurchase = "abacus_purchase"
            var InAppPurchaseSKU = "abacus_sku"
            var InAppPurchaseOrderId = "abacus_order_id"

            var DailyExam = "abacus_daily_exam"
            var DailyExamLevel = "exam_level"

            var NumberPuzzleSequence = "number_puzzle_squence"

            var deviceId = "deviceId"
        }
    }
    annotation class Purchase {
        companion object {
            var Purchase_limit = 9999
            var Purchase_limit_free = 20

            var AdsShow = "N"
            var AdsShowCount = "AdsShowCount"
            var AdsShowNumberPuzzleStep = 20

            var Purchase_All = "Purchase_All"
            var Purchase_Ads = "Purchase_Ads"
            var Purchase_Toddler_Single_digit_level1 = "Purchase_Toddler_Single_digit_level1"
            var Purchase_Add_Sub_level2 = "Purchase_Add_Sub_level2"
            var Purchase_Mul_Div_level3 = "Purchase_Mul_Div_level3"

            var Purchase_Material_Maths = "Purchase_Material_Maths"
            var Purchase_Material_Nursery = "Purchase_Material_Nursery"

        }
    }

    annotation class RemoteConfig {
        companion object {
            var videoList = "video"
            var displayPlanList = "display_plan"
            var supportEmail = "supportEmail"
            var newVersionNotes = "newVersionNotes"
            var bulkLogin = "bulkLogin"
            var privacyPolicyUrl = "privacyPolicyUrl"
            var versionCode = "versionCode"
        }
    }
    annotation class AbacusProgress {
        companion object {
            var PREF_PAGE_SUM = "pageSum"
            var CompleteAbacusPos = "CompleteAbacusPos"

            var TrackFetch = "TrackFetch"

            // firebase database field
//            var Track = "TrackJigar"
            var Track = "TrackNew"
            var Position = "Position"

            var Settings = "Settings"
            var baseUrl = "baseUrl"
            var iPath = "iPath"

            var Ads = "Ads"
            var isAdmob = "isAdmob"
            var resetImage = "resetImage"
        }
    }

    annotation class Settings {
        companion object {
            const val Setting_sound = "Setting_sound"
            const val Setting_NumberPuzzleVolume = "Setting_NumberPuzzleVolume"
            const val Setting__hint_sound = "Setting_hint_sound"
            const val Setting_display_abacus_number = "Setting_display_abacus_number"
            const val Setting_display_help_message = "Setting_display_help_message"
            const val Setting_hide_table = "Setting_hide_table"
            const val Setting_auto_reset_abacus = "Setting_auto_reset_abacus"
            const val Setting_left_hand = "Setting_left_hand"
            const val Setting_bg_music_volume = "Setting_bg_music_volume"
            const val Setting_bg_music_volume_default = 5
            var Setting_answer = "Setting_answer"
            const val Setting_answer_Step = "Step"
            const val Setting_answer_Final = "Final"
            const val Setting_answer_with_tools = "tools"

            var abacus_colorful = "abacus_colorful"

            var isSetTheam = "isSetTheam"
            var isHomeTourWatch = "isHomeTourWatch"
            var appOpenCount = "appOpenCount"
            var isFreeModeTourWatch = "isFreeModeTourWatch"
            var Theam = "Theam"
            var TheamTempView = "TheamTempView"
            const val theam_Poligon_default = "Poligon" // black
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
            const val theam_Default = theam_Poligon_default
            const val theam_face = "Eyes"
            const val theam_diamond = "diamond"
            const val theam_garnet = "garnet"
            const val theam_Egg = "Egg"
            const val theam_shape = "Shape"
            const val theam_Star = "Star"

            var Free_Mode_Beads_Move_Count = "free_mode_beads_move_count"
            var Free_Mode_Beads_Move_Count_Limit = 70
            var SW_FreeMode = "SW_FreeMode"
            var SW_DecimalMode = "SW_DecimalMode"
            var SW_Random = "SW_Random"
            var SW_Reset = "SW_Reset"

            var Toddler_No = "Toddler_Numbers_new"
            var Toddler_No_Count = "Toddler_Number_Count"
            var SW_Range_min = "SW_Range_min_values_new"
            var SW_Range_max = "SW_Range_max_values_new"
        }

    }

    annotation class HomeClicks {
        companion object {
            const val Menu_My_Profile = 1
            const val Menu_Addition_Subtraction = 2
            const val Menu_Formulas = 3
            const val Menu_Starter = 4
            const val Menu_Number = 5
            const val Menu_AboutUs = 6
            const val Menu_Multiplication = 7
            const val Menu_Division = 8
            const val Menu_Exercise = 9
            const val Menu_Subscribe = 10
            const val Menu_DailyExam = 11
            const val Menu_CustomChallengeMode = 12
            const val Menu_PractiseMaterial = 13
            const val Menu_Setting = 14

            const val Menu_Click_Youtube = 16
            const val Menu_Share = 18

            const val Menu_Number_Puzzle = 20

            const val OtherApp_Abacus = 101
            const val OtherApp_Number = 102
            const val OtherApp_Sudoku = 103

        }
    }

    annotation class extras_Comman {
        companion object {
            var FROM = "from"
            var Title = "Title"
            var AbacusType = "AbacusType"
            val data = "data"
            val previousAbacusData = "previousAbacusData"
            var AbacusTypeNumber = "Number"
            var AbacusTypeAdditionSubtraction = "AdditionSubtraction"
            var AbacusTypeMultiplication = "Multiplication"
            var AbacusTypeDivision = "Division"

            var Que2_str = "Que2_str"
            var Que2_type = "Que2_type"
            var Que1_digit_type = "Que1_digit_type"

            var From = "From"
            var To = "To"
            var isType_random = "isType_random"

            var examGivenCount = "examGivenCount"

            var examLevelLable = "examLevelLable"
            var examResult = "ExamResult"
            var examAbacusType = "ExamAbacusType"

            var DownloadType_Maths = "maths"
            var DownloadType_Nursery = "nursery"
            var DownloadType = "downloadType"

            var type = "type"
            var order = "order"

            var tour = "tour"

            var Level = "LevelNews" // level page store
            var examLevel = "examLevels" // exam store

            var typeBulkLogin = "Need Login"
            var typeNeedHelp = "Need Help"
        }
    }

    annotation class APIStatus {
        companion object {
            var SUCCESS = "SUCCESS"
            var ERROR = "ERROR"
            var ERROR_CODE_USER_NOT_VERIFIED = "USER_NOT_VERIFIED"
            var ERROR_CODE_OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER = "OTHER_STUDENT_IS_ASSOCIATED_WITH_THIS_ORDER"
            var ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER = "THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER"
            var PURCHASE_ERROR_CODE = "PURCHASE_ERROR_CODE"
        }
    }


    // TODO api param
    interface apiHeader {
        companion object {
            const val consumer_key = "consumer-key"
            const val consumer_secret = "consumer-secret"
            const val consumer_nonce = "consumer-nonce"
        }
    }

    interface apiParams {
        companion object {
            const val levelId = "level_id"
            const val pageId = "page_id"
            const val limit = "limit"
            const val total = "total"
            const val hint = "hint"
            const val file = "file"
            const val level = "level"
            const val type = "type"
        }
    }


}
package com.jigar.me.utils

//Application Constants Values
object Constants {

    const val CLICK_TYPE_LONG = "long"
    const val CLICK_TYPE_DETAIL = "detail"
    const val NOTIFICATION_PERMISSION = "notification_permission"
    const val avatarId = "avatarId"
    const val childName = "childName"
    const val PLAN_ASSIGN_FROM_ADMIN_DATA = "plan_assign_from_admin"
    const val PLAN_ASSIGN_FROM_ADMIN_FOR_REVIEW = "FOR_REVIEW"
    const val PLAN_ASSIGN_FROM_ADMIN_ON_REQUEST = "ON_REQUEST"
    const val APP_REVIEW_DATA = "app_reviews"
    const val APP_REVIEW_STATUS_REJECT = "REJECT"
    const val APP_REVIEW_STATUS_APPROVE = "APPROVE"

    const val appLanguage = "appLanguage"
    const val appLanguage_arebic = "ar"

    var banner_rate_us = "banner_rate_us"
    var banner_share = "banner_share"
    var banner_bulk_login = "banner_bulk_login"
//    var banner_purchase = "banner_purchase"
//    var banner_offer = "banner_offer"

    // The ids a push sends as `type`. The same ids are used by iOS (AppNavigationNotificationType) and the admin panel /
    // backend (use-cases/v2/push-notifications/notification-types.js); keep all three in step.
    const val notificationTypeHome = "home"
    const val notificationTypeFreeMode = "free_mode"
    const val notificationTypeExam = "exam"
    const val notificationTypeCustomChallenge = "custom_challenge"
    const val notificationTypeExercise = "exercise"
    const val notificationTypeNumberSequence = "number_sequence"
    const val notificationTypeSettings = "settings"
    const val notificationTypePurchase = "purchase"
    const val notificationTypeYoutubeVideo = "youtube_video"
    const val notificationTypeYoutubeChannel = "youtube_channel"
    const val notificationTypeRateApp = "rate_app"
    const val notificationTypeShareApp = "share_app"
    const val notificationTypeGameZone = "game_zone"
    const val notificationTypeCrossMath = "cross_math"
    const val notificationTypeSudoku = "sudoku"
    const val notificationTypeMathPyramid = "math_pyramid"
    const val notificationTypeTargetNumber = "target_number"
    const val notificationTypeBalloonPop = "balloon_pop"
    const val notificationTypeSpeedCompare = "speed_compare"
    const val notificationTypeMissingOperator = "missing_operator"
    const val notificationTypeMagicSquare = "magic_square"
    const val notificationTypeCalcudoku = "calcudoku"
    const val notificationTypeMerge2048 = "merge_2048"
    const val notificationTypeEquationMatch = "equation_match"
    const val notificationTypeNumberPath = "number_path"
    const val notificationTypeTrueFalse = "true_false"
    const val notificationTypePlaceValue = "place_value"
    const val notificationTypeClockMaster = "clock_master"
    const val notificationTypeMathBingo = "math_bingo"
    const val notificationTypeKakuro = "kakuro"
    const val notificationTypeNumberDetective = "number_detective"
    const val notificationTypeNumberSnake = "number_snake"
    const val notificationTypePractice = "practice"
    const val notificationTypeLevel1 = "level_1"
    const val notificationTypeLevel2 = "level_2"
    const val notificationTypeLevel3 = "level_3"
    const val notificationTypeLevel4 = "level_4"

    const val bottomSheetWidthBaseOnRatio5 = 5

    const val examLevelMaxRecordHistory = 50

    const val EXAM_CLICK_ON_CORRECT_ANSWER_ANIMATION_DURATION: Long = 700
    const val EXAM_CLICK_ON_CORRECT_ANSWER_ANIMATION_ALPHA: Float = 0.2F


    var Que = "Que"
    var Sign = "Sign"

    var last_sync_time = "last_sync_time_new"
    var last_sync_default_time = "2023-07-01T12:00:00.000Z"
}
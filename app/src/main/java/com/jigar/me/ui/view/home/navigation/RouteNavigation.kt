package com.jigar.me.ui.view.home.navigation

sealed class RouteNavigation(val route: String) {

    object Splash : RouteNavigation("Splash")

    object Home : RouteNavigation("Home")

    object AbacusFreeMode : RouteNavigation("AbacusFreeMode")

    object LevelCategory : RouteNavigation("LevelCategory/{levelId}") {
        fun levelCategory(levelId: String): String = "LevelCategory/$levelId"
    }

    object Set : RouteNavigation("AbacusSet/{levelCategoryId}/{name}") {
        fun abacusSet(levelCategoryId: String,name : String): String = "AbacusSet/$levelCategoryId/$name"
    }

    object AbacusDoPractice : RouteNavigation("AbacusDoPractice/{setId}/{saveResults}") {
        fun doPractice(setId: String, saveResults: Boolean = true): String = "AbacusDoPractice/$setId/$saveResults"
    }

    object AbacusList : RouteNavigation("AbacusList/{setId}") {
        fun list(setId: String): String = "AbacusList/$setId"
    }

    object MathGameZone : RouteNavigation("MathGameZone")

    object NumberSequencePuzzleHome : RouteNavigation("NumberSequencePuzzleHome")
    object NumberSequencePuzzlePlay : RouteNavigation("NumberSequencePuzzlePlay/{type}") {
        fun play(type: Int): String = "NumberSequencePuzzlePlay/$type"
    }

    object SudokuHome : RouteNavigation("SudokuHome")
    object SudokuPlay : RouteNavigation("SudokuPlay/{size}/{difficulty}/{isNewPuzzle}") {
        fun play(size: String, difficulty: String, isNewPuzzle: Boolean): String =
            "SudokuPlay/$size/$difficulty/$isNewPuzzle"
    }

    object MathPyramidHome : RouteNavigation("MathPyramidHome")
    object MathPyramidPlay : RouteNavigation("MathPyramidPlay/{levels}/{difficulty}") {
        fun play(levels: Int, difficulty: String): String = "MathPyramidPlay/$levels/$difficulty"
    }

    object TargetNumberHome : RouteNavigation("TargetNumberHome")
    object TargetNumberPlay : RouteNavigation("TargetNumberPlay/{target_level}/{target_diff}") {
        fun play(level: Int, diff: String): String = "TargetNumberPlay/$level/$diff"
    }

    object Settings : RouteNavigation("Settings")

    object Purchase : RouteNavigation("Purchase")

    object YoutubeVideo : RouteNavigation("YoutubeVideo")

    object WhatsLearning : RouteNavigation("WhatsLearning")

    object MyAccount : RouteNavigation("MyAccount")

    object ReportHistory : RouteNavigation("ReportHistory")

    object FAQs : RouteNavigation("FAQs")

    object Exercise : RouteNavigation("Exercise")

    object ExamHome : RouteNavigation("ExamHome")
    object ExamPlay : RouteNavigation("ExamPlay/{saveResults}") {
        fun play(saveResults: Boolean = true): String = "ExamPlay/$saveResults"
    }

    object CCMHome : RouteNavigation("CCMHome")
    object CCMPlay : RouteNavigation("CCMPlay")

    object CredentialsLoginLandscape : RouteNavigation("CredentialsLoginLandscape")

    object TodayTableHome : RouteNavigation("TodayTableHome/{tableNumber}") {
        fun create(tableNumber: Int) = "TodayTableHome/$tableNumber"
    }
    object TodayTableDrill : RouteNavigation("TodayTableDrill/{tableNumber}") {
        fun create(tableNumber: Int) = "TodayTableDrill/$tableNumber"
    }
    object TodayTableFlashcard : RouteNavigation("TodayTableFlashcard/{tableNumber}") {
        fun create(tableNumber: Int) = "TodayTableFlashcard/$tableNumber"
    }
    object TodayTableFillBlank : RouteNavigation("TodayTableFillBlank/{tableNumber}") {
        fun create(tableNumber: Int) = "TodayTableFillBlank/$tableNumber"
    }
}

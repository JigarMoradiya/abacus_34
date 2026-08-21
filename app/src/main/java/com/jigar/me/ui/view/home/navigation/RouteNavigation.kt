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
    object TrophyRoom : RouteNavigation("TrophyRoom")

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

    object BalloonPopHome : RouteNavigation("BalloonPopHome")
    object BalloonPopPlay : RouteNavigation("BalloonPopPlay/{balloon_diff}") {
        fun play(diff: String): String = "BalloonPopPlay/$diff"
    }

    object SpeedCompareHome : RouteNavigation("SpeedCompareHome")
    object SpeedCompareDuel : RouteNavigation("SpeedCompareDuel/{speed_duel_diff}") {
        fun play(diff: String): String = "SpeedCompareDuel/$diff"
    }
    object SpeedComparePlay : RouteNavigation("SpeedComparePlay/{speed_diff}") {
        fun play(diff: String): String = "SpeedComparePlay/$diff"
    }

    object MissingOperatorHome : RouteNavigation("MissingOperatorHome")
    object MissingOperatorPlay : RouteNavigation("MissingOperatorPlay/{missing_op_diff}") {
        fun play(diff: String): String = "MissingOperatorPlay/$diff"
    }

    object MagicSquareHome : RouteNavigation("MagicSquareHome")
    object MagicSquarePlay : RouteNavigation("MagicSquarePlay/{magic_square_diff}") {
        fun play(diff: String): String = "MagicSquarePlay/$diff"
    }

    object CalcudokuHome : RouteNavigation("CalcudokuHome")
    object CalcudokuPlay : RouteNavigation("CalcudokuPlay/{calcudoku_diff}") {
        fun play(diff: String): String = "CalcudokuPlay/$diff"
    }

    object Merge2048Home : RouteNavigation("Merge2048Home")
    object Merge2048Play : RouteNavigation("Merge2048Play/{merge2048_diff}") {
        fun play(diff: String): String = "Merge2048Play/$diff"
    }

    object EquationMatchHome : RouteNavigation("EquationMatchHome")
    object EquationMatchPlay : RouteNavigation("EquationMatchPlay/{equation_match_diff}") {
        fun play(diff: String): String = "EquationMatchPlay/$diff"
    }

    object CrossMathHome : RouteNavigation("CrossMathHome")
    object CrossMathPlay : RouteNavigation("CrossMathPlay/{cross_math_diff}/{cross_math_level}") {
        fun play(diff: String, level: Int): String = "CrossMathPlay/$diff/$level"
    }

    object NumberPathHome : RouteNavigation("NumberPathHome")
    object NumberPathPlay : RouteNavigation("NumberPathPlay/{number_path_diff}/{number_path_level}") {
        fun play(diff: String, level: Int): String = "NumberPathPlay/$diff/$level"
    }

    object TrueFalseHome : RouteNavigation("TrueFalseHome")
    object TrueFalsePlay : RouteNavigation("TrueFalsePlay/{true_false_diff}") {
        fun play(diff: String): String = "TrueFalsePlay/$diff"
    }

    object PlaceValueHome : RouteNavigation("PlaceValueHome")
    object PlaceValuePlay : RouteNavigation("PlaceValuePlay/{place_value_diff}") {
        fun play(diff: String): String = "PlaceValuePlay/$diff"
    }

    object ClockMasterHome : RouteNavigation("ClockMasterHome")
    object ClockMasterPlay : RouteNavigation("ClockMasterPlay/{clock_master_diff}") {
        fun play(diff: String): String = "ClockMasterPlay/$diff"
    }

    object MathBingoHome : RouteNavigation("MathBingoHome")
    object MathBingoPlay : RouteNavigation("MathBingoPlay/{math_bingo_diff}") {
        fun play(diff: String): String = "MathBingoPlay/$diff"
    }

    object KakuroHome : RouteNavigation("KakuroHome")
    object KakuroPlay : RouteNavigation("KakuroPlay/{kakuro_diff}/{kakuro_level}") {
        fun play(diff: String, level: Int): String = "KakuroPlay/$diff/$level"
    }

    object NumberDetectiveHome : RouteNavigation("NumberDetectiveHome")
    object NumberDetectivePlay : RouteNavigation("NumberDetectivePlay/{number_detective_diff}") {
        fun play(diff: String): String = "NumberDetectivePlay/$diff"
    }

    object NumberSnakeHome : RouteNavigation("NumberSnakeHome")
    object NumberSnakePlay : RouteNavigation("NumberSnakePlay/{number_snake_diff}") {
        fun play(diff: String): String = "NumberSnakePlay/$diff"
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

    object Level1Home : RouteNavigation("Level1Home")
    object Level1Lesson : RouteNavigation("Level1Lesson/{lessonId}") {
        fun create(lessonId: Int) = "Level1Lesson/$lessonId"
    }
    object Level1Learn : RouteNavigation("Level1Learn/{lessonId}") {
        fun create(lessonId: Int) = "Level1Learn/$lessonId"
    }
    object Level1Practice : RouteNavigation("Level1Practice/{lessonId}") {
        fun create(lessonId: Int) = "Level1Practice/$lessonId"
    }
    object Level1Quiz : RouteNavigation("Level1Quiz/{lessonId}") {
        fun create(lessonId: Int) = "Level1Quiz/$lessonId"
    }
    object Level2Home : RouteNavigation("Level2Home")
    object Level2Lesson : RouteNavigation("Level2Lesson/{lessonId}") {
        fun create(lessonId: Int) = "Level2Lesson/$lessonId"
    }
    object Level2FormulaRef : RouteNavigation("Level2FormulaRef/{groupId}") {
        fun create(groupId: Int = 1) = "Level2FormulaRef/$groupId"
    }
    object Level2Learn : RouteNavigation("Level2Learn/{lessonId}") {
        fun create(lessonId: Int) = "Level2Learn/$lessonId"
    }
    object Level2Practice : RouteNavigation("Level2Practice/{lessonId}") {
        fun create(lessonId: Int) = "Level2Practice/$lessonId"
    }
    object Level2Quiz : RouteNavigation("Level2Quiz/{lessonId}") {
        fun create(lessonId: Int) = "Level2Quiz/$lessonId"
    }
    object Level3Home : RouteNavigation("Level3Home")
    object Level3Config : RouteNavigation("Level3Config/{modeOrdinal}") {
        fun create(modeOrdinal: Int) = "Level3Config/$modeOrdinal"
    }
    object Level3Guided : RouteNavigation("Level3Guided/{terms}/{digits}/{flashMs}/{autoAbacus}") {
        fun create(terms: Int, digits: Int, flashMs: Int, autoAbacus: Boolean) =
            "Level3Guided/$terms/$digits/$flashMs/$autoAbacus"
    }
    object Level3Anzan : RouteNavigation("Level3Anzan/{modeOrd}/{terms}/{digits}/{flashMs}") {
        fun create(modeOrd: Int, terms: Int, digits: Int, flashMs: Int) =
            "Level3Anzan/$modeOrd/$terms/$digits/$flashMs"
    }
    object Level3SpeedDrill : RouteNavigation("Level3SpeedDrill/{digits}/{timeLimitSecs}") {
        fun create(digits: Int, timeLimitSecs: Int) = "Level3SpeedDrill/$digits/$timeLimitSecs"
    }
    object Level3FlashPicker : RouteNavigation("Level3FlashPicker")
    object Level3FlashPlay : RouteNavigation("Level3FlashPlay/{diffIndex}") {
        fun create(diffIndex: Int) = "Level3FlashPlay/$diffIndex"
    }
    object Level4TablePicker : RouteNavigation("Level4TablePicker")

    object TableMixPicker : RouteNavigation("TableMixPicker")
    object TableMixHome : RouteNavigation("TableMixHome/{tables}") {
        fun create(tables: List<Int>) = "TableMixHome/${tables.joinToString(",")}"
    }
    object TableMixDrill : RouteNavigation("TableMixDrill/{tables}") {
        fun create(tables: List<Int>) = "TableMixDrill/${tables.joinToString(",")}"
    }
    object TableMixFlashcard : RouteNavigation("TableMixFlashcard/{tables}") {
        fun create(tables: List<Int>) = "TableMixFlashcard/${tables.joinToString(",")}"
    }
    object TableMixFillBlank : RouteNavigation("TableMixFillBlank/{tables}") {
        fun create(tables: List<Int>) = "TableMixFillBlank/${tables.joinToString(",")}"
    }
}

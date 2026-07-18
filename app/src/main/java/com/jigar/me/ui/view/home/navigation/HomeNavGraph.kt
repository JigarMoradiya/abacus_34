package com.jigar.me.ui.view.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jigar.me.ui.view.home.screens.activities.ccm.home.CCMHomeRoute
import com.jigar.me.ui.view.home.screens.activities.ccm.play.CCMPlayRoute
import com.jigar.me.ui.view.home.screens.activities.exam.home.ExamHomeRoute
import com.jigar.me.ui.view.home.screens.activities.exam.play.ExamPlayRoute
import com.jigar.me.ui.view.home.screens.activities.exercise.ExerciseRoute
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.AbacusDoPracticeRoute
import com.jigar.me.ui.view.home.screens.abacus_practice.abacus_list.AbacusListRoute
import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.SetScreen
import com.jigar.me.ui.view.home.screens.home.HomeScreen
import com.jigar.me.ui.view.home.screens.my_account.FAQsRoute
import com.jigar.me.ui.view.home.screens.my_account.MyAccountRoute
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountViewModel
import com.jigar.me.ui.view.login.screens.login.LoginWithCredentialsLandscapeScreen
import com.jigar.me.ui.view.home.screens.reports.ReportHistoryRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.MathGameZoneScreenRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.MathPyramidHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.math_pyramid.MathPyramidPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.NumberSequencePuzzleHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.number_sequence_puzzle.NumberSequencePuzzlePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.SudokuHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.SudokuPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.TargetNumberHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.target_number.TargetNumberPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.BalloonPopHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.balloon_pop.BalloonPopPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.SpeedCompareHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.SpeedComparePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.MissingOperatorHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.MissingOperatorPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.MagicSquareHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.MagicSquarePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.CalcudokuHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.CalcudokuPlayRoute
import com.jigar.me.ui.view.home.screens.purchase.PurchaseScreenRoute
import com.jigar.me.ui.view.home.screens.settings.SettingsScreenRoute
import com.jigar.me.ui.view.home.screens.whats_learning.WhatsLearningScreenRoute
import com.jigar.me.ui.view.home.screens.youtube.YoutubeVideoScreenRoute
import com.jigar.me.ui.view.home.screens.abacus_free_mode.components.AbacusFreeModeScreen
import com.jigar.me.ui.view.home.screens.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.LevelCategoryScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.login.screens.splash.SplashScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TodayTableHomeScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TableDrillScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TableFlashcardScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TableFillBlankScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TablePickerScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TableMixPickerScreen
import com.jigar.me.ui.view.home.screens.levels.level4.TableMixHomeScreen
import com.jigar.me.ui.view.home.screens.levels.level1.Level1HomeScreen
import com.jigar.me.ui.view.home.screens.levels.level1.Level1LessonScreen
import com.jigar.me.ui.view.home.screens.levels.level1.learn.Level1LearnScreen
import com.jigar.me.ui.view.home.screens.levels.level1.practice.Level1PracticeScreen
import com.jigar.me.ui.view.home.screens.levels.level1.quiz.Level1QuizScreen
import com.jigar.me.ui.view.home.screens.levels.level2.Level2ChapterType
import com.jigar.me.ui.view.home.screens.levels.level2.Level2HomeScreen
import com.jigar.me.ui.view.home.screens.levels.level2.Level2LessonScreen
import com.jigar.me.ui.view.home.screens.levels.level2.level2Chapters
import com.jigar.me.ui.view.home.screens.levels.level2.formula.Level2FormulaScreen
import com.jigar.me.ui.view.home.screens.levels.level2.learn.Level2LearnScreen
import com.jigar.me.ui.view.home.screens.levels.level2.practice.Level2PracticeScreen
import com.jigar.me.ui.view.home.screens.levels.level2.quiz.Level2QuizScreen
import com.jigar.me.ui.view.home.screens.levels.level3.L3Mode
import com.jigar.me.ui.view.home.screens.levels.level3.L3Config
import com.jigar.me.ui.view.home.screens.levels.level3.l3FlashDifficulties
import com.jigar.me.ui.view.home.screens.levels.level3.home.Level3HomeScreen
import com.jigar.me.ui.view.home.screens.levels.level3.config.Level3ConfigScreen
import com.jigar.me.ui.view.home.screens.levels.level3.guided.Level3GuidedScreen
import com.jigar.me.ui.view.home.screens.levels.level3.anzan.Level3AnzanScreen
import com.jigar.me.ui.view.home.screens.levels.level3.speed_drill.Level3SpeedDrillScreen
import com.jigar.me.ui.view.home.screens.levels.level3.flash.Level3FlashPickerScreen
import com.jigar.me.ui.view.home.screens.levels.level3.flash.Level3FlashPlayScreen

@Composable
fun HomeNavGraph(
    homeActivityViewModel: HomeActivityViewModel,
    navController: NavHostController = rememberNavController(),
    initialRoute: String? = null,
    onInitialRouteHandled: () -> Unit = {},
    onFinishActivity: () -> Unit = {},
) {
    LaunchedEffect(initialRoute) {
        if (!initialRoute.isNullOrEmpty() && initialRoute != RouteNavigation.Home.route) {
            navController.navigate(initialRoute)
            onInitialRouteHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = RouteNavigation.Splash.route
    ) {
        composable(route = RouteNavigation.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(RouteNavigation.Home.route) {
                        popUpTo(RouteNavigation.Splash.route) { inclusive = true }
                    }
                },
                onFinishActivity = onFinishActivity
            )
        }

        composable(route = RouteNavigation.Home.route) {
            LaunchedEffect(Unit) {
                homeActivityViewModel.fetchReviewsIfCredentialLogin()
                homeActivityViewModel.fetchAbacusDataIfNeeded()
            }
            HomeScreen(
                onNavigateToLevelCategory = { levelId ->
                    navController.navigate(RouteNavigation.LevelCategory.levelCategory(levelId))
                },
                onNavigateToAbacusFreeMode = {
                    navController.navigate(RouteNavigation.AbacusFreeMode.route)
                },
                onNavigateToMathGameZone = {
                    navController.navigate(RouteNavigation.MathGameZone.route)
                },
                onNavigateToSettings = {
                    navController.navigate(RouteNavigation.Settings.route)
                },
                onNavigateToMyAccount = {
                    navController.navigate(RouteNavigation.MyAccount.route)
                },
                onNavigateToExercise = {
                    navController.navigate(RouteNavigation.Exercise.route)
                },
                onNavigateToExamHome = {
                    navController.navigate(RouteNavigation.ExamHome.route)
                },
                onNavigateToCCMHome = {
                    navController.navigate(RouteNavigation.CCMHome.route)
                },
                onNavigateToPurchase = {
                    navController.navigate(RouteNavigation.Purchase.route)
                },
                onNavigateToYoutubeVideo = {
                    navController.navigate(RouteNavigation.YoutubeVideo.route)
                },
                onNavigateToWhatsLearning = {
                    navController.navigate(RouteNavigation.WhatsLearning.route)
                },
                onNavigateToLevel1 = {
                    navController.navigate(RouteNavigation.Level1Home.route)
                },
                onNavigateToLevel2 = {
                    navController.navigate(RouteNavigation.Level2Home.route)
                },
                onNavigateToLevel3 = {
                    navController.navigate(RouteNavigation.Level3Home.route)
                },
                onNavigateToLevel4 = {
                    navController.navigate(RouteNavigation.Level4TablePicker.route)
                },
            )
        }

        composable(route = RouteNavigation.AbacusFreeMode.route) {
            val viewModel: AbacusFreeModeViewModel = hiltViewModel()
            AbacusFreeModeScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.LevelCategory.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.StringType }
            )
        ) {
            LevelCategoryScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToSet = { levelCategory ->
                    navController.navigate(RouteNavigation.Set.abacusSet(levelCategory.id,levelCategory.name))
                }
            )
        }

        composable(
            route = RouteNavigation.Set.route,
            arguments = listOf(
                navArgument("levelCategoryId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) {
            SetScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToDoPractice = { setId, saveResults ->
                    navController.navigate(RouteNavigation.AbacusDoPractice.doPractice(setId, saveResults))
                },
                onNavigateToList = { setId ->
                    navController.navigate(RouteNavigation.AbacusList.list(setId))
                },
            )
        }

        composable(
            route = RouteNavigation.AbacusDoPractice.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.StringType },
                navArgument("saveResults") { type = NavType.BoolType; defaultValue = true }
            )
        ) {
            AbacusDoPracticeRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.AbacusList.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.StringType }
            )
        ) {
            AbacusListRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.MathGameZone.route) {
            MathGameZoneScreenRoute(
                onBackClick = { navController.popBackStack() },
                onNavigateToNumberSequencePuzzle = {
                    navController.navigate(RouteNavigation.NumberSequencePuzzleHome.route)
                },
                onNavigateToSudoku = {
                    navController.navigate(RouteNavigation.SudokuHome.route)
                },
                onNavigateToMathPyramid = {
                    navController.navigate(RouteNavigation.MathPyramidHome.route)
                },
                onNavigateToTargetNumber = {
                    navController.navigate(RouteNavigation.TargetNumberHome.route)
                },
                onNavigateToBalloonPop = {
                    navController.navigate(RouteNavigation.BalloonPopHome.route)
                },
                onNavigateToSpeedCompare = {
                    navController.navigate(RouteNavigation.SpeedCompareHome.route)
                },
                onNavigateToMissingOperator = {
                    navController.navigate(RouteNavigation.MissingOperatorHome.route)
                },
                onNavigateToMagicSquare = {
                    navController.navigate(RouteNavigation.MagicSquareHome.route)
                },
                onNavigateToCalcudoku = {
                    navController.navigate(RouteNavigation.CalcudokuHome.route)
                },
            )
        }

        composable(route = RouteNavigation.NumberSequencePuzzleHome.route) {
            NumberSequencePuzzleHomeRoute(
                navController = navController,
                homeActivityViewModel = homeActivityViewModel,
                onPuzzleSelect = { type ->
                    navController.navigate(RouteNavigation.NumberSequencePuzzlePlay.play(type))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.NumberSequencePuzzlePlay.route,
            arguments = listOf(
                navArgument("type") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getInt("type") ?: 3
            NumberSequencePuzzlePlayRoute(
                navController = navController,
                gridSize = type
            )
        }

        composable(route = RouteNavigation.SudokuHome.route) {
            SudokuHomeRoute(
                navController = navController,
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { size, difficulty, isNewPuzzle ->
                    navController.navigate(
                        RouteNavigation.SudokuPlay.play(size, difficulty, isNewPuzzle)
                    )
                }
            )
        }

        composable(
            route = RouteNavigation.SudokuPlay.route,
            arguments = listOf(
                navArgument("size") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("isNewPuzzle") { type = NavType.BoolType }
            )
        ) {
            SudokuPlayRoute(navController = navController)
        }

        composable(route = RouteNavigation.MathPyramidHome.route) {
            MathPyramidHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { levels, difficulty ->
                    navController.navigate(RouteNavigation.MathPyramidPlay.play(levels, difficulty))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MathPyramidPlay.route,
            arguments = listOf(
                navArgument("levels") { type = NavType.IntType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val levels = backStackEntry.arguments?.getInt("levels") ?: 4
            val difficultyName = backStackEntry.arguments?.getString("difficulty")
            MathPyramidPlayRoute(
                levels = levels,
                difficultyName = difficultyName,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.TargetNumberHome.route) {
            TargetNumberHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { level, diff ->
                    navController.navigate(RouteNavigation.TargetNumberPlay.play(level, diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TargetNumberPlay.route,
            arguments = listOf(
                navArgument("target_level") { type = NavType.IntType },
                navArgument("target_diff") { type = NavType.StringType }
            )
        ) {
            TargetNumberPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.BalloonPopHome.route) {
            BalloonPopHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.navigate(RouteNavigation.BalloonPopPlay.play(diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.BalloonPopPlay.route,
            arguments = listOf(
                navArgument("balloon_diff") { type = NavType.StringType }
            )
        ) {
            BalloonPopPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.SpeedCompareHome.route) {
            SpeedCompareHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.navigate(RouteNavigation.SpeedComparePlay.play(diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.SpeedComparePlay.route,
            arguments = listOf(
                navArgument("speed_diff") { type = NavType.StringType }
            )
        ) {
            SpeedComparePlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.MissingOperatorHome.route) {
            MissingOperatorHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.navigate(RouteNavigation.MissingOperatorPlay.play(diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MissingOperatorPlay.route,
            arguments = listOf(
                navArgument("missing_op_diff") { type = NavType.StringType }
            )
        ) {
            MissingOperatorPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.MagicSquareHome.route) {
            MagicSquareHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.navigate(RouteNavigation.MagicSquarePlay.play(diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MagicSquarePlay.route,
            arguments = listOf(
                navArgument("magic_square_diff") { type = NavType.StringType }
            )
        ) {
            MagicSquarePlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.CalcudokuHome.route) {
            CalcudokuHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.navigate(RouteNavigation.CalcudokuPlay.play(diff))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.CalcudokuPlay.route,
            arguments = listOf(
                navArgument("calcudoku_diff") { type = NavType.StringType }
            )
        ) {
            CalcudokuPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.Settings.route) {
            SettingsScreenRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.Purchase.route) {
            PurchaseScreenRoute(
                homeActivityViewModel = homeActivityViewModel,
                onClose = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.YoutubeVideo.route) {
            YoutubeVideoScreenRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.WhatsLearning.route) {
            WhatsLearningScreenRoute(
                onClose = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.MyAccount.route) {
            MyAccountRoute(
                onBackClick = { navController.popBackStack() },
                onNavigateToFAQs = { navController.navigate(RouteNavigation.FAQs.route) },
                onNavigateToPurchase = { navController.navigate(RouteNavigation.Purchase.route) },
                onNavigateToSettings = { navController.navigate(RouteNavigation.Settings.route) },
                onNavigateToReportHistory = { navController.navigate(RouteNavigation.ReportHistory.route) },
                onNavigateToWhatsLearning = { navController.navigate(RouteNavigation.WhatsLearning.route) },
                onNavigateToCredentials = { navController.navigate(RouteNavigation.CredentialsLoginLandscape.route) },
            )
        }

        composable(route = RouteNavigation.CredentialsLoginLandscape.route) {
            val myAccountEntry = remember(navController) {
                navController.getBackStackEntry(RouteNavigation.MyAccount.route)
            }
            val myAccountViewModel: MyAccountViewModel = hiltViewModel(myAccountEntry)
            LoginWithCredentialsLandscapeScreen(
                onGoBack = { navController.popBackStack() },
                onLoginSuccess = {
                    myAccountViewModel.onLoginSuccess()
                    navController.popBackStack()
                }
            )
        }

        composable(route = RouteNavigation.ReportHistory.route) {
            ReportHistoryRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.FAQs.route) {
            FAQsRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.Exercise.route) {
            ExerciseRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.ExamHome.route) {
            ExamHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onStartPlay = { navController.navigate(RouteNavigation.ExamPlay.play(true)) },
                onStartPlayWithoutSaving = { navController.navigate(RouteNavigation.ExamPlay.play(false)) }
            )
        }

        composable(
            route = RouteNavigation.ExamPlay.route,
            arguments = listOf(navArgument("saveResults") { type = NavType.BoolType; defaultValue = true })
        ) {
            ExamPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.CCMHome.route) {
            CCMHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onStartPlay = { navController.navigate(RouteNavigation.CCMPlay.route) }
            )
        }

        composable(route = RouteNavigation.CCMPlay.route) {
            CCMPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TodayTableHome.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TodayTableHomeScreen(
                tableNumber = tableNumber,
                onNavigateToDrill = { navController.navigate(RouteNavigation.TodayTableDrill.create(tableNumber)) },
                onNavigateToFlashcard = { navController.navigate(RouteNavigation.TodayTableFlashcard.create(tableNumber)) },
                onNavigateToFillBlank = { navController.navigate(RouteNavigation.TodayTableFillBlank.create(tableNumber)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = RouteNavigation.TodayTableDrill.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableDrillScreen(tableNumber = tableNumber, onBackClick = { navController.popBackStack() })
        }
        composable(
            route = RouteNavigation.TodayTableFlashcard.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableFlashcardScreen(tableNumber = tableNumber, onBackClick = { navController.popBackStack() })
        }
        composable(
            route = RouteNavigation.TodayTableFillBlank.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableFillBlankScreen(tableNumber = tableNumber, onBackClick = { navController.popBackStack() })
        }

        composable(route = RouteNavigation.Level1Home.route) {
            Level1HomeScreen(
                onBackClick        = { navController.popBackStack() },
                onNavigateToLesson = { lessonId ->
                    navController.navigate(RouteNavigation.Level1Lesson.create(lessonId))
                }
            )
        }

        composable(
            route     = RouteNavigation.Level1Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1LessonScreen(
                lessonId             = lessonId,
                onBackClick          = { navController.popBackStack() },
                onNavigateToLearn    = { navController.navigate(RouteNavigation.Level1Learn.create(it)) },
                onNavigateToPractice = { navController.navigate(RouteNavigation.Level1Practice.create(it)) },
                onNavigateToQuiz     = { navController.navigate(RouteNavigation.Level1Quiz.create(it)) },
            )
        }

        composable(
            route     = RouteNavigation.Level1Learn.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1LearnScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level1Practice.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1PracticeScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level1Quiz.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1QuizScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(route = RouteNavigation.Level2Home.route) {
            Level2HomeScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToChapter = { chapterId ->
                    val ch = level2Chapters.find { it.id == chapterId }
                    if (ch?.type == Level2ChapterType.FORMULA_REF) {
                        navController.navigate(RouteNavigation.Level2FormulaRef.create(1))
                    } else {
                        navController.navigate(RouteNavigation.Level2Lesson.create(chapterId))
                    }
                }
            )
        }

        composable(
            route     = RouteNavigation.Level2FormulaRef.route,
            arguments = listOf(androidx.navigation.navArgument("groupId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
            Level2FormulaScreen(
                initialGroupId = groupId,
                onBackClick    = { navController.popBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Lesson.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2LessonScreen(
                lessonId             = lessonId,
                onBackClick          = { navController.popBackStack() },
                onNavigateToLearn    = { navController.navigate(RouteNavigation.Level2Learn.create(it)) },
                onNavigateToPractice = { navController.navigate(RouteNavigation.Level2Practice.create(it)) },
                onNavigateToQuiz     = { navController.navigate(RouteNavigation.Level2Quiz.create(it)) },
            )
        }

        composable(
            route     = RouteNavigation.Level2Learn.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2LearnScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Practice.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2PracticeScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Quiz.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2QuizScreen(
                lessonId    = lessonId,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack() },
            )
        }

        composable(route = RouteNavigation.Level3Home.route) {
            Level3HomeScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick      = { navController.popBackStack() },
                onNavigateToMode = { mode ->
                    if (mode == L3Mode.FLASH) {
                        navController.navigate(RouteNavigation.Level3FlashPicker.route)
                    } else {
                        navController.navigate(RouteNavigation.Level3Config.create(mode.ordinal))
                    }
                }
            )
        }

        composable(
            route     = RouteNavigation.Level3Config.route,
            arguments = listOf(navArgument("modeOrdinal") { type = NavType.IntType })
        ) { backStackEntry ->
            val ord  = backStackEntry.arguments?.getInt("modeOrdinal") ?: 0
            val mode = L3Mode.entries[ord.coerceIn(0, L3Mode.entries.lastIndex)]
            Level3ConfigScreen(
                mode        = mode,
                onBackClick = { navController.popBackStack() },
                onStart     = { cfg ->
                    when (cfg.mode) {
                        L3Mode.GUIDED     -> navController.navigate(
                            RouteNavigation.Level3Guided.create(cfg.terms, cfg.digits, cfg.flashMs, cfg.autoAbacus))
                        L3Mode.SEMI_ANZAN, L3Mode.FULL_ANZAN -> navController.navigate(
                            RouteNavigation.Level3Anzan.create(cfg.mode.ordinal, cfg.terms, cfg.digits, cfg.flashMs))
                        L3Mode.SPEED_DRILL -> navController.navigate(
                            RouteNavigation.Level3SpeedDrill.create(cfg.digits, cfg.timeLimitSecs))
                        else -> Unit
                    }
                }
            )
        }

        composable(
            route     = RouteNavigation.Level3Guided.route,
            arguments = listOf(
                navArgument("terms")      { type = NavType.IntType  },
                navArgument("digits")     { type = NavType.IntType  },
                navArgument("flashMs")    { type = NavType.IntType  },
                navArgument("autoAbacus") { type = NavType.BoolType; defaultValue = false },
            )
        ) { bs ->
            val cfg = L3Config(
                mode       = L3Mode.GUIDED,
                terms      = bs.arguments?.getInt("terms")     ?: 5,
                digits     = bs.arguments?.getInt("digits")    ?: 1,
                flashMs    = bs.arguments?.getInt("flashMs")   ?: 800,
                autoAbacus = bs.arguments?.getBoolean("autoAbacus") ?: false,
            )
            Level3GuidedScreen(
                config      = cfg,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(
            route     = RouteNavigation.Level3Anzan.route,
            arguments = listOf(
                navArgument("modeOrd") { type = NavType.IntType },
                navArgument("terms")   { type = NavType.IntType },
                navArgument("digits")  { type = NavType.IntType },
                navArgument("flashMs") { type = NavType.IntType },
            )
        ) { bs ->
            val modeOrd = bs.arguments?.getInt("modeOrd") ?: L3Mode.SEMI_ANZAN.ordinal
            val cfg = L3Config(
                mode    = L3Mode.entries[modeOrd.coerceIn(0, L3Mode.entries.lastIndex)],
                terms   = bs.arguments?.getInt("terms")   ?: 5,
                digits  = bs.arguments?.getInt("digits")  ?: 1,
                flashMs = bs.arguments?.getInt("flashMs") ?: 800,
            )
            Level3AnzanScreen(
                config      = cfg,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(
            route     = RouteNavigation.Level3SpeedDrill.route,
            arguments = listOf(
                navArgument("digits")        { type = NavType.IntType },
                navArgument("timeLimitSecs") { type = NavType.IntType },
            )
        ) { bs ->
            val cfg = L3Config(
                mode          = L3Mode.SPEED_DRILL,
                digits        = bs.arguments?.getInt("digits")        ?: 1,
                timeLimitSecs = bs.arguments?.getInt("timeLimitSecs") ?: 60,
            )
            Level3SpeedDrillScreen(
                config      = cfg,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(route = RouteNavigation.Level3FlashPicker.route) {
            Level3FlashPickerScreen(
                onBackClick        = { navController.popBackStack() },
                onSelectDifficulty = { idx ->
                    navController.navigate(RouteNavigation.Level3FlashPlay.create(idx))
                }
            )
        }

        composable(
            route     = RouteNavigation.Level3FlashPlay.route,
            arguments = listOf(navArgument("diffIndex") { type = NavType.IntType })
        ) { bs ->
            val idx = bs.arguments?.getInt("diffIndex") ?: 0
            Level3FlashPlayScreen(
                diffIndex   = idx,
                onBackClick = { navController.popBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(route = RouteNavigation.Level4TablePicker.route) {
            TablePickerScreen(
                homeActivityViewModel = homeActivityViewModel,
                onTableSelected = { tableNumber ->
                    navController.navigate(RouteNavigation.TodayTableHome.create(tableNumber))
                },
                onMixPractice = {
                    navController.navigate(RouteNavigation.TableMixPicker.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = RouteNavigation.TableMixPicker.route) {
            TableMixPickerScreen(
                homeActivityViewModel = homeActivityViewModel,
                onMixSelected = { tables ->
                    navController.navigate(RouteNavigation.TableMixHome.create(tables))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TableMixHome.route,
            arguments = listOf(navArgument("tables") { type = NavType.StringType })
        ) { backStackEntry ->
            val tables = backStackEntry.arguments?.getString("tables")
                ?.split(",")?.mapNotNull { it.toIntOrNull() } ?: listOf(1, 2, 3)
            TableMixHomeScreen(
                tables = tables,
                onNavigateToDrill = { navController.navigate(RouteNavigation.TableMixDrill.create(tables)) },
                onNavigateToFlashcard = { navController.navigate(RouteNavigation.TableMixFlashcard.create(tables)) },
                onNavigateToFillBlank = { navController.navigate(RouteNavigation.TableMixFillBlank.create(tables)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TableMixDrill.route,
            arguments = listOf(navArgument("tables") { type = NavType.StringType })
        ) { backStackEntry ->
            val tables = backStackEntry.arguments?.getString("tables")
                ?.split(",")?.mapNotNull { it.toIntOrNull() } ?: listOf(1, 2, 3)
            TableDrillScreen(
                tableNumber = tables.first(),
                tables = tables,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TableMixFlashcard.route,
            arguments = listOf(navArgument("tables") { type = NavType.StringType })
        ) { backStackEntry ->
            val tables = backStackEntry.arguments?.getString("tables")
                ?.split(",")?.mapNotNull { it.toIntOrNull() } ?: listOf(1, 2, 3)
            TableFlashcardScreen(
                tableNumber = tables.first(),
                tables = tables,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TableMixFillBlank.route,
            arguments = listOf(navArgument("tables") { type = NavType.StringType })
        ) { backStackEntry ->
            val tables = backStackEntry.arguments?.getString("tables")
                ?.split(",")?.mapNotNull { it.toIntOrNull() } ?: listOf(1, 2, 3)
            TableFillBlankScreen(
                tableNumber = tables.first(),
                tables = tables,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

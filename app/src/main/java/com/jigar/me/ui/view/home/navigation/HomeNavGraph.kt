package com.jigar.me.ui.view.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.jigar.me.ui.view.home.screens.math_game_zone.ZoneDailyMarkerViewModel
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
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.SpeedCompareDuelRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.SpeedCompareHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.SpeedComparePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.MissingOperatorHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.MissingOperatorPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.MagicSquareHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.magic_square.MagicSquarePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.CalcudokuHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.calcudoku.CalcudokuPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.Merge2048HomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.Merge2048PlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.EquationMatchHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.equation_match.EquationMatchPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.CrossMathHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.cross_math.CrossMathPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.NumberPathHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.number_path.NumberPathPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.TrueFalseHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.TrueFalsePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.PlaceValueHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.PlaceValuePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.ClockMasterHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.clock_master.ClockMasterPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.MathBingoHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.MathBingoPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.KakuroHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.kakuro.KakuroPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.trophy_room.TrophyRoomScreen
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
    // Deep links must wait until Splash has handed off to Home: navigating
    // while Splash is still current gets popped away by Splash's own
    // popUpTo(Splash){inclusive} hand-off, silently dropping the deep link
    // on cold starts.
    val currentEntry = navController.currentBackStackEntryAsState().value

    // Daily challenge counts only when a play screen actually opens — not
    // when a game's home page is merely peeked at.
    val dailyMarker: ZoneDailyMarkerViewModel = hiltViewModel()
    LaunchedEffect(currentEntry?.destination?.route) {
        currentEntry?.destination?.route?.let { dailyMarker.onRouteVisited(it) }
    }

    LaunchedEffect(initialRoute, currentEntry?.destination?.route) {
        val current = currentEntry?.destination?.route
        if (!initialRoute.isNullOrEmpty() && initialRoute != RouteNavigation.Home.route &&
            current != null && current != RouteNavigation.Splash.route
        ) {
            // Deep link — not a user tap; must never be dropped by the RESUMED guard
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
                    // Programmatic handoff — not a user tap; must never be dropped
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
                    navController.safeNavigate(RouteNavigation.LevelCategory.levelCategory(levelId))
                },
                onNavigateToAbacusFreeMode = {
                    navController.safeNavigate(RouteNavigation.AbacusFreeMode.route)
                },
                onNavigateToMathGameZone = {
                    navController.safeNavigate(RouteNavigation.MathGameZone.route)
                },
                onNavigateToSettings = {
                    navController.safeNavigate(RouteNavigation.Settings.route)
                },
                onNavigateToMyAccount = {
                    navController.safeNavigate(RouteNavigation.MyAccount.route)
                },
                onNavigateToExercise = {
                    navController.safeNavigate(RouteNavigation.Exercise.route)
                },
                onNavigateToExamHome = {
                    navController.safeNavigate(RouteNavigation.ExamHome.route)
                },
                onNavigateToCCMHome = {
                    navController.safeNavigate(RouteNavigation.CCMHome.route)
                },
                onNavigateToPurchase = {
                    navController.safeNavigate(RouteNavigation.Purchase.route)
                },
                onNavigateToYoutubeVideo = {
                    navController.safeNavigate(RouteNavigation.YoutubeVideo.route)
                },
                onNavigateToWhatsLearning = {
                    navController.safeNavigate(RouteNavigation.WhatsLearning.route)
                },
                onNavigateToLevel1 = {
                    navController.safeNavigate(RouteNavigation.Level1Home.route)
                },
                onNavigateToLevel2 = {
                    navController.safeNavigate(RouteNavigation.Level2Home.route)
                },
                onNavigateToLevel3 = {
                    navController.safeNavigate(RouteNavigation.Level3Home.route)
                },
                onNavigateToLevel4 = {
                    navController.safeNavigate(RouteNavigation.Level4TablePicker.route)
                },
            )
        }

        composable(route = RouteNavigation.AbacusFreeMode.route) {
            val viewModel: AbacusFreeModeViewModel = hiltViewModel()
            AbacusFreeModeScreen(
                viewModel = viewModel,
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() },
                onNavigateToSet = { levelCategory ->
                    navController.safeNavigate(RouteNavigation.Set.abacusSet(levelCategory.id,levelCategory.name))
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
                onBackClick = { navController.safePopBackStack() },
                onNavigateToDoPractice = { setId, saveResults ->
                    navController.safeNavigate(RouteNavigation.AbacusDoPractice.doPractice(setId, saveResults))
                },
                onNavigateToList = { setId ->
                    navController.safeNavigate(RouteNavigation.AbacusList.list(setId))
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
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.AbacusList.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.StringType }
            )
        ) {
            AbacusListRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.MathGameZone.route) {
            MathGameZoneScreenRoute(
                onBackClick = { navController.safePopBackStack() },
                onNavigateToNumberSequencePuzzle = {
                    navController.safeNavigate(RouteNavigation.NumberSequencePuzzleHome.route)
                },
                onNavigateToSudoku = {
                    navController.safeNavigate(RouteNavigation.SudokuHome.route)
                },
                onNavigateToMathPyramid = {
                    navController.safeNavigate(RouteNavigation.MathPyramidHome.route)
                },
                onNavigateToTargetNumber = {
                    navController.safeNavigate(RouteNavigation.TargetNumberHome.route)
                },
                onNavigateToBalloonPop = {
                    navController.safeNavigate(RouteNavigation.BalloonPopHome.route)
                },
                onNavigateToSpeedCompare = {
                    navController.safeNavigate(RouteNavigation.SpeedCompareHome.route)
                },
                onNavigateToMissingOperator = {
                    navController.safeNavigate(RouteNavigation.MissingOperatorHome.route)
                },
                onNavigateToMagicSquare = {
                    navController.safeNavigate(RouteNavigation.MagicSquareHome.route)
                },
                onNavigateToCalcudoku = {
                    navController.safeNavigate(RouteNavigation.CalcudokuHome.route)
                },
                onNavigateToMerge2048 = {
                    navController.safeNavigate(RouteNavigation.Merge2048Home.route)
                },
                onNavigateToEquationMatch = {
                    navController.safeNavigate(RouteNavigation.EquationMatchHome.route)
                },
                onNavigateToCrossMath = {
                    navController.safeNavigate(RouteNavigation.CrossMathHome.route)
                },
                onNavigateToNumberPath = {
                    navController.safeNavigate(RouteNavigation.NumberPathHome.route)
                },
                onNavigateToTrueFalse = {
                    navController.safeNavigate(RouteNavigation.TrueFalseHome.route)
                },
                onNavigateToPlaceValue = {
                    navController.safeNavigate(RouteNavigation.PlaceValueHome.route)
                },
                onNavigateToClockMaster = {
                    navController.safeNavigate(RouteNavigation.ClockMasterHome.route)
                },
                onNavigateToMathBingo = {
                    navController.safeNavigate(RouteNavigation.MathBingoHome.route)
                },
                onNavigateToKakuro = {
                    navController.safeNavigate(RouteNavigation.KakuroHome.route)
                },
                onNavigateToTrophyRoom = {
                    navController.safeNavigate(RouteNavigation.TrophyRoom.route)
                },
            )
        }

        composable(route = RouteNavigation.TrophyRoom.route) {
            TrophyRoomScreen(onBackClick = { navController.safePopBackStack() })
        }

        composable(route = RouteNavigation.NumberSequencePuzzleHome.route) {
            NumberSequencePuzzleHomeRoute(
                navController = navController,
                homeActivityViewModel = homeActivityViewModel,
                onPuzzleSelect = { type ->
                    navController.safeNavigate(RouteNavigation.NumberSequencePuzzlePlay.play(type))
                },
                onBackClick = { navController.safePopBackStack() }
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
                    navController.safeNavigate(
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
                    navController.safeNavigate(RouteNavigation.MathPyramidPlay.play(levels, difficulty))
                },
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.TargetNumberHome.route) {
            TargetNumberHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { level, diff ->
                    navController.safeNavigate(RouteNavigation.TargetNumberPlay.play(level, diff))
                },
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.BalloonPopHome.route) {
            BalloonPopHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.BalloonPopPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.BalloonPopPlay.route,
            arguments = listOf(
                navArgument("balloon_diff") { type = NavType.StringType }
            )
        ) {
            BalloonPopPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.SpeedCompareHome.route) {
            SpeedCompareHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.SpeedComparePlay.play(diff))
                },
                onStartDuel = { diff ->
                    navController.safeNavigate(RouteNavigation.SpeedCompareDuel.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.SpeedComparePlay.route,
            arguments = listOf(
                navArgument("speed_diff") { type = NavType.StringType }
            )
        ) {
            SpeedComparePlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.SpeedCompareDuel.route,
            arguments = listOf(
                navArgument("speed_duel_diff") { type = NavType.StringType }
            )
        ) {
            SpeedCompareDuelRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.MissingOperatorHome.route) {
            MissingOperatorHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.MissingOperatorPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MissingOperatorPlay.route,
            arguments = listOf(
                navArgument("missing_op_diff") { type = NavType.StringType }
            )
        ) {
            MissingOperatorPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.MagicSquareHome.route) {
            MagicSquareHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.MagicSquarePlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MagicSquarePlay.route,
            arguments = listOf(
                navArgument("magic_square_diff") { type = NavType.StringType }
            )
        ) {
            MagicSquarePlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.CalcudokuHome.route) {
            CalcudokuHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.CalcudokuPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.CalcudokuPlay.route,
            arguments = listOf(
                navArgument("calcudoku_diff") { type = NavType.StringType }
            )
        ) {
            CalcudokuPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.Merge2048Home.route) {
            Merge2048HomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.Merge2048Play.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.Merge2048Play.route,
            arguments = listOf(
                navArgument("merge2048_diff") { type = NavType.StringType }
            )
        ) {
            Merge2048PlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.EquationMatchHome.route) {
            EquationMatchHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.EquationMatchPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.EquationMatchPlay.route,
            arguments = listOf(
                navArgument("equation_match_diff") { type = NavType.StringType }
            )
        ) {
            EquationMatchPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.CrossMathHome.route) {
            CrossMathHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff, level ->
                    navController.safeNavigate(RouteNavigation.CrossMathPlay.play(diff, level))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.CrossMathPlay.route,
            arguments = listOf(
                navArgument("cross_math_diff") { type = NavType.StringType },
                navArgument("cross_math_level") { type = NavType.IntType }
            )
        ) {
            CrossMathPlayRoute(
                onBackClick = { navController.safePopBackStack() },
                onNextLevel = { diff, level ->
                    // Replace the current play screen so Back returns to the roadmap.
                    navController.safeNavigate(RouteNavigation.CrossMathPlay.play(diff, level)) {
                        popUpTo(RouteNavigation.CrossMathHome.route)
                    }
                }
            )
        }

        composable(route = RouteNavigation.NumberPathHome.route) {
            NumberPathHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff, level ->
                    navController.safeNavigate(RouteNavigation.NumberPathPlay.play(diff, level))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.NumberPathPlay.route,
            arguments = listOf(
                navArgument("number_path_diff") { type = NavType.StringType },
                navArgument("number_path_level") { type = NavType.IntType }
            )
        ) {
            NumberPathPlayRoute(
                onBackClick = { navController.safePopBackStack() },
                onNextLevel = { diff, level ->
                    // Replace the current play screen so Back returns to the roadmap.
                    navController.safeNavigate(RouteNavigation.NumberPathPlay.play(diff, level)) {
                        popUpTo(RouteNavigation.NumberPathHome.route)
                    }
                }
            )
        }

        composable(route = RouteNavigation.TrueFalseHome.route) {
            TrueFalseHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.TrueFalsePlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TrueFalsePlay.route,
            arguments = listOf(
                navArgument("true_false_diff") { type = NavType.StringType }
            )
        ) {
            TrueFalsePlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.PlaceValueHome.route) {
            PlaceValueHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.PlaceValuePlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.PlaceValuePlay.route,
            arguments = listOf(
                navArgument("place_value_diff") { type = NavType.StringType }
            )
        ) {
            PlaceValuePlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.ClockMasterHome.route) {
            ClockMasterHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.ClockMasterPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.ClockMasterPlay.route,
            arguments = listOf(
                navArgument("clock_master_diff") { type = NavType.StringType }
            )
        ) {
            ClockMasterPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.MathBingoHome.route) {
            MathBingoHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff ->
                    navController.safeNavigate(RouteNavigation.MathBingoPlay.play(diff))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.MathBingoPlay.route,
            arguments = listOf(
                navArgument("math_bingo_diff") { type = NavType.StringType }
            )
        ) {
            MathBingoPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.KakuroHome.route) {
            KakuroHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onStartPlay = { diff, level ->
                    navController.safeNavigate(RouteNavigation.KakuroPlay.play(diff, level))
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.KakuroPlay.route,
            arguments = listOf(
                navArgument("kakuro_diff") { type = NavType.StringType },
                navArgument("kakuro_level") { type = NavType.IntType }
            )
        ) {
            KakuroPlayRoute(
                onBackClick = { navController.safePopBackStack() },
                onNextLevel = { diff, level ->
                    // Replace the current play screen so Back returns to the roadmap.
                    navController.safeNavigate(RouteNavigation.KakuroPlay.play(diff, level)) {
                        popUpTo(RouteNavigation.KakuroHome.route)
                    }
                }
            )
        }

        composable(route = RouteNavigation.Settings.route) {
            SettingsScreenRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.Purchase.route) {
            PurchaseScreenRoute(
                homeActivityViewModel = homeActivityViewModel,
                onClose = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.YoutubeVideo.route) {
            YoutubeVideoScreenRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.WhatsLearning.route) {
            WhatsLearningScreenRoute(
                onClose = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.MyAccount.route) {
            MyAccountRoute(
                onBackClick = { navController.safePopBackStack() },
                onNavigateToFAQs = { navController.safeNavigate(RouteNavigation.FAQs.route) },
                onNavigateToPurchase = { navController.safeNavigate(RouteNavigation.Purchase.route) },
                onNavigateToSettings = { navController.safeNavigate(RouteNavigation.Settings.route) },
                onNavigateToReportHistory = { navController.safeNavigate(RouteNavigation.ReportHistory.route) },
                onNavigateToWhatsLearning = { navController.safeNavigate(RouteNavigation.WhatsLearning.route) },
                onNavigateToCredentials = { navController.safeNavigate(RouteNavigation.CredentialsLoginLandscape.route) },
            )
        }

        composable(route = RouteNavigation.CredentialsLoginLandscape.route) {
            val myAccountEntry = remember(navController) {
                navController.getBackStackEntry(RouteNavigation.MyAccount.route)
            }
            val myAccountViewModel: MyAccountViewModel = hiltViewModel(myAccountEntry)
            LoginWithCredentialsLandscapeScreen(
                onGoBack = { navController.safePopBackStack() },
                onLoginSuccess = {
                    myAccountViewModel.onLoginSuccess()
                    navController.safePopBackStack()
                }
            )
        }

        composable(route = RouteNavigation.ReportHistory.route) {
            ReportHistoryRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.FAQs.route) {
            FAQsRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.Exercise.route) {
            ExerciseRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.ExamHome.route) {
            ExamHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() },
                onStartPlay = { navController.safeNavigate(RouteNavigation.ExamPlay.play(true)) },
                onStartPlayWithoutSaving = { navController.safeNavigate(RouteNavigation.ExamPlay.play(false)) }
            )
        }

        composable(
            route = RouteNavigation.ExamPlay.route,
            arguments = listOf(navArgument("saveResults") { type = NavType.BoolType; defaultValue = true })
        ) {
            ExamPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.CCMHome.route) {
            CCMHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() },
                onStartPlay = { navController.safeNavigate(RouteNavigation.CCMPlay.route) }
            )
        }

        composable(route = RouteNavigation.CCMPlay.route) {
            CCMPlayRoute(
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(
            route = RouteNavigation.TodayTableHome.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TodayTableHomeScreen(
                tableNumber = tableNumber,
                onNavigateToDrill = { navController.safeNavigate(RouteNavigation.TodayTableDrill.create(tableNumber)) },
                onNavigateToFlashcard = { navController.safeNavigate(RouteNavigation.TodayTableFlashcard.create(tableNumber)) },
                onNavigateToFillBlank = { navController.safeNavigate(RouteNavigation.TodayTableFillBlank.create(tableNumber)) },
                onBackClick = { navController.safePopBackStack() }
            )
        }
        composable(
            route = RouteNavigation.TodayTableDrill.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableDrillScreen(tableNumber = tableNumber, onBackClick = { navController.safePopBackStack() })
        }
        composable(
            route = RouteNavigation.TodayTableFlashcard.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableFlashcardScreen(tableNumber = tableNumber, onBackClick = { navController.safePopBackStack() })
        }
        composable(
            route = RouteNavigation.TodayTableFillBlank.route,
            arguments = listOf(navArgument("tableNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val tableNumber = backStackEntry.arguments?.getInt("tableNumber") ?: 7
            TableFillBlankScreen(tableNumber = tableNumber, onBackClick = { navController.safePopBackStack() })
        }

        composable(route = RouteNavigation.Level1Home.route) {
            Level1HomeScreen(
                onBackClick        = { navController.safePopBackStack() },
                onNavigateToLesson = { lessonId ->
                    navController.safeNavigate(RouteNavigation.Level1Lesson.create(lessonId))
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
                onBackClick          = { navController.safePopBackStack() },
                onNavigateToLearn    = { navController.safeNavigate(RouteNavigation.Level1Learn.create(it)) },
                onNavigateToPractice = { navController.safeNavigate(RouteNavigation.Level1Practice.create(it)) },
                onNavigateToQuiz     = { navController.safeNavigate(RouteNavigation.Level1Quiz.create(it)) },
            )
        }

        composable(
            route     = RouteNavigation.Level1Learn.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1LearnScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level1Practice.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1PracticeScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level1Quiz.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
            Level1QuizScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(route = RouteNavigation.Level2Home.route) {
            Level2HomeScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.safePopBackStack() },
                onNavigateToChapter = { chapterId ->
                    val ch = level2Chapters.find { it.id == chapterId }
                    if (ch?.type == Level2ChapterType.FORMULA_REF) {
                        navController.safeNavigate(RouteNavigation.Level2FormulaRef.create(1))
                    } else {
                        navController.safeNavigate(RouteNavigation.Level2Lesson.create(chapterId))
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
                onBackClick    = { navController.safePopBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Lesson.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2LessonScreen(
                lessonId             = lessonId,
                onBackClick          = { navController.safePopBackStack() },
                onNavigateToLearn    = { navController.safeNavigate(RouteNavigation.Level2Learn.create(it)) },
                onNavigateToPractice = { navController.safeNavigate(RouteNavigation.Level2Practice.create(it)) },
                onNavigateToQuiz     = { navController.safeNavigate(RouteNavigation.Level2Quiz.create(it)) },
            )
        }

        composable(
            route     = RouteNavigation.Level2Learn.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2LearnScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Practice.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2PracticeScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(
            route     = RouteNavigation.Level2Quiz.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: return@composable
            Level2QuizScreen(
                lessonId    = lessonId,
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.safePopBackStack() },
            )
        }

        composable(route = RouteNavigation.Level3Home.route) {
            Level3HomeScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick      = { navController.safePopBackStack() },
                onNavigateToMode = { mode ->
                    if (mode == L3Mode.FLASH) {
                        navController.safeNavigate(RouteNavigation.Level3FlashPicker.route)
                    } else {
                        navController.safeNavigate(RouteNavigation.Level3Config.create(mode.ordinal))
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
                onBackClick = { navController.safePopBackStack() },
                onStart     = { cfg ->
                    when (cfg.mode) {
                        L3Mode.GUIDED     -> navController.safeNavigate(
                            RouteNavigation.Level3Guided.create(cfg.terms, cfg.digits, cfg.flashMs, cfg.autoAbacus))
                        L3Mode.SEMI_ANZAN, L3Mode.FULL_ANZAN -> navController.safeNavigate(
                            RouteNavigation.Level3Anzan.create(cfg.mode.ordinal, cfg.terms, cfg.digits, cfg.flashMs))
                        L3Mode.SPEED_DRILL -> navController.safeNavigate(
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
                onBackClick = { navController.safePopBackStack() },
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
                onBackClick = { navController.safePopBackStack() },
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
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(route = RouteNavigation.Level3FlashPicker.route) {
            Level3FlashPickerScreen(
                onBackClick        = { navController.safePopBackStack() },
                onSelectDifficulty = { idx ->
                    navController.safeNavigate(RouteNavigation.Level3FlashPlay.create(idx))
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
                onBackClick = { navController.safePopBackStack() },
                onFinished  = { navController.popBackStack(RouteNavigation.Level3Home.route, false) },
            )
        }

        composable(route = RouteNavigation.Level4TablePicker.route) {
            TablePickerScreen(
                homeActivityViewModel = homeActivityViewModel,
                onTableSelected = { tableNumber ->
                    navController.safeNavigate(RouteNavigation.TodayTableHome.create(tableNumber))
                },
                onMixPractice = {
                    navController.safeNavigate(RouteNavigation.TableMixPicker.route)
                },
                onBackClick = { navController.safePopBackStack() }
            )
        }

        composable(route = RouteNavigation.TableMixPicker.route) {
            TableMixPickerScreen(
                homeActivityViewModel = homeActivityViewModel,
                onMixSelected = { tables ->
                    navController.safeNavigate(RouteNavigation.TableMixHome.create(tables))
                },
                onBackClick = { navController.safePopBackStack() }
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
                onNavigateToDrill = { navController.safeNavigate(RouteNavigation.TableMixDrill.create(tables)) },
                onNavigateToFlashcard = { navController.safeNavigate(RouteNavigation.TableMixFlashcard.create(tables)) },
                onNavigateToFillBlank = { navController.safeNavigate(RouteNavigation.TableMixFillBlank.create(tables)) },
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() }
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
                onBackClick = { navController.safePopBackStack() }
            )
        }
    }
}

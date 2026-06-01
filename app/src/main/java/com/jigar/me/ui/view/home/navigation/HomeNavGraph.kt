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
import com.jigar.me.ui.view.home.screens.purchase.PurchaseScreenRoute
import com.jigar.me.ui.view.home.screens.settings.SettingsScreenRoute
import com.jigar.me.ui.view.home.screens.whats_learning.WhatsLearningScreenRoute
import com.jigar.me.ui.view.home.screens.youtube.YoutubeVideoScreenRoute
import com.jigar.me.ui.view.home.screens.abacus_free_mode.components.AbacusFreeModeScreen
import com.jigar.me.ui.view.home.screens.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.LevelCategoryScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.login.screens.splash.SplashScreen

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
                onNavigateToDoPractice = { setId ->
                    navController.navigate(RouteNavigation.AbacusDoPractice.doPractice(setId))
                },
                onNavigateToList = { setId ->
                    navController.navigate(RouteNavigation.AbacusList.list(setId))
                },
            )
        }

        composable(
            route = RouteNavigation.AbacusDoPractice.route,
            arguments = listOf(
                navArgument("setId") { type = NavType.StringType }
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
                onStartPlay = { navController.navigate(RouteNavigation.ExamPlay.route) }
            )
        }

        composable(route = RouteNavigation.ExamPlay.route) {
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
    }
}

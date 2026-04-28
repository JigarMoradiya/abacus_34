package com.jigar.me.ui.view.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jigar.me.ui.view.home.screens.activities.CCMHomeRoute
import com.jigar.me.ui.view.home.screens.activities.CCMPlayRoute
import com.jigar.me.ui.view.home.screens.activities.ExamHomeRoute
import com.jigar.me.ui.view.home.screens.activities.ExamPlayRoute
import com.jigar.me.ui.view.home.screens.activities.ExerciseRoute
import com.jigar.me.ui.view.home.screens.category.AbacusDoPracticeRoute
import com.jigar.me.ui.view.home.screens.category.AbacusListRoute
import com.jigar.me.ui.view.home.screens.category.CategoryScreen
import com.jigar.me.ui.view.home.screens.home.HomeScreen
import com.jigar.me.ui.view.home.screens.my_account.FAQsRoute
import com.jigar.me.ui.view.home.screens.my_account.MyAccountRoute
import com.jigar.me.ui.view.home.screens.my_account.ReportHistoryRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.MathGameZoneScreenRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.MathPyramidHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.MathPyramidPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.NumberSequencePuzzleHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.NumberSequencePuzzlePlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.SudokuHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.SudokuPlayRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.TargetNumberHomeRoute
import com.jigar.me.ui.view.home.screens.math_game_zone.TargetNumberPlayRoute
import com.jigar.me.ui.view.home.screens.purchase.PurchaseScreenRoute
import com.jigar.me.ui.view.home.screens.settings.SettingsScreenRoute
import com.jigar.me.ui.view.home.screens.whats_learning.WhatsLearningScreenRoute
import com.jigar.me.ui.view.home.screens.youtube.YoutubeVideoScreenRoute
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.components.AbacusFreeModeScreen
import com.jigar.me.ui.view.jetpack.fragments.abacus_free_mode.viewmodel.AbacusFreeModeViewModel
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel

@Composable
fun HomeNavGraph(
    homeActivityViewModel: HomeActivityViewModel,
    navController: NavHostController = rememberNavController(),
    initialRoute: String? = null,
    onInitialRouteHandled: () -> Unit = {},
) {
    LaunchedEffect(initialRoute) {
        if (!initialRoute.isNullOrEmpty() && initialRoute != RouteNavigation.Home.route) {
            navController.navigate(initialRoute)
            onInitialRouteHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = RouteNavigation.Home.route
    ) {
        composable(route = RouteNavigation.Home.route) {
            HomeScreen(
                onNavigateToCategory = { levelId ->
                    navController.navigate(RouteNavigation.Category.category(levelId))
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
            route = RouteNavigation.Category.route,
            arguments = listOf(
                navArgument("levelId") { type = NavType.StringType }
            )
        ) {
            CategoryScreen(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToDoPractice = { setId ->
                    navController.navigate(RouteNavigation.AbacusDoPractice.doPractice(setId))
                },
                onNavigateToList = { setId ->
                    navController.navigate(RouteNavigation.AbacusList.list(setId))
                },
                onNavigateToPurchase = {
                    navController.navigate(RouteNavigation.Purchase.route)
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
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) },
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
                },
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) }
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
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) },
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
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) },
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
            )
        }

        composable(route = RouteNavigation.ReportHistory.route) {
            ReportHistoryRoute(
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
                onBackClick = { navController.popBackStack() },
                onNavigateToPurchase = { navController.navigate(RouteNavigation.Purchase.route) }
            )
        }

        composable(route = RouteNavigation.ExamHome.route) {
            ExamHomeRoute(
                homeActivityViewModel = homeActivityViewModel,
                onBackClick = { navController.popBackStack() },
                onStartPlay = { navController.navigate(RouteNavigation.ExamPlay.route) },
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) }
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
                onStartPlay = { navController.navigate(RouteNavigation.CCMPlay.route) },
                onPurchase = { navController.navigate(RouteNavigation.Purchase.route) }
            )
        }

        composable(route = RouteNavigation.CCMPlay.route) {
            CCMPlayRoute(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

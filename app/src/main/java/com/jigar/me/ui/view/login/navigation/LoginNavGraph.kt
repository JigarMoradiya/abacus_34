package com.jigar.me.ui.view.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jigar.me.ui.view.home.screens.my_account.FAQsRoute
import com.jigar.me.ui.view.login.screens.login.LoginScreen
import com.jigar.me.ui.view.login.screens.login_home.LoginHomeScreen
import com.jigar.me.ui.view.login.screens.splash.SplashScreen

@Composable
fun LoginNavGraph(
    navController: NavHostController = rememberNavController(),
    onNavigateToHome: () -> Unit,
    onFinishActivity: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoute.Splash.route
    ) {
        composable(route = LoginRoute.Splash.route) {
            SplashScreen(
                onNavigateToLoginHome = {
                    navController.navigate(LoginRoute.LoginHome.route) {
                        popUpTo(LoginRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = onNavigateToHome,
                onFinishActivity = onFinishActivity,
            )
        }

        composable(route = LoginRoute.LoginHome.route) {
            LoginHomeScreen(
                onNavigateToLogin = { navController.navigate(LoginRoute.Login.route) },
                onNavigateToFAQs = { navController.navigate(LoginRoute.FAQs.route) },
                onNavigateToHome = onNavigateToHome,
            )
        }

        composable(route = LoginRoute.Login.route) {
            LoginScreen(
                onNavigateToFAQs = { navController.navigate(LoginRoute.FAQs.route) },
                onGoBack = { navController.popBackStack() },
                onNavigateToHome = onNavigateToHome,
            )
        }

        composable(route = LoginRoute.FAQs.route) {
            FAQsRoute(onBackClick = { navController.popBackStack() })
        }
    }
}

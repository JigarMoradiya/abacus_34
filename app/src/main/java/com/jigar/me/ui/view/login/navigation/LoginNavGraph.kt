package com.jigar.me.ui.view.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jigar.me.ui.view.home.screens.my_account.FAQsRoute
import com.jigar.me.ui.view.login.screens.login.LoginScreen
import com.jigar.me.ui.view.login.screens.login_home.LoginHomeScreen
import com.jigar.me.ui.view.login.screens.splash.SplashScreen
import com.jigar.me.ui.view.other.contactus.ContactUsScreen

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
                onNavigateToHome = onNavigateToHome,
                onFinishActivity = onFinishActivity,
            )
        }

        composable(route = LoginRoute.LoginHome.route) {
            LoginHomeScreen(
                onNavigateToLogin = { navController.navigate(LoginRoute.Login.route) },
                onNavigateToFAQs = { navController.navigate(LoginRoute.FAQs.route) },
                onNavigateToContactUs = { type ->
                    navController.navigate(LoginRoute.ContactUs.contactUs(type))
                },
                onNavigateToHome = onNavigateToHome,
            )
        }

        composable(
            route = LoginRoute.ContactUs.route,
            arguments = listOf(
                navArgument(LoginRoute.ContactUs.ARG_TYPE) { type = NavType.StringType }
            )
        ) {
            ContactUsScreen(onFinish = { navController.popBackStack() })
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

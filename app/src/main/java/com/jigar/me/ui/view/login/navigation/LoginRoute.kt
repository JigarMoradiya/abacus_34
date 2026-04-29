package com.jigar.me.ui.view.login.navigation

sealed class LoginRoute(val route: String) {
    object Splash : LoginRoute("login_splash")
    object LoginHome : LoginRoute("login_home")
    object Login : LoginRoute("login_credentials")
    object FAQs : LoginRoute("login_faqs")
}

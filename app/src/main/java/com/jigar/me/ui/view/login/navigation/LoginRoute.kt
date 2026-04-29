package com.jigar.me.ui.view.login.navigation

sealed class LoginRoute(val route: String) {
    object Splash : LoginRoute("login_splash")
    object LoginHome : LoginRoute("login_home")
    object Login : LoginRoute("login_credentials")
    object FAQs : LoginRoute("login_faqs")

    /** Argument key matches `AppConstants.extras_Comman.type` so the same `SavedStateHandle` lookup works. */
    object ContactUs : LoginRoute("login_contact_us/{type}") {
        const val ARG_TYPE = "type"
        fun contactUs(type: String): String = "login_contact_us/$type"
    }
}

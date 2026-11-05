package com.jigar.me.ui.view.login.screens.splash.viewmodels

import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

data class SplashUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showAppUpdatePopup: Boolean = false,
    val showNoInternetPopup: Boolean = false,
    val showErrorPopup: Boolean = false,
    val errorPopupMessage: String? = null,
    val noInternet: ConsumableCommand<Unit>? = null,
    val finishActivity: ConsumableCommand<Unit>? = null,
    val navigateToLoginHome: ConsumableCommand<Unit>? = null,
    val navigateToHome: ConsumableCommand<Unit>? = null,
)

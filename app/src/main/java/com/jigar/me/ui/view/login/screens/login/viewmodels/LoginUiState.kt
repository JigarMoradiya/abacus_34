package com.jigar.me.ui.view.login.screens.login.viewmodels

import androidx.annotation.StringRes
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val privacyPolicyUrl: String = "",
    val navigateToHome: ConsumableCommand<Unit>? = null,
)

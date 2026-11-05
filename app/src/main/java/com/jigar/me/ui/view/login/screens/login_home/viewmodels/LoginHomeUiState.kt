package com.jigar.me.ui.view.login.screens.login_home.viewmodels

import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

data class LoginHomeUiState(
    val isLoading: Boolean = false,
    val notesHtml: String = "",
    val bulkLoginHtml: String = "",
    val privacyPolicyUrl: String = "",
    val errorMessage: String? = null,
    val navigateToHome: ConsumableCommand<Unit>? = null,
)

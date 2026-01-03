package com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.viewmodels


data class CCMHomeUiState(
    val error: Int? = null,
    val totalQuestion: Int = 10,
    val questionGap: Int = 3,
    val questionMinLength: Int = 1,
    val questionMaxLength: Int = 3,
    val isQuestionSpeak: Boolean = false,
    val isQuestionShowNumber: Boolean = true,
    val isQuestionShowWord: Boolean = false,
)
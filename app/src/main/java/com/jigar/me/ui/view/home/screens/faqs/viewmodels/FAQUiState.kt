package com.jigar.me.ui.view.home.screens.faqs.viewmodels

import com.jigar.me.data.local.data.FAQs

data class FAQUiState(
    val error: Int? = null,
    val isUserLoggedIn: Boolean = false,
    val faqsList: List<FAQs> = emptyList(),
)
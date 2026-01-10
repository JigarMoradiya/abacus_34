package com.jigar.me.ui.view.jetpack.fragments.other.faqs.viewmodels

import com.jigar.me.data.local.data.FAQs

data class FAQUiState(
    val error: Int? = null,
    val faqsList: List<FAQs> = emptyList(),
)
package com.jigar.me.ui.view.home.screens.category.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails

data class CategoryUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val pages: List<DisplayPages> = emptyList(),
    val purchasedSku: List<InAppSkuDetails> = emptyList(),
    val showNoData: Boolean = false,
    val error: Int? = null
)
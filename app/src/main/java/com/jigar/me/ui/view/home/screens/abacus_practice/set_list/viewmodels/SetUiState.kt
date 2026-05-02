package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails

data class SetUiState(
    val isLoading: Boolean = true,
    val name: String = "Pages of Level",
    val pages: List<DisplayPages> = emptyList(),
    val purchasedSku: List<InAppSkuDetails> = emptyList(),
    val showNoData: Boolean = false,
    val error: Int? = null
)
package com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages

data class SetUiState(
    val isLoading: Boolean = true,
    val pageTitle: String = "Pages of Level",
    val levelName: String = "",
    val pages: List<DisplayPages> = emptyList(),
    val showNoData: Boolean = false,
    val error: Int? = null
)
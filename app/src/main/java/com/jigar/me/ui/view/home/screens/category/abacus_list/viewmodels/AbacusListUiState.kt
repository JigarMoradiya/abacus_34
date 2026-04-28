package com.jigar.me.ui.view.home.screens.category.abacus_list.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus

data class AbacusListUiState(
    val abacus: List<Abacus> = emptyList(),
    val error: Int? = null
)
package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.temp_abacus_list.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus

data class AbacusListUiState(
    val abacus: List<Abacus> = emptyList(),
    val error: Int? = null
)
package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.jigar.me.data.model.dbtable.abacus_all_data.Set

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    repository: CategoryRepository
) : ViewModel() {

    val allSets: StateFlow<List<Set>> =
        repository.getAllSets()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}

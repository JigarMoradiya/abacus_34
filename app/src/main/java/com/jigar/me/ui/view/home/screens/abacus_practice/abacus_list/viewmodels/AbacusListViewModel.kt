package com.jigar.me.ui.view.home.screens.abacus_practice.abacus_list.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AbacusListViewModel @Inject constructor(
    private val abacusDataRepository: AbacusDataRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<AbacusListUiState>() {

    override val TAG = "AbacusListViewModel"

    override fun getInitialState() = AbacusListUiState()
    val setId: String? = savedStateHandle["setId"]
    init {
        initialLoad()
    }

    fun initialLoad()  = viewModelScope.launch {
        setId?.let {
            val abacusList = abacusDataRepository.getAbacus(setId).first()
            updateState_ {
                copy(abacus = abacusList)
            }
        }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
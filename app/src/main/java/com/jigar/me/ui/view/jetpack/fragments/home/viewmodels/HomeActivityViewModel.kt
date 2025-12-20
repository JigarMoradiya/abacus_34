package com.jigar.me.ui.view.jetpack.fragments.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category.CategoryRepository
import com.jigar.me.ui.view.jetpack.fragments.home.interator.GetAbacusDataUseCase
import com.jigar.me.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    repository: CategoryRepository,
    private val prefManager: AppPreferencesHelper,
    private val getAbacusDataUseCase : GetAbacusDataUseCase
) : ViewModel() {

    val allSets: StateFlow<List<Set>> =
        repository.getAllSets()
            .stateIn(
                viewModelScope,
                SharingStarted.Companion.WhileSubscribed(5_000),
                emptyList()
            )


    fun fetchAbacusData() = viewModelScope.launch{
        val defaultDateTime = Constants.last_sync_default_time
        val dateTime = prefManager.getCustomParam(Constants.last_sync_time,defaultDateTime)
        val request = FetchAbacusDataRequest(true,true,true,true,true,last_sync_time = dateTime)
        getAbacusDataUseCase(
            params = request,
            onStart = { },
            onEachEmit = { },
            onCompletion = {},
            onError = {}
        ).catch {}.collect()
    }
}
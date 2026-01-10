package com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.api.GetStatisticsUseCase
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAccountViewModel @Inject constructor(
    private val getStatistics: GetStatisticsUseCase,
    private val pref: AppPreferencesHelper,
) : StatefulViewModel<MyAccountUiState>() {

    override val TAG = "MyAccountViewModel"

    override fun getInitialState() = MyAccountUiState()

    init {
        val statisticsData = pref.getCustomParam(AppConstants.STATISTICS_DATA,"")
        if (!statisticsData.isEmpty()){
            val statistics = Gson().fromJson(statisticsData, Statistics::class.java)
            updateState_ {
                copy(statistics = statistics,privacyPolicyUrl = pref.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
        }else{
            updateState_ {
                copy(privacyPolicyUrl = pref.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
        }
        getStatistics()
    }
    fun logoutOpen() {
        updateState_ {
            copy(isShowLogoutPopup = true)
        }
    }
    fun makeLogout() {
        pref.clearPref()
    }
    private fun getStatistics() = viewModelScope.launch {
        getStatistics(
            params = Unit,
            onStart = {  },
            onEachEmit = { statistics ->
                pref.setCustomParam(AppConstants.STATISTICS_DATA, Gson().toJson(statistics))
                updateState_ {
                    copy(statistics = statistics)
                }
            },
            onError = {
                onFailure(it)
            }
        ).catch {}.collect()
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
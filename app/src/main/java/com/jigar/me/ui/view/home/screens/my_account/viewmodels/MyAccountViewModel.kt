package com.jigar.me.ui.view.home.screens.my_account.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.DBRepository
import com.jigar.me.ui.jetpack.api.GetStatisticsUseCase
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.base.inapp.BillingRepository
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
    private val dbRepository: DBRepository,
    private val billingRepository: BillingRepository,
) : StatefulViewModel<MyAccountUiState>() {

    override val TAG = "MyAccountViewModel"

    override fun getInitialState() = MyAccountUiState()

    init {
        val isLoggedIn = pref.isUserLoggedIn()
        val statisticsData = pref.getCustomParam(AppConstants.STATISTICS_DATA, "")
        if (isLoggedIn && statisticsData.isNotEmpty()) {
            val statistics = Gson().fromJson(statisticsData, Statistics::class.java)
            updateState_ {
                copy(
                    isLoggedIn = true,
                    statistics = statistics,
                    privacyPolicyUrl = pref.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl, "")
                )
            }
        } else {
            updateState_ {
                copy(
                    isLoggedIn = isLoggedIn,
                    privacyPolicyUrl = pref.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl, "")
                )
            }
        }
        if (isLoggedIn) getStatistics()
    }
    fun onLoginSuccess() {
        updateState_ { copy(isLoggedIn = true) }
        getStatistics()
        billingRepository.startDataSourceConnections()
    }

    fun logoutOpenClose(isShow : Boolean) {
        updateState_ {
            copy(isShowLogoutPopup = isShow)
        }
    }
    fun makeLogout() {
        logoutOpenClose(false)
        pref.clearPref()
        viewModelScope.launch { dbRepository.deleteSetProgress() }
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
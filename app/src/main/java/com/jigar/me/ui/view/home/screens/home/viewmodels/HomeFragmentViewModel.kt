package com.jigar.me.ui.view.home.screens.home.viewmodels

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
    private val abacusDataRepository: AbacusDataRepository,
) : StatefulViewModel<HomeUiState>() {

    override val TAG = "HomeFragmentViewModel"

    override fun getInitialState() = HomeUiState()

    init {
        viewModelScope.launch { loadHomeMenu() }
    }

    fun closeConflictPopup() {
        updateState_ { copy(isShowPurchasedConflictPopup = false) }
    }
    fun showHideNotificationSettingPopup(isShowNotificationPopup: Boolean) {
        updateState_ {
            copy(isShowNotificationSettingPopup = isShowNotificationPopup)
        }
    }

    fun hideFreeTrialPopup() {
        updateState_ {
            copy(isShowFreeTrialPopup = false)
        }
    }

    private suspend fun loadHomeMenu() {
        try {
            val displayMenuList = getDisplayMenuList()
            abacusDataRepository.getLevels(displayMenuList).collect {
                updateState_ {
                    copy(menuLevels = it)
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun getDisplayMenuList(): List<String> {
        val menuListStr = prefs.getCustomParam(AppConstants.RemoteConfig.displayMenuList, "")
        return if (menuListStr.isNotEmpty() && menuListStr.length > 5) {
            val type = object : TypeToken<ArrayList<String>>() {}.type
            Gson().fromJson(menuListStr, type)
        } else {
            listOf(
                AppConstants.HomeClicks.Menu_Abacus_Free_Mode,
                AppConstants.HomeClicks.Menu_Practice_Abacus,
                AppConstants.HomeClicks.Menu_Abacus_Exercise,
                AppConstants.HomeClicks.Menu_Exam,
                AppConstants.HomeClicks.Menu_CCM,
                AppConstants.HomeClicks.Menu_Math_Game,
                AppConstants.HomeClicks.Menu_Purchase_Store,
                AppConstants.HomeClicks.Menu_Settings,
                AppConstants.HomeClicks.Menu_My_Account,
                AppConstants.HomeClicks.Menu_Video_Tutorial
            )
        }
    }


    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

}
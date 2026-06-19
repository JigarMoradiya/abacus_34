package com.jigar.me.ui.view.home.screens.home.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.google.gson.Gson
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.view.home.screens.home.interator.BackgroundMusicController
import com.jigar.me.ui.view.login.data.PostLoginHandler
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.RevenueCatHelper
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.awaitSyncPurchases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    repository: AbacusDataRepository,
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    private val postLoginHandler: PostLoginHandler,
) : ViewModel() {

    private val _isPurchasedFlow = MutableStateFlow(CommonUtils.checkPurchaseForExerciseExamCCM(prefs))
    val isPurchasedFlow: StateFlow<Boolean> = _isPurchasedFlow.asStateFlow()

    init {
        // React to ANY RC customer info update — covers app init, login, restore, and RC listener
        viewModelScope.launch {
            RevenueCatHelper.customerInfoFlow.collect { info ->
                if (info != null) {
                    _isPurchasedFlow.value = CommonUtils.checkPurchaseForExerciseExamCCM(prefs)
                }
            }
        }
        viewModelScope.launch {
            if (prefs.isUserLoggedIn()) {
                // Re-identify user in RC on every launch so purchases are correctly attributed
                val loginData = Gson().fromJson(prefs.getLoginData(), LoginData::class.java)
                loginData?.id?.let { userId ->
                    try { Purchases.sharedInstance.awaitLogIn(userId) } catch (_: Exception) {}
                }
                try { Purchases.sharedInstance.awaitSyncPurchases() } catch (_: Exception) {}
            }
            // Restore for ALL users — finds purchases tied to Google account on this device
            try { Purchases.sharedInstance.awaitRestore() } catch (_: Exception) {}
            try { RevenueCatHelper.update(Purchases.sharedInstance.awaitCustomerInfo()) } catch (_: Exception) {}
        }
    }

    fun isPurchasedSelectedLevel(name: String) = CommonUtils.checkLevelIsPurchase(name, prefs)
    fun isPurchasedForModule() = _isPurchasedFlow.value
    fun refreshPurchaseState() { _isPurchasedFlow.value = CommonUtils.checkPurchaseForExerciseExamCCM(prefs) }
    fun isUserLoggedIn(): Boolean = prefs.isUserLoggedIn()

    val allSets: StateFlow<List<Set>> =
        repository.getAllSets()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val bgController = BackgroundMusicController(context, prefs)

    fun fetchReviewsIfCredentialLogin() {
        if (prefs.getCustomParamBoolean(AppConstants.IS_CREDENTIAL_LOGIN, false)) {
            viewModelScope.launch { postLoginHandler.fetchAdminAssignPlan() }
        }
    }

    fun fetchAbacusDataIfNeeded() {
        viewModelScope.launch { postLoginHandler.fetchAbacusDataSilently() }
    }

    fun updateMusicVolume(volume: Int) = bgController.updateVolume(volume)
    fun onResume() = bgController.playIfNeeded()
    fun onPause() = bgController.pause()

    override fun onCleared() {
        bgController.release()
    }
}

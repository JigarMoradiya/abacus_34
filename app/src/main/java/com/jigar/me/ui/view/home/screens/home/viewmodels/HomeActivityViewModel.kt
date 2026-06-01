package com.jigar.me.ui.view.home.screens.home.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.repository.abacus_data.AbacusDataRepository
import com.jigar.me.ui.jetpack.core.repository.abacus_data.PurchaseRepository
import com.jigar.me.ui.view.home.screens.home.interator.BackgroundMusicController
import com.jigar.me.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    repository: AbacusDataRepository,
    @ApplicationContext private val context: Context,
    private val prefs: AppPreferencesHelper,
    purchaseRepository: PurchaseRepository,
) : ViewModel() {

    val purchasedSku: StateFlow<List<InAppSkuDetails>> =
        purchaseRepository.getPurchasedSku()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun isPurchasedSelectedLevel(purchasedSKU: List<InAppSkuDetails>, name: String) = CommonUtils.checkLevelIsPurchase(purchasedSKU, name, prefs)
    fun isPurchasedForModule(purchasedSKU: List<InAppSkuDetails>) = CommonUtils.checkPurchaseForExerciseExamCCM(prefs, purchasedSKU)
    fun isUserLoggedIn(): Boolean = prefs.isUserLoggedIn()

    val allSets: StateFlow<List<Set>> =
        repository.getAllSets()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val bgController = BackgroundMusicController(context, prefs)

    fun updateMusicVolume(volume: Int) = bgController.updateVolume(volume)
    fun onResume() = bgController.playIfNeeded()
    fun onPause() = bgController.pause()

    override fun onCleared() {
        bgController.release()
    }
}

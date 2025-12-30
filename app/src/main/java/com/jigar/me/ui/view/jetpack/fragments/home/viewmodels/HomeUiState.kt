package com.jigar.me.ui.view.jetpack.fragments.home.viewmodels

import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.ui.view.jetpack.core.domain.ConsumableCommand

data class HomeUiState(
    val error: Int? = null,
    val isLoading: Boolean? = null,
    val menuLevels: List<Level> = emptyList(),
    val purchasedList: List<InAppSkuDetails> = emptyList(),
    val purchasedRequest: List<GooglePurchasedPlanRequest> = emptyList(),
    val checkNotificationPermission: ConsumableCommand<Unit>? = null,
    val showFreeTrialPopup: ConsumableCommand<FreeTrialParam>? = null,
    val purchasedConflictPopup: ConsumableCommand<String>? = null,
)

data class FreeTrialParam(
    val remainingDays: Int = 0,
    val discountPer: Int = 0,
    val discountPerLifeTime: Int = 0,
)
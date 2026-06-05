package com.jigar.me.ui.view.home.screens.home.viewmodels

import com.jigar.me.R
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

data class HomeUiState(
    val error: Int? = null,
    val isLoading: Boolean? = null,
    val menuLevels: List<Level> = emptyList(),
    val purchasedRequest: List<GooglePurchasedPlanRequest> = emptyList(),

    val checkNotificationPermission: ConsumableCommand<Unit>? = null,
    val isShowNotificationSettingPopup: Boolean = false,

    val isShowPurchasedConflictPopup: Boolean = false,
    val purchasedConflictPopupType: String? = null,

    val isShowFreeTrialPopup: Boolean = false,
    val freeTrialParam: FreeTrialParam? = null,
)

data class FreeTrialParam(
    val remainingDays: Int = 0,
    val discountPer: Int = 0,
    val discountPerLifeTime: Int = 0,
    val manualFreeTrialDays: Int = 0,
)

data class FreeTrialUiConfig(
    val title: String,
    val desc: String,
    val yesText: String,
    val noText: String? = null,
    val showNow: Boolean = false,
    val showTrialStart: Boolean = false,
    val showDayLeft: Boolean = false,
    val showNoButton: Boolean = false,
    val numberRes: Int? = null
)

fun getFreeTrialUi(
    daysLeft: Int,
    manualFreeTrialDays: Int,
): FreeTrialUiConfig {
    if (manualFreeTrialDays == 3){
        return when {
            daysLeft >= 3 -> {
                FreeTrialUiConfig(
                    title = "Welcome to your 3 days free trial!",
                    desc = "You now have full access - explore, learn & enjoy your journey.",
                    yesText = "Start Learning",
                    showTrialStart = true,
                    numberRes = R.drawable.ic_number_3
                )
            }
            daysLeft == 2 -> {
                FreeTrialUiConfig(
                    title = "2 days remaining in your free trial.",
                    desc = "Keep your learning data and achievements safe by unlocking full access before your trial ends.",
                    yesText = "Subscribe Now",
                    noText = "Maybe Later",
                    showDayLeft = true,
                    showNoButton = true,
                    numberRes = R.drawable.ic_number_2
                )
            }
            daysLeft == 1 -> {
                FreeTrialUiConfig(
                    title = "Last day of your free trial!",
                    desc = "Stay on track with your learning - subscribe today.",
                    yesText = "Subscribe Now",
                    noText = "Maybe Later",
                    showDayLeft = true,
                    showNoButton = true,
                    numberRes = R.drawable.ic_number_1
                )
            }
            else -> {
                FreeTrialUiConfig(
                    title = "Your free trial has ended.",
                    desc = "Subscribe now to continue your learning journey.",
                    yesText = "View Plans",
                    showDayLeft = true,
                    numberRes = R.drawable.ic_number_0
                )
            }
        }
    }else{
        return when {
            daysLeft >= 7 -> {
                FreeTrialUiConfig(
                    title = "Welcome to your 7 days free trial!",
                    desc = "You now have full access - explore, learn & enjoy your journey.",
                    yesText = "Start Learning",
                    showTrialStart = true
                )
            }
            daysLeft == 6 -> FreeTrialUiConfig(
                title = "6 days left in your free trial.",
                desc = "Keep exploring and see all the features waiting for you!",
                yesText = "Subscribe Now",
                noText = "Continue Learning",
                showNow = true,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_6
            )
            daysLeft == 5 -> FreeTrialUiConfig(
                title = "Your free trial ends in 5 days.",
                desc = "Enjoy full access to all lessons during your trial!",
                yesText = "Subscribe Now",
                noText = "Keep Going",
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_5
            )
            daysLeft == 4 -> FreeTrialUiConfig(
                title = "Only 4 days left!",
                desc = "You're on the right track - don't stop now, success is just ahead!",
                yesText = "Subscribe Now",
                noText = "Continue",
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_4
            )
            daysLeft == 3 -> FreeTrialUiConfig(
                title = "3 days remaining in your free trial.",
                desc = "Keep your learning data and achievements safe by unlocking full access before your trial ends.",
                yesText = "Subscribe Now",
                noText = "Maybe Later",
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_3
            )
            daysLeft == 2 -> FreeTrialUiConfig(
                title = "Your free trial ends soon - only 2 days left!",
                desc = "Continue your learning adventure by subscribing today.",
                yesText = "Subscribe Now",
                noText = "Remind Me Later",
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_2
            )
            daysLeft == 1 -> FreeTrialUiConfig(
                title = "Last day of your free trial!",
                desc = "Stay on track with your learning - subscribe today.",
                yesText = "Subscribe Now",
                noText = "Maybe Later",
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_1
            )
            else -> FreeTrialUiConfig(
                title = "Your free trial has ended.",
                desc = "Subscribe now to continue your learning journey.",
                yesText = "View Plans",
                showDayLeft = true,
                numberRes = R.drawable.ic_number_0
            )
        }
    }

}

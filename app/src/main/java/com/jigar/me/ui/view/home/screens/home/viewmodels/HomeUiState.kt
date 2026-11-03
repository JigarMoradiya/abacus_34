package com.jigar.me.ui.view.home.screens.home.viewmodels

import com.jigar.me.R
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import androidx.annotation.StringRes

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

    // Streak
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakShields: Int = 0,
    val streakMilestoneAwarded: Int? = null,

    // Review gate — shown right after the streak dialog is dismissed, when eligible
    val showReviewGate: Boolean = false,
    // One-time emotional review ask on day 2 — takes priority over showReviewGate above
    val showDay2Review: Boolean = false,

    // Removed: the store provides the discount (the time-limited offer card on Home is gone)
    // val homeOffer: HomeOfferUi? = null,
    val isPremium: Boolean = false,

    // Freemium time limit: the day 3 / day 7 parent reminder is due (the grown-up question comes first, then the plans)
    val showParentReminder: Boolean = false,
    // ... and whether it is the returning-user prompt (days 3, 10, 21, 45 of use) rather than the free-week reminder
    val reminderIsReturning: Boolean = false,

    // A failed renewal: the store's "manage subscription" page, set while the payment-problem prompt is due
    val billingIssueUrl: String? = null,
)

// Removed: the store provides the discount (no longer shown; kept so the old HomeOfferCard code still reads)
data class HomeOfferUi(
    val title: String?,          // campaign name from Remote Config, null -> generic title
    val startedAtMillis: Long,
    val durationMin: Int,
    val discountPercent: Int,
    val targetsLifetime: Boolean = false,   // which plan this campaign discounts
)

data class FreeTrialParam(
    val remainingDays: Int = 0,
    val discountPer: Int = 0,
    val discountPerLifeTime: Int = 0,
    val manualFreeTrialDays: Int = 0,
)

data class FreeTrialUiConfig(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    @StringRes val yesText: Int,
    @StringRes val noText: Int? = null,
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
                    title = R.string.ft_title_welcome_3,
                    desc = R.string.ft_desc_full_access,
                    yesText = R.string.ft_btn_start_learning,
                    showTrialStart = true,
                    numberRes = R.drawable.ic_number_3
                )
            }
            daysLeft == 2 -> {
                FreeTrialUiConfig(
                    title = R.string.ft_title_2_remaining,
                    desc = R.string.ft_desc_keep_safe,
                    yesText = R.string.ft_btn_subscribe_now,
                    noText = R.string.ft_btn_maybe_later,
                    showDayLeft = true,
                    showNoButton = true,
                    numberRes = R.drawable.ic_number_2
                )
            }
            daysLeft == 1 -> {
                FreeTrialUiConfig(
                    title = R.string.ft_title_last_day,
                    desc = R.string.ft_desc_stay_on_track,
                    yesText = R.string.ft_btn_subscribe_now,
                    noText = R.string.ft_btn_maybe_later,
                    showDayLeft = true,
                    showNoButton = true,
                    numberRes = R.drawable.ic_number_1
                )
            }
            else -> {
                FreeTrialUiConfig(
                    title = R.string.ft_title_ended,
                    desc = R.string.ft_desc_subscribe_continue,
                    yesText = R.string.view_plans,
                    showDayLeft = true,
                    numberRes = R.drawable.ic_number_0
                )
            }
        }
    }else{
        return when {
            daysLeft >= 7 -> {
                FreeTrialUiConfig(
                    title = R.string.ft_title_welcome_7,
                    desc = R.string.ft_desc_full_access,
                    yesText = R.string.ft_btn_start_learning,
                    showTrialStart = true
                )
            }
            daysLeft == 6 -> FreeTrialUiConfig(
                title = R.string.ft_title_6_left,
                desc = R.string.ft_desc_keep_exploring,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_continue_learning,
                showNow = true,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_6
            )
            daysLeft == 5 -> FreeTrialUiConfig(
                title = R.string.ft_title_5_days,
                desc = R.string.ft_desc_enjoy_lessons,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_keep_going,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_5
            )
            daysLeft == 4 -> FreeTrialUiConfig(
                title = R.string.ft_title_4_left,
                desc = R.string.ft_desc_right_track,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_continue,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_4
            )
            daysLeft == 3 -> FreeTrialUiConfig(
                title = R.string.ft_title_3_remaining,
                desc = R.string.ft_desc_keep_safe,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_maybe_later,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_3
            )
            daysLeft == 2 -> FreeTrialUiConfig(
                title = R.string.ft_title_2_soon,
                desc = R.string.ft_desc_adventure,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_remind_later,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_2
            )
            daysLeft == 1 -> FreeTrialUiConfig(
                title = R.string.ft_title_last_day,
                desc = R.string.ft_desc_stay_on_track,
                yesText = R.string.ft_btn_subscribe_now,
                noText = R.string.ft_btn_maybe_later,
                showDayLeft = true,
                showNoButton = true,
                numberRes = R.drawable.ic_number_1
            )
            else -> FreeTrialUiConfig(
                title = R.string.ft_title_ended,
                desc = R.string.ft_desc_subscribe_continue,
                yesText = R.string.view_plans,
                showDayLeft = true,
                numberRes = R.drawable.ic_number_0
            )
        }
    }

}

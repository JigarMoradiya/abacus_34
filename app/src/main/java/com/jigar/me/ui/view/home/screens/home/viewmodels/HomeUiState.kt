package com.jigar.me.ui.view.home.screens.home.viewmodels

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

    // Streak
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakShields: Int = 0,
    val streakMilestoneAwarded: Int? = null,

    // Review gate — shown right after the streak dialog is dismissed, when eligible
    val showReviewGate: Boolean = false,
    // One-time emotional review ask on day 2 — takes priority over showReviewGate above
    val showDay2Review: Boolean = false,
    val showDay2Feedback: Boolean = false,   // the one-page form behind the thumb down

    // the time-limited offer card on Home (Remote Config `home_offer`), null = none running
    val homeOffer: HomeOfferUi? = null,
    val isPremium: Boolean = false,
    /** owns the lifetime plan: the only people a lifetime offer is hidden from (a yearly or monthly subscriber still sees it) */
    val ownsLifetime: Boolean = false,

    // Freemium time limit: the day 3 / day 7 parent reminder is due (the grown-up question comes first, then the plans)
    val showParentReminder: Boolean = false,
    // ... and whether it is the returning-user prompt (days 3, 10, 21, 45 of use) rather than the free-week reminder
    val reminderIsReturning: Boolean = false,

    // A failed renewal: the store's "manage subscription" page, set while the payment-problem prompt is due
    val billingIssueUrl: String? = null,
)

// The time-limited Home offer card (Remote Config `home_offer`)
data class HomeOfferUi(
    val title: String?,          // campaign name from Remote Config, null -> generic title
    val startedAtMillis: Long,
    val durationMin: Int,
    val discountPercent: Int,
    val targetsLifetime: Boolean = false,   // which plan this campaign discounts
    // a Google Play offer on the lifetime plan (set in the Play Console) instead of the timed Home offer: its "30% OFF" / "₹500 OFF" text,
    // and whether a countdown is shown (never for a store offer: it runs for days)
    val storeBadge: String? = null,
    val storeOriginalPrice: String? = null,
    val endMillis: Long? = null,
    val hasTimer: Boolean = true,
)

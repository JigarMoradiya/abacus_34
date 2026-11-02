package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.annotation.StringRes
import com.jigar.me.R

/** What the user was trying to open when the paywall appeared — drives the sheet's headline. */
enum class PaywallContext(@StringRes val headlineRes: Int, @StringRes val subtitleRes: Int) {
    LEVEL2(R.string.paywall_headline_level2, R.string.paywall_subtitle_level2),
    LEVEL3(R.string.paywall_headline_level3, R.string.paywall_subtitle_level3),
    TABLES(R.string.paywall_headline_tables, R.string.paywall_subtitle_tables),
    PRACTICE(R.string.paywall_headline_practice, R.string.paywall_subtitle_practice),
    EXERCISE(R.string.paywall_headline_exercise, R.string.paywall_subtitle_exercise),
    EXAM(R.string.paywall_headline_exam, R.string.paywall_subtitle_exam),
    CCM(R.string.paywall_headline_ccm, R.string.paywall_subtitle_ccm),
    GAMES(R.string.paywall_headline_games, R.string.paywall_subtitle_games),
    REPORTS(R.string.paywall_headline_reports, R.string.paywall_subtitle_reports),
    GENERIC(R.string.paywall_headline_generic, R.string.paywall_subtitle_generic),
    /** The plans page at the end of the first-launch tutorial (lifetime is not offered there). */
    ONBOARDING(R.string.paywall_headline_onboarding, R.string.paywall_subtitle_onboarding),
    /** The parent reminder on day 3 / day 7 of a new install's free week. */
    FREE_ENDING(R.string.paywall_headline_free_ending, R.string.paywall_subtitle_free_ending),
    /** Any locked tap once the free week is over (the sheet switches to this headline by itself). */
    FREE_ENDED(R.string.paywall_headline_free_ended, R.string.paywall_subtitle_free_ended),
    /** The returning-user prompt: a free parent who keeps coming back (days 3, 10, 21, 45 by default). */
    RETURNING(R.string.paywall_headline_returning, R.string.paywall_subtitle_returning);

    /** Stable name for analytics, the same as on iOS (for example "level3"). */
    val analyticsName: String get() = name.lowercase()
}

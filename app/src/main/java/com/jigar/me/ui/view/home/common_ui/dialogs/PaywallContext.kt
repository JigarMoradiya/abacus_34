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
}

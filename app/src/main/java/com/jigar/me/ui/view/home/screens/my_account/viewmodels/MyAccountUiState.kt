package com.jigar.me.ui.view.home.screens.my_account.viewmodels

import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector
import com.jigar.me.R
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.utils.WeeklySummaryManager


data class MyAccountUiState(
    val error: Int? = null,
    val privacyPolicyUrl: String? = null,
    val statistics: Statistics? = null,
    val isShowLogoutPopup: Boolean = false,
    val isLoggedIn: Boolean = false,
    val weeklyStats: WeeklySummaryManager.WeeklyStats = WeeklySummaryManager.WeeklyStats(0, 0, 0),
)

data class MyAccountMenu(
    val tag: String,
    val menuTitle: String,
    val menuIcon: ImageVector? = null,
    val subMenu: List<MyAccountMenu> = emptyList()
)

fun getMenuList(context: Context, isLoggedIn: Boolean): List<MyAccountMenu> {
    return listOf(
        MyAccountMenu("my_account", context.getString(R.string.my_account),
            subMenu = listOf(
                MyAccountMenu("free_premium", context.getString(R.string.free_and_premium), Icons.Outlined.StarBorder),
                MyAccountMenu("subscription", context.getString(R.string.subscription), Icons.Outlined.Subscriptions),
                MyAccountMenu("report_history", context.getString(R.string.report_history_cards), Icons.Outlined.Assessment),
                MyAccountMenu("setting", context.getString(R.string.settings), Icons.Outlined.Settings),
            )
        ),
        MyAccountMenu("more", context.getString(R.string.more),
            subMenu = buildList {
                add(MyAccountMenu("faqs", context.getString(R.string.faqs), Icons.AutoMirrored.Outlined.Help))
                add(MyAccountMenu("need_help", context.getString(R.string.need_help), Icons.Outlined.SupportAgent))
                add(MyAccountMenu("rate_us_on_the_play_store", context.getString(R.string.rate_us_on_the_play_store), Icons.Outlined.StarRate))
                add(MyAccountMenu("share_app", context.getString(R.string.share_with_friend), Icons.Outlined.Share))
                add(MyAccountMenu("privacy_policy", context.getString(R.string.privacy_policy), Icons.Outlined.PrivacyTip))
                if (isLoggedIn) {
                    add(MyAccountMenu("logout", context.getString(R.string.logout), Icons.AutoMirrored.Outlined.Logout))
                }
            }
        )
    )
}

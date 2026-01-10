package com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector
import com.jigar.me.data.model.data.Statistics


data class MyAccountUiState(
    val error: Int? = null,
    val privacyPolicyUrl: String? = null,
    val statistics: Statistics? = null,
    val isShowLogoutPopup: Boolean = false,
)

data class MyAccountMenu(
    val tag: String,
    val menuTitle: String,
    val menuIcon: ImageVector? = null,
    val subMenu: List<MyAccountMenu> = emptyList()
)

fun getMenuList(): List<MyAccountMenu> {
    return listOf(
        MyAccountMenu("my_account", "My Account",
            subMenu = listOf(
                MyAccountMenu("about_app", "What's Learn in Application", Icons.Outlined.Info),
                MyAccountMenu("subscription", "Subscription", Icons.Outlined.Subscriptions),
                MyAccountMenu("report_history", "Report History Cards", Icons.Outlined.Assessment),
                MyAccountMenu("setting", "Settings", Icons.Outlined.Settings),
            )
        ),
        MyAccountMenu("more", "More",
            subMenu = listOf(
                MyAccountMenu("faqs", "FAQs", Icons.AutoMirrored.Outlined.Help),
                MyAccountMenu("need_help", "Need Help", Icons.Outlined.SupportAgent),
                MyAccountMenu("privacy_policy", "Privacy Policy", Icons.Outlined.PrivacyTip),
                MyAccountMenu("logout", "Logout", Icons.AutoMirrored.Outlined.Logout)
            )
        )
    )
}

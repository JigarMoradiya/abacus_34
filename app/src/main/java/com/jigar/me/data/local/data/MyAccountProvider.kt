package com.jigar.me.data.local.data

import android.content.Context
import com.jigar.me.R

object MyAccountProvider {
    fun getMenuList(context: Context): List<MyAccountMenu>{
        val list = listOf(
            MyAccountMenu("about_app", context.getString(R.string.about_app_label),R.drawable.menu_ic_learn_in_application,isPaid = true),
            MyAccountMenu("subscription", context.getString(R.string.subscription),R.drawable.menu_ic_subscription),
            MyAccountMenu("report_history", context.getString(R.string.report_card),R.drawable.menu_ic_report_history,isPaid = true),
            MyAccountMenu("setting", context.getString(R.string.txt_setting_title),R.drawable.menu_ic_settings),
        )

        return listOf(
            MyAccountMenu("my_account", context.getString(R.string.my_account), null,list),
            MyAccountMenu("more", context.getString(R.string.more_), null,
                listOf(
                    MyAccountMenu("faqs", context.getString(R.string.faqs),R.drawable.menu_ic_faq),
                    MyAccountMenu("need_help", context.getString(R.string.need_help),R.drawable.menu_ic_need_help),
                    MyAccountMenu("privacy_policy", context.getString(R.string.privacy_policy),R.drawable.menu_ic_privacy),
                    MyAccountMenu("logout", context.getString(R.string.logout),R.drawable  .menu_ic_logout)
                ))
        )
    }
}
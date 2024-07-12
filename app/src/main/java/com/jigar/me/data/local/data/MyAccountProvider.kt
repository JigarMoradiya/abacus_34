package com.jigar.me.data.local.data

import android.content.Context
import com.jigar.me.R

object MyAccountProvider {
    fun getMenuList(context: Context): List<MyAccountMenu>{
        val list = listOf(
            MyAccountMenu("subscription", context.getString(R.string.subscription),R.mipmap.ic_account_subscription),
            MyAccountMenu("report_history", context.getString(R.string.report_card),R.mipmap.ic_account_report_history,isPaid = true),
            MyAccountMenu("edit_profile", context.getString(R.string.edit_profile),R.mipmap.ic_account_edit_profile),
            MyAccountMenu("change_password", context.getString(R.string.change_password),R.mipmap.ic_account_change_password),
            MyAccountMenu("setting", context.getString(R.string.txt_setting_title),R.mipmap.ic_account_setting),
        )

        return listOf(
            MyAccountMenu("my_account", context.getString(R.string.my_account), null,list),
            MyAccountMenu("more", context.getString(R.string.more_), null,
                listOf(
//                    MyAccountMenu("about_us", context.getString(R.string.about_us),R.mipmap.ic_account_about_us),
                    MyAccountMenu("faqs", context.getString(R.string.faqs),R.mipmap.ic_account_faq),
                    MyAccountMenu("need_help", context.getString(R.string.need_help),R.mipmap.ic_account_need_help),
                    MyAccountMenu("rate_us_on_the_play_store", context.getString(R.string.rate_us_on_the_play_store),R.mipmap.ic_account_rate_us),
                    MyAccountMenu("privacy_policy", context.getString(R.string.privacy_policy),R.mipmap.ic_account_privacy_policy),
                    MyAccountMenu("logout", context.getString(R.string.logout),R.mipmap.ic_account_logout)
                ))
        )
    }
}
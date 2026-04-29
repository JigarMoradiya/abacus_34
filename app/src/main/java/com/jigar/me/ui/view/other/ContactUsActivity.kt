package com.jigar.me.ui.view.other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.home.common_ui.BackgroundUI
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.other.contactus.ContactUsScreen
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsActivity : BaseActivity() {

    @Inject
    lateinit var preferences: AppPreferencesHelper

    companion object {
        fun getInstance(context: Context?, type: String) {
            Intent(context, ContactUsActivity::class.java).apply {
                putExtra(AppConstants.extras_Comman.type, type)
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BackgroundUI()
            MyApplicationTheme {
                CompositionLocalProvider(LocalPreferencesHelper provides preferences) {
                    ContactUsScreen(onFinish = { finish() })
                }
            }
        }
    }
}

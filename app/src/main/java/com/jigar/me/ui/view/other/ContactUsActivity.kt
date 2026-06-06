package com.jigar.me.ui.view.other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.other.contactus.ContactUsScreen
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUsActivity : ComponentActivity() {

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
            HomePageBackground()
            MyApplicationTheme {
                ContactUsScreen(onFinish = { finish() })
            }
        }
    }
}

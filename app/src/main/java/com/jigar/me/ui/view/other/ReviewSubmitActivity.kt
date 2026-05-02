package com.jigar.me.ui.view.other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.BackgroundUI
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.other.review_submit.ReviewSubmitScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReviewSubmitActivity : ComponentActivity() {

    @Inject
    lateinit var preferences: AppPreferencesHelper

    companion object {
        fun getInstance(context: Context?) {
            Intent(context, ReviewSubmitActivity::class.java).apply {
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
                ReviewSubmitScreen(onFinish = { finish() })
            }
        }
    }
}

package com.jigar.me.ui.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.ui.view.home.common_ui.BackgroundUI
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.login.navigation.LoginNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginDashboardActivity : ComponentActivity() {

    companion object {
        @JvmStatic
        fun getInstance(context: Context?) {
            Intent(context, LoginDashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
                LoginNavGraph(
                    onNavigateToHome = {
                        HomeActivity.getInstance(this@LoginDashboardActivity)
                        finish()
                    },
                    onFinishActivity = { finish() },
                )
            }
        }
    }
}

package com.jigar.me.ui.view.home

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.home.navigation.HomeNavGraph
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.jetpack.fragments.common.LocalPreferencesHelper
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.HomeActivityViewModel
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    @Inject
    lateinit var preferences: AppPreferencesHelper

    @Inject
    lateinit var ttsManager: TextToSpeechManager

    private val homeActivityViewModel: HomeActivityViewModel by viewModels()

    companion object {
        @JvmStatic
        fun getInstance(context: Context?) {
            Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        homeActivityViewModel.fetchAbacusData()
        prefManager.setCustomParamInt(AppConstants.NOTCH_HEIGHT, 0)
        logDeviceQualifiers(this)

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalPreferencesHelper provides preferences) {
                    HomeNavGraph(homeActivityViewModel = homeActivityViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeActivityViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        homeActivityViewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }

    private fun logDeviceQualifiers(context: Context) {
        val config = context.resources.configuration
        prefManager.setCustomParamInt(AppConstants.screenWidthDp, config.screenWidthDp)
    }
}

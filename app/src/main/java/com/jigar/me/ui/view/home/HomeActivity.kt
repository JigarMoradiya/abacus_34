package com.jigar.me.ui.view.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.view.home.common_ui.BackgroundUI
import com.jigar.me.ui.view.home.navigation.HomeNavGraph
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
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

    private var deepLinkRoute by mutableStateOf<String?>(null)

    companion object {
        const val EXTRA_DEEP_LINK_ROUTE = "deep_link_route"

        @JvmStatic
        fun getInstance(context: Context?) {
            Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(this)
            }
        }

        @JvmStatic
        fun getInstance(context: Context?, route: String) {
            Intent(context, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(EXTRA_DEEP_LINK_ROUTE, route)
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deepLinkRoute = intent.getStringExtra(EXTRA_DEEP_LINK_ROUTE)
        homeActivityViewModel.fetchAbacusData()
        logDeviceQualifiers(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        // Detect notch
        window.decorView.post {
            DeviceInfo.hasNotch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.decorView.rootWindowInsets?.displayCutout != null
            } else {
                false
            }
            // Detect tablet
            DeviceInfo.isLargeTablet = resources.configuration.smallestScreenWidthDp >= 840 && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            DeviceInfo.isTablet = resources.configuration.smallestScreenWidthDp >= 600 && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            // initialize audio player
            AudioPlayerManager.init(this)

            setContent {
                BackgroundUI()
                MyApplicationTheme {
                    CompositionLocalProvider(LocalPreferencesHelper provides preferences) {
                        HomeNavGraph(
                            homeActivityViewModel = homeActivityViewModel,
                            initialRoute = deepLinkRoute,
                            onInitialRouteHandled = { deepLinkRoute = null }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkRoute = intent.getStringExtra(EXTRA_DEEP_LINK_ROUTE)
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

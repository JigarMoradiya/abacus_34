package com.jigar.me.ui.view.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.TextToSpeechManager
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.navigation.HomeNavGraph
import com.jigar.me.ui.view.home.theme.MyApplicationTheme
import com.jigar.me.ui.view.ab.AbDebugScreens
import com.jigar.me.ui.view.ab.AbThemeController
import com.jigar.me.BuildConfig
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.LocalTextToSpeechManager
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeActivityViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

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
        logDeviceQualifiers(this)
        AbThemeController.init(preferences)

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

            com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags.showResult = BuildConfig.DEBUG && intent.getBooleanExtra("ab_show_result", false)
            val abScreen = if (BuildConfig.DEBUG) intent.getStringExtra("ab_screen") else null
            if (abScreen != null) {
                // Debug-only preview of one restyled screen (see AbKitGallery.kt), skipping the normal app shell.
                setContent { AbDebugScreens.Render(abScreen, intent.getBooleanExtra("ab_dark", false)) }
                return@post
            }
            setContent {
                HomePageBackground()
                MyApplicationTheme {
                    CompositionLocalProvider(
                        LocalPreferencesHelper provides preferences,
                        LocalTextToSpeechManager provides ttsManager
                    ) {
                        // The app is full screen: if the status or navigation bar is showing (it can come back after a dialog,
                        // the shade, or on a tablet), the screens must not move down to make room for it. Their windowInsetsPadding
                        // keeps only the camera-cutout part of the insets.
                        androidx.compose.foundation.layout.Box(
                            Modifier.fillMaxSize().consumeWindowInsets(WindowInsets.systemBars)
                        ) {
                            HomeNavGraph(
                                homeActivityViewModel = homeActivityViewModel,
                                initialRoute = deepLinkRoute,
                                onInitialRouteHandled = { deepLinkRoute = null },
                                onFinishActivity = { finish() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Back from a dialog / the shade / another app, the system bars can stay visible: hide them again.
        if (hasFocus) WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
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

    @OptIn(UnstableApi::class)
    private fun logDeviceQualifiers(context: Context) {
        val config = context.resources.configuration
        preferences.setCustomParamInt(AppConstants.screenWidthDp, config.screenWidthDp)
    }
}

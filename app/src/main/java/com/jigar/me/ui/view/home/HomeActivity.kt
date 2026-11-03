package com.jigar.me.ui.view.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import com.jigar.me.ui.view.home.navigation.RouteNavigation
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
class HomeActivity : AppCompatActivity() {   // AppCompat so the chosen app language also applies on Android 7-12

    @Inject
    lateinit var preferences: AppPreferencesHelper

    @Inject
    lateinit var ttsManager: TextToSpeechManager

    private val homeActivityViewModel: HomeActivityViewModel by viewModels()

    private var deepLinkRoute by mutableStateOf<String?>(null)

    companion object {
        const val EXTRA_DEEP_LINK_ROUTE = "deep_link_route"

        /** The screens our own notifications open. This activity is exported, so any other app could send any route string: only these
         *  (and, in a debug build, any route for testing) are followed; anything else is ignored. */
        private val ALLOWED_DEEP_LINKS = setOf(
            RouteNavigation.Home.route, RouteNavigation.Purchase.route,
            RouteNavigation.Level1Home.route, RouteNavigation.Level2Home.route, RouteNavigation.Level3Home.route,
            RouteNavigation.Level4TablePicker.route,
        )

        fun allowedDeepLink(route: String?): String? =
            if (route != null && (BuildConfig.DEBUG || route in ALLOWED_DEEP_LINKS)) route else null

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

        deepLinkRoute = allowedDeepLink(intent.getStringExtra(EXTRA_DEEP_LINK_ROUTE))
        logDeviceQualifiers(this)
        AbThemeController.init(preferences)
        // the voice follows the app language: after a language change this screen is created again, so the voice is set again too
        ttsManager.applySettings()
        // debug builds only: `--ez ab_dark_app true` forces dark mode for screenshot runs
        if (BuildConfig.DEBUG && intent.getBooleanExtra("ab_dark_app", false)) AbThemeController.set(com.jigar.me.ui.view.ab.AbThemeMode.Dark)

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
            com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags.paywallTrialDays = if (BuildConfig.DEBUG) intent.getIntExtra("ab_paywall_trial", 0) else 0
            // Freemium time limit: refresh the cached state the gates read; debug builds can force it
            // (`--ei ab_freemium_days 7 --ei ab_freemium_core 0 --ei ab_freemium_day 8`)
            if (BuildConfig.DEBUG) {
                com.jigar.me.utils.FreemiumWindow.debugDays = if (intent.hasExtra("ab_freemium_days")) intent.getIntExtra("ab_freemium_days", 0) else null
                com.jigar.me.utils.FreemiumWindow.debugCore = if (intent.hasExtra("ab_freemium_core")) intent.getIntExtra("ab_freemium_core", 2) else null
                com.jigar.me.utils.FreemiumWindow.debugDay = if (intent.hasExtra("ab_freemium_day")) intent.getIntExtra("ab_freemium_day", 0) else null
            }
            if (BuildConfig.DEBUG) {
                com.jigar.me.utils.FreemiumWindow.debugBillingIssue = intent.getBooleanExtra("ab_billing_issue", false)
                com.jigar.me.utils.FreemiumWindow.debugReturningDay = if (intent.hasExtra("ab_returning_day")) intent.getIntExtra("ab_returning_day", 0) else null
            }
            com.jigar.me.utils.FreemiumWindow.refresh(preferences)
            if (BuildConfig.DEBUG && intent.getBooleanExtra("ab_freemium_notify_now", false)) com.jigar.me.utils.FreemiumWindow.debugNotifyNow(this)
            if (BuildConfig.DEBUG && intent.getBooleanExtra("ab_notify_now", false)) {
                // the streak reminder and the weekly summary right now, to read them in the notification shade
                com.jigar.me.utils.StreakManager.debugShowNow = true
                val work = androidx.work.WorkManager.getInstance(this)
                work.enqueue(androidx.work.OneTimeWorkRequestBuilder<com.jigar.me.utils.workers.StreakNotificationWorker>().build())
                work.enqueue(androidx.work.OneTimeWorkRequestBuilder<com.jigar.me.utils.workers.WeeklySummaryWorker>().build())
            }
            val abScreen = if (BuildConfig.DEBUG) intent.getStringExtra("ab_screen") else null
            if (abScreen != null) {
                // Debug-only preview of one restyled screen (see AbKitGallery.kt), skipping the normal app shell.
                setContent {
                    CompositionLocalProvider(
                        LocalPreferencesHelper provides preferences,
                        LocalTextToSpeechManager provides ttsManager
                    ) { AbDebugScreens.Render(abScreen, intent.getBooleanExtra("ab_dark", false)) }
                }
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
        deepLinkRoute = allowedDeepLink(intent.getStringExtra(EXTRA_DEEP_LINK_ROUTE))
    }

    override fun onResume() {
        super.onResume()
        com.jigar.me.utils.FreemiumWindow.refresh(preferences)   // the free week may have ended while the app was in the background
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

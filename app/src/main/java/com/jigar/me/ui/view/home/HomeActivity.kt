package com.jigar.me.ui.view.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
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
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbThemeController
import com.jigar.me.ui.view.ab.AbThemeMode
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.windowSky
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
        // English is the default language, not the phone's (needs an activity: AppCompat reaches the system setting through it)
        com.jigar.me.utils.LanguageManager.startUp(preferences.getCustomParam(AppConstants.RemoteConfig.enabledLanguages, ""))

        deepLinkRoute = allowedDeepLink(intent.getStringExtra(EXTRA_DEEP_LINK_ROUTE))
        logDeviceQualifiers(this)
        AbThemeController.init(preferences)
        // A language change creates this screen again. Until its first frame is drawn the window shows its own background, which was the
        // theme's near-white (a white flash): paint it with the sky the screens draw instead, in the mode the person chose.
        val sky = AbWorld.Meadow.windowSky(if (AbThemeController.mode == AbThemeMode.Dark) AbColors.Dark else AbColors.Light)
        window.setBackgroundDrawable(GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(sky.first.toArgb(), sky.second.toArgb())))
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
                if (intent.getBooleanExtra("ab_home_offer", false)) {
                    preferences.setCustomParam(AppConstants.RemoteConfig.homeOffer, "{\"enabled\":true,\"duration_min\":10,\"name\":\"Special offer\",\"product\":\"yearly\"}")
                    com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags.homeOfferPreview = true
                }
                if (intent.getBooleanExtra("ab_home_store_offer", false)) com.jigar.me.ui.view.home.screens.math_game_zone.common.AbDebugFlags.homeStoreOfferPreview = true
                // Streak state for tests (`--es ab_streak 2,1,0,0` = streak 2, last counted day 1 day ago, 0 shields, 0 problems today; no badges claimed yet)
                intent.getStringExtra("ab_streak")?.split(",")?.map { it.trim().toIntOrNull() ?: 0 }?.takeIf { it.size >= 4 }?.let { v ->
                    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                    val last = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -v[1]) }
                    preferences.setCustomParamInt(AppConstants.Streak.currentStreak, v[0])
                    preferences.setCustomParamInt(AppConstants.Streak.longestStreak, maxOf(v[0], preferences.getCustomParamInt(AppConstants.Streak.longestStreak, 0)))
                    preferences.setCustomParam(AppConstants.Streak.lastActivityDate, fmt.format(last.time))
                    preferences.setCustomParamInt(AppConstants.Streak.streakShields, v[2])
                    preferences.setCustomParam(AppConstants.Streak.todayDate, fmt.format(java.util.Calendar.getInstance().time))
                    preferences.setCustomParamInt(AppConstants.Streak.todayProblems, v[3])
                    preferences.setCustomParam(AppConstants.Streak.claimedRewards, "")
                    preferences.setCustomParamInt(AppConstants.Streak.pendingMilestone, 0)
                }
                // Level 2 stars for screenshots (`--es ab_l2_stars 3,3,3,2,0,3,3,3,3,3`, one per chapter 1..10; the first value also forgets that the two Level 2 cards were shown)
                intent.getStringExtra("ab_l2_stars")?.split(",")?.forEachIndexed { i, n ->
                    n.trim().toIntOrNull()?.let { preferences.setCustomParamInt("l2_quiz_stars_${i + 1}", it) }
                    if (i == 0) { preferences.setCustomParamBoolean("l2_free_card_shown", false); preferences.setCustomParamBoolean("l2_done_card_shown", false) }
                }
                // Level 1 stars for screenshots (`--es ab_l1_stars 3,3,2,3,2`, one per lesson 1..5; `--es ab_l1_stars 0,0,0,0,0` clears them, which also
                // forgets that the "You finished Level 1!" card was shown)
                intent.getStringExtra("ab_l1_stars")?.split(",")?.forEachIndexed { i, n ->
                    n.trim().toIntOrNull()?.let { preferences.setCustomParamInt("l1_quiz_stars_${i + 1}", it) }
                    if (i == 0) preferences.setCustomParamBoolean("l1_complete_card_shown", false)
                }
            }
            com.jigar.me.utils.FreemiumWindow.refresh(preferences)
            if (BuildConfig.DEBUG && intent.getBooleanExtra("ab_freemium_notify_now", false)) com.jigar.me.utils.FreemiumWindow.debugNotifyNow(this)
            if (BuildConfig.DEBUG) intent.getStringExtra("ab_offer_notify_now")?.let { com.jigar.me.utils.OfferNotifications.debugNotifyNow(this, it) }
            if (BuildConfig.DEBUG && intent.getBooleanExtra("ab_notify_now", false)) {
                // the streak reminder and the weekly summary right now, to read them in the notification shade
                com.jigar.me.utils.StreakManager.debugShowNow = true
                com.jigar.me.utils.StreakManager.debugTitleIndex = if (intent.hasExtra("ab_streak_title")) intent.getIntExtra("ab_streak_title", 0) else null
                com.jigar.me.utils.StreakManager.debugStreak = if (intent.hasExtra("ab_streak_value")) intent.getIntExtra("ab_streak_value", 1) else null
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
                        LocalTextToSpeechManager provides ttsManager,
                        LocalConfiguration provides (uiConfiguration ?: LocalConfiguration.current),
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

    // The configuration the screens read (null until the first change: Compose's own is used). On Android 13+ the system tells Compose about
    // a language change; on Android 7-12 AppCompat only updates the resources and calls onConfigurationChanged, so the screens get a new
    // configuration from here to draw their strings again.
    private var uiConfiguration by mutableStateOf<Configuration?>(null)

    // A language change is handled here and the screen is NOT created again (that showed black and blank frames for a moment): the screens
    // draw their strings again in the new language, the layout flips for a right-to-left language, and the voice follows the language.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        window.decorView.layoutDirection = newConfig.layoutDirection
        uiConfiguration = Configuration(resources.configuration)
        ttsManager.applySettings()
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

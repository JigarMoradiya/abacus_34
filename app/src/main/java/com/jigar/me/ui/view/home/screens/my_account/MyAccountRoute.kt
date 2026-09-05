package com.jigar.me.ui.view.home.screens.my_account

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.home.HomeActivity
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.common_ui.dialogs.FreemiumLoginBottomSheet
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateBottomSheet
import com.jigar.me.ui.view.home.screens.my_account.components.MyAccountScreen
import com.jigar.me.ui.view.home.screens.my_account.viewmodels.MyAccountViewModel
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.AppReviewManager
import com.jigar.me.utils.ParentalGateSessionCache
import com.jigar.me.utils.extensions.openMail
import com.jigar.me.utils.extensions.openURL
import kotlinx.coroutines.launch

@Composable
fun MyAccountRoute(
    onBackClick: () -> Unit,
    onNavigateToFAQs: () -> Unit,
    onNavigateToPurchase: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReportHistory: () -> Unit,
    onNavigateToWhatsLearning: () -> Unit,
    onNavigateToCredentials: () -> Unit,
) {
    val viewModel: MyAccountViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prefs = LocalPreferencesHelper.current
    var showLoginSheet by remember { mutableStateOf(false) }
    var showLoginSheetForHistory by remember { mutableStateOf(false) }
    var showURLGate by remember { mutableStateOf(false) }
    var pendingUrlAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showActionGate by remember { mutableStateOf(false) }
    var pendingNavAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun openURLWithGate(action: () -> Unit) {
        if (ParentalGateSessionCache.urlGatePassedThisSession) action()
        else { pendingUrlAction = action; showURLGate = true }
    }
    fun openWithGate(action: () -> Unit) {
        pendingNavAction = action; showActionGate = true
    }
    var showRateUsSheet by remember { mutableStateOf(false) }
//    var showDebugPreview by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            BackButtonWithText(
                title = stringResource(R.string.parent),
                onBackClick = onBackClick
            )
            MyAccountScreen(uiState, modifier = Modifier.weight(1f)) { tag ->
                when (tag) {
                    "login" -> showLoginSheet = true
                    "faqs" -> openWithGate { onNavigateToFAQs() }
                    "subscription" -> openWithGate { onNavigateToPurchase() }
                    "setting" -> onNavigateToSettings()
                    "report_history" -> {
                        if (uiState.isLoggedIn) onNavigateToReportHistory()
                        else showLoginSheetForHistory = true
                    }
                    "about_app" -> onNavigateToWhatsLearning()
                    "rate_us_on_the_play_store" -> openWithGate { showRateUsSheet = true }
                    "need_help" -> openWithGate {
                        ContactUsActivity.getInstance(context, AppConstants.extras_Comman.typeNeedHelp)
                    }
                    "privacy_policy" -> {
                        uiState.privacyPolicyUrl?.let { url -> openURLWithGate { context.openURL(url) } }
                    }
                    "logout" -> openWithGate { viewModel.logoutOpenClose(true) }
                }
            }

//            if (BuildConfig.DEBUG) {
//                DebugReviewGateCard(prefs, onPreviewClick = { showDebugPreview = true })
//            }
        }

        // Sheets inside the Box are guaranteed to draw over the full screen content.
//        if (BuildConfig.DEBUG) {
//            val activity = LocalContext.current as? Activity
//            val scope = rememberCoroutineScope()
//            ReviewGateBottomSheet(
//                visible = showDebugPreview,
//                onDismiss = { showDebugPreview = false },
//                onNegative = {
//                    showDebugPreview = false
//                    AppReviewManager.onGateNegative(prefs)
//                },
//                onPositive = {
//                    showDebugPreview = false
//                    activity?.let { act -> scope.launch { AppReviewManager.onGatePositive(prefs, act) } }
//                }
//            )
//        }

        ReviewGateBottomSheet(
            visible = showRateUsSheet,
            onDismiss = { showRateUsSheet = false },
            onNegative = {
                showRateUsSheet = false
                context.openMail(prefs)
            },
            onPositive = {
                showRateUsSheet = false
                openURLWithGate { context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}") }
            }
        )

        AnimatedVisibility(
            visible = uiState.isShowLogoutPopup,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CustomPopupView(
                title = stringResource(R.string.logout_alert),
                description = stringResource(R.string.logout_alert_msg),
                positiveButtonText = stringResource(R.string.yes_i_m_sure),
                negativeButtonText = stringResource(R.string.no),
                icon = R.drawable.ic_alert,
                theme = PopupTheme.CONFIRM,
                accent = ButtonType.RED,
                widthMultiplier = 0.5f,
                onPositiveTapped = {
                    viewModel.makeLogout()
                    HomeActivity.getInstance(context)
                },
                onNegativeTapped = { viewModel.logoutOpenClose(false) }
            )
        }
    }

    if (showLoginSheet) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = false,
            subtitle = stringResource(R.string.login_to_view_account_description),
            onLoginSuccess = {
                showLoginSheet = false
                viewModel.onLoginSuccess()
            },
            onNavigateToCredentials = {
                showLoginSheet = false
                onNavigateToCredentials()
            },
            onDismiss = { showLoginSheet = false }
        )
    }

    if (showLoginSheetForHistory) {
        FreemiumLoginBottomSheet(
            showContinueWithoutSaving = false,
            onLoginSuccess = {
                showLoginSheetForHistory = false
                viewModel.onLoginSuccess()
                onNavigateToReportHistory()
            },
            onDismiss = { showLoginSheetForHistory = false }
        )
    }

    if (showURLGate) {
        ParentalGateDialog(
            onPassed = {
                showURLGate = false
                ParentalGateSessionCache.markPassed()
                pendingUrlAction?.invoke()
                pendingUrlAction = null
            },
            onCancelled = { showURLGate = false; pendingUrlAction = null }
        )
    }

    if (showActionGate) {
        ParentalGateDialog(
            onPassed = {
                showActionGate = false
                pendingNavAction?.invoke()
                pendingNavAction = null
            },
            onCancelled = { showActionGate = false; pendingNavAction = null }
        )
    }

}

// ── DEBUG ONLY — review-gate test harness ──────────────────────────────────
// Never compiled into release builds (gated by BuildConfig.DEBUG at the call
// site). Lets you force/reset the milestone state so any of the 13 real
// trigger sites (quiz, Anzan/Flash/Guided/SpeedDrill, table drill, exam, ccm,
// exercise, abacus practice, streak milestone, app-open) will show the gate
// on the very next qualifying action — or preview the sheet right here.
@Composable
private fun DebugReviewGateCard(prefs: AppPreferencesHelper, onPreviewClick: () -> Unit) {
    var refreshTick by remember { mutableIntStateOf(0) }

    val nextMilestone = remember(refreshTick) {
        prefs.getCustomParamInt(AppConstants.Review.nextMilestoneDay, AppConstants.Review.milestones.first())
    }
    val lastAskDate = remember(refreshTick) {
        prefs.getCustomParam(AppConstants.Review.lastAskDate, "").ifBlank { "(none)" }
    }
    val totalActiveDays = remember(refreshTick) {
        prefs.getCustomParamInt(AppConstants.Streak.totalActiveDays, 0)
    }
    val eligibleNow = remember(refreshTick) { AppReviewManager.shouldShowReviewGate(prefs) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text("🧪 DEBUG — Review Gate Test Harness", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
        Text("totalActiveDays = $totalActiveDays")
        Text("nextMilestoneDay = $nextMilestone")
        Text("lastAskDate = $lastAskDate")
        Text("shouldShowReviewGate() = $eligibleNow")

        Row(
            modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                // Makes the gate eligible right now, anywhere in the app.
                prefs.setCustomParamInt(AppConstants.Review.nextMilestoneDay, 0)
                prefs.setCustomParam(AppConstants.Review.lastAskDate, "")
                refreshTick++
            }) { Text("Force Eligible") }

            Button(onClick = {
                // Back to a fresh-install state.
                prefs.setCustomParamInt(AppConstants.Review.nextMilestoneDay, AppConstants.Review.milestones.first())
                prefs.setCustomParam(AppConstants.Review.lastAskDate, "")
                refreshTick++
            }) { Text("Reset") }

            Button(onClick = { onPreviewClick() }) { Text("Preview Sheet") }
        }
    }
}
package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.DeviceInfo.screenHorizontalPadding
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.AbacusQuestionPanel
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.AbacusViewItem
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.SetTimer
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.SumCompleteCelebration
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems.UseWhichHandTextUi
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.screens.reports.dialogs.ExerciseExamCompleteResultDialog
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.login.screens.login_home.viewmodels.LoginHomeViewModel
import com.jigar.me.utils.extensions.toastL

@Composable
fun AbacusDoPracticeRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: AbacusDoPracticeViewModel = hiltViewModel()
    val loginViewModel: LoginHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginUiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.pauseSetTimer()
                viewModel.persistSetTime()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel.abacusCalc.stateVersion) {
        viewModel.handleMatch()
    }

    // After Google login succeeds, submit the pending result
    loginUiState.navigateToHome?.consume {
        uiState.submitExerciseRequest?.let { viewModel.submitExamApi(it) }
    }

    loginUiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            context.toastL(msg)
            loginViewModel.consumeError()
        }
    }

    // Where the hand instruction line at the foot of the screen starts, and where the question panel's room is (both in
    // root coordinates), so the panel can stop just above that line.
    var handLineTop by remember { mutableFloatStateOf(0f) }
    var panelTop by remember { mutableFloatStateOf(0f) }
    var panelHeight by remember { mutableFloatStateOf(0f) }
    val localDensity = LocalDensity.current
    // A phone keeps a little air at the top of the panel, which reaches down to just above the hand line. That is
    // measured (not a fixed number) so it also holds on every screen size.
    val panelTopInset = if (DeviceInfo.isTablet) 0.dp else 6.dp
    val panelBottomInset = if (handLineTop > 0f && panelHeight > 0f) {
        with(localDensity) { ((panelTop + panelHeight - handLineTop) / density + 6f).coerceAtLeast(0f).dp }
    } else if (DeviceInfo.isTablet) 0.dp else 40.dp
    val abacusOnLeft = viewModel.isAbacusOnLeftHand

    AbTheme {
    Box(Modifier.fillMaxSize()) {
    AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())

    // abacus and questions: the abacus keeps clear of the system bars, the question panel uses the full height of the screen
    Row(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .padding(
                start = if (abacusOnLeft) screenHorizontalPadding() else 0.dp,
                end = if (!abacusOnLeft) Dimens16 else if (DeviceInfo.isTablet) 0.dp else Dimens10,
            )
    ) {
        val abacusBody: @Composable () -> Unit = {
            Box(
                Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                    // on a phone the header is tall next to the screen, so only the abacus is kept clear of it
                    .padding(top = if (DeviceInfo.isTablet) 0.dp else 54.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.currentAbacus != null) {
                    AbacusViewItem(viewModel, uiState)
                }
            }
        }
        val questionPanel: @Composable () -> Unit = {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .onGloballyPositioned { panelTop = it.positionInRoot().y; panelHeight = it.size.height.toFloat() }
            ) {
                AbacusQuestionPanel(
                    viewModel = viewModel,
                    uiState = uiState,
                    topInset = panelTopInset,
                    bottomInset = panelBottomInset,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        if (abacusOnLeft) {
            abacusBody()
            questionPanel()
        } else {
            questionPanel()
            abacusBody()
        }
    }

    // Header and the hand instruction line, above the abacus and the panel
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        AbTopBar(
            title = "Abacus No : ${(uiState.currentIndexOfAbacus + 1)}",
            onBack = onBackClick,
            modifier = Modifier.padding(
                start = screenHorizontalPadding(),
                end = Dimens16,
                top = DeviceInfo.screenTopPadding(),
            ),
            trailing = { SetTimer(uiState) },
        )

        Spacer(Modifier.weight(1f))

        UseWhichHandTextUi(uiState, viewModel, onTopChanged = { handLineTop = it })
    }

    // Quick, non-blocking sparkle burst when a sum finishes in step-by-step
    // or final-answer mode — purely decorative, so it never traps input or
    // pauses the timer/practice flow while it plays.
    if (uiState.isSumComplete && (uiState.isStepByStep || uiState.isFinalAnswer)) {
        SumCompleteCelebration()
    }

    if (uiState.isLoading || loginUiState.isLoading) {
        Loader()
    }

    AnimatedVisibility(
        visible = uiState.isShowCompletePopup,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (uiState.isShowSubmitAnswer == true) {
            uiState.submitExerciseRequest?.let {
                ExerciseExamCompleteResultDialog(
                    selectedTheme = viewModel.selectedTheme,
                    request = it,
                    saveResults = uiState.saveResults,
                    onLoginToSave = if (!uiState.saveResults && !uiState.resultSaved) {
                        { loginViewModel.signInWithGoogle(context) }
                    } else null,
                    onClose = {
                        val pct = (it.no_of_right_answers ?: 0).toFloat() / (it.no_of_questions ?: 1).toFloat()
                        if (pct >= 0.8f) reviewGate.attempt(prefs) { onBackClick() } else onBackClick()
                    },
                    onGiveAgain = onBackClick,
                )
            }
        } else {
            CustomPopupView(
                title = stringResource(R.string.congratulations),
                description = stringResource(R.string.txt_set_completed_msg),
                positiveButtonText = stringResource(R.string.ok_thanks),
                negativeButtonText = stringResource(R.string.close),
                icon = R.drawable.ic_alert_complete_page,
                theme = PopupTheme.CELEBRATION,
                accent = ButtonType.ORANGE,
                widthMultiplier = 0.5f,
                onPositiveTapped = onBackClick,
                onNegativeTapped = onBackClick
            )
        }
    }

    }
    }

    ReviewGateHost(reviewGate, prefs)
}

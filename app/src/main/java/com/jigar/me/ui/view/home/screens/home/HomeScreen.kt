package com.jigar.me.ui.view.home.screens.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.ContentScale
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.abCardEntrance
import com.jigar.me.ui.view.ab.abSheetEntrance
import com.jigar.me.ui.view.home.screens.home.components.HomeStreakChip
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbHeroTitle
import com.jigar.me.ui.view.ab.AbParentalGate
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.home.components.HomeSectionDivider
import com.jigar.me.ui.view.home.screens.home.components.HomeSpeechBubble
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.material3.CardDefaults
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.Loader
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.ui.view.home.common_ui.MadeInIndiaBadge
import com.jigar.me.ui.view.home.common_ui.SafeAdFreeBadge
import com.jigar.me.ui.view.home.common_ui.isIndianUser
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.PopupTheme
import com.jigar.me.ui.view.home.common_ui.dialogs.FreeTrialDialog
import com.jigar.me.ui.view.home.common_ui.sheets.Day2ReviewGateBottomSheet
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateBottomSheet
import com.jigar.me.ui.view.home.screens.home.components.HomeMenuScreen
import com.jigar.me.ui.view.home.screens.home.components.HomeOfferBadge
import com.jigar.me.ui.view.home.screens.home.components.HomeOfferCard
import com.jigar.me.ui.view.home.screens.home.components.HomeOfferSidebarCard
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeFragmentViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens14
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.common_ui.dialogs.ParentalGateDialog
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.checkPermissions
import kotlin.math.max
import kotlin.math.min
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.utils.WeeklySummaryManager
import androidx.core.net.toUri
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens28
import com.jigar.me.ui.view.home.theme.PrimaryBlue

// ── Pill shape ────────────────────────────────────────────────────────────────

private val PillShape = RoundedCornerShape(percent = 50)

// ── Speech bubble shape ───────────────────────────────────────────────────────

private data class SpeechBubbleShapeImpl(
    private val cornerRadiusPx: Float,
    private val tailWidthPx: Float,
    private val tailHeightPx: Float,
    private val tailOffsetXPx: Float
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val cr = min(cornerRadiusPx, (size.height - tailHeightPx) / 2)
        val bH = size.height - tailHeightPx
        val midX = size.width / 2f + tailOffsetXPx
        val tailL = midX - tailWidthPx / 2f
        val tailR = midX + tailWidthPx / 2f

        path.moveTo(cr, 0f)
        path.lineTo(size.width - cr, 0f)
        path.arcTo(Rect(size.width - 2 * cr, 0f, size.width, 2 * cr), -90f, 90f, false)
        path.lineTo(size.width, bH - cr)
        path.arcTo(Rect(size.width - 2 * cr, bH - 2 * cr, size.width, bH), 0f, 90f, false)
        path.lineTo(tailR, bH)
        path.lineTo(midX, size.height)
        path.lineTo(tailL, bH)
        path.lineTo(cr, bH)
        path.arcTo(Rect(0f, bH - 2 * cr, 2 * cr, bH), 90f, 90f, false)
        path.lineTo(0f, cr)
        path.arcTo(Rect(0f, 0f, 2 * cr, 2 * cr), 180f, 90f, false)
        path.close()
        return Outline.Generic(path)
    }
}

@Composable
private fun rememberSpeechBubbleShape(
    cornerRadius: Dp,
    tailWidth: Dp = 18.dp,
    tailHeight: Dp = 10.dp,
    tailOffsetX: Dp = 36.dp
): Shape {
    val density = LocalDensity.current
    return remember(density, cornerRadius, tailWidth, tailHeight, tailOffsetX) {
        with(density) {
            SpeechBubbleShapeImpl(
                cornerRadiusPx = cornerRadius.toPx(),
                tailWidthPx = tailWidth.toPx(),
                tailHeightPx = tailHeight.toPx(),
                tailOffsetXPx = tailOffsetX.toPx()
            )
        }
    }
}

// ── Main Screen ───────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onNavigateToLevelCategory: (levelId: String) -> Unit,
    onNavigateToAbacusFreeMode: () -> Unit,
    onNavigateToMathGameZone: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMyAccount: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToExamHome: () -> Unit,
    onNavigateToCCMHome: () -> Unit,
    onNavigateToPurchase: () -> Unit,
    onNavigateToYoutubeVideo: () -> Unit,
    onNavigateToWhatsLearning: () -> Unit,
    onNavigateToLevel1: () -> Unit,
    onNavigateToLevel2: () -> Unit,
    onNavigateToLevel3: () -> Unit,
    onNavigateToLevel4: () -> Unit,
) {
    val viewModel: HomeFragmentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showURLGate by remember { mutableStateOf(false) }
    var pendingUrlAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showWeeklyReport by remember { mutableStateOf(false) }
    // Tracked locally (not via ParentalGateSessionCache) because opening an
    // external link backgrounds the app, which resets that session cache --
    // relying on it here would re-ask the gate on every single tap instead of
    // once per visit to this screen.
    var gateResolvedThisVisit by remember { mutableStateOf(false) }

    fun openURLWithGate(action: () -> Unit) {
        if (gateResolvedThisVisit) action()
        else { pendingUrlAction = action; showURLGate = true }
    }

    val resumeActivityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* no-op */ }

    val requestMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* denial is handled on the next app open, not immediately */ }

    fun onMenuClick(level: String, id: String? = null) {
        when (level) {
            AppConstants.HomeClicks.Menu_Practice_Abacus  -> id?.let { onNavigateToLevelCategory(it) }
            AppConstants.HomeClicks.Menu_Abacus_Free_Mode -> onNavigateToAbacusFreeMode()
            AppConstants.HomeClicks.Menu_Math_Game        -> onNavigateToMathGameZone()
            AppConstants.HomeClicks.Menu_Settings         -> onNavigateToSettings()
            AppConstants.HomeClicks.Menu_My_Account       -> onNavigateToMyAccount()
            AppConstants.HomeClicks.Menu_Abacus_Exercise  -> onNavigateToExercise()
            AppConstants.HomeClicks.Menu_Exam             -> onNavigateToExamHome()
            AppConstants.HomeClicks.Menu_CCM              -> onNavigateToCCMHome()
            AppConstants.HomeClicks.Menu_Purchase_Store   -> onNavigateToPurchase()
            AppConstants.HomeClicks.Menu_Video_Tutorial   -> onNavigateToYoutubeVideo()
        }
    }

    // Mascot float animation
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val mascotOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "mascotFloat"
    )

    val bubbleShape = rememberSpeechBubbleShape(cornerRadius = 16.dp)

    AbTheme {
    Box(Modifier.fillMaxSize()) {
    AbBackground(AbWorld.Meadow, Modifier.fillMaxSize())
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalW = maxWidth
        val totalH = maxHeight
        val isTablet = DeviceInfo.isTablet
        val mascotMaxH = min(totalH.value * 0.55f, 240f).dp
        // Time-limited offer (Remote Config `home_offer`) -- hidden for premium users.
        // Hoisted above the left/right panel split so both can react to it: on tablet,
        // it (or "This Week" when there's no active offer) sits below the mascot, and
        // "This Week" additionally moves next to the level pager while the offer's on.
        val activeOffer = uiState.homeOffer?.takeIf { !uiState.isPremium }

        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            // ── Full-width top bar ─────────────────────────────────────────
            HomeTopBar(
                // the kit buttons play the click cue themselves
                onMyAccountClick = { onMenuClick(AppConstants.HomeClicks.Menu_My_Account) },
                onSettingsClick = { onMenuClick(AppConstants.HomeClicks.Menu_Settings) }
            )

            // ── Body: mascot left (22%) + content right (78%) ─────────────
            Row(modifier = Modifier.fillMaxSize()) {

                // ── Left panel: speech bubble + mascot (+ banner on tablet) ──
                Column(
                    modifier = Modifier
                        .width(totalW * if (DeviceInfo.isTablet) 0.25f else 0.22f)
                        .fillMaxHeight()
                        .padding(horizontal = Dimens10),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isTablet) Spacer(modifier = Modifier.weight(1f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isTablet) Modifier.padding(top = Dimens12)
                                else Modifier.padding(bottom = Dimens12)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens12)
                    ) {
                        // Speech bubble: a friendly greeting, drawn from the kit colours
                        HomeSpeechBubble()

                        Image(
                            painter = painterResource(R.drawable._mascot_),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(mascotMaxH)
                                .fillMaxWidth()
                                .graphicsLayer { translationY = mascotOffsetY }
                        )
                    }

                    if (isTablet) {
                        // Badge sits just below the mascot with a small fixed gap. Only
                        // "This Week" anchors the very bottom (the offer card has its
                        // own dedicated slot in the right panel's bottom row instead).
                        Spacer(modifier = Modifier.height(Dimens12))
                        if (isIndianUser(context)) {
                            MadeInIndiaBadge()
                        } else {
                            SafeAdFreeBadge()
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        WeeklyReportCard(viewModel.weeklyStats()) { showWeeklyReport = true }
                        Spacer(modifier = Modifier.height(Dimens8))
                    }
                }

                // ── Right panel: streak | level pager | menu list ──────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Streak + level cards pager (+ "This Week" on phone only, whenever
                    // the offer badge has taken its usual bottom-row spot; on tablet
                    // "This Week" has its own permanent slot below the mascot instead).
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens16)
                            .padding(bottom = Dimens8),
                        horizontalArrangement = Arrangement.spacedBy(Dimens12, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.padding(bottom = Dimens12)) {
                            StreakCard(
                                currentStreak = uiState.currentStreak,
                                bestStreak    = uiState.longestStreak,
                                shields       = uiState.streakShields
                            )
                        }
                        LevelsPager(
                            modifier = Modifier.width(if (isTablet) 280.dp else 220.dp),
                            onLevel1 = onNavigateToLevel1,
                            onLevel2 = onNavigateToLevel2,
                            onLevel3 = onNavigateToLevel3,
                            onLevel4 = onNavigateToLevel4,
                        )
                        if (activeOffer != null && !isTablet) {
                            WeeklyReportCard(viewModel.weeklyStats()) { showWeeklyReport = true }
                        }
                    }

                    // "Choose your activity" divider: rods fading out to the sides and a coloured pill with the sentence
                    HomeSectionDivider(
                        text = "Choose your activity and start learning!",
                        modifier = Modifier.padding(horizontal = Dimens16).padding(bottom = Dimens4)
                    )

                    // Horizontal menu list
                    HomeMenuScreen(
                        uiState = uiState,
                        onMenuClick = { onMenuClick(it.name, it.id) },
                        modifier = Modifier.weight(1f)
                    )

                    // Offer badge (or weekly report) + Made in India/Safe badge + English
                    // cross-promo — phone only. When the timed offer is active it takes
                    // this first slot (compact, same size class as its neighbours) and
                    // "This Week" moves up to the top row instead -- see activeOffer above.
                    if (!isTablet) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens16)
                                .padding(top = Dimens8, bottom = Dimens12), // same as iOS
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (activeOffer != null) {
                                HomeOfferBadge(
                                    title = activeOffer.title,
                                    startedAtMillis = activeOffer.startedAtMillis,
                                    durationMin = activeOffer.durationMin,
                                    discountPercent = activeOffer.discountPercent,
                                    targetsLifetime = activeOffer.targetsLifetime,
                                    onClick = { AudioPlayerManager.playSoundBtnClick(); onNavigateToPurchase() },
                                    onExpired = viewModel::onHomeOfferExpired,
                                )
                            } else {
                                WeeklyReportCard(viewModel.weeklyStats()) { showWeeklyReport = true }
                            }
                            if (isIndianUser(context)) {
                                MadeInIndiaBadge()
                            } else {
                                SafeAdFreeBadge()
                            }
                            EnglishBanner(context = context, onUrlClick = {
                            openURLWithGate {
                                runCatching {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = "https://play.google.com/store/apps/details?id=com.vedaavi.english.learning&hl=en".toUri()
                                        addCategory(Intent.CATEGORY_BROWSABLE)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        })
                        }
                    } else {
                        // Tablet: home offer card (only when active) bottom-left, wrapped
                        // to its own natural width -- NOT stretched/weighted -- and
                        // English cross-promo always bottom-right, also wrapped. Neither
                        // card is weighted, so Arrangement.SpaceBetween supplies the gap
                        // as pure whitespace instead of either card stretching into it.
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens16)
                                .padding(bottom = Dimens12)
                                .padding(top = Dimens8),
                            horizontalArrangement = if (activeOffer != null) Arrangement.SpaceBetween else Arrangement.End,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (activeOffer != null) {
                                HomeOfferSidebarCard(
                                    title = activeOffer.title,
                                    startedAtMillis = activeOffer.startedAtMillis,
                                    durationMin = activeOffer.durationMin,
                                    discountPercent = activeOffer.discountPercent,
                                    targetsLifetime = activeOffer.targetsLifetime,
                                    onClick = { AudioPlayerManager.playSoundBtnClick(); onNavigateToPurchase() },
                                    onExpired = viewModel::onHomeOfferExpired
                                )
                            }
                            EnglishBanner(context = context, onUrlClick = {
                                openURLWithGate {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = "https://play.google.com/store/apps/details?id=com.vedaavi.english.learning&hl=en".toUri()
                                            addCategory(Intent.CATEGORY_BROWSABLE)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }

        // ── Dialogs ───────────────────────────────────────────────────────

        if (uiState.isLoading == true) Loader()

        AnimatedVisibility(
            visible = uiState.isShowPurchasedConflictPopup,
            enter = fadeIn(), exit = fadeOut()
        ) {
            val title = if (uiState.purchasedConflictPopupType ==
                AppConstants.APIStatus.ERROR_CODE_THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER
            ) {
                stringResource(R.string.your_login_is_associated_with_other_purchases)
            } else {
                stringResource(R.string.your_device_purchases_is_associated_with_other_login)
            }
            CustomPopupView(
                title = title,
                description = stringResource(R.string.want_to_move_purchase_with_this_login),
                positiveButtonText = stringResource(R.string.yes_i_want_to_move),
                negativeButtonText = stringResource(R.string.no_move_later),
                notes = stringResource(R.string.no_move_later_msg),
                widthMultiplier = 0.8f,
                onPositiveTapped = { viewModel.closeConflictPopup() },
                onNegativeTapped = { viewModel.closeConflictPopup() },
                icon = R.drawable.ic_alert,
                theme = PopupTheme.CONFIRM,
                accent = ButtonType.BLUE
            )
        }

        uiState.checkNotificationPermission?.consume {
            context.checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
        }

        if (uiState.isShowNotificationSettingPopup) {
            NotificationPermissionSheet(
                onGoToSettings = {
                    viewModel.showHideNotificationSettingPopup(false)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .apply { data = Uri.fromParts("package", context.packageName, null) }
                    resumeActivityResultLauncher.launch(intent)
                },
                onDismiss = { viewModel.showHideNotificationSettingPopup(false) }
            )
        }

        AnimatedVisibility(
            visible = uiState.isShowFreeTrialPopup,
            enter = fadeIn(), exit = fadeOut()
        ) {
            uiState.freeTrialParam?.let { freeTrialParam ->
                FreeTrialDialog(
                    remainingDays = freeTrialParam.remainingDays,
                    discountPer = freeTrialParam.discountPer,
                    discountPerLifetime = freeTrialParam.discountPerLifeTime,
                    manualFreeTrialDays = freeTrialParam.manualFreeTrialDays,
                    onYes = {
                        viewModel.hideFreeTrialPopup()
                        if (freeTrialParam.remainingDays >= 7) onNavigateToWhatsLearning()
                        else onNavigateToPurchase()
                    },
                    onNo = { viewModel.hideFreeTrialPopup() },
                    onDismiss = { viewModel.hideFreeTrialPopup() }
                )
            }
        }

        // ── Streak milestone reward dialog ─────────────────────────────
        uiState.streakMilestoneAwarded?.let { milestone ->
            StreakRewardDialog(
                milestone = milestone,
                onDismiss = { viewModel.dismissStreakMilestone() }
            )
        }

        // ── Review gate sheet — shown after the streak dialog (if any) clears ──
        val reviewActivity = LocalContext.current as? android.app.Activity
        ReviewGateBottomSheet(
            visible = uiState.showReviewGate,
            onDismiss = { viewModel.onReviewGateNegative() },
            onNegative = { viewModel.onReviewGateNegative() },
            onPositive = { reviewActivity?.let { viewModel.onReviewGatePositive(it) } }
        )

        // ── One-time emotional review ask on day 2 ──
        // System back-press must also stamp the "shown" flag, same as tap-outside --
        // otherwise a back-press dismissal leaves it unset and the sheet re-qualifies
        // to show again on the next app open.
        BackHandler(enabled = uiState.showDay2Review) { viewModel.dismissDay2Review() }
        Day2ReviewGateBottomSheet(
            visible = uiState.showDay2Review,
            onDismiss = { viewModel.dismissDay2Review() },
            onFiveStars = {
                viewModel.onDay2ReviewFiveStars()
                context.openURL("https://play.google.com/store/apps/details?id=${context.packageName}")
            },
            onLowRating = { viewModel.onDay2ReviewLowRating() }
        )

        if (showURLGate) {
            AbParentalGate(
                onSuccess = {
                    showURLGate = false
                    gateResolvedThisVisit = true
                    pendingUrlAction?.invoke()
                    pendingUrlAction = null
                },
                onClose = { showURLGate = false; pendingUrlAction = null }
            )
        }

        if (showWeeklyReport) {
            WeeklyReportDialog(viewModel.weeklyStats()) { showWeeklyReport = false }
        }
    }
    }
    }
}

// ── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(
    onMyAccountClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens16)
            .padding(top = DeviceInfo.screenTopPadding(), bottom = Dimens8),
        contentAlignment = Alignment.Center
    ) {
        // Centre: app title with its tagline and the abacus emoji
        AbHeroTitle(
            title = "Vedaavi Abacus",
            tagline = "Count • Calculate • Conquer",
            taglineIcon = painterResource(R.drawable.abacus_emoji),
        )

        // Left: My Account pill
        AbButton(
            text = "Parent", tone = AbTone.Primary, size = AbSize.Compact, icon = Icons.Default.Person,
            modifier = Modifier.align(Alignment.CenterStart), onClick = onMyAccountClick,
        )

        // Right: Settings pill
        AbButton(
            text = "Settings", tone = AbTone.Success, size = AbSize.Compact, icon = Icons.Default.Settings,
            modifier = Modifier.align(Alignment.CenterEnd), onClick = onSettingsClick,
        )
    }
}

// ── Streak Card ───────────────────────────────────────────────────────────────

@Composable
private fun StreakCard(currentStreak: Int, bestStreak: Int, shields: Int = 0) {
    val dayLabel = "$currentStreak Day${if (currentStreak == 1) "" else "s"}"
    val shieldsStr = if (shields > 0) " " + "🛡️".repeat(shields) else ""
    HomeStreakChip(title = dayLabel + shieldsStr, subtitle = "Streak  •  Best: $bestStreak")
}

// ── Streak Reward Dialog ──────────────────────────────────────────────────────

@Composable
private fun StreakRewardDialog(milestone: Int, onDismiss: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val badgeName = AppConstants.Streak.milestoneNames[milestone] ?: "Champion"
    LaunchedEffect(Unit) { AudioPlayerManager.playSoundClap() }
    val gold = Color(0xFFFFB800)
    val goldEdge = Color(0xFFB07A00)
    val shape = RoundedCornerShape(AbShapes.lg * s)
    // The dimmed backdrop only fades and swallows taps (outside does nothing); the gold card alone pops in.
    AbPopupLayer(onScrimTap = null) { layerWidth, _, shown ->
        Column(
            modifier = Modifier
                .offset(y = 24.dp * s)
                .width(minOf(layerWidth * 0.55f, 520.dp * s))
                .abCardEntrance(shown)
                .shadow(24.dp * s, shape, clip = false, ambientColor = gold, spotColor = gold)
                .padding(bottom = 6.dp * s)
                .drawBehind {
                    val r = AbShapes.lg.toPx() * s
                    drawRoundRect(goldEdge, Offset(0f, 6.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .background(gold.copy(alpha = if (colors.isDark) 0.12f else 0.10f), shape)
                .border(3.dp, gold, shape)
                .padding(start = 20.dp * s, end = 20.dp * s, top = 8.dp * s, bottom = 18.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp * s),
        ) {
            // trophy badge, half outside the card
            Box(
                Modifier
                    .negativeTopPadding(52.dp * s)
                    .size(84.dp * s)
                    .drawBehind {
                        drawCircle(goldEdge, center = center.copy(y = center.y + 5.dp.toPx() * s))
                        drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFE680), gold)))
                        drawCircle(Color.White, radius = size.minDimension / 2 - 2.dp.toPx(), style = Stroke(4.dp.toPx()))
                    },
                contentAlignment = Alignment.Center,
            ) { Text("🏆", fontSize = (44 * s).sp) }

            Text(
                "Milestone Reached!",
                style = AbTypography.title(s),
                color = colors.accentOrange.let { if (colors.isDark) it else it.abDarkened(0.15f) },
            )

            // the streak count as a gold pill
            Row(
                Modifier
                    .height(38.dp * s)
                    .padding(bottom = 3.dp * s)
                    .drawBehind {
                        val c = CornerRadius(size.height / 2)
                        drawRoundRect(goldEdge, Offset(0f, 3.dp.toPx() * s), size, c)
                        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFF2A91C))), size = size, cornerRadius = c)
                        val sw = 2.dp.toPx()
                        drawRoundRect(
                            Color.White.copy(alpha = 0.85f), Offset(sw / 2, sw / 2), Size(size.width - sw, size.height - sw),
                            CornerRadius(size.height / 2 - sw / 2), style = Stroke(sw),
                        )
                    }
                    .padding(horizontal = 16.dp * s),
                horizontalArrangement = Arrangement.spacedBy(6.dp * s),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painterResource(R.drawable.fire), null, Modifier.size(22.dp * s), contentScale = ContentScale.Fit)
                Text(
                    "$milestone-Day Streak",
                    style = AbTypography.fixed(20f * s, FontWeight.ExtraBold).copy(shadow = Shadow(Color.Black.copy(alpha = 0.2f), Offset(0f, 1f), 0f)),
                    color = Color.White,
                )
            }

            Text(
                "You earned the \"$badgeName\" badge! 🎉",
                style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp * s),
            )

            AbButton(
                text = "Awesome! Keep Going 🔥", tone = AbTone.Warning, size = AbSize.Medium, fillWidth = true,
                modifier = Modifier.padding(top = 4.dp * s), onClick = onDismiss,
            )
        }
    }
}

/** Pulls the content up by [amount] and gives that room back to the layout (SwiftUI's negative padding). */
private fun Modifier.negativeTopPadding(amount: Dp): Modifier = layout { measurable, constraints ->
    val p = measurable.measure(constraints)
    val up = amount.roundToPx()
    layout(p.width, (p.height - up).coerceAtLeast(0)) { p.place(0, -up) }
}

// ── Notification Permission Bottom Sheet ──────────────────────────────────────

@Composable
private fun NotificationPermissionSheet(
    onGoToSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    // The dimmed backdrop only fades; the sheet alone rises from the bottom edge.
    AbPopupLayer(onScrimTap = onDismiss) { layerWidth, _, shown ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            Row(
                modifier = Modifier
                    .width(layerWidth * 0.8f)
                    .abSheetEntrance(shown)
                    .pointerInput(Unit) { detectTapGestures(onTap = { }) }
                    .shadow(20.dp, sheet, clip = false, ambientColor = Color.Black, spotColor = Color.Black)
                    .background(colors.bgSecondary, sheet)
                    .border(3.dp, colors.brandPrimary.copy(alpha = if (colors.isDark) 0.7f else 0.4f), sheet)
                    .navigationBarsPadding()
                    .padding(start = 24.dp * s, end = 24.dp * s, top = 20.dp * s, bottom = 16.dp * s),
                horizontalArrangement = Arrangement.spacedBy(20.dp * s),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // ── Left: icon + title + description ──
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp * s),
                ) {
                    AbHeroBadge(icon = Icons.Rounded.Notifications, accent = colors.accentPink, size = 56.dp * s)
                    // the title and its line below sit close: Baloo's line box leaves a wide empty band between two lines
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((-4).dp * s)) {
                        Text(
                            "Stay on Top of Learning", style = AbTypography.headline(s), color = colors.accentPink,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "Allow notifications so the app can gently remind your child when it's the perfect time to practice. You're always in control — turn off anytime from Settings.",
                            style = AbTypography.fixed(13f * s, FontWeight.SemiBold), color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                // ── Right: steps + buttons ──
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp * s),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.7f), RoundedCornerShape(AbShapes.md * s))
                            .padding(horizontal = 14.dp * s, vertical = 10.dp * s),
                        verticalArrangement = Arrangement.spacedBy(8.dp * s),
                    ) {
                        NotificationStep(number = "1", text = "Tap \"Go to Settings\" below")
                        NotificationStep(number = "2", text = "Tap \"Notifications\"")
                        NotificationStep(number = "3", text = "Turn on \"Allow Notifications\"")
                    }
                    AbButton(
                        text = "Go to Settings", tone = AbTone.Primary, size = AbSize.Medium, icon = Icons.Default.Settings,
                        fillWidth = true, onClick = onGoToSettings,
                    )
                    Box(
                        Modifier
                            .abSquash(cornerRadius = AbShapes.sm * s, sound = AbSound.Back, onClick = onDismiss)
                            .padding(vertical = 4.dp * s, horizontal = 12.dp * s),
                    ) {
                        Text("Maybe Later", style = AbTypography.label(s), color = colors.textSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationStep(number: String, text: String) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp * s)) {
        Box(Modifier.size(22.dp * s).background(colors.brandPrimary, CircleShape), contentAlignment = Alignment.Center) {
            Text(number, color = Color.White, style = AbTypography.fixed(12f * s, FontWeight.ExtraBold))
        }
        Text(text, style = AbTypography.fixed(13f * s, FontWeight.SemiBold), color = colors.textPrimary)
    }
}

// ── Level Cards Pager ─────────────────────────────────────────────────────────

private data class LevelInfo(
    val number: Int, val emoji: String, val title: String,
    val start: Color, val end: Color
)

private val levelInfoList = listOf(
    LevelInfo(1, "🔢", "Bead Basics",    Color(0xFF2E7D32), Color(0xFF43A047)),
    LevelInfo(2, "➕", "Add & Subtract", Color(0xFF1565C0), Color(0xFF1976D2)),
    LevelInfo(3, "⚡", "Speed & Anzan",  Color(0xFF6A1B9A), Color(0xFF7B1FA2)),
    LevelInfo(4, "✖️", "Times Tables",   Color(0xFFE65100), Color(0xFFF57C00)),
)

@Composable
private fun LevelsPager(
    onLevel1: () -> Unit,
    onLevel2: () -> Unit,
    onLevel3: () -> Unit,
    onLevel4: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = remember { listOf(onLevel1, onLevel2, onLevel3, onLevel4) }
    val pagerState = rememberPagerState(pageCount = { levelInfoList.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % levelInfoList.size)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens4)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            LevelMiniCard(info = levelInfoList[page], onClick = actions[page])
        }
        val dotInk = LocalAbColors.current.textPrimary
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens4), verticalAlignment = Alignment.CenterVertically) {
            repeat(levelInfoList.size) { i ->
                val selected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .size(if (selected) Dimens8 else Dimens6)
                        .background(dotInk.copy(alpha = if (selected) 0.8f else 0.25f), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun LevelMiniCard(info: LevelInfo, onClick: () -> Unit) {
    val s = LocalAbScale.current.factor
    // A chunky capsule: the card's own gradient on a darker 3D edge that sinks under the press.
    Row(
        modifier = Modifier
            .padding(horizontal = Dimens6)
            .padding(top = Dimens6, bottom = AppDimens.Dimens2)
            .fillMaxWidth()
            .abSquash(
                face = Color.Transparent, edge = info.start.abDarkened(0.3f), stroke = null,
                cornerRadius = AbShapes.pill, edgeHeight = 4.dp, sound = null,
                onClick = { AudioPlayerManager.playSoundBtnClick(); onClick() },
            )
            .background(Brush.horizontalGradient(listOf(info.start, info.end)), PillShape)
            .padding(horizontal = Dimens14, vertical = Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens8)
    ) {
        Box(
            modifier = Modifier
                .size((34 * s).dp)
                .background(Color.White.copy(alpha = 0.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = info.emoji, fontSize = (15 * s).sp)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy((-6f * s).dp)) {
            Text(
                text = "LEVEL ${info.number}",
                style = AbTypography.fixed(11.5f * s, FontWeight.Bold).copy(letterSpacing = 1.sp),
                color = Color.White.copy(alpha = 0.85f)
            )
            Text(
                text = info.title,
                style = AbTypography.fixed(17f * s, FontWeight.Black),
                color = Color.White,
                maxLines = 1
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size((12 * s).dp)
        )
    }
}

// ── English App Banner ────────────────────────────────────────────────────────

@Composable
private fun EnglishBanner(context: Context, onUrlClick: () -> Unit) {
    val isTablet = DeviceInfo.isTablet
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val ink = colors.textPrimary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "bannerScale"
    )
    val clickAction: () -> Unit = { onUrlClick() }
    val chevronTint = Color(0xFF9374EF).copy(alpha = 0.55f)

    if (isTablet) {
        // Tablet: icon left + 3-line text + chevron right. Each line gets an exact-height box so Baloo's tall line box
        // does not spread the lines apart.
        Row(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(Color(0xFF5532D2).copy(alpha = 0.10f), RoundedCornerShape(percent = 50))
                .border(1.dp, Color(0xFF9374EF).copy(alpha = 0.35f), RoundedCornerShape(percent = 50))
                .clickable(interactionSource = interactionSource, indication = null, onClick = clickAction)
                .padding(horizontal = Dimens12, vertical = AppDimens.Dimens2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens8)
        ) {
            Image(
                painter = painterResource(R.drawable._english),
                contentDescription = null,
                modifier = Modifier.size(Dimens24)
            )
            Column {
                BannerLine("Also try", 9f * s, FontWeight.SemiBold, ink.copy(alpha = 0.55f))
                BannerLine("Vedaavi English", 11f * s, FontWeight.Bold, ink.copy(alpha = 0.75f))
                BannerLine("Fun learning for kids", 9f * s, FontWeight.SemiBold, ink.copy(alpha = 0.55f))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronTint,
                modifier = Modifier.size((9 * s).dp)
            )
        }
    } else {
        // Phone: horizontal compact layout
        Row(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(Color(0xFF5532D2).copy(alpha = 0.10f), RoundedCornerShape(percent = 50))
                .border(1.dp, Color(0xFF9374EF).copy(alpha = 0.35f), RoundedCornerShape(percent = 50))
                .clickable(interactionSource = interactionSource, indication = null, onClick = clickAction)
                .padding(horizontal = Dimens8, vertical = Dimens4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens6)
        ) {
            Image(
                painter = painterResource(R.drawable._english),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Column {
                BannerLine("Also try: Vedaavi English!", 11f * s, FontWeight.Bold, ink.copy(alpha = 0.75f))
                BannerLine("Fun learning for kids", 9f * s, FontWeight.SemiBold, ink.copy(alpha = 0.55f))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronTint,
                modifier = Modifier.size((9 * s).dp)
            )
        }
    }
}

/** One line of the English banner in an exact-height box (the font size is also the line height). */
@Composable
private fun BannerLine(text: String, size: Float, weight: FontWeight, color: Color) {
    androidx.compose.foundation.layout.Box(Modifier.height((size * 1.1f).dp), contentAlignment = Alignment.CenterStart) {
        Text(
            text = text, color = color, style = AbTypography.fixed(size, weight), maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.wrapContentHeight(unbounded = true)
        )
    }
}

// ── Weekly parent report ────────────────────────────────────────────────────

@Composable
private fun WeeklyReportCard(stats: WeeklySummaryManager.WeeklyStats, onClick: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(
        modifier = Modifier
            .background(Color(0xFF2E7D32).copy(alpha = 0.10f), RoundedCornerShape(percent = 50))
            .border(1.dp, Color(0xFF43A047).copy(alpha = 0.35f), RoundedCornerShape(percent = 50))
            // the tap ripple follows the pill: clip to its shape before the click area
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)
    ) {
        Text("📊", fontSize = (11 * s).sp)
        // the title and its line below sit close: Baloo's line box leaves a band between two lines
        Column(verticalArrangement = Arrangement.spacedBy((-3).dp * s)) {
            Text(
                text = "This Week",
                color = colors.textPrimary.copy(alpha = 0.85f),
                style = AbTypography.fixed(11f * s, FontWeight.ExtraBold)
            )
            Text(
                text = if (stats.problems > 0) "${stats.problems} solved · ${max(1, stats.days)}d" else "Tap to view",
                color = colors.textPrimary.copy(alpha = 0.6f),
                style = AbTypography.fixed(9f * s, FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun WeeklyReportDialog(stats: WeeklySummaryManager.WeeklyStats, onDismiss: () -> Unit) {
    // The dimmed backdrop only fades (tapping it closes); the card alone pops in.
    AbPopupLayer(onScrimTap = onDismiss) { _, _, shown ->
        Box(Modifier.abCardEntrance(shown)) { WeeklyReportContent(stats, onClose = onDismiss) }
    }
}

@Composable
fun WeeklyReportContent(
    stats: WeeklySummaryManager.WeeklyStats,
    onClose: (() -> Unit)? = null,
    compact: Boolean = false, // smaller paddings: used on My Account, like iOS
    modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val abScale = LocalAbScale.current
    // A tablet gets a bigger card (iOS: 1 / 1.45 / 1.7)
    val s = if (abScale.factor >= 1.5f) 1.7f else if (abScale.isTablet) 1.45f else 1f
    val isPopup = onClose != null
    val shape = RoundedCornerShape(AppDimens.Dimens20)
    val edge = if (compact) 3.dp else 6.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // popup rows sit close together (the gaps were about 27 dp, Baloo's line boxes already add their own air)
        verticalArrangement = Arrangement.spacedBy(if (compact) AppDimens.Dimens10 else AppDimens.Dimens4),
        modifier = modifier
            .then(if (isPopup) Modifier.width(minOf(androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp * 0.6f, 540.dp * s)) else Modifier)
            .shadow(if (compact) 5.dp else 14.dp, shape, clip = false, ambientColor = Color.Black.copy(alpha = if (compact) 0.08f else 0.25f), spotColor = Color.Black.copy(alpha = if (compact) 0.08f else 0.25f))
            .padding(bottom = edge)
            .drawBehind {
                val r = AppDimens.Dimens20.toPx()
                drawRoundRect(colors.outline, Offset(0f, edge.toPx()), size, CornerRadius(r, r))
            }
            .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.97f else 0.95f), shape)
            .border(if (compact) 2.dp else 3.dp, colors.accentGreen.copy(alpha = if (colors.isDark) 0.7f else 0.55f), shape)
            .padding(
                horizontal = if (compact) AppDimens.Dimens16 else AppDimens.Dimens30,
                vertical = if (compact) AppDimens.Dimens12 else AppDimens.Dimens24
            )
    ) {
        // Coloured header band: popup presentation only; the inline My Account embed keeps a plain title.
        val titleSize = (if (compact) 22f else 28f) * s
        if (isPopup) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppDimens.Dimens4)
                    .clip(RoundedCornerShape(AppDimens.Dimens14))
                    .background(Brush.horizontalGradient(listOf(colors.brandPrimary, colors.brandPrimaryDark)))
                    .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10),
                contentAlignment = Alignment.Center,
            ) { Text("📊 This Week", color = Color.White, style = AbTypography.fixed(titleSize, FontWeight.ExtraBold)) }
        } else {
            Text("📊 This Week", color = colors.brandPrimary, style = AbTypography.fixed(titleSize, FontWeight.ExtraBold))
        }

        if (stats.problems > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(if (compact) AppDimens.Dimens8 else AppDimens.Dimens12)) {
                WRTile("⭐", "${stats.problems}", "Problems", colors.brandPrimary, compact, s)
                WRTile("📅", "${max(1, stats.days)}", "Days", colors.accentGreen, compact, s)
                WRTile("🔥", "${stats.streak}", "Streak", colors.accentOrange, compact, s)
            }
            Text(
                "Great progress this week — keep it going! 🌟", color = colors.textSecondary, textAlign = TextAlign.Center,
                style = AbTypography.fixed((if (compact) 14f else 17f) * s, FontWeight.SemiBold),
            )
        } else {
            Text("🌱", fontSize = ((if (compact) 30 else 40) * s).sp)
            Text(
                "No practice yet this week", textAlign = TextAlign.Center,
                color = colors.accentGreen.let { if (colors.isDark) it else it.abDarkened(0.2f) },
                style = AbTypography.fixed((if (compact) 17f else 22f) * s, FontWeight.ExtraBold),
            )
            Text(
                "Practice together to build this week's report!", color = colors.textSecondary, textAlign = TextAlign.Center,
                style = AbTypography.fixed((if (compact) 14f else 17f) * s, FontWeight.SemiBold),
            )
        }
        if (onClose != null) {
            AbButton(
                text = stringResource(R.string.close), tone = AbTone.Warning, size = AbSize.Medium, icon = Icons.Default.Check,
                modifier = Modifier.padding(top = AppDimens.Dimens4), onClick = onClose,
            )
        }
    }
}

@Composable
private fun WRTile(emoji: String, value: String, label: String, color: Color, compact: Boolean, s: Float) {
    val colors = LocalAbColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
        modifier = Modifier
            .widthIn(min = ((if (compact) 54 else 68) * s).dp)
            .background(color.copy(alpha = if (colors.isDark) 0.18f else 0.12f), RoundedCornerShape(AppDimens.Dimens12))
            .padding(horizontal = AppDimens.Dimens8, vertical = if (compact) AppDimens.Dimens8 else AppDimens.Dimens10)
    ) {
        Text(emoji, fontSize = ((if (compact) 24 else 30) * s).sp)
        Text(value, color = color, style = AbTypography.fixed((if (compact) 22f else 28f) * s, FontWeight.ExtraBold))
        Text(label, color = colors.textSecondary, style = AbTypography.fixed(11f * s, FontWeight.SemiBold))
    }
}

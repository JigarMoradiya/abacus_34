package com.jigar.me.ui.view.home.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.jigar.me.ui.view.home.common_ui.dialogs.CustomPopupView
import com.jigar.me.ui.view.home.common_ui.dialogs.FreeTrialDialog
import com.jigar.me.ui.view.home.screens.home.components.HomeMenuScreen
import com.jigar.me.ui.view.home.screens.home.viewmodels.HomeFragmentViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens14
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.checkPermissions
import kotlin.math.min
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

// ── Today's table accent palette ──────────────────────────────────────────────

private val tableAccentPalette = listOf(
    Color(0xFFE53935), Color(0xFFF57C00), Color(0xFFF9A825),
    Color(0xFF00ACC1), Color(0xFF00897B), Color(0xFF7B1FA2),
    Color(0xFFC2185B), Color(0xFF5E35B1), Color(0xFF00695C),
    Color(0xFFE64A19), Color(0xFF1565C0), Color(0xFF2E7D32),
    Color(0xFF0984E3), Color(0xFFBF360C)
)

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
) {
    val viewModel: HomeFragmentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val resumeActivityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* no-op */ }

    val requestMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.filter { !it.value }.also {
            if (it.isNotEmpty()) viewModel.showHideNotificationSettingPopup(true)
        }
    }

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val totalW = maxWidth
        val totalH = maxHeight
        val isTablet = DeviceInfo.isTablet
        val mascotMaxH = min(totalH.value * 0.55f, 240f).dp

        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            // ── Full-width top bar ─────────────────────────────────────────
            HomeTopBar(
                onMyAccountClick = {
                    AudioPlayerManager.playSoundBtnClick()
                    onMenuClick(AppConstants.HomeClicks.Menu_My_Account)
                },
                onSettingsClick = {
                    AudioPlayerManager.playSoundBtnClick()
                    onMenuClick(AppConstants.HomeClicks.Menu_Settings)
                }
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = Dimens4,
                                    shape = bubbleShape,
                                    ambientColor = PrimaryBlue.copy(alpha = 0.12f),
                                    spotColor = PrimaryBlue.copy(alpha = 0.12f)
                                )
                                .background(Color.White.copy(alpha = 0.95f), bubbleShape)
                                .border(1.5.dp, PrimaryBlue.copy(alpha = 0.3f), bubbleShape)
                                .padding(horizontal = Dimens12)
                                .padding(top = Dimens10)
                                .padding(bottom = Dimens20),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Dimens4)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo_small),
                                contentDescription = null,
                                modifier = Modifier.size(Dimens28)
                            )
                            Text(
                                text = "Ready to count\n& calculate today?",
                                color = PrimaryBlue,
                                style = MaterialTheme.typography.labelSmall.scaled(),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

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
                        Spacer(modifier = Modifier.weight(1f))
                        EnglishBanner(context = context)
                        Spacer(modifier = Modifier.height(Dimens8))
                    }
                }

                // ── Right panel: streak | today's table | menu list ────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Streak + today's table
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens16)
                            .padding(bottom = Dimens8),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StreakCard(currentStreak = 0, bestStreak = 0)
                        Spacer(modifier = Modifier.width(Dimens12))
                        TodayTableCard()
                    }

                    // "Choose your activity" divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens16)
                            .padding(bottom = Dimens4),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = PrimaryBlue.copy(alpha = 0.35f),
                            thickness = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(Dimens6))
                        Image(
                            painter = painterResource(R.drawable.fireworks),
                            contentDescription = null,
                            modifier = Modifier.size(if (DeviceInfo.isTablet) Dimens20 else Dimens16)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Choose your activity and start learning!",
                            color = PrimaryBlue.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelSmall.scaled(),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(Dimens6))
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = PrimaryBlue.copy(alpha = 0.35f),
                            thickness = 1.5.dp
                        )
                    }

                    // Horizontal menu list
                    HomeMenuScreen(
                        uiState = uiState,
                        onMenuClick = {
                            AudioPlayerManager.playSoundBtnClick()
                            onMenuClick(it.name, it.id)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // English app cross-promo banner (phone only; tablet shows it in mascot panel)
                    if (!isTablet) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens16)
                                .padding(bottom = Dimens8),
                            horizontalArrangement = Arrangement.End
                        ) {
                            EnglishBanner(context = context)
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
                onNegativeTapped = { viewModel.closeConflictPopup() }
            )
        }

        uiState.checkNotificationPermission?.consume {
            context.checkPermissions(Constants.NOTIFICATION_PERMISSION, requestMultiplePermissions)
        }

        AnimatedVisibility(
            visible = uiState.isShowNotificationSettingPopup,
            enter = fadeIn(), exit = fadeOut()
        ) {
            CustomPopupView(
                title = stringResource(R.string.permission_alert),
                description = stringResource(R.string.notification_permission_msg),
                positiveButtonText = stringResource(R.string.okay),
                negativeButtonText = stringResource(R.string.give_later),
                widthMultiplier = 0.7f,
                onPositiveTapped = {
                    viewModel.showHideNotificationSettingPopup(false)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    resumeActivityResultLauncher.launch(intent)
                },
                onNegativeTapped = { viewModel.showHideNotificationSettingPopup(false) }
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
        // Center: app title
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Vedaavi Abacus",
                style = MaterialTheme.typography.displayLarge.scaled(),
                fontWeight = FontWeight.Black,
                color = PrimaryBlue,
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens4)
            ) {
                Text(
                    text = "Count • Calculate • Conquer",
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue.copy(alpha = 0.75f)
                )
                Image(
                    painter = painterResource(R.drawable.abacus_emoji),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens16)
                )
            }
        }

        // Left: My Account pill
        GradientPill(
            text = "Parent",
            icon = Icons.Default.Person,
            gradient = Brush.linearGradient(listOf(Color(0xFF42A5F5), PrimaryBlue)),
            shadowColor = PrimaryBlue.copy(alpha = 0.45f),
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onMyAccountClick
        )

        // Right: Settings pill
        GradientPill(
            text = "Settings",
            icon = Icons.Default.Settings,
            gradient = Brush.linearGradient(listOf(Color(0xFF43A047), Color(0xFF2E7D32))),
            shadowColor = Color(0xFF2E7D32).copy(alpha = 0.45f),
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun GradientPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    shadowColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "pillScale"
    )
    Row(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(elevation = Dimens6, shape = PillShape, spotColor = shadowColor, ambientColor = shadowColor)
            .background(gradient, PillShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = Dimens12, vertical = Dimens4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens6)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(Dimens16)
        )
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium.scaled(),
            fontWeight = FontWeight.Bold
        )
    }
}

// ── Streak Card ───────────────────────────────────────────────────────────────

@Composable
private fun StreakCard(currentStreak: Int, bestStreak: Int) {
    Row(
        modifier = Modifier
            .shadow(
                elevation = Dimens4, shape = PillShape,
                ambientColor = PrimaryBlue.copy(alpha = 0.28f),
                spotColor   = PrimaryBlue.copy(alpha = 0.28f)
            )
            .background(
                brush = Brush.verticalGradient(listOf(Color(0xFFE8EAF6), Color(0xFFE3F2FD))),
                shape = PillShape
            )
            .border(1.5.dp, PrimaryBlue.copy(alpha = 0.5f), PillShape)
            .padding(horizontal = Dimens14, vertical = Dimens8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens8)
    ) {
        Text(text = "🔥", style = MaterialTheme.typography.titleMedium.scaled())
        Column {
            Text(
                text = "$currentStreak Day${if (currentStreak == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                fontWeight = FontWeight.Black,
                color = PrimaryBlue
            )
            Text(
                text = "Streak  •  Best: $bestStreak",
                style = MaterialTheme.typography.labelSmall.scaled(),
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue
            )
        }
    }
}

// ── Today's Table Card ────────────────────────────────────────────────────────

@Composable
private fun TodayTableCard() {
    val dayOfYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR) }
    val tableNumber = remember { 2 + (dayOfYear - 1) % 14 }   // cycles 2–15
    val cardGrey = Color(0xFF757575)

    Row(
        modifier = Modifier
            .shadow(
                elevation = 4.dp, shape = PillShape,
                ambientColor = cardGrey.copy(alpha = 0.15f),
                spotColor   = cardGrey.copy(alpha = 0.15f)
            )
            .background(Color.White.copy(alpha = 0.88f), PillShape)
            .border(1.5.dp, cardGrey.copy(alpha = 0.35f), PillShape)
            .padding(horizontal = Dimens14, vertical = Dimens8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens8)
    ) {
        Box(
            modifier = Modifier
                .size(AppDimens.Dimens40)
                .background(cardGrey.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×$tableNumber",
                style = MaterialTheme.typography.labelMedium.scaled(),
                fontWeight = FontWeight.Black,
                color = cardGrey
            )
        }
        Column {
            Text(
                text = "Today's Table",
                style = MaterialTheme.typography.labelSmall.scaled(),
                fontWeight = FontWeight.Medium,
                color = cardGrey.copy(alpha = 0.7f)
            )
            Text(
                text = "Practice ×$tableNumber table today! 🧮",
                style = MaterialTheme.typography.labelSmall.scaled(),
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

// ── English App Banner ────────────────────────────────────────────────────────

@Composable
private fun EnglishBanner(context: Context) {
    val isTablet = DeviceInfo.isTablet
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "bannerScale"
    )
    val clickAction: () -> Unit = {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/apps/details?id=com.vedaavi.english.learning&hl=en".toUri()
                addCategory(Intent.CATEGORY_BROWSABLE)
            }
            context.startActivity(intent)
        }
    }

    if (isTablet) {
        // Tablet: icon left + 3-line text + chevron right
        Row(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .background(Color(0xFF5532D2).copy(alpha = 0.10f), RoundedCornerShape(percent = 50))
                .border(1.dp, Color(0xFF9374EF).copy(alpha = 0.35f), RoundedCornerShape(percent = 50))
                .clickable(interactionSource = interactionSource, indication = null, onClick = clickAction)
                .padding(horizontal = Dimens16, vertical = Dimens8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens8)
        ) {
            Image(
                painter = painterResource(R.drawable._english),
                contentDescription = null,
                modifier = Modifier.size(AppDimens.Dimens32)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = "Also try",
                    color = Color(0xFF5532D2).copy(alpha = 0.50f),
                    style = MaterialTheme.typography.labelSmall.scaled()
                )
                Text(
                    text = "Vedaavi English 📚",
                    color = Color(0xFF5532D2).copy(alpha = 0.70f),
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fun learning for kids",
                    color = Color(0xFF5532D2).copy(alpha = 0.50f),
                    style = MaterialTheme.typography.labelSmall.scaled()
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9374EF).copy(alpha = 0.55f),
                modifier = Modifier.size(Dimens10)
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
                modifier = Modifier.size(Dimens24)
            )
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    text = "Also try: Vedaavi English! 📚",
                    color = Color(0xFF5532D2).copy(alpha = 0.70f),
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fun learning for kids",
                    color = Color(0xFF5532D2).copy(alpha = 0.50f),
                    style = MaterialTheme.typography.labelSmall.scaled(),
                    fontWeight = FontWeight.Normal
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9374EF).copy(alpha = 0.55f),
                modifier = Modifier.size(Dimens10)
            )
        }
    }
}

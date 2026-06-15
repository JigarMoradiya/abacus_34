package com.jigar.me.ui.view.home.screens.levels.level2.learn

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jigar.me.data.local.data.DeviceInfo
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay
import com.jigar.me.ui.view.home.screens.levels.level2.level2Lessons
import com.jigar.me.ui.view.home.screens.levels.level2.allLevel2LearnContent
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements

private enum class L2LearnPhase { ZERO, SETUP_ARROW, INITIAL, OP_ARROW, RESULT }

@Composable
fun Level2LearnScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson  = level2Lessons.find { it.id == lessonId } ?: return
    val content = allLevel2LearnContent[lessonId] ?: return
    val steps   = content.learnSteps

    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]

    val prefs         = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    val isTablet    = DeviceInfo.isTablet
    val abCalc      = remember { AbacusCalculations(lesson.columns) }
    var animPhase   by remember { mutableStateOf(L2LearnPhase.RESULT) }
    var rodMovement by remember { mutableStateOf<List<RodMovement>>(emptyList()) }
    var showArrows  by remember { mutableStateOf(false) }

    LaunchedEffect(stepIndex) {
        val s     = steps.getOrNull(stepIndex) ?: return@LaunchedEffect
        val toVal = s.abacusState?.value
        showArrows  = false
        rodMovement = emptyList()
        abCalc.resetAbacusData()
        if (toVal == null) { animPhase = L2LearnPhase.RESULT; return@LaunchedEffect }
        val fromVal = s.fromValue
        if (fromVal != null) {
            animPhase = L2LearnPhase.ZERO
            delay(1200)
            animPhase   = L2LearnPhase.SETUP_ARROW
            rodMovement = l2FormulaRodMovements(0, fromVal, lesson.columns)
            showArrows  = true
            delay(1800)
            animPhase   = L2LearnPhase.INITIAL
            showArrows  = false
            rodMovement = emptyList()
            abCalc.setAbacusValueFromString(fromVal.toString())
            delay(1200)
            animPhase   = L2LearnPhase.OP_ARROW
            rodMovement = l2FormulaRodMovements(fromVal, toVal, lesson.columns)
            showArrows  = true
            delay(1800)
            animPhase   = L2LearnPhase.RESULT
            showArrows  = false
            rodMovement = emptyList()
            abCalc.setAbacusValueFromString(toVal.toString())
        } else {
            animPhase = L2LearnPhase.RESULT
            abCalc.setAbacusValueFromString(toVal.toString())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier              = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // ── LEFT: back button + abacus/emoji ──────────────────────────────
            Column(modifier = Modifier.weight(0.40f).fillMaxHeight()) {
                BackButtonWithText(
                    title       = "Lesson ${lesson.id}  ·  Learn",
                    onBackClick = onBackClick
                )
                Box(
                    modifier         = Modifier.fillMaxSize().padding(bottom = AppDimens.Dimens12),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        step.abacusState != null -> {
                            Column(
                                modifier            = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier         = Modifier.weight(1f).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AbacusWithDecimalCanvas(
                                        selectedTheme               = selectedTheme,
                                        screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Learn,
                                        abacusData                  = abCalc,
                                        numberOfColumns             = lesson.columns,
                                        rodMovement                 = rodMovement,
                                        showDirectionHint           = showArrows,
                                        isBeadSoundOn               = false,
                                        isDisplayCurrentAbacusInput = false,
                                        onRodMovementChange         = {},
                                        onShowDirectionHintsChange  = {},
                                        onShowHighlighterChange     = {},
                                        onReset = {}, onNext = {},
                                    )
                                }
                                // Phase badge below abacus
                                if (step.fromValue != null) {
                                    val fromVal = step.fromValue
                                    val toVal   = step.abacusState.value
                                    val label   = when (animPhase) {
                                        L2LearnPhase.ZERO        -> "Start: 0"
                                        L2LearnPhase.SETUP_ARROW -> "+ $fromVal"
                                        L2LearnPhase.INITIAL     -> "= $fromVal ✓"
                                        L2LearnPhase.OP_ARROW    -> {
                                            val diff = toVal - fromVal
                                            if (diff > 0) "+ $diff" else "− ${-diff}"
                                        }
                                        L2LearnPhase.RESULT      -> {
                                            val diff = toVal - fromVal
                                            if (diff > 0) "$fromVal + $diff = $toVal ✓" else "$fromVal − ${-diff} = $toVal ✓"
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = AppDimens.Dimens8)
                                            .background(
                                                when (animPhase) {
                                                    L2LearnPhase.RESULT     -> Color(0xFF43A047).copy(0.88f)
                                                    L2LearnPhase.INITIAL    -> Color(0xFF43A047).copy(0.88f)
                                                    L2LearnPhase.SETUP_ARROW,
                                                    L2LearnPhase.OP_ARROW   -> lesson.endColor.copy(0.88f)
                                                    else                    -> Color.Black.copy(0.55f)
                                                },
                                                RoundedCornerShape(AppDimens.Dimens24)
                                            )
                                            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                                    ) {
                                        Text(
                                            text       = label,
                                            style      = MaterialTheme.typography.headlineSmall.scaled(),
                                            color      = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                        else -> Text(text = step.emoji, fontSize = 80.sp.scaled())
                    }
                }
            }

            // ── RIGHT: gradient card ───────────────────────────────────────────
            val cardShape = RoundedCornerShape(AppDimens.Dimens24)
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .then(if (!isTablet) Modifier.fillMaxHeight() else Modifier)
                    .padding(end = AppDimens.Dimens16, top = AppDimens.Dimens12, bottom = AppDimens.Dimens12)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = lesson.endColor.copy(0.3f),
                        ambientColor = lesson.endColor.copy(0.3f))
                    .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)), cardShape)
                    .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16),
            ) {
                AnimatedContent(
                    targetState    = stepIndex,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    modifier       = Modifier.fillMaxWidth(),
                    label          = "step-content"
                ) { idx ->
                    val s = steps[idx]
                    Column(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6)) {
                            steps.indices.forEach { i ->
                                Box(
                                    modifier = Modifier
                                        .size(if (i == idx) AppDimens.Dimens10 else AppDimens.Dimens8)
                                        .background(
                                            if (i == idx) Color.White else Color.White.copy(0.4f),
                                            CircleShape
                                        )
                                )
                            }
                        }
                        Spacer(Modifier.height(AppDimens.Dimens6))
                        Text(
                            text       = "Step ${idx + 1} of ${steps.size}",
                            style      = MaterialTheme.typography.labelMedium.scaled(),
                            color      = Color.White.copy(alpha = 0.80f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(AppDimens.Dimens6))
                        Text(text = s.emoji, fontSize = 48.sp.scaled())
                        Spacer(Modifier.height(AppDimens.Dimens8))
                        Text(
                            text       = s.title,
                            style      = MaterialTheme.typography.headlineSmall.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign  = TextAlign.Center
                        )
                        Spacer(Modifier.height(AppDimens.Dimens10))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(0.15f), RoundedCornerShape(AppDimens.Dimens16))
                                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12)
                        ) {
                            Text(
                                text       = s.body,
                                style      = MaterialTheme.typography.bodyMedium.scaled(),
                                color      = Color.White.copy(alpha = 0.95f),
                                textAlign  = TextAlign.Center,
                                lineHeight = 22.sp.scaled()
                            )
                        }
                        if (s.fromValue != null) {
                            Spacer(Modifier.height(AppDimens.Dimens8))
                            Text(
                                text       = "👀 Watch the abacus animate!",
                                style      = MaterialTheme.typography.labelSmall.scaled(),
                                color      = Color.White.copy(0.70f),
                                textAlign  = TextAlign.Center
                            )
                        }

                        Spacer(Modifier.height(AppDimens.Dimens20))

                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            if (idx > 0) {
                                L2LearnNavBtn("← Back", filled = false) { stepIndex-- }
                            } else {
                                Spacer(Modifier.size(AppDimens.Dimens4))
                            }
                            if (idx < steps.size - 1) {
                                L2LearnNavBtn("Next →", filled = true) { stepIndex++ }
                            } else {
                                L2LearnNavBtn("Done! 🎉", filled = true, onClick = onFinished)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun L2LearnNavBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.20f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { AudioPlayerManager.playSoundBtnClick(); onClick() }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens10),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = if (filled) Color(0xFF1565C0) else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

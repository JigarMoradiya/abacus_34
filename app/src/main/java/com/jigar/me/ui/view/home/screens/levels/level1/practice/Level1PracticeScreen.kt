package com.jigar.me.ui.view.home.screens.levels.level1.practice

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.buttons.L2ActionBtn
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.level1.generatePracticeProblems

@Composable
fun Level1PracticeScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson   = level1Lessons.find { it.id == lessonId } ?: return
    val problems = remember { generatePracticeProblems(lessonId) }

    var problemIndex      by remember { mutableIntStateOf(0) }
    var showHint          by remember { mutableStateOf(false) }
    var showDone          by remember { mutableStateOf(false) }
    var hintAbacusTarget  by remember { mutableIntStateOf(0) }

    LaunchedEffect(problemIndex) { showHint = false }

    val problem = problems[minOf(problemIndex, problems.size - 1)]
    val prefs   = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header: back button + inline progress bar ─────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title       = "Lesson ${lesson.id}  ·  Practice",
                    onBackClick = onBackClick
                )
                LinearProgressIndicator(
                    progress   = { (problemIndex.toFloat() + 1f) / problems.size },
                    modifier   = Modifier
                        .weight(1f)
                        .padding(end = AppDimens.Dimens32)
                        .height(AppDimens.Dimens8),
                    color      = lesson.endColor,
                    trackColor = lesson.endColor.copy(alpha = 0.25f),
                )
            }

            // ── Main content ───────────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12
                val spacing = AppDimens.Dimens16

                Row(
                    modifier = Modifier
                        .padding(top = vPad, end = AppDimens.Dimens20, bottom = vPad)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: target number + revealed abacus ──────
                    Box(
                        modifier         = Modifier.weight(0.35f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.95f)
                                .padding(bottom = AppDimens.Dimens8),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = showHint,
                                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                                label = "left-content"
                            ) { hintVisible ->
                                if (hintVisible) {
                                    val cols   = problem.abacusState.rods.size
                                    val abCalc = remember(cols) { AbacusCalculations(cols) }
                                    LaunchedEffect(hintAbacusTarget) {
                                        abCalc.setAbacusValueFromString(hintAbacusTarget.toString())
                                    }
                                    Box(
                                        modifier         = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AbacusWithDecimalCanvas(
                                            selectedTheme             = selectedTheme,
                                            screenType                = AppConstants.AbacusScreen.screenTypeLevel1Practice,
                                            abacusData                = abCalc,
                                            numberOfColumns           = cols,
                                            rodMovement               = emptyList(),
                                            showDirectionHint         = false,
                                            isBeadSoundOn             = false,
                                            isDisplayCurrentAbacusInput = false,
                                            onRodMovementChange       = {},
                                            onShowDirectionHintsChange = {},
                                            onShowHighlighterChange   = {},
                                            onReset = {}, onNext = {},
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            "Set this number:",
                                            style      = MaterialTheme.typography.labelLarge.scaled(),
                                            color      = lesson.startColor.copy(0.80f),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(AppDimens.Dimens8))
                                        Text(
                                            text       = "${problem.targetNumber}",
                                            fontSize   = 88.sp.scaled(),
                                            color      = lesson.startColor,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Right panel: instruction + buttons ───────────────
                    val rightShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .padding(bottom = AppDimens.Dimens8)
                            .shadow(AppDimens.Dimens6, rightShape,
                                spotColor    = lesson.endColor.copy(0.25f),
                                ambientColor = lesson.endColor.copy(0.15f))
                            .background(
                                Brush.linearGradient(listOf(lesson.startColor.copy(0.70f), lesson.endColor.copy(0.70f))),
                                rightShape
                            )
                            .padding(AppDimens.Dimens20),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("🎯", fontSize = 44.sp.scaled())

                            Text(
                                text       = "Set this number on\nyour abacus!",
                                style      = MaterialTheme.typography.headlineSmall.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                            ) {
                                Text(
                                    text      = problem.instruction,
                                    style     = MaterialTheme.typography.bodyMedium.scaled(),
                                    color     = Color.White.copy(0.90f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                L2ActionBtn(
                                    label      = if (showHint) "Got it! ✓" else "Show Me How 👀",
                                    filled     = false,
                                    startColor = lesson.startColor,
                                    onClick    = {
                                        if (!showHint) hintAbacusTarget = problem.targetNumber
                                        showHint = !showHint
                                    }
                                )
                                L2ActionBtn(
                                    label      = "I Did It! ✅",
                                    filled     = true,
                                    startColor = lesson.startColor,
                                    onClick    = {
                                        if (problemIndex < problems.size - 1) problemIndex++
                                        else showDone = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Practice complete popup overlay ────────────────────────────
        AnimatedVisibility(
            visible       = showDone,
            enter         = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)),
            exit          = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f),
            modifier      = Modifier.fillMaxSize()
        ) {
            Box(
                modifier         = Modifier.fillMaxSize().background(Color.Black.copy(0.45f)),
                contentAlignment = Alignment.Center
            ) {
                val popupShape = RoundedCornerShape(AppDimens.Dimens24)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .shadow(AppDimens.Dimens16, popupShape,
                            spotColor    = lesson.endColor.copy(0.4f),
                            ambientColor = lesson.endColor.copy(0.4f))
                        .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)), popupShape)
                        .padding(AppDimens.Dimens24),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
                    ) {
                        Text("🌟🌟🌟", fontSize = 52.sp.scaled())
                        Text(
                            "Practice Complete!",
                            style      = MaterialTheme.typography.headlineMedium.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign  = TextAlign.Center
                        )
                        Text(
                            "Amazing work! You set all ${problems.size} numbers on the abacus.\nReady to take the quiz?",
                            style      = MaterialTheme.typography.bodyLarge.scaled(),
                            color      = Color.White.copy(0.90f),
                            textAlign  = TextAlign.Center
                        )
                        Spacer(Modifier.height(AppDimens.Dimens4))
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                            L2ActionBtn(
                                label      = "Try Again 🔄",
                                filled     = false,
                                startColor = lesson.startColor,
                                onClick    = { problemIndex = 0; showDone = false }
                            )
                            L2ActionBtn(
                                label      = "Continue →",
                                filled     = true,
                                startColor = lesson.startColor,
                                onClick    = { onFinished() }
                            )
                        }
                    }
                }
            }
        }
    }
}

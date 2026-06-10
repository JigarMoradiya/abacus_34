package com.jigar.me.ui.view.home.screens.levels

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
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

@Composable
fun Level2PracticeScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson   = level2Lessons.find { it.id == lessonId } ?: return
    val problems = remember { generateLevel2PracticeProblems(lessonId) }

    var problemIndex     by remember { mutableIntStateOf(0) }
    var showHint         by remember { mutableStateOf(false) }
    var showDone         by remember { mutableStateOf(false) }
    var hintAbacusTarget by remember { mutableIntStateOf(0) }

    LaunchedEffect(problemIndex) { showHint = false }

    val problem = problems[minOf(problemIndex, problems.size - 1)]
    val prefs   = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header: back button + progress bar ────────────────────────────
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

            // ── Main content ──────────────────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: equation or hint abacus ───────────────
                    Box(
                        modifier         = Modifier.weight(0.45f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        val leftShape = RoundedCornerShape(AppDimens.Dimens24)
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.95f)
                                .padding(bottom = AppDimens.Dimens8)
                                .shadow(AppDimens.Dimens8, leftShape,
                                    spotColor    = lesson.endColor.copy(0.25f),
                                    ambientColor = lesson.endColor.copy(0.15f))
                                .background(Color.White.copy(0.20f), leftShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState    = showHint,
                                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                                label          = "left-content"
                            ) { hintVisible ->
                                if (hintVisible) {
                                    val abCalc = remember { AbacusCalculations(1) }
                                    LaunchedEffect(hintAbacusTarget) {
                                        abCalc.setAbacusValueFromString(hintAbacusTarget.toString())
                                    }
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        AbacusWithDecimalCanvas(
                                            selectedTheme               = selectedTheme,
                                            screenType                  = AppConstants.AbacusScreen.screenTypeLevel1PracticeHint,
                                            abacusData                  = abCalc,
                                            numberOfColumns             = 1,
                                            rodMovement                 = emptyList(),
                                            showDirectionHint           = false,
                                            isBeadSoundOn               = false,
                                            isDisplayCurrentAbacusInput = false,
                                            onRodMovementChange         = {},
                                            onShowDirectionHintsChange  = {},
                                            onShowHighlighterChange     = {},
                                            onReset = {}, onNext = {},
                                        )
                                    }
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier            = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text       = "Solve:",
                                            style      = MaterialTheme.typography.labelLarge.scaled(),
                                            color      = lesson.startColor.copy(0.80f),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(AppDimens.Dimens8))
                                        Text(
                                            text       = "${problem.a} ${problem.op} ${problem.b} = ?",
                                            fontSize   = 44.sp,
                                            color      = lesson.startColor,
                                            fontWeight = FontWeight.Black,
                                            textAlign  = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Right panel: instruction + buttons ────────────────
                    val rightShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(0.55f)
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
                            modifier            = Modifier.fillMaxSize()
                        ) {
                            Text("🧮", fontSize = 44.sp)

                            Text(
                                text       = "What is ${problem.a} ${problem.op} ${problem.b}?",
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
                                    text      = "Set the answer on your abacus:\n${problem.instruction}",
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
                                        if (!showHint) hintAbacusTarget = problem.result
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

        // ── Practice complete popup ────────────────────────────────────────────
        AnimatedVisibility(
            visible  = showDone,
            enter    = fadeIn(tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)),
            exit     = fadeOut(tween(200)) + scaleOut(targetScale = 0.85f),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier         = Modifier.fillMaxSize().background(Color.Black.copy(0.45f)),
                contentAlignment = Alignment.Center
            ) {
                val popupShape = RoundedCornerShape(AppDimens.Dimens24)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.50f)
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
                        Text("🌟🌟🌟", fontSize = 52.sp)
                        Text(
                            "Practice Complete!",
                            style      = MaterialTheme.typography.headlineMedium.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign  = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12)
                        ) {
                            Text(
                                "Amazing! You solved all ${problems.size} equations on the abacus!\nReady for the quiz?",
                                style      = MaterialTheme.typography.bodyLarge.scaled(),
                                color      = Color.White.copy(0.90f),
                                textAlign  = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(AppDimens.Dimens4))
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                            L2ActionBtn("Try Again 🔄", filled = false, startColor = lesson.startColor) {
                                problemIndex = 0; showDone = false
                            }
                            L2ActionBtn("Continue →", filled = true, startColor = lesson.startColor) {
                                onFinished()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun L2ActionBtn(label: String, filled: Boolean, startColor: Color = Color(0xFF1B5E20), onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.25f), shape)
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = if (filled) startColor else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

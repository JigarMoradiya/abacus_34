package com.jigar.me.ui.view.home.screens.levels

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
fun Level1PracticeScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson   = level1Lessons.find { it.id == lessonId } ?: return
    val content  = allLessonContent[lessonId] ?: return
    val problems = content.practiceProblems

    var problemIndex by remember { mutableIntStateOf(0) }
    var showHint     by remember { mutableStateOf(false) }
    var showDone     by remember { mutableStateOf(false) }

    // Reset hint when problem changes
    LaunchedEffect(problemIndex) { showHint = false }

    if (showDone) {
        PracticeCompleteOverlay(lesson = lesson, onContinue = onFinished)
        return
    }

    val problem = problems[problemIndex]
    val prefs   = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(
                title       = "Lesson ${lesson.id}  ·  Practice",
                onBackClick = onBackClick
            )

            // Progress bar
            LinearProgressIndicator(
                progress    = { (problemIndex.toFloat() + 1f) / problems.size },
                modifier    = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens16).height(AppDimens.Dimens8),
                color       = lesson.endColor,
                trackColor  = lesson.endColor.copy(alpha = 0.25f),
            )
            Spacer(Modifier.height(AppDimens.Dimens8))

            BoxWithConstraints(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val hPad    = AppDimens.Dimens16
                val vPad    = AppDimens.Dimens12
                val spacing = AppDimens.Dimens16

                Row(
                    modifier = Modifier
                        .padding(horizontal = hPad, vertical = vPad)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: target number + revealed abacus ────────
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
                                    spotColor    = lesson.endColor.copy(0.3f),
                                    ambientColor = lesson.endColor.copy(0.3f))
                                .background(
                                    Brush.linearGradient(listOf(lesson.startColor.copy(0.9f), lesson.endColor.copy(0.9f))),
                                    leftShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = showHint,
                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                label = "left-content"
                            ) { hintVisible ->
                                if (hintVisible) {
                                    val cols    = problem.abacusState.rods.size
                                    val abCalc  = remember(cols) { AbacusCalculations(cols) }
                                    LaunchedEffect(problem.targetNumber) {
                                        abCalc.setAbacusValueFromString(problem.targetNumber.toString())
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                        modifier = Modifier.padding(AppDimens.Dimens16)
                                    ) {
                                        Text("Answer:", style = MaterialTheme.typography.labelLarge.scaled(),
                                            color = Color.White.copy(0.80f), fontWeight = FontWeight.Bold)
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
                                            modifier = Modifier.fillMaxWidth(0.70f).weight(1f)
                                        )
                                        Text(
                                            text       = "${problem.targetNumber}",
                                            fontSize   = 40.sp,
                                            color      = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                } else {
                                    // Big number display
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text("Set this number:", style = MaterialTheme.typography.labelLarge.scaled(),
                                            color = Color.White.copy(0.80f), fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(AppDimens.Dimens8))
                                        Text(
                                            text       = "${problem.targetNumber}",
                                            fontSize   = 88.sp,
                                            color      = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Right panel: instruction + buttons ─────────────────
                    val rightShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                            .padding(bottom = AppDimens.Dimens8)
                            .shadow(AppDimens.Dimens6, rightShape,
                                spotColor    = Color.White.copy(0.12f),
                                ambientColor = Color.White.copy(0.08f))
                            .background(Color.White.copy(0.15f), rightShape)
                            .padding(AppDimens.Dimens20),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterVertically),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Problem counter
                            Text(
                                text       = "Problem ${problemIndex + 1} of ${problems.size}",
                                style      = MaterialTheme.typography.labelLarge.scaled(),
                                color      = Color.White.copy(0.80f),
                                fontWeight = FontWeight.Bold
                            )

                            Text("🎯", fontSize = 48.sp)

                            Text(
                                text       = "Set this number on\nyour abacus!",
                                style      = MaterialTheme.typography.headlineSmall.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center
                            )

                            // Hint instruction pill
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

                            Spacer(Modifier.height(AppDimens.Dimens4))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                // Show Me How
                                ActionBtn(
                                    label   = if (showHint) "Got it! ✓" else "Show Me How 👀",
                                    filled  = false,
                                    onClick = { showHint = !showHint }
                                )

                                // I Did It
                                ActionBtn(
                                    label  = "I Did It! ✅",
                                    filled = true,
                                    onClick = {
                                        if (problemIndex < problems.size - 1) {
                                            problemIndex++
                                        } else {
                                            showDone = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(
                if (filled) Color(0xFF1B5E20) else Color.White.copy(0.22f),
                shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun PracticeCompleteOverlay(lesson: Level1LessonData, onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Lesson ${lesson.id}  ·  Practice", onBackClick = onContinue)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val shape = RoundedCornerShape(AppDimens.Dimens24)
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .fillMaxWidth(0.55f)
                    .shadow(AppDimens.Dimens12, shape,
                        spotColor    = lesson.endColor.copy(0.4f),
                        ambientColor = lesson.endColor.copy(0.4f))
                    .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)), shape)
                    .padding(AppDimens.Dimens24),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterVertically)
                ) {
                    Text("🌟🌟🌟", fontSize = 56.sp)
                    Text(
                        "Practice Complete!",
                        style      = MaterialTheme.typography.headlineMedium.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        "Amazing work! You set all 5 numbers on\nthe abacus. Ready to take the quiz?",
                        style      = MaterialTheme.typography.bodyLarge.scaled(),
                        color      = Color.White.copy(0.90f),
                        textAlign  = TextAlign.Center
                    )
                    Spacer(Modifier.height(AppDimens.Dimens8))
                    Box(
                        modifier = Modifier
                            .shadow(AppDimens.Dimens4, RoundedCornerShape(AppDimens.Dimens100))
                            .background(Color.White, RoundedCornerShape(AppDimens.Dimens100))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) { onContinue() }
                            .padding(horizontal = AppDimens.Dimens24, vertical = AppDimens.Dimens12),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Continue →",
                            style      = MaterialTheme.typography.titleMedium.scaled(),
                            color      = lesson.startColor,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

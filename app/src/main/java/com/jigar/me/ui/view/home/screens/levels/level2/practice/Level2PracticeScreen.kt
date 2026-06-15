package com.jigar.me.ui.view.home.screens.levels.level2.practice

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.buttons.L2ActionBtn
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay
import com.jigar.me.ui.view.home.screens.levels.level2.level2Lessons
import com.jigar.me.ui.view.home.screens.levels.level2.generateLevel2PracticeProblems
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements

private enum class L2PracticePhase { ZERO, SETUP_ARROW, INITIAL, OP_ARROW, RESULT }


@Composable
fun Level2PracticeScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson   = level2Lessons.find { it.id == lessonId } ?: return
    val problems = remember { generateLevel2PracticeProblems(lessonId) }

    var problemIndex by remember { mutableIntStateOf(0) }
    var showDone     by remember { mutableStateOf(false) }

    val problem = problems[minOf(problemIndex, problems.size - 1)]
    val prefs   = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    val isTablet    = DeviceInfo.isTablet
    val abCalc      = remember { AbacusCalculations(lesson.columns) }
    var animPhase   by remember { mutableStateOf(L2PracticePhase.ZERO) }
    var rodMovement by remember { mutableStateOf<List<RodMovement>>(emptyList()) }
    var showArrows  by remember { mutableStateOf(false) }
    var animTrigger by remember { mutableIntStateOf(0) }
    var animStarted by remember { mutableStateOf(false) }

    LaunchedEffect(problemIndex) {
        animStarted = false
        animPhase   = L2PracticePhase.ZERO
        showArrows  = false
        rodMovement = emptyList()
        abCalc.resetAbacusData()
    }

    LaunchedEffect(animTrigger) {
        if (animTrigger == 0) return@LaunchedEffect
        val p = problems[minOf(problemIndex, problems.size - 1)]
        showArrows  = false
        rodMovement = emptyList()
        animPhase   = L2PracticePhase.ZERO
        abCalc.resetAbacusData()
        delay(1200)
        animPhase   = L2PracticePhase.SETUP_ARROW
        rodMovement = l2FormulaRodMovements(0, p.a, lesson.columns)
        showArrows  = true
        delay(1800)
        animPhase   = L2PracticePhase.INITIAL
        showArrows  = false
        rodMovement = emptyList()
        abCalc.setAbacusValueFromString(p.a.toString())
        delay(1200)
        animPhase   = L2PracticePhase.OP_ARROW
        rodMovement = l2FormulaRodMovements(p.a, p.result, lesson.columns)
        showArrows  = true
        delay(1800)
        animPhase   = L2PracticePhase.RESULT
        showArrows  = false
        rodMovement = emptyList()
        abCalc.setAbacusValueFromString(p.result.toString())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

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
                        .padding(top = AppDimens.Dimens12, end = AppDimens.Dimens20, bottom = AppDimens.Dimens12)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: equation or animated abacus ───────────────
                    Box(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxHeight()
                            .padding(bottom = AppDimens.Dimens8),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState    = animStarted,
                            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                            modifier       = Modifier.fillMaxSize(),
                            label          = "l2-left"
                        ) { started ->
                            if (started) {
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
                                            screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Practice,
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
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = AppDimens.Dimens8)
                                            .background(
                                                when (animPhase) {
                                                    L2PracticePhase.RESULT     -> Color(0xFF43A047).copy(0.88f)
                                                    L2PracticePhase.INITIAL    -> Color(0xFF43A047).copy(0.88f)
                                                    L2PracticePhase.SETUP_ARROW,
                                                    L2PracticePhase.OP_ARROW   -> lesson.endColor.copy(0.88f)
                                                    else                       -> Color.Black.copy(0.55f)
                                                },
                                                RoundedCornerShape(AppDimens.Dimens24)
                                            )
                                            .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens6)
                                    ) {
                                        Text(
                                            text = when (animPhase) {
                                                L2PracticePhase.ZERO        -> "Start: 0"
                                                L2PracticePhase.SETUP_ARROW -> "+ ${problem.a}"
                                                L2PracticePhase.INITIAL     -> "= ${problem.a} ✓"
                                                L2PracticePhase.OP_ARROW    -> "${problem.op} ${problem.b}"
                                                L2PracticePhase.RESULT      -> "${problem.a} ${problem.op} ${problem.b} = ${problem.result} ✓"
                                            },
                                            style      = MaterialTheme.typography.bodyMedium.scaled(),
                                            color      = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    modifier            = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("🧮", fontSize = 48.sp.scaled())
                                    Spacer(Modifier.height(AppDimens.Dimens12))
                                    Text(
                                        text       = "${problem.a} ${problem.op} ${problem.b} = ?",
                                        fontSize   = 40.sp.scaled(),
                                        color      = lesson.endColor,
                                        fontWeight = FontWeight.Black,
                                        textAlign  = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(AppDimens.Dimens12))
                                    Text(
                                        text       = "Try it on your\nabacus! →",
                                        style      = MaterialTheme.typography.labelLarge.scaled(),
                                        color      = lesson.startColor.copy(0.80f),
                                        textAlign  = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // ── Right panel: equation + buttons ───────────────────────
                    val rightShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(0.65f)
                            .then(if (!isTablet) Modifier.fillMaxHeight() else Modifier)
                            .padding(bottom = AppDimens.Dimens8)
                            .shadow(AppDimens.Dimens6, rightShape,
                                spotColor    = lesson.endColor.copy(0.25f),
                                ambientColor = lesson.endColor.copy(0.15f))
                            .background(
                                Brush.linearGradient(listOf(lesson.startColor.copy(0.70f), lesson.endColor.copy(0.70f))),
                                rightShape
                            )
                            .padding(AppDimens.Dimens16),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier            = Modifier.fillMaxWidth()
                        ) {
                            Text("🧮", fontSize = 44.sp.scaled())

                            Text(
                                text       = "${problem.a} ${problem.op} ${problem.b} = ?",
                                fontSize   = 40.sp.scaled(),
                                color      = Color.White,
                                fontWeight = FontWeight.Black,
                                textAlign  = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = AppDimens.Dimens8)
                                    .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens10)
                            ) {
                                Text(
                                    text      = "Solve it on your abacus, then\ncheck your answer:\n${problem.instruction}",
                                    style     = MaterialTheme.typography.bodyMedium.scaled(),
                                    color     = Color.White.copy(0.90f),
                                    textAlign = TextAlign.Center,
                                    modifier  = Modifier.fillMaxWidth(),
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = AppDimens.Dimens12),
                                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterHorizontally),
                                verticalAlignment     = Alignment.CenterVertically,
                            ) {
                                L2ActionBtn(
                                    label      = if (!animStarted) "Show Me How 👀" else "Replay ▶",
                                    filled     = false,
                                    startColor = lesson.startColor,
                                    onClick    = { animStarted = true; animTrigger++ }
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

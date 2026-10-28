package com.jigar.me.ui.view.home.screens.levels.level2.practice

import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abChunkyFace
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.home.screens.levels.levelSp
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbInsetBox
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.LevelHeroPanel
import com.jigar.me.ui.view.home.screens.levels.LevelPanelFit
import com.jigar.me.ui.view.home.screens.levels.LevelProgressTopBar
import com.jigar.me.ui.view.home.screens.levels.LevelResultPopup
import com.jigar.me.ui.view.home.screens.levels.LevelFitToBox
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.level2.generateLevel2PracticeProblems
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements
import com.jigar.me.ui.view.home.screens.levels.level2.level2Lessons
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelText
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay

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
    val selectedTheme = AbacusTheme.active(prefs)

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

    LevelScreenRoot(AbWorld.Sunset) {
        Column(modifier = Modifier.levelForeground()) {

            // ── Header: back button + progress ────────────────────────────────
            LevelProgressTopBar(
                title  = "Lesson ${lesson.id}  ·  Practice",
                filled = problemIndex + 1,
                total  = problems.size,
                onBack = onBackClick,
            )

            // ── Main content ──────────────────────────────────────────────────
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val leftW = maxWidth * 0.35f

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = AppDimens.Dimens16, end = AppDimens.Dimens20) // the panel keeps the same edge as the back button
                        .padding(vertical = AppDimens.Dimens12),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: equation or animated abacus ───────────────
                    AbGlassPanel(modifier = Modifier.width(leftW).fillMaxHeight()) {
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
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
                                ) {
                                    // the abacus fills the card, shrinking to fit inside it
                                    Box(
                                        modifier         = Modifier.weight(1f).fillMaxWidth().padding(AppDimens.Dimens8),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LevelFitToBox {
                                            AbacusWithDecimalCanvas(
                                                selectedTheme               = selectedTheme,
                                                screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Practice,
                                                abacusData                  = abCalc,
                                                numberOfColumns             = lesson.columns,
                                                rodMovement                 = rodMovement,
                                                showDirectionHint           = showArrows,
                                                isBeadSoundOn               = false,
                                                isDisplayCurrentAbacusInput = false,
                                                frameStyle                  = AbacusFrameStyle.Toy,
                                                onRodMovementChange         = {},
                                                onShowDirectionHintsChange  = {},
                                                onShowHighlighterChange     = {},
                                                onReset = {}, onNext = {},
                                            )
                                        }
                                    }
                                    val badgeColor by animateColorAsState(
                                        targetValue = when (animPhase) {
                                            L2PracticePhase.RESULT, L2PracticePhase.INITIAL -> Color(0xFF43A047).copy(alpha = 0.88f)
                                            L2PracticePhase.SETUP_ARROW, L2PracticePhase.OP_ARROW -> lesson.endColor.copy(alpha = 0.88f)
                                            else -> Color.Black.copy(alpha = 0.55f)
                                        },
                                        animationSpec = tween(250),
                                        label = "phase-badge"
                                    )
                                    Text(
                                        text = when (animPhase) {
                                            L2PracticePhase.ZERO        -> "Start: 0"
                                            L2PracticePhase.SETUP_ARROW -> "+ ${problem.a}"
                                            L2PracticePhase.INITIAL     -> "= ${problem.a} ✓"
                                            L2PracticePhase.OP_ARROW    -> "${problem.op} ${problem.b}"
                                            L2PracticePhase.RESULT      -> "${problem.a} ${problem.op} ${problem.b} = ${problem.result} ✓"
                                        },
                                        style    = levelText(14f, FontWeight.Black),
                                        color    = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = AppDimens.Dimens8)
                                            .background(badgeColor, CircleShape)
                                            .padding(horizontal = AppDimens.Dimens14, vertical = AppDimens.Dimens6)
                                    )
                                }
                            } else {
                                Column(
                                    modifier            = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically)
                                ) {
                                    Text("🧮", fontSize = levelSp(48f))
                                    Text(
                                        text      = "${problem.a} ${problem.op} ${problem.b} = ?",
                                        style     = levelText(40f, FontWeight.Black),
                                        color     = lesson.endColor.abReadable(),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text      = "Try it on your\nabacus! →",
                                        style     = levelText(18f, FontWeight.ExtraBold),
                                        color     = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier  = Modifier
                                            .abChunkyFace(lesson.endColor.abReadable(), lesson.endColor.abReadable().abDarkened(0.3f), null, 22.dp, 3.dp)
                                            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens6)
                                    )
                                }
                            }
                        }
                    }

                    // ── Right panel: equation + buttons ───────────────────────
                    LevelHeroPanel(
                        accent           = lesson.endColor,
                        modifier         = Modifier.weight(1f).then(if (!isTablet) Modifier.fillMaxHeight() else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        LevelPanelFit {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                modifier            = Modifier.padding(AppDimens.Dimens20)
                            ) {
                                Text("🧮", fontSize = levelSp(44f))

                                Text(
                                    text      = "${problem.a} ${problem.op} ${problem.b} = ?",
                                    style     = levelText(38f, FontWeight.Black),
                                    color     = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                AbInsetBox {
                                    Text(
                                        text      = "Solve it on your abacus, then\ncheck your answer:\n${problem.instruction}",
                                        style     = levelParagraph(14f, FontWeight.Medium),
                                        color     = Color.White.copy(alpha = 0.90f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                    verticalAlignment     = Alignment.CenterVertically,
                                ) {
                                    AbPanelButton(
                                        label   = if (!animStarted) "Show Me How 👀" else "Replay ▶",
                                        accent  = lesson.endColor,
                                        filled  = false,
                                        onClick = { animStarted = true; animTrigger++ }
                                    )
                                    AbPanelButton(
                                        label   = "I Did It! ✅",
                                        accent  = lesson.endColor,
                                        onClick = {
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
        }

        // ── Practice complete popup ────────────────────────────────────────────
        if (showDone) {
            LevelResultPopup(accent = lesson.endColor) {
                Text("🌟🌟🌟", fontSize = levelSp(52f))
                Text(
                    "Practice Complete!",
                    style     = AbTypography.fixed(levelMainTitleSize, FontWeight.Black),
                    color     = Color.White,
                    textAlign = TextAlign.Center
                )
                AbInsetBox {
                    Text(
                        "Amazing! You solved all ${problems.size} equations on the abacus!\nReady for the quiz?",
                        style     = levelParagraph(15f, FontWeight.Medium),
                        color     = Color.White.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(AppDimens.Dimens4))
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                    AbPanelButton(
                        label   = "Try Again 🔄",
                        accent  = lesson.endColor,
                        filled  = false,
                        onClick = { problemIndex = 0; showDone = false }
                    )
                    AbPanelButton(
                        label   = "Continue →",
                        accent  = lesson.endColor,
                        onClick = { onFinished() }
                    )
                }
            }
        }
    }
}

package com.jigar.me.ui.view.home.screens.levels.level1.practice

import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.jigar.me.ui.view.home.screens.levels.level1.generatePracticeProblems
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelText
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

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
    val selectedTheme = AbacusTheme.active(prefs)

    LevelScreenRoot(AbWorld.Meadow) {
        Column(modifier = Modifier.levelForeground()) {

            // ── Header: back button + progress ─────────────────────────
            LevelProgressTopBar(
                title  = "Lesson ${lesson.id}  ·  Practice",
                filled = problemIndex + 1,
                total  = problems.size,
                onBack = onBackClick,
            )

            // ── Main content ───────────────────────────────────────────
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val hPad       = AppDimens.Dimens16
                val vPad       = AppDimens.Dimens12
                val spacing    = AppDimens.Dimens16
                val leftPanelW = (maxWidth - hPad * 2 - spacing) * 0.44f

                Row(
                    modifier              = Modifier.fillMaxSize().padding(horizontal = hPad, vertical = vPad),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: target number + revealed abacus ──────
                    AbGlassPanel(modifier = Modifier.width(leftPanelW).fillMaxHeight()) {
                        AnimatedContent(
                            targetState    = showHint,
                            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                            modifier       = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                            label          = "left-content"
                        ) { hintVisible ->
                            if (hintVisible) {
                                val cols   = problem.abacusState.rods.size
                                val abCalc = remember(cols) { AbacusCalculations(cols) }
                                LaunchedEffect(hintAbacusTarget) {
                                    abCalc.setAbacusValueFromString(hintAbacusTarget.toString())
                                }
                                // the abacus fills the card, shrinking to fit inside it
                                Box(
                                    modifier         = Modifier.fillMaxSize().padding(AppDimens.Dimens8),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LevelFitToBox {
                                        AbacusWithDecimalCanvas(
                                            selectedTheme             = selectedTheme,
                                            screenType                = AppConstants.AbacusScreen.screenTypeLevel1Practice,
                                            abacusData                = abCalc,
                                            numberOfColumns           = cols,
                                            rodMovement               = emptyList(),
                                            showDirectionHint         = false,
                                            isBeadSoundOn             = false,
                                            isDisplayCurrentAbacusInput = false,
                                            frameStyle                = AbacusFrameStyle.Toy,
                                            onRodMovementChange       = {},
                                            onShowDirectionHintsChange = {},
                                            onShowHighlighterChange   = {},
                                            onReset = {}, onNext = {},
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8, Alignment.CenterVertically),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // the caption is a chunky pill in the lesson colour, big enough to read next to the number
                                    Text(
                                        "Set this number:",
                                        style = levelText(20f, FontWeight.ExtraBold),
                                        color = Color.White,
                                        modifier = Modifier
                                            .abChunkyFace(lesson.endColor.abReadable(), lesson.endColor.abReadable().abDarkened(0.3f), null, 50.dp, 3.dp)
                                            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens4)
                                    )
                                    Text(
                                        text  = "${problem.targetNumber}",
                                        style = levelText(80f, FontWeight.Black),
                                        color = lesson.endColor.abReadable()
                                    )
                                }
                            }
                        }
                    }

                    // ── Right panel: instruction + buttons ───────────────
                    LevelHeroPanel(
                        accent           = lesson.endColor,
                        modifier         = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        LevelPanelFit {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                modifier = Modifier.padding(AppDimens.Dimens20)
                            ) {
                                Text("🎯", fontSize = levelSp(44f))

                                Text(
                                    text      = "Set this number on\nyour abacus!",
                                    style     = AbTypography.fixed(levelMainTitleSize, FontWeight.Black),
                                    color     = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                AbInsetBox {
                                    Text(
                                        text      = problem.instruction,
                                        style     = levelParagraph(15f, FontWeight.Medium),
                                        color     = Color.White.copy(alpha = 0.90f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    AbPanelButton(
                                        label   = if (showHint) "Got it! ✓" else "Show Me How 👀",
                                        accent  = lesson.endColor,
                                        filled  = false,
                                        onClick = {
                                            if (!showHint) hintAbacusTarget = problem.targetNumber
                                            showHint = !showHint
                                        }
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

        // ── Practice complete popup overlay ────────────────────────────
        if (showDone) {
            LevelResultPopup(accent = lesson.endColor) {
                Text("🌟🌟🌟", fontSize = levelSp(52f))
                Text(
                    "Practice Complete!",
                    style     = AbTypography.fixed(levelMainTitleSize, FontWeight.Black),
                    color     = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Amazing work! You set all ${problems.size} numbers on the abacus.\nReady to take the quiz?",
                    style     = levelParagraph(15f, FontWeight.Medium),
                    color     = Color.White.copy(alpha = 0.90f),
                    textAlign = TextAlign.Center,
                    modifier  = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(AppDimens.Dimens16))
                        .padding(AppDimens.Dimens12)
                )
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

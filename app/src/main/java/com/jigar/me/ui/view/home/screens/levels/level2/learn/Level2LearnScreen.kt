package com.jigar.me.ui.view.home.screens.levels.level2.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.home.screens.levels.levelDp
import com.jigar.me.ui.view.home.screens.levels.levelSp
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.local.data.RodMovement
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbInsetBox
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.LevelHeroPanel
import com.jigar.me.ui.view.home.screens.levels.LevelPanelFit
import com.jigar.me.ui.view.home.screens.levels.LevelFitToBox
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.level2.allLevel2LearnContent
import com.jigar.me.ui.view.home.screens.levels.level2.l2FormulaRodMovements
import com.jigar.me.ui.view.home.screens.levels.level2.level2Lessons
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelText
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay

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
    val selectedTheme = AbacusTheme.active(prefs)

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

    LevelScreenRoot(AbWorld.Sunset) {
        BoxWithConstraints(modifier = Modifier.levelForeground()) {
            val screenH = maxHeight
            val leftW   = maxWidth * 0.40f

            Row(
                modifier              = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // ── LEFT: back button + abacus/emoji ──────────────────────────────
                Column(modifier = Modifier.width(leftW).fillMaxHeight()) {
                    AbTopBar(
                        title    = "Lesson ${lesson.id}  ·  Learn",
                        onBack   = onBackClick,
                        modifier = Modifier.levelTopBarPadding(end = 0.dp),
                    )
                    Box(
                        modifier         = Modifier.fillMaxWidth().weight(1f).padding(bottom = AppDimens.Dimens12),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            step.abacusState != null -> {
                                Column(
                                    modifier            = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8, Alignment.CenterVertically)
                                ) {
                                    // The abacus sits in a calm glass panel and shrinks to the room inside it.
                                    Box(
                                        modifier         = Modifier.weight(1f, fill = false),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AbGlassPanel(
                                            modifier = Modifier
                                                .heightIn(max = screenH * 0.75f)
                                                .aspectRatio(0.55f)
                                        ) {
                                            Box(
                                                modifier         = Modifier.fillMaxSize().padding(AppDimens.Dimens20),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                LevelFitToBox {
                                                    AbacusWithDecimalCanvas(
                                                        selectedTheme               = selectedTheme,
                                                        screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Learn,
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
                                        }
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
                                        val badgeColor by animateColorAsState(
                                            targetValue = when (animPhase) {
                                                L2LearnPhase.RESULT, L2LearnPhase.INITIAL -> Color(0xFF43A047).copy(alpha = 0.88f)
                                                L2LearnPhase.SETUP_ARROW, L2LearnPhase.OP_ARROW -> lesson.endColor.copy(alpha = 0.88f)
                                                else -> Color.Black.copy(alpha = 0.55f)
                                            },
                                            animationSpec = tween(250),
                                            label = "phase-badge"
                                        )
                                        Text(
                                            text     = label,
                                            style    = levelText(16f, FontWeight.Black),
                                            color    = Color.White,
                                            modifier = Modifier
                                                .padding(bottom = AppDimens.Dimens8)
                                                .background(badgeColor, CircleShape)
                                                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
                                        )
                                    }
                                }
                            }
                            else -> Text(text = step.emoji, fontSize = levelSp(80f))
                        }
                    }
                }

                // ── RIGHT: hero panel ──────────────────────────────────────────────
                LevelHeroPanel(
                    accent   = lesson.endColor,
                    modifier = Modifier
                        .weight(1f)
                        .then(if (!isTablet) Modifier.fillMaxHeight() else Modifier)
                        .padding(end = AppDimens.Dimens16, top = AppDimens.Dimens12, bottom = AppDimens.Dimens12),
                ) {
                    LevelPanelFit {
                        AnimatedContent(
                            targetState    = stepIndex,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            modifier       = Modifier.fillMaxWidth(),
                            label          = "step-content"
                        ) { idx ->
                            val s = steps[idx]
                            Column(
                                modifier            = Modifier.fillMaxWidth().padding(AppDimens.Dimens20),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                            ) {
                                AbProgressBar(
                                    filled   = idx + 1,
                                    total    = maxOf(steps.size, 1),
                                    modifier = Modifier.widthIn(max = levelDp(220f)),
                                    onColor  = true,
                                )
                                AbCountPill(text = "Step ${idx + 1} of ${steps.size}")
                                Text(text = s.emoji, fontSize = levelSp(48f))
                                Text(
                                    text      = s.title,
                                    style     = AbTypography.fixed(levelMainTitleSize, FontWeight.Black),
                                    color     = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                AbInsetBox {
                                    Text(
                                        text      = s.body,
                                        style     = levelParagraph(15f, FontWeight.Medium),
                                        color     = Color.White.copy(alpha = 0.95f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (s.fromValue != null) {
                                    Text(
                                        text      = "👀 Watch the abacus animate!",
                                        style     = levelText(12f, FontWeight.Medium),
                                        color     = Color.White.copy(alpha = 0.70f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(Modifier.height(AppDimens.Dimens20))

                                Row(
                                    modifier              = if (idx > 0) Modifier else Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    if (idx > 0) {
                                        AbPanelButton(label = "← Back", accent = lesson.endColor, filled = false) { stepIndex-- }
                                    } else {
                                        Spacer(Modifier.weight(1f))
                                    }
                                    if (idx < steps.size - 1) {
                                        AbPanelButton(label = "Next →", accent = lesson.endColor) { stepIndex++ }
                                    } else {
                                        AbPanelButton(label = "Done! 🎉", accent = lesson.endColor, onClick = onFinished)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

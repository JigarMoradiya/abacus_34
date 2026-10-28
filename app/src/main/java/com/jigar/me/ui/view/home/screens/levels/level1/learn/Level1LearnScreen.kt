package com.jigar.me.ui.view.home.screens.levels.level1.learn

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.view.home.screens.levels.levelDp
import com.jigar.me.ui.view.home.screens.levels.levelSp
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbFitToSpace
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbInsetBox
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.screens.levels.LevelHeroPanel
import com.jigar.me.ui.view.home.screens.levels.LevelFitToBox
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.level1.allLessonContent
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelTopBarPadding
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

@Composable
fun Level1LearnScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson  = level1Lessons.find { it.id == lessonId } ?: return
    val content = allLessonContent[lessonId] ?: return
    val steps   = content.learnSteps

    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]

    val prefs         = LocalPreferencesHelper.current
    val selectedTheme = AbacusTheme.active(prefs)

    val hPad    = AppDimens.Dimens16
    val vPad    = AppDimens.Dimens12
    val spacing = AppDimens.Dimens16

    LevelScreenRoot(AbWorld.Meadow) {
        BoxWithConstraints(modifier = Modifier.levelForeground()) {
            val screenH = maxHeight
            val leftW   = maxWidth * 0.40f

            Row(
                modifier              = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // ── LEFT: back button top, abacus / emoji centred below ────────────
                Column(modifier = Modifier.width(leftW).fillMaxHeight()) {
                    AbTopBar(
                        title    = "Lesson ${lesson.id}  ·  Learn",
                        onBack   = onBackClick,
                        modifier = Modifier.levelTopBarPadding(end = 0.dp),
                    )

                    Box(
                        modifier         = Modifier.fillMaxWidth().weight(1f).padding(bottom = vPad),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            step.showFrameOnly -> {
                                AbacusFrameOnlyView(
                                    selectedTheme = selectedTheme,
                                    modifier = Modifier.fillMaxHeight(0.82f).aspectRatio(0.55f)
                                )
                            }
                            step.abacusState != null -> {
                                val cols   = step.abacusState.rods.size
                                val value  = step.abacusState.value
                                val abCalc = remember(cols) { AbacusCalculations(cols) }
                                LaunchedEffect(value) { abCalc.setAbacusValueFromString(value.toString()) }

                                // The abacus sits in a calm glass panel and shrinks to the room inside it.
                                AbGlassPanel(
                                    modifier = Modifier
                                        .heightIn(max = screenH * (if (cols > 1) 0.82f else 0.64f))
                                        .aspectRatio(if (cols > 1) 0.9f else 0.55f)
                                ) {
                                    Box(
                                        modifier         = Modifier.fillMaxSize().padding(AppDimens.Dimens20),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LevelFitToBox {
                                            AbacusWithDecimalCanvas(
                                                selectedTheme             = selectedTheme,
                                                screenType                = AppConstants.AbacusScreen.screenTypeLevel1Learn,
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
                                                onReset                   = {},
                                                onNext                    = {},
                                                modifier                  = Modifier
                                            )
                                        }
                                    }
                                }
                            }
                            else -> Text(text = step.emoji, fontSize = levelSp(80f))
                        }
                    }
                }

                // ── RIGHT: hero panel — wraps content height, centred vertically (shrinks on a screen too short for it) ──
                AbFitToSpace(
                    modifier     = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(top = vPad, bottom = vPad, end = hPad),
                    stretchWidth = true,
                ) {
                    LevelHeroPanel(
                        accent   = lesson.endColor,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AnimatedContent(
                            targetState    = stepIndex,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label    = "step-content"
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

@Composable
private fun AbacusFrameOnlyView(selectedTheme: String, modifier: Modifier = Modifier) {
    val preset   = remember(selectedTheme) { AbacusTheme.colorPreset(selectedTheme) }
    val topCol   = if (selectedTheme == "poligon_rainbow") Color(0xFFD7CCC8) else preset.abacusTopGradient
    val midCol   = if (selectedTheme == "poligon_rainbow") Color(0xFFE0E0E0) else preset.abacusCenterGradient
    val botCol   = if (selectedTheme == "poligon_rainbow") Color(0xFFCFD8DC) else preset.abacusBottomGradient
    val t        = preset.abacusTopGradient
    val beamCol  = Color(
        red   = t.red   + 0.8f * (1f - t.red),
        green = t.green + 0.8f * (1f - t.green),
        blue  = t.blue  + 0.8f * (1f - t.blue),
    )
    Canvas(modifier = modifier) {
        val strokeW = size.width * 0.045f
        val inset   = strokeW / 2 + 2f
        val corner  = strokeW * 2f
        val frameRect = androidx.compose.ui.geometry.Rect(inset, inset, size.width - inset, size.height - inset)
        val framePath = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(frameRect, CornerRadius(corner)))
        }
        // shadow
        drawPath(framePath, color = Color.Black.copy(alpha = 0.18f), style = Stroke(width = strokeW + 2f))
        // gradient frame border
        drawPath(
            path  = framePath,
            brush = Brush.verticalGradient(listOf(topCol, midCol, botCol), startY = inset, endY = size.height - inset),
            style = Stroke(width = strokeW)
        )
        // beam: positioned at ~28% from top, height ~6.5% of total
        val beamTop  = size.height * 0.285f
        val beamH    = size.height * 0.065f
        val beamLeft = inset + strokeW / 2f
        val beamW    = size.width - (inset + strokeW / 2f) * 2
        // beam shadow (dy only — horizontal offset causes visual asymmetry)
        drawRoundRect(Color.Black.copy(alpha = 0.20f), Offset(beamLeft, beamTop + 2f), Size(beamW, beamH), CornerRadius(0f))
        // beam fill
        drawRoundRect(beamCol, Offset(beamLeft, beamTop), Size(beamW, beamH), CornerRadius(0f))
    }
}

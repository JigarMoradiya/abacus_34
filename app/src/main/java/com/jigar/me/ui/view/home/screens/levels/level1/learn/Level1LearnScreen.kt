package com.jigar.me.ui.view.home.screens.levels.level1.learn

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
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
import androidx.compose.ui.unit.sp
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.level1.allLessonContent

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
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    val hPad    = AppDimens.Dimens16
    val vPad    = AppDimens.Dimens12
    val spacing = AppDimens.Dimens16

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier              = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // ── LEFT: back button top, abacus / emoji centred below ────────────
            Column(modifier = Modifier.weight(0.40f).fillMaxHeight()) {
                BackButtonWithText(
                    title       = "Lesson ${lesson.id}  ·  Learn",
                    onBackClick = onBackClick
                )

                Box(
                    modifier         = Modifier.fillMaxSize().padding(bottom = vPad),
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

                            AbacusWithDecimalCanvas(
                                selectedTheme             = selectedTheme,
                                screenType                = AppConstants.AbacusScreen.screenTypeLevel1Learn,
                                abacusData                = abCalc,
                                numberOfColumns           = cols,
                                rodMovement               = emptyList(),
                                showDirectionHint         = false,
                                isBeadSoundOn             = false,
                                isDisplayCurrentAbacusInput = false,
                                onRodMovementChange       = {},
                                onShowDirectionHintsChange = {},
                                onShowHighlighterChange   = {},
                                onReset                   = {},
                                onNext                    = {},
                                modifier                  = Modifier
                            )
                        }
                        else -> Text(text = step.emoji, fontSize = 80.sp.scaled())
                    }
                }
            }

            // ── RIGHT: gradient card — wraps content height, centred vertically ──
            val cardShape = RoundedCornerShape(AppDimens.Dimens24)
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .padding(end = hPad, top = vPad, bottom = vPad)
                    .shadow(AppDimens.Dimens8, cardShape,
                        spotColor    = lesson.endColor.copy(0.3f),
                        ambientColor = lesson.endColor.copy(0.3f))
                    .background(
                        Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)),
                        cardShape
                    )
                    .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens16),
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
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Progress dots
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
                                .fillMaxWidth()
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
                        Spacer(Modifier.height(AppDimens.Dimens20))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            if (idx > 0) {
                                LearnNavBtn(label = "← Back", filled = false) { stepIndex-- }
                            } else {
                                Spacer(Modifier.size(AppDimens.Dimens4))
                            }
                            if (idx < steps.size - 1) {
                                LearnNavBtn(label = "Next →", filled = true) { stepIndex++ }
                            } else {
                                LearnNavBtn(label = "Done! 🎉", filled = true, onClick = onFinished)
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

@Composable
private fun LearnNavBtn(label: String, filled: Boolean, onClick: () -> Unit) {
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

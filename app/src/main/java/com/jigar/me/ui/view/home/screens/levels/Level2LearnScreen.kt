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

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()

        Row(
            modifier              = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16),
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
                            val cols   = 1
                            val abCalc = remember(cols) { AbacusCalculations(cols) }
                            LaunchedEffect(step.abacusState.value) {
                                abCalc.setAbacusValueFromString(step.abacusState.value.toString())
                            }
                            AbacusWithDecimalCanvas(
                                selectedTheme               = selectedTheme,
                                screenType                  = AppConstants.AbacusScreen.screenTypeLevel1Practice,
                                abacusData                  = abCalc,
                                numberOfColumns             = cols,
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
                        else -> Text(text = step.emoji, fontSize = 80.sp)
                    }
                }
            }

            // ── RIGHT: gradient card ───────────────────────────────────────────
            val cardShape = RoundedCornerShape(AppDimens.Dimens24)
            Box(
                modifier = Modifier
                    .weight(0.60f)
                    .fillMaxHeight()
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
                    modifier       = Modifier.fillMaxSize(),
                    label          = "step-content"
                ) { idx ->
                    val s = steps[idx]
                    Column(
                        modifier            = Modifier.fillMaxSize(),
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
                        Text(text = s.emoji, fontSize = 48.sp)
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
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(Modifier.weight(1f))

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
            .clickable(remember { MutableInteractionSource() }, null) { onClick() }
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

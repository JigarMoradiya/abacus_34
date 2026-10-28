package com.jigar.me.ui.view.ab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.home.screens.abacus_practice.level_list.viewmodels.LevelProgress
import kotlin.math.max

// The three progress tints are the iOS colour sets CustomStepByStep / CustomFinalAnswer / CustomFormalExam, which are the
// same values as R.color.step_by_step_answer / final_answer / formal_exam.
private val StepByStepTint = Color(0xFF1E88E5)
private val FinalAnswerTint = Color(0xFF00897B)
private val FormalExamTint = Color(0xFFC659A7)

/**
 * The card for one Practice level: a level ribbon, a hand-drawn character that grows up with the level, and the three
 * progress rows (step by step, final answer, formal exam). Sizes itself from the frame it is given (the modifier must
 * give it a bounded width and height). Mirrors `AbPracticeLevelCard.swift`.
 *
 * Needs `AbLevelLook.of(level)` (name, light, deep) and `AbCritterView(level, modifier)` from the critters file.
 *
 * @param levelLabel The ribbon text, already resolved ("Level 3"). The character name under it comes from `AbLevelLook`.
 */
@Composable
fun AbPracticeLevelCard(
    level: Int,
    progress: LevelProgress,
    modifier: Modifier = Modifier,
    levelLabel: String = "Level $level",
    onClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val dens = LocalDensity.current.density
    BoxWithConstraints(modifier) {
        val w = maxWidth.value
        val h = maxHeight.value
        val look = AbLevelLook.of(level)
        val edge = max(6f, h * 0.022f)
        val faceH = h - edge
        val radius = (w * 0.11f).dp
        val shape = RoundedCornerShape(radius)
        Box(
            Modifier
                .size(maxWidth, maxHeight)
                .abSquash(
                    face = Color.Transparent, edge = look.deep.abDarkened(0.4f), stroke = null,
                    cornerRadius = radius, edgeHeight = edge.dp, sound = null, onClick = onClick,
                )
                .clearAndSetSemantics {
                    contentDescription = levelLabel
                    role = Role.Button
                },
        ) {
            Box(Modifier.fillMaxSize()) {
                // Scene
                Box(Modifier.fillMaxSize().clip(shape)) {
                    Canvas(Modifier.fillMaxSize()) { drawRect(Brush.verticalGradient(listOf(look.light, look.deep))) }
                    AbLevelScene(Modifier.fillMaxSize())
                    // ground the character stands on
                    Box(
                        Modifier
                            .abPosition((w * 0.5f).dp, (faceH * 0.575f).dp)
                            .requiredSize((w * 0.56f).dp, (w * 0.1f).dp)
                            .background(look.deep.abDarkened(0.25f).copy(alpha = 0.35f), AbCardEllipse),
                    )
                    AbCritterView(
                        level,
                        Modifier.abPosition((w * 0.5f).dp, (faceH * 0.385f).dp).requiredSize((w * 0.64f).dp),
                    )
                }

                // Level ribbon
                Text(
                    levelLabel,
                    style = abFixedStyle(w * 0.105f, FontWeight.ExtraBold).copy(shadow = abShadow(dens, Color.Black.copy(alpha = 0.3f), dy = 1.5f)),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = (w * 0.04f).dp)
                        .drawBehind {
                            drawRoundRect(
                                look.deep.abDarkened(0.4f), topLeft = Offset(0f, (w * 0.016f).dp.toPx()), size = size,
                                cornerRadius = CornerRadius(size.height / 2f),
                            )
                        }
                        .background(
                            Brush.verticalGradient(listOf(look.deep.abBlended(Color.White, 0.1f), look.deep.abDarkened(0.12f))),
                            CircleShape,
                        )
                        .border(max(2f, w * 0.012f).dp, Color.White.copy(alpha = 0.9f), CircleShape)
                        .padding(horizontal = (w * 0.09f).dp, vertical = (w * 0.015f).dp),
                )

                // Name tag under the character
                Text(
                    look.name,
                    style = abFixedStyle(w * 0.07f, FontWeight.ExtraBold),
                    color = look.deep.abDarkened(0.35f),
                    maxLines = 1,
                    modifier = Modifier
                        .abPosition((w * 0.5f).dp, (faceH * 0.585f).dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .padding(horizontal = (w * 0.06f).dp, vertical = (w * 0.01f).dp),
                )

                // Progress panel
                val panelShape = RoundedCornerShape((w * 0.07f).dp)
                Column(
                    Modifier
                        .abPosition((w * 0.5f).dp, (faceH - w * 0.24f).dp)
                        .width((w * 0.92f).dp)
                        .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.94f else 0.95f), panelShape)
                        .border(2.dp, colors.outline, panelShape)
                        .padding(horizontal = (w * 0.06f).dp, vertical = (w * 0.04f).dp),
                    verticalArrangement = Arrangement.spacedBy((w * 0.022f).dp),
                ) {
                    AbPracticeProgressRow(progress.stepCompleted, progress.stepTotal, StepByStepTint, w)
                    AbPracticeProgressRow(progress.finalCompleted, progress.finalTotal, FinalAnswerTint, w)
                    AbPracticeProgressRow(progress.examCompleted, progress.examTotal, FormalExamTint, w)
                }

                // The white rim of the card
                Box(Modifier.fillMaxSize().border(max(3f, w * 0.016f).dp, Color.White.copy(alpha = 0.92f), shape))
            }
        }
    }
}

/** Clouds, sparkles and little dots behind the character. */
@Composable
private fun AbLevelScene(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        // dx, dy, width, height of each puff, relative to the cloud size
        val puffs = arrayOf(
            floatArrayOf(0f, 0f, 1f, 0.45f), floatArrayOf(-0.28f, 0.05f, 0.6f, 0.38f),
            floatArrayOf(0.3f, 0.06f, 0.62f, 0.38f), floatArrayOf(0.02f, -0.14f, 0.55f, 0.42f),
        )
        fun cloud(cx: Float, cy: Float, s: Float) {
            for (p in puffs) {
                drawOval(
                    Color.White.copy(alpha = 0.28f),
                    topLeft = Offset(cx + p[0] * s - p[2] * s / 2f, cy + p[1] * s - p[3] * s / 2f),
                    size = Size(p[2] * s, p[3] * s),
                )
            }
        }
        cloud(w * 0.2f, h * 0.2f, w * 0.3f)
        cloud(w * 0.82f, h * 0.34f, w * 0.26f)
        // x, y, radius (relative to the width)
        val dots = arrayOf(
            floatArrayOf(0.1f, 0.5f, 0.018f), floatArrayOf(0.9f, 0.18f, 0.02f),
            floatArrayOf(0.84f, 0.52f, 0.014f), floatArrayOf(0.16f, 0.38f, 0.014f),
        )
        for (d in dots) {
            drawCircle(Color.White.copy(alpha = 0.45f), radius = d[2] * w, center = Offset(d[0] * w, d[1] * h))
        }
    }
}

/** One progress row: a chunky bar and a count chip. */
@Composable
private fun AbPracticeProgressRow(completed: Int, total: Int, tint: Color, w: Float) {
    val colors = LocalAbColors.current
    val frac = if (total > 0) completed.toFloat() / total else 0f
    val barH = max(8f, w * 0.046f)
    val spec = spring<Float>(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
    val shownFrac by animateFloatAsState(frac, spec, label = "abPracticeBar")
    val shownAlpha by animateFloatAsState(if (frac > 0f) 1f else 0f, spec, label = "abPracticeBarAlpha")
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy((w * 0.03f).dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithConstraints(Modifier.weight(1f).height(barH.dp)) {
            Box(Modifier.fillMaxSize().background(colors.surfaceTint, CircleShape).border(1.5.dp, colors.outline, CircleShape))
            Box(
                Modifier
                    .width(maxOf(barH.dp, maxWidth * shownFrac))
                    .fillMaxHeight()
                    .alpha(shownAlpha)
                    .background(Brush.verticalGradient(listOf(tint.abBlended(Color.White, 0.3f), tint)), CircleShape),
            )
        }
        Box(
            Modifier
                .background(tint, CircleShape)
                .widthIn(min = (w * 0.2f).dp)
                .padding(horizontal = (w * 0.025f).dp, vertical = (w * 0.006f).dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("$completed/$total", style = abFixedStyle(w * 0.052f, FontWeight.ExtraBold, tabular = true), color = Color.White, maxLines = 1)
        }
    }
}

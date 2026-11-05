package com.jigar.me.ui.view.home.screens.activities.exam.play.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbChip
import com.jigar.me.ui.view.ab.AbCritterView
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbOptionButton
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.base.abacus_base.utils.MathUtils
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.ExamGenerator
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.MainQuestionType
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionParts
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayViewModel
import com.jigar.me.ui.view.home.screens.activities.rememberEncouragements
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * One exam question: a friend who cheers on the left, the question (a chip, the sum on a calm panel, a blinking hint and
 * the four answers) in the middle and an empty slot on the right that keeps the question centred.
 */
@Composable
fun ExamQuestionSection(
    uiState: ExamPlayUiState,
    viewModel: ExamPlayViewModel,
    modifier: Modifier = Modifier
) {
    // Safety check
    if (uiState.examPaper.isEmpty() || uiState.currentIndex >= uiState.examPaper.size) {
        return
    }

    // Beginner, intermediate and expert get a different friend, like the Practice levels do.
    val critterLevel = when (uiState.selectedDifficulty) {
        AppConstants.EXAM.examDifficultyExpert -> 3
        AppConstants.EXAM.examDifficultyIntermediate -> 2
        else -> 1
    }

    Row(
        modifier = modifier.padding(horizontal = DeviceInfo.screenHorizontalPadding()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // The friend on the left is balanced by an empty slot on the right, so the question stays centred.
        Box(Modifier.width(ActivityDimens.examMascotSlot), contentAlignment = Alignment.Center) { ExamMascot(critterLevel, uiState.currentIndex) }
        ExamQuestion(uiState, viewModel)
        Spacer(Modifier.width(ActivityDimens.examMascotSlot))
    }
}

@Composable
private fun ExamQuestion(uiState: ExamPlayUiState, viewModel: ExamPlayViewModel) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val blinkAlpha by rememberBlinkAlpha()
    val question = uiState.examPaper[uiState.currentIndex]
    var parts: QuestionParts? = null
    if (question.queType != MainQuestionType.question.name) {
        parts = ExamGenerator.questionPartsFromSpace(question.que, question.index ?: 0)
    }

    // longer sums with no operator sign in them (a column of numbers) get a smaller size
    val hasOperator = listOf("×", "x", "*", "÷", "/").any { question.que.contains(it) }
    var fontSize = 36f
    if (!hasOperator) {
        val lines = MathUtils.splitQuestionIntoLines(question.que).size
        if (lines == 4) fontSize = 30f else if (lines == 5) fontSize = 28f
    }
    val questionStyle = AbTypography.fixed(fontSize * s, FontWeight.ExtraBold)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        AbChip(
            text = "Question ${uiState.currentIndex + 1}",
            tone = AbTone.Purple,
            modifier = Modifier.padding(bottom = AppDimens.Dimens8),
            icon = Icons.AutoMirrored.Filled.Help,
        )

        AbGlassPanel(padding = 14.dp) {
            // ---------- Question ----------
            if (question.queType == MainQuestionType.question.name) {
                Text(
                    text = "${question.que} = ?",
                    style = questionStyle,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                )
            } else {
                val left = parts?.left
                val number = parts?.number
                val right = parts?.right
                Row(verticalAlignment = Alignment.CenterVertically) {

                    if (!left.isNullOrEmpty()) {
                        Text(
                            text = left,
                            style = questionStyle,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                        )
                    }

                    if (question.queType == MainQuestionType.abacus.name) {
                        val numberOfColumns = number.toString().length
                        val abacusCalc = AbacusCalculations(numberOfColumns = numberOfColumns)
                        abacusCalc.setAbacusValueFromString(number.toString())
                        AbacusWithDecimalCanvas(
                            selectedTheme = viewModel.selectedTheme,
                            screenType = AppConstants.AbacusScreen.screenTypeExam,
                            abacusData = abacusCalc,
                            numberOfColumns = numberOfColumns,
                            rodMovement = emptyList(),
                            showDirectionHint = false,
                            isBeadSoundOn = false,
                            isDisplayCurrentAbacusInput = false,
                            onRodMovementChange = { },
                            onShowDirectionHintsChange = { },
                            onShowHighlighterChange = { },
                            onReset = {},
                            onNext = {},
                        )
                    } else {
                        ExamBlankBox(40.dp * s)
                    }

                    if (!right.isNullOrEmpty()) {
                        Text(
                            text = right,
                            style = questionStyle,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                        )
                    }

                    val tail = if (question.queType == MainQuestionType.missingNumber.name) {
                        val newQue = MathUtils.formatQuestion(question.que)
                        "= ${MathUtils.calculateStringExpression(newQue).toInt()}"
                    } else {
                        " = ?"
                    }
                    Text(
                        text = tail,
                        style = questionStyle,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12),
                    )
                }
            }
        }

        // ---------- Hint, right under the question ----------
        Text(
            text = stringResource(R.string.TapCorrectAns),
            style = AbTypography.fixed(13f * s, FontWeight.Bold),
            color = colors.textSecondary,
            modifier = Modifier
                .padding(top = AppDimens.Dimens10)
                .alpha(blinkAlpha),
        )

        // ---------- Options ----------
        Column(
            modifier = Modifier.padding(top = AppDimens.Dimens8),
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                ExamOptionTile(question.option1) { viewModel.onOptionSelected(question.option1, question.answer) }
                ExamOptionTile(question.option2) { viewModel.onOptionSelected(question.option2, question.answer) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                ExamOptionTile(question.option3) { viewModel.onOptionSelected(question.option3, question.answer) }
                ExamOptionTile(question.option4) { viewModel.onOptionSelected(question.option4, question.answer) }
            }
        }
    }
}

/**
 * An answer choice: the kit's neutral option tile. It is silent itself, because the view model plays the click when the
 * answer is chosen.
 */
@Composable
private fun ExamOptionTile(value: Int, onClick: () -> Unit) {
    AbOptionButton(
        text = value.toString(),
        width = ActivityDimens.examOptionWidth,
        height = ActivityDimens.examOptionHeight,
        sound = null,
        onClick = onClick,
    )
}

/**
 * An empty answer slot in a fill-in-the-blank question: a dashed box with a "?" that breathes gently, so it reads as a
 * place to fill rather than as part of the sum.
 */
@Composable
private fun ExamBlankBox(blank: Dp) {
    val colors = LocalAbColors.current
    val tone = colors.accentOrange
    val transition = rememberInfiniteTransition(label = "examBlank")
    val pulse by transition.animateFloat(
        initialValue = 1f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "examBlankPulse",
    )
    val box = blank * 1.4f
    Box(
        Modifier
            .padding(horizontal = AppDimens.Dimens8)
            .graphicsLayer { scaleX = pulse; scaleY = pulse }
            .size(box)
            .background(tone.copy(alpha = if (colors.isDark) 0.22f else 0.14f), RoundedCornerShape(blank * 0.3f))
            .drawBehind {
                val sw = max(2.5.dp.toPx(), blank.toPx() * 0.07f)
                val corner = blank.toPx() * 0.3f - sw / 2f
                drawRoundRect(
                    color = tone,
                    topLeft = Offset(sw / 2f, sw / 2f),
                    size = Size(this.size.width - sw, this.size.height - sw),
                    cornerRadius = CornerRadius(corner, corner),
                    style = Stroke(
                        width = sw, cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(blank.toPx() * 0.28f, blank.toPx() * 0.2f)),
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text("?", style = AbTypography.fixed(blank.value, FontWeight.ExtraBold), color = tone)
    }
}

/**
 * A friendly character that cheers beside the question: a speech bubble with a star, and the character bobbing a little.
 * A new cheer pops in from the bottom of the bubble.
 */
@Composable
private fun ExamMascot(level: Int, cheerIndex: Int) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val slot = ActivityDimens.examMascotSlot
    val critterSize = slot * 0.86f
    val cheers = rememberEncouragements()
    val tail = 7.dp * s

    // the character bobs: a sine with a period of about 2.6 s, 3 dp up and down
    val bob = rememberInfiniteTransition(label = "examBob")
    val phase by bob.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2618, easing = LinearEasing)),
        label = "examBobPhase",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp * s),
    ) {
        AnimatedContent(
            targetState = cheerIndex,
            transitionSpec = {
                val popIn = scaleIn(spring(0.8f, Spring.StiffnessMediumLow), 0.7f, TransformOrigin(0.5f, 1f)) + fadeIn()
                val popOut = scaleOut(spring(0.8f, Spring.StiffnessMediumLow), 0.7f, TransformOrigin(0.5f, 1f)) + fadeOut()
                ContentTransform(popIn, popOut, sizeTransform = SizeTransform(clip = false) { _, _ -> snap() })
            },
            label = "examCheer",
        ) { index ->
            Row(
                Modifier
                    .widthIn(max = slot - 4.dp)
                    .drawBehind {
                        val bubble = bubblePath(this.size, 16.dp.toPx() * s, tail.toPx())
                        translate(0f, 3.dp.toPx() * s) { drawPath(bubble, colors.outline) }
                        drawPath(bubble, colors.bgSecondary)
                        drawPath(bubble, colors.outline, style = Stroke(2.5.dp.toPx()))
                    }
                    .padding(start = 12.dp * s, end = 12.dp * s, top = 8.dp * s, bottom = 8.dp * s + tail),
                horizontalArrangement = Arrangement.spacedBy(5.dp * s, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Star, null, Modifier.size((12f * AB_SF_TO_VECTOR).dp * s), tint = Color(0xFFFFB800))
                AbShrinkText(
                    text = if (cheers.isEmpty()) "" else cheers[index % cheers.size],
                    style = AbTypography.fixed(13f * s, FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    minScale = 0.7f,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(Modifier.size(critterSize, critterSize * 1.02f), contentAlignment = Alignment.BottomCenter) {
            AbCritterView(
                level = level,
                modifier = Modifier
                    .size(critterSize)
                    .graphicsLayer { translationY = sin(phase) * 3.dp.toPx() * s - critterSize.toPx() * 0.05f },
            )
        }
    }
}

/** A speech bubble: one outline (a rounded body plus a tail pointing down at the character, in the middle). */
private fun bubblePath(size: Size, corner: Float, tail: Float): Path {
    val w = size.width
    val h = size.height - tail
    val r = min(corner, h / 2f)
    val cx = (w * 0.5f).coerceIn(r + tail, w - r - tail)
    return Path().apply {
        moveTo(r, 0f)
        lineTo(w - r, 0f)
        arcTo(Rect(w - 2 * r, 0f, w, 2 * r), -90f, 90f, false)
        lineTo(w, h - r)
        arcTo(Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
        lineTo(cx + tail, h)
        lineTo(cx, size.height)
        lineTo(cx - tail, h)
        lineTo(r, h)
        arcTo(Rect(0f, h - 2 * r, 2 * r, h), 90f, 90f, false)
        lineTo(0f, r)
        arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
        close()
    }
}

@Composable
fun rememberBlinkAlpha(): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")

    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )
}

package com.jigar.me.ui.view.home.screens.levels

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay

private val CORRECT_COLOR = Color(0xFF2E7D32)
private val WRONG_COLOR   = Color(0xFFC62828)

@Composable
fun Level1QuizScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson    = level1Lessons.find { it.id == lessonId } ?: return
    val content   = allLessonContent[lessonId] ?: return
    val questions = content.quizQuestions

    var qIndex      by remember { mutableIntStateOf(0) }
    var selected    by remember { mutableStateOf<Int?>(null) }
    var score       by remember { mutableIntStateOf(0) }
    var showResult  by remember { mutableStateOf(false) }

    // Auto-advance after answer
    LaunchedEffect(selected) {
        val sel = selected ?: return@LaunchedEffect
        if (sel == questions[qIndex].correctAnswer) score++
        delay(900)
        if (qIndex < questions.size - 1) {
            qIndex++
            selected = null
        } else {
            showResult = true
        }
    }

    if (showResult) {
        QuizResultScreen(
            lesson     = lesson,
            score      = score,
            total      = questions.size,
            onRetry    = {
                qIndex     = 0
                selected   = null
                score      = 0
                showResult = false
            },
            onContinue = onFinished
        )
        return
    }

    val question = questions[qIndex]

    val prefs = LocalPreferencesHelper.current
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(
                title       = "Lesson ${lesson.id}  ·  Quiz",
                onBackClick = onBackClick
            )

            // Progress bar
            LinearProgressIndicator(
                progress    = { (qIndex.toFloat() + 1f) / questions.size },
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
                    // ── Left panel: abacus diagram ─────────────────────────
                    AnimatedContent(
                        targetState = question,
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                        modifier     = Modifier.weight(0.45f).fillMaxHeight(),
                        label        = "quiz-abacus"
                    ) { q ->
                        val leftShape = RoundedCornerShape(AppDimens.Dimens24)
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.95f)
                                .padding(bottom = AppDimens.Dimens8)
                                .shadow(AppDimens.Dimens8, leftShape,
                                    spotColor    = lesson.endColor.copy(0.3f),
                                    ambientColor = lesson.endColor.copy(0.3f))
                                .background(
                                    Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)),
                                    leftShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val cols   = q.abacusState.rods.size
                            val abCalc = remember(cols) { AbacusCalculations(cols) }
                            LaunchedEffect(q.abacusState.value) {
                                abCalc.setAbacusValueFromString(q.abacusState.value.toString())
                            }
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
                                modifier = Modifier
                                    .fillMaxHeight(0.75f)
                                    .fillMaxWidth(if (cols > 1) 0.80f else 0.55f)
                            )
                        }
                    }

                    // ── Right panel: question + choices ────────────────────
                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                            .padding(vertical = AppDimens.Dimens4),
                        verticalArrangement   = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically),
                        horizontalAlignment   = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Question ${qIndex + 1} of ${questions.size}",
                            style      = MaterialTheme.typography.labelLarge.scaled(),
                            color      = Color.White.copy(0.80f),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "What number is this? 🤔",
                            style      = MaterialTheme.typography.headlineSmall.scaled(),
                            color      = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign  = TextAlign.Center
                        )

                        Spacer(Modifier.height(AppDimens.Dimens8))

                        // 2×2 choice grid
                        val rows = question.choices.chunked(2)
                        rows.forEach { rowChoices ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowChoices.forEach { choice ->
                                    ChoiceButton(
                                        choice     = choice,
                                        selected   = selected,
                                        correct    = question.correctAnswer,
                                        modifier   = Modifier.weight(1f),
                                        onClick    = { if (selected == null) selected = choice }
                                    )
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
private fun ChoiceButton(
    choice: Int,
    selected: Int?,
    correct: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isSelected    = selected == choice
    val isCorrect     = choice == correct
    val answered      = selected != null
    val scale by animateFloatAsState(
        if (isSelected) 0.92f else 1f,
        animationSpec = tween(120),
        label = "scale"
    )

    val bgColor = when {
        !answered          -> Color.White.copy(0.18f)
        isCorrect          -> CORRECT_COLOR
        isSelected         -> WRONG_COLOR
        else               -> Color.White.copy(0.10f)
    }
    val textColor = if (!answered) Color.White else if (isCorrect || isSelected) Color.White else Color.White.copy(0.5f)

    val shape = RoundedCornerShape(AppDimens.Dimens16)
    Box(
        modifier = modifier
            .scale(scale)
            .shadow(if (!answered) AppDimens.Dimens6 else AppDimens.Dimens2, shape)
            .background(bgColor, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                enabled           = !answered
            ) { onClick() }
            .padding(vertical = AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = "$choice",
            fontSize   = 30.sp,
            color      = textColor,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun QuizResultScreen(
    lesson: Level1LessonData,
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
) {
    val stars = when {
        score >= total - 1 -> 3
        score >= total / 2 -> 2
        else               -> 1
    }
    val starEmoji = "⭐".repeat(stars) + "☆".repeat(3 - stars)
    val message = when (stars) {
        3    -> "Perfect Score! You're a Star! 🌟"
        2    -> "Great Job! Keep Practicing! 💪"
        else -> "Good Try! Let's Practice More! 🎯"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            BackButtonWithText(title = "Lesson ${lesson.id}  ·  Quiz", onBackClick = onContinue)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val shape = RoundedCornerShape(AppDimens.Dimens24)
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.80f)
                    .fillMaxWidth(0.58f)
                    .shadow(AppDimens.Dimens12, shape,
                        spotColor    = lesson.endColor.copy(0.45f),
                        ambientColor = lesson.endColor.copy(0.45f))
                    .background(Brush.linearGradient(listOf(lesson.startColor, lesson.endColor)), shape)
                    .padding(AppDimens.Dimens28),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterVertically)
                ) {
                    Text(starEmoji, fontSize = 52.sp)

                    Text(
                        "$score / $total Correct",
                        style      = MaterialTheme.typography.headlineMedium.scaled(),
                        color      = Color.White,
                        fontWeight = FontWeight.Black
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.18f), RoundedCornerShape(AppDimens.Dimens16))
                            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12)
                    ) {
                        Text(
                            message,
                            style      = MaterialTheme.typography.bodyLarge.scaled(),
                            color      = Color.White,
                            textAlign  = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(AppDimens.Dimens4))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                        ResultBtn("Try Again 🔄", filled = false, onClick = onRetry)
                        ResultBtn("Continue →", filled = true, onClick = onContinue)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.22f), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { onClick() }
            .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style      = MaterialTheme.typography.labelLarge.scaled(),
            color      = if (filled) Color(0xFF1A237E) else Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

package com.jigar.me.ui.view.home.screens.levels.level1.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsOptionButton
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.level4.optionType
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay
import com.jigar.me.ui.view.home.screens.levels.level1.level1Lessons
import com.jigar.me.ui.view.home.screens.levels.level1.generateQuizQuestions

@Composable
fun Level1QuizScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson    = level1Lessons.find { it.id == lessonId } ?: return
    val questions = remember { generateQuizQuestions(lessonId) }

    var qIndex     by remember { mutableIntStateOf(0) }
    var selected   by remember { mutableStateOf<Int?>(null) }
    var score      by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()
    LaunchedEffect(showResult) {
        if (!showResult) return@LaunchedEffect
        val stars = when {
            score >= questions.size - 1 -> 3
            score >= questions.size / 2  -> 2
            else -> 1
        }
        val key = "l1_quiz_stars_$lessonId"
        if (stars > prefs.getCustomParamInt(key, 0)) prefs.setCustomParamInt(key, stars)
    }

    LaunchedEffect(selected) {
        val sel = selected ?: return@LaunchedEffect
        if (sel == questions[qIndex].correctAnswer) {
            score++
            AudioPlayerManager.playSoundCorrectAns()
        } else {
            AudioPlayerManager.playSoundOptionWrong()
        }
        delay(900)
        if (qIndex < questions.size - 1) { qIndex++; selected = null }
        else {
            if (score.toFloat() / questions.size >= 0.70f) AudioPlayerManager.playSoundClap()
            showResult = true
        }
    }

    val question      = questions[minOf(qIndex, questions.size - 1)]
    val selectedTheme = prefs.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default)

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            // ── Header: back button + inline progress bar ──────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(
                    title       = "Lesson ${lesson.id}  ·  Quiz",
                    onBackClick = onBackClick
                )
                LinearProgressIndicator(
                    progress   = { (qIndex.toFloat() + 1f) / questions.size },
                    modifier   = Modifier
                        .weight(1f)
                        .padding(end = AppDimens.Dimens32)
                        .height(AppDimens.Dimens8),
                    color      = lesson.endColor,
                    trackColor = lesson.endColor.copy(alpha = 0.25f),
                )
            }

            // ── Main content ───────────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = AppDimens.Dimens12, end = AppDimens.Dimens20, bottom = AppDimens.Dimens12)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // ── Left panel: abacus ─────────────────────────────
                    AnimatedContent(
                        targetState  = question,
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                        modifier     = Modifier.weight(0.35f).fillMaxHeight(),
                        label        = "quiz-abacus"
                    ) { q ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.95f)
                                .padding(bottom = AppDimens.Dimens8),
                            contentAlignment = Alignment.Center
                        ) {
                            val cols   = q.abacusState.rods.size
                            val abCalc = remember(cols) { AbacusCalculations(cols) }
                            LaunchedEffect(q.abacusState.value) {
                                abCalc.setAbacusValueFromString(q.abacusState.value.toString())
                            }
                            Box(
                                modifier         = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AbacusWithDecimalCanvas(
                                    selectedTheme               = selectedTheme,
                                    screenType                  = AppConstants.AbacusScreen.screenTypeLevel1Learn,
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
                        }
                    }

                    // ── Right panel: question + options ────────────────
                    val rightShape = RoundedCornerShape(AppDimens.Dimens24)
                    Box(
                        modifier = Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .padding(bottom = AppDimens.Dimens8)
                            .shadow(AppDimens.Dimens6, rightShape,
                                spotColor    = lesson.endColor.copy(0.25f),
                                ambientColor = lesson.endColor.copy(0.15f))
                            .background(
                                Brush.linearGradient(
                                    listOf(lesson.startColor.copy(0.70f), lesson.endColor.copy(0.70f))
                                ),
                                rightShape
                            )
                            .padding(AppDimens.Dimens20),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12, Alignment.CenterVertically),
                            modifier = Modifier.fillMaxSize()
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

                            Spacer(Modifier.height(AppDimens.Dimens4))

                            // Shuffle once per question so correct answer isn't always first
                            val shuffledChoices = remember(question) { question.choices.shuffled() }

                            // Options grid — 2×2, equal width buttons filling available space
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Dimens10),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (shuffledChoices.size >= 2) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Dimens10),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        KidsOptionButton(
                                            text     = "${shuffledChoices[0]}",
                                            type     = optionType(shuffledChoices[0], question.correctAnswer, selected),
                                            fontSize = examOptionHeight.value.sp * 0.6f,
                                            enabled  = selected == null,
                                            onClick  = { if (selected == null) selected = shuffledChoices[0] },
                                            modifier = Modifier.weight(1f).height(examOptionHeight)
                                        )
                                        KidsOptionButton(
                                            text     = "${shuffledChoices[1]}",
                                            type     = optionType(shuffledChoices[1], question.correctAnswer, selected),
                                            fontSize = examOptionHeight.value.sp * 0.6f,
                                            enabled  = selected == null,
                                            onClick  = { if (selected == null) selected = shuffledChoices[1] },
                                            modifier = Modifier.weight(1f).height(examOptionHeight)
                                        )
                                    }
                                }
                                if (shuffledChoices.size >= 4) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Dimens10),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        KidsOptionButton(
                                            text     = "${shuffledChoices[2]}",
                                            type     = optionType(shuffledChoices[2], question.correctAnswer, selected),
                                            fontSize = examOptionHeight.value.sp * 0.6f,
                                            enabled  = selected == null,
                                            onClick  = { if (selected == null) selected = shuffledChoices[2] },
                                            modifier = Modifier.weight(1f).height(examOptionHeight)
                                        )
                                        KidsOptionButton(
                                            text     = "${shuffledChoices[3]}",
                                            type     = optionType(shuffledChoices[3], question.correctAnswer, selected),
                                            fontSize = examOptionHeight.value.sp * 0.6f,
                                            enabled  = selected == null,
                                            onClick  = { if (selected == null) selected = shuffledChoices[3] },
                                            modifier = Modifier.weight(1f).height(examOptionHeight)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Quiz result popup overlay ──────────────────────────────────
        val stars     = when { score >= questions.size - 1 -> 3; score >= questions.size / 2 -> 2; else -> 1 }
        val starEmoji = "⭐".repeat(stars) + "☆".repeat(3 - stars)
        val message   = when (stars) {
            3    -> "Perfect Score! You're a Star! 🌟"
            2    -> "Great Job! Keep Practicing! 💪"
            else -> "Good Try! Let's Practice More! 🎯"
        }
        AnimatedVisibility(
            visible  = showResult,
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
                        Text(starEmoji, fontSize = 52.sp.scaled())
                        Text(
                            "$score / ${questions.size} Correct",
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
                                message,
                                style      = MaterialTheme.typography.bodyLarge.scaled(),
                                color      = Color.White.copy(0.90f),
                                textAlign  = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(AppDimens.Dimens4))
                        Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12)) {
                            QuizResultBtn("Try Again 🔄", filled = false) {
                                qIndex = 0; selected = null; score = 0; showResult = false
                            }
                            QuizResultBtn("Continue →", filled = true, onClick = {
                                if (stars == 3) reviewGate.attempt(prefs) { onFinished() } else onFinished()
                            })
                        }
                    }
                }
            }
        }

        ReviewGateHost(reviewGate, prefs)
    }
}

@Composable
private fun QuizResultBtn(label: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppDimens.Dimens100)
    Box(
        modifier = Modifier
            .shadow(AppDimens.Dimens4, shape)
            .background(if (filled) Color.White else Color.White.copy(0.22f), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { AudioPlayerManager.playSoundBtnClick(); onClick() }
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

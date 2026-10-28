package com.jigar.me.ui.view.home.screens.levels.level2.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Help
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
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbInsetBox
import com.jigar.me.ui.view.ab.AbPanelButton
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.AbacusFrameStyle
import com.jigar.me.ui.view.base.abacus_base.AbacusCalculations
import com.jigar.me.ui.view.base.abacus_base.components.abacus_canvas.AbacusWithDecimalCanvas
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.LevelHeroPanel
import com.jigar.me.ui.view.home.screens.levels.LevelPanelFit
import com.jigar.me.ui.view.home.screens.levels.LevelProgressTopBar
import com.jigar.me.ui.view.home.screens.levels.LevelQuizOptions
import com.jigar.me.ui.view.home.screens.levels.LevelResultPopup
import com.jigar.me.ui.view.home.screens.levels.LevelFitToBox
import com.jigar.me.ui.view.home.screens.levels.LevelScreenRoot
import com.jigar.me.ui.view.home.screens.levels.level2.generateLevel2QuizQuestions
import com.jigar.me.ui.view.home.screens.levels.level2.level2Lessons
import com.jigar.me.ui.view.home.screens.levels.levelForeground
import com.jigar.me.ui.view.home.screens.levels.levelMainTitleSize
import com.jigar.me.ui.view.home.screens.levels.levelParagraph
import com.jigar.me.ui.view.home.screens.levels.levelText
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.delay

@Composable
fun Level2QuizScreen(
    lessonId: Int,
    onBackClick: () -> Unit,
    onFinished: () -> Unit,
) {
    val lesson    = level2Lessons.find { it.id == lessonId } ?: return
    val questions = remember { generateLevel2QuizQuestions(lessonId) }

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
        val key = "l2_quiz_stars_$lessonId"
        if (stars > prefs.getCustomParamInt(key, 0)) prefs.setCustomParamInt(key, stars)
    }

    LaunchedEffect(selected) {
        val sel = selected ?: return@LaunchedEffect
        if (sel == questions[qIndex].result) {
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

    val isTablet      = DeviceInfo.isTablet
    val question      = questions[minOf(qIndex, questions.size - 1)]
    val selectedTheme = AbacusTheme.active(prefs)

    LevelScreenRoot(AbWorld.Sunset) {
        Column(modifier = Modifier.levelForeground()) {

            // ── Header: back button + progress ────────────────────────────────
            LevelProgressTopBar(
                title  = "Lesson ${lesson.id}  ·  Quiz",
                filled = qIndex + 1,
                total  = questions.size,
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
                    // ── Left panel: abacus shows the result ───────────────
                    AbGlassPanel(modifier = Modifier.width(leftW).fillMaxHeight()) {
                        AnimatedContent(
                            targetState    = question,
                            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                            modifier       = Modifier.fillMaxSize(),
                            label          = "quiz-abacus"
                        ) { q ->
                            val abCalc = remember { AbacusCalculations(lesson.columns) }
                            LaunchedEffect(q.abacusState.value) {
                                abCalc.setAbacusValueFromString(q.abacusState.value.toString())
                            }
                            // the abacus fills the card, shrinking to fit inside it
                            Box(
                                modifier         = Modifier.fillMaxSize().padding(AppDimens.Dimens8),
                                contentAlignment = Alignment.Center
                            ) {
                                LevelFitToBox {
                                    AbacusWithDecimalCanvas(
                                        selectedTheme               = selectedTheme,
                                        screenType                  = AppConstants.AbacusScreen.screenTypeLevel2Practice,
                                        abacusData                  = abCalc,
                                        numberOfColumns             = lesson.columns,
                                        rodMovement                 = emptyList(),
                                        showDirectionHint           = false,
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

                    // ── Right panel: equation + options ───────────────────
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
                                AbCountPill(text = "Question ${qIndex + 1} of ${questions.size}", icon = Icons.Rounded.Help)

                                val expr = if (question.c != null)
                                    "${question.a} ${question.op} ${question.b} ${question.op2} ${question.c} = ?"
                                else
                                    "${question.a} ${question.op} ${question.b} = ?"
                                Text(
                                    expr,
                                    style     = levelText(24f, FontWeight.Black),
                                    color     = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    "What's the answer? 🤔",
                                    style     = levelText(24f, FontWeight.Bold),
                                    color     = Color.White.copy(alpha = 0.90f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.height(AppDimens.Dimens4))

                                val shuffledChoices = remember(question) { question.choices.shuffled() }
                                LevelQuizOptions(
                                    choices  = shuffledChoices,
                                    correct  = question.result,
                                    selected = selected,
                                    onPick   = { selected = it }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Quiz result popup ─────────────────────────────────────────────────
        if (showResult) {
            val stars     = when { score >= questions.size - 1 -> 3; score >= questions.size / 2 -> 2; else -> 1 }
            val starEmoji = "⭐".repeat(stars) + "☆".repeat(3 - stars)
            val message   = when (stars) {
                3    -> "Perfect Score! You're a Math Star! 🌟"
                2    -> "Great Job! Keep Practicing! 💪"
                else -> "Good Try! Let's Practice More! 🎯"
            }
            LevelResultPopup(accent = lesson.endColor) {
                Text(starEmoji, fontSize = levelSp(52f))
                Text(
                    "$score / ${questions.size} Correct",
                    style = AbTypography.fixed(levelMainTitleSize, FontWeight.Black),
                    color = Color.White
                )
                AbInsetBox {
                    Text(
                        message,
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
                        onClick = { qIndex = 0; selected = null; score = 0; showResult = false }
                    )
                    AbPanelButton(
                        label   = "Continue →",
                        accent  = lesson.endColor,
                        onClick = { if (stars == 3) reviewGate.attempt(prefs) { onFinished() } else onFinished() }
                    )
                }
            }
        }

        ReviewGateHost(reviewGate, prefs)
    }
}

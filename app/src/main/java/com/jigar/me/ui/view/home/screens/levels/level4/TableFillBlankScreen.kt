package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbOptionButton
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.FitOnPhone
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.abEmojiStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.abTabletSize
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemPrimary
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemSecondary
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionWidth
import kotlinx.coroutines.delay

@Composable
fun TableFillBlankScreen(
    tableNumber: Int,
    tables: List<Int> = listOf(tableNumber),
    onBackClick: () -> Unit,
) {
    val questions = remember { generateFillBlankQuestions(tables) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // Background timer — cancelled when result shows, resets on retry
    LaunchedEffect(showResult) {
        if (!showResult) {
            elapsedSeconds = 0
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                ScreenTopBar(title = "Fill the Blanks", onBack = onBackClick) {
                    if (!showResult) {
                        AbProgressBar(
                            filled = currentIndex + 1,
                            total = maxOf(1, questions.size),
                            modifier = Modifier.widthIn(max = abResponsive(220f).dp).fillMaxWidth(),
                        )
                        // Score badge
                        TableScoreBadge(score)
                        // Next / Results button
                        AbButton(
                            text = if (currentIndex < questions.size - 1) "Next" else "Results",
                            tone = AbTone.Primary,
                            size = AbSize.Compact,
                            enabled = selectedAnswer != null,
                            icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            iconAtEnd = true,
                            onClick = {
                                if (selectedAnswer != null) {
                                    if (currentIndex < questions.size - 1) {
                                        currentIndex++
                                        selectedAnswer = null
                                    } else {
                                        showResult = true
                                    }
                                }
                            },
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (showResult) {
                        FillBlankResultContent(
                            questions = questions,
                            userAnswers = userAnswers,
                            score = score,
                            elapsedSeconds = elapsedSeconds,
                            onRetry = {
                                userAnswers.clear()
                                score = 0; currentIndex = 0; selectedAnswer = null; showResult = false
                            },
                            onBack = onBackClick
                        )
                    } else if (questions.isNotEmpty()) {
                        FitOnPhone(Modifier.fillMaxSize()) {
                            FillBlankContent(
                                question = questions[currentIndex],
                                currentIndex = currentIndex,
                                totalQuestions = questions.size,
                                selectedAnswer = selectedAnswer,
                                onChoiceSelected = { choice ->
                                    if (selectedAnswer == null) {
                                        selectedAnswer = choice
                                        userAnswers[currentIndex] = choice
                                        if (choice == questions[currentIndex].correctAnswer) score++
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Question card + options ──────────────────────────────────────────────────

@Composable
private fun FillBlankContent(
    question: FillBlankQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswer: Int?,
    onChoiceSelected: (Int) -> Unit,
) {
    val isCorrect = selectedAnswer == question.correctAnswer
    val blankBg by animateColorAsState(
        targetValue = when {
            selectedAnswer == null -> Color.White.copy(alpha = 0.18f)
            isCorrect -> Color(0xFF4CAF50)
            else -> Color(0xFFF44336)
        },
        animationSpec = tween(300),
        label = "blankBg"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.width(tableCardWidth())) {
            AbHeroPanel(Color(0xFF8E24AA), Modifier.matchParentSize(), padding = 0.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(Dimens12)
            ) {
                AbCountPill(
                    text = "Question ${currentIndex + 1} of $totalQuestions",
                    icon = Icons.Rounded.Help,
                )
                Text(
                    text = "✏️",
                    style = abEmojiStyle(abResponsive(28f)),
                    modifier = Modifier.padding(top = Dimens8)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens4),
                    modifier = Modifier.padding(vertical = Dimens8)
                ) {
                    when (question.blankType) {
                        FillBlankType.ANSWER -> {
                            EquationText("${question.multiplier} × ${question.multiplicand} = ")
                            BlankBox(selectedAnswer, blankBg)
                        }
                        FillBlankType.MULTIPLICAND -> {
                            EquationText("${question.multiplier} × ")
                            BlankBox(selectedAnswer, blankBg)
                            EquationText(" = ${question.multiplier * question.multiplicand}")
                        }
                    }
                }
                Text(
                    text = "✏️  Fill in the blank!",
                    style = abFixedStyle(abTabletSize(10f, 15f, 18f), FontWeight.ExtraBold),
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFC74500), CircleShape)
                        .padding(horizontal = Dimens12, vertical = Dimens6)
                )
            }
        }

        Spacer(Modifier.height(Dimens12))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                question.choices.take(2).forEach { choice -> FillOption(choice, question.correctAnswer, selectedAnswer, onChoiceSelected) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                question.choices.drop(2).take(2).forEach { choice -> FillOption(choice, question.correctAnswer, selectedAnswer, onChoiceSelected) }
            }
        }
    }
}

@Composable
private fun FillOption(choice: Int, answer: Int, selectedAnswer: Int?, onChoiceSelected: (Int) -> Unit) {
    AbOptionButton(
        text = "$choice",
        state = optionState(choice, answer, selectedAnswer),
        width = examOptionWidth,
        height = examOptionHeight,
        onClick = { onChoiceSelected(choice) },
    )
}

@Composable
private fun EquationText(text: String) {
    Text(
        text = text,
        style = abFont(36f),
        color = Color.White.copy(alpha = 0.90f)
    )
}

@Composable
private fun BlankBox(selectedAnswer: Int?, blankBg: Color) {
    val shape = RoundedCornerShape(Dimens12)
    Text(
        text = if (selectedAnswer != null) "$selectedAnswer" else "?",
        style = abFont(28f, FontWeight.Black),
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(blankBg, shape)
            .then(
                if (selectedAnswer == null)
                    Modifier.border(2.dp, Color.White.copy(alpha = 0.55f), shape)
                else Modifier
            )
            .padding(horizontal = Dimens16, vertical = Dimens8)
    )
}

// ─── Result view ──────────────────────────────────────────────────────────────

@Composable
private fun FillBlankResultContent(
    questions: List<FillBlankQuestion>,
    userAnswers: Map<Int, Int>,
    score: Int,
    elapsedSeconds: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalAbColors.current
    val pct = if (questions.isNotEmpty()) score.toDouble() / questions.size else 0.0
    val starValue = pct * 3.0
    val starSize = ToolbarIconSize * 1.4f
    val message = when {
        pct >= 0.9 -> "Perfect! You're a table master! 🏆"
        pct >= 0.6 -> "Great job! Almost there! 🎉"
        pct >= 0.3 -> "Good effort! Keep practicing! 💪"
        else -> "Don't give up! Try again! 🎯"
    }
    val divider = colors.outline

    Row(modifier = Modifier.fillMaxSize()) {

        // ── Left 35%: question list (card, vertically centered) ──────────────
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.35f),
            contentAlignment = Alignment.Center
        ) {
            AbGlassPanel(
                modifier = Modifier
                    .padding(horizontal = Dimens12, vertical = Dimens8)
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.85f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your Answers",
                        style = abFont(14f, FontWeight.ExtraBold),
                        color = colors.systemPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens8)
                    )
                    HorizontalDivider(color = divider)
                    Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                        questions.forEachIndexed { idx, q ->
                            val chosen = userAnswers[idx]
                            val correct = chosen == q.correctAnswer
                            val chosenColor = if (chosen == null) Color.Gray else if (correct) Color(0xFF4CAF50) else Color(0xFFF44336)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (idx % 2 == 0) Color(0xFF7B1FA2).copy(alpha = 0.05f)
                                        else Color.Transparent
                                    )
                                    .padding(vertical = Dimens6, horizontal = Dimens8),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Dimens4)
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)
                                ) {
                                    val given = abFont(12f)
                                    val picked = abFont(12f, FontWeight.ExtraBold)
                                    when (q.blankType) {
                                        FillBlankType.ANSWER -> {
                                            Text("${q.multiplier}×${q.multiplicand}=", style = given, color = colors.systemSecondary)
                                            Text(if (chosen != null) "$chosen" else "–", style = picked, color = chosenColor)
                                        }
                                        FillBlankType.MULTIPLICAND -> {
                                            Text("${q.multiplier}×", style = given, color = colors.systemSecondary)
                                            Text(if (chosen != null) "$chosen" else "–", style = picked, color = chosenColor)
                                            Text("=${q.multiplier * q.multiplicand}", style = given, color = colors.systemSecondary)
                                        }
                                    }
                                }
                                Icon(
                                    imageVector = if (correct) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = if (correct) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    modifier = Modifier.size((14f * AB_SF_TO_VECTOR).dp)
                                )
                            }
                            if (idx < questions.size - 1) {
                                HorizontalDivider(color = divider)
                            }
                        }
                    }
                }
            }
        }

        // Vertical divider
        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(divider))

        // ── Right 65%: summary ───────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxHeight().weight(0.65f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens16), verticalAlignment = Alignment.CenterVertically) {
                (0..2).forEach { idx ->
                    val fill = (starValue - idx).coerceIn(0.0, 1.0).toFloat()
                    ProgressiveStar(fill = fill, starSize = starSize, delayMs = idx * 120L)
                }
            }

            Spacer(Modifier.height(Dimens16))

            // Time badge
            Row(
                modifier = Modifier
                    .background(Color(0xFF7B1FA2).copy(alpha = 0.10f), RoundedCornerShape(Dimens8))
                    .padding(horizontal = Dimens16, vertical = Dimens6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens6)
            ) {
                Text("⏱️", style = abFont(16f))
                Text(
                    text = formatTime(elapsedSeconds),
                    style = abFont(16f, FontWeight.Bold),
                    color = colors.systemPrimary
                )
            }

            Spacer(Modifier.height(Dimens12))

            // Score circle
            val circleSize = ToolbarIconSize * 2.0f
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFF6A1B9A), Color(0xFF7B1FA2))),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Dimens4)) {
                    Text(
                        text = "$score",
                        style = abFixedStyle(circleSize.value * 0.38f, FontWeight.Black),
                        color = Color.White
                    )
                    Text(
                        text = "out of ${questions.size}",
                        style = abFont(10f),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(Dimens16))

            Text(
                text = message,
                style = abFont(16f, FontWeight.Bold),
                color = colors.systemPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens24)
            )

            Spacer(Modifier.height(Dimens20))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens16), verticalAlignment = Alignment.CenterVertically) {
                AbButton(text = "Back", tone = AbTone.Ghost, size = AbSize.Medium, icon = Icons.AutoMirrored.Rounded.ArrowBack, onClick = onBack)
                AbButton(text = "Play Again!", tone = AbTone.Primary, size = AbSize.Medium, icon = Icons.Rounded.Replay, onClick = onRetry)
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

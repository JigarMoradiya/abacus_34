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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.common_ui.buttons.KidsOptionButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens28
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens50
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionWidth
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlinx.coroutines.delay

@Composable
fun TableFillBlankScreen(
    tableNumber: Int,
    onBackClick: () -> Unit,
) {
    val questions = remember { generateFillBlankQuestions(tableNumber) }
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

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButtonWithText(title = "Fill the Blanks", onBackClick = onBackClick)
                if (!showResult) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ToolbarIconSize)
                            .padding(horizontal = Dimens50),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens8)
                                .clip(RoundedCornerShape(100.dp)),
                            color = Color(0xFF7B1FA2),
                            trackColor = Color.Black.copy(alpha = 0.08f)
                        )
                    }
                    // Score badge
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF4CAF50).copy(alpha = 0.14f), RoundedCornerShape(Dimens8))
                            .padding(horizontal = Dimens12, vertical = Dimens6),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens4)
                    ) {
                        Text("✓", style = MaterialTheme.typography.bodyLarge.scaled(), fontWeight = FontWeight.Black, color = Color(0xFF4CAF50))
                        Text("$score", style = MaterialTheme.typography.titleMedium.scaled(), fontWeight = FontWeight.Black, color = Color(0xFF4CAF50))
                    }
                    // Next / Results button
                    KidsActionButton(
                        text = if (currentIndex < questions.size - 1) "Next" else "Results",
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        type = if (selectedAnswer == null) ButtonType.DISABLE else ButtonType.BLUE,
                        isIconStart = false,
                        isSmall = true,
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
                        modifier = Modifier.padding(start = Dimens8, end = Dimens16)
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
                    FillBlankContent(
                        question = questions[currentIndex],
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        selectedAnswer = selectedAnswer,
                        onChoiceSelected = { choice ->
                            if (selectedAnswer == null) {
                                AudioPlayerManager.playSoundBtnClick()
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

// ─── Question card + options ──────────────────────────────────────────────────

@Composable
private fun FillBlankContent(
    question: FillBlankQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswer: Int?,
    onChoiceSelected: (Int) -> Unit,
) {
    val isTablet = DeviceInfo.isTablet
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Card(
            shape = RoundedCornerShape(Dimens28),
            elevation = CardDefaults.cardElevation(Dimens8),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth(0.80f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF6A1B9A), Color(0xFF7B1FA2), Color(0xFF8E24AA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.padding(Dimens16)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(100.dp))
                            .padding(horizontal = Dimens16, vertical = Dimens4)
                    ) {
                        Text(
                            text = "Q ${currentIndex + 1} of $totalQuestions",
                            style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "✏️",
                        style = MaterialTheme.typography.headlineMedium.scaled(),
                        modifier = Modifier.padding(top = Dimens8)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = Dimens12)
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
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF57F17), RoundedCornerShape(100.dp))
                            .padding(horizontal = Dimens12, vertical = Dimens6)
                    ) {
                        Text(
                            text = "✏️  Fill in the blank!",
                            style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens20))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens10),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                KidsOptionButton(text = "${question.choices[0]}", type = optionType(question.choices[0], question.correctAnswer, selectedAnswer), fontSize = examOptionHeight.value.sp * 0.6f, onClick = { onChoiceSelected(question.choices[0]) }, modifier = Modifier.width(examOptionWidth).height(examOptionHeight))
                KidsOptionButton(text = "${question.choices[1]}", type = optionType(question.choices[1], question.correctAnswer, selectedAnswer), fontSize = examOptionHeight.value.sp * 0.6f, onClick = { onChoiceSelected(question.choices[1]) }, modifier = Modifier.width(examOptionWidth).height(examOptionHeight))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                KidsOptionButton(text = "${question.choices[2]}", type = optionType(question.choices[2], question.correctAnswer, selectedAnswer), fontSize = examOptionHeight.value.sp * 0.6f, onClick = { onChoiceSelected(question.choices[2]) }, modifier = Modifier.width(examOptionWidth).height(examOptionHeight))
                KidsOptionButton(text = "${question.choices[3]}", type = optionType(question.choices[3], question.correctAnswer, selectedAnswer), fontSize = examOptionHeight.value.sp * 0.6f, onClick = { onChoiceSelected(question.choices[3]) }, modifier = Modifier.width(examOptionWidth).height(examOptionHeight))
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EquationText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.scaled(),
        fontWeight = FontWeight.SemiBold,
        color = Color.White.copy(alpha = 0.90f)
    )
}

@Composable
private fun BlankBox(selectedAnswer: Int?, blankBg: Color) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .background(blankBg, RoundedCornerShape(Dimens12))
            .then(
                if (selectedAnswer == null)
                    Modifier.border(2.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(Dimens12))
                else Modifier
            )
            .padding(horizontal = Dimens16, vertical = Dimens8),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (selectedAnswer != null) "$selectedAnswer" else "?",
            style = MaterialTheme.typography.headlineMedium.scaled(),
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
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
    val isTablet = DeviceInfo.isTablet
    val pct = if (questions.isNotEmpty()) score.toDouble() / questions.size else 0.0
    val starValue = pct * 3.0
    val starSize = ToolbarIconSize * 1.4f
    val message = when {
        pct >= 0.9 -> "Perfect! You're a table master! 🏆"
        pct >= 0.6 -> "Great job! Almost there! 🎉"
        pct >= 0.3 -> "Good effort! Keep practicing! 💪"
        else -> "Don't give up! Try again! 🎯"
    }

    Row(modifier = Modifier.fillMaxSize()) {

        // ── Left 35%: question list (card, vertically centered) ──────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.35f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(Dimens16),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens8),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens12, vertical = Dimens8)
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Answers",
                        style = if (isTablet) MaterialTheme.typography.titleLarge.scaled() else MaterialTheme.typography.titleSmall.scaled(),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6A1B9A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens8)
                    )
                    questions.forEachIndexed { idx, q ->
                        val chosen = userAnswers[idx]
                        val correct = chosen == q.correctAnswer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (idx % 2 == 0) Color(0xFF7B1FA2).copy(alpha = 0.05f)
                                    else Color.Transparent
                                )
                                .padding(vertical = Dimens4, horizontal = Dimens8),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                when (q.blankType) {
                                    FillBlankType.ANSWER -> {
                                        Text("${q.multiplier}×${q.multiplicand}=", style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(), color = Color.Black.copy(alpha = 0.6f))
                                        Text(
                                            text = if (chosen != null) "$chosen" else "–",
                                            style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (chosen == null) Color.Gray else if (correct) Color(0xFF4CAF50) else Color(0xFFF44336)
                                        )
                                    }
                                    FillBlankType.MULTIPLICAND -> {
                                        Text("${q.multiplier}×", style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(), color = Color.Black.copy(alpha = 0.6f))
                                        Text(
                                            text = if (chosen != null) "$chosen" else "–",
                                            style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (chosen == null) Color.Gray else if (correct) Color(0xFF4CAF50) else Color(0xFFF44336)
                                        )
                                        Text("=${q.multiplier * q.multiplicand}", style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(), color = Color.Black.copy(alpha = 0.6f))
                                    }
                                }
                            }
                            Icon(
                                imageVector = if (correct) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (correct) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(Dimens16)
                            )
                        }
                        if (idx < questions.size - 1) {
                            HorizontalDivider(color = Color(0xFF7B1FA2).copy(alpha = 0.08f))
                        }
                    }
                }
            }
        }

        // Vertical divider
        Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.Gray.copy(alpha = 0.2f)))

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
                    .padding(horizontal = Dimens12, vertical = Dimens4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens6)
            ) {
                Text("⏱", style = MaterialTheme.typography.bodyLarge.scaled())
                Text(
                    text = formatTime(elapsedSeconds),
                    style = MaterialTheme.typography.bodyLarge.scaled(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B1FA2)
                )
            }

            Spacer(Modifier.height(Dimens12))

            // Score circle
            Box(
                modifier = Modifier
                    .size(ToolbarIconSize * 2.0f)
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFF6A1B9A), Color(0xFF7B1FA2))),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayLarge.scaled(),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "out of ${questions.size}",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(Dimens12))

            Text(
                text = message,
                style = if (isTablet) MaterialTheme.typography.titleMedium.scaled() else MaterialTheme.typography.bodyLarge.scaled(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens24)
            )

            Spacer(Modifier.height(Dimens12))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens16), verticalAlignment = Alignment.CenterVertically) {
                KidsActionButton(text = "Back", icon = Icons.AutoMirrored.Rounded.ArrowBack, type = ButtonType.NEGATIVE, isIconStart = true, isSmall = true, onClick = onBack)
                KidsActionButton(text = "Play Again!", icon = Icons.Default.Refresh, type = ButtonType.BLUE, isIconStart = true, isSmall = true, onClick = onRetry)
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}

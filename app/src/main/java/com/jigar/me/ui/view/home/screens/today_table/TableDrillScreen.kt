package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens50
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionWidth
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.PrimaryBlue

@Composable
fun TableDrillScreen(
    tableNumber: Int,
    onBackClick: () -> Unit,
) {
    val questions = remember { generateTableQuestions(tableNumber) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var showingTable by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            // Header — hidden when result is showing
            if (!showResult) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButtonWithText(title = "Learn & Drill", onBackClick = onBackClick)
                    if (!showingTable && questions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(ToolbarIconSize)
                                .padding(horizontal = Dimens50),
                            contentAlignment = Alignment.Center
                        ) {
                            LinearProgressIndicator(
                                progress = {
                                    (currentIndex + 1).toFloat() / questions.size.coerceAtLeast(1)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens8)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = PrimaryBlue,
                                trackColor = Color.Black.copy(alpha = 0.08f)
                            )
                        }
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
                            modifier = Modifier.padding(end = Dimens16)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Content fills remaining height
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    showResult -> {
                        TableResultContent(
                            score = score,
                            total = questions.size,
                            onRetry = {
                                score = 0; currentIndex = 0; selectedAnswer = null
                                showResult = false; showingTable = false
                            },
                            onBack = onBackClick
                        )
                    }
                    showingTable -> {
                        TablePhase(tableNumber = tableNumber, onStartDrill = { showingTable = false })
                    }
                    else -> {
                        if (questions.isNotEmpty()) {
                            DrillPhase(
                                question = questions[currentIndex],
                                currentIndex = currentIndex,
                                totalCount = questions.size,
                                selectedAnswer = selectedAnswer,
                                onAnswerSelected = { choice ->
                                    if (selectedAnswer == null) {
                                        selectedAnswer = choice
                                        if (choice == questions[currentIndex].answer) score++
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

// 0.35 / 0.65 split — same as TodayTableHomeScreen
@Composable
private fun TablePhase(tableNumber: Int, onStartDrill: () -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left panel 35% — scrollable table with overflow banner
        Box(modifier = Modifier.fillMaxHeight().weight(0.35f)) {
            TableDisplayCard(tableNumber = tableNumber)
        }
        // Vertical divider
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Gray.copy(alpha = 0.2f))
        )
        // Right panel 65% — vertically centered start area
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.65f)
                .padding(horizontal = Dimens16),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🎯", style = MaterialTheme.typography.displayLarge.scaled())
            Spacer(Modifier.height(Dimens12))
            Text(
                text = "Ready to drill?",
                style = MaterialTheme.typography.titleLarge.scaled(),
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBlue
            )
            Spacer(Modifier.height(Dimens8))
            Text(
                text = "10 random questions from the ×$tableNumber table",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                color = PrimaryBlue.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Dimens16))
            KidsActionButton(
                text = "Start Drill",
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                type = ButtonType.BLUE,
                isIconStart = false,
                isSmall = true,
                onClick = onStartDrill
            )
        }
    }
}

// drillPhase — question + options vertically centered, no scroll
@Composable
private fun DrillPhase(
    question: TableQuestionData,
    currentIndex: Int,
    totalCount: Int,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Question card
        Box(
            modifier = Modifier
                .fillMaxWidth(0.80f)
                .background(
                    brush = Brush.linearGradient(
                        listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.82f))
                    ),
                    shape = RoundedCornerShape(Dimens20)
                )
                .padding(Dimens16),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Q${currentIndex + 1} of $totalCount",
                    style = MaterialTheme.typography.bodyMedium.scaled(),
                    color = Color.White.copy(alpha = 0.65f)
                )
                Spacer(Modifier.height(Dimens8))
                Text(
                    text = "${question.multiplier} × ${question.multiplicand} = ?",
                    style = MaterialTheme.typography.displayLarge.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens24))

        // Options grid — 2×2 matching ExamQuestionSection: fixed examOptionWidth per button
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens10),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (question.choices.size >= 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                    KidsOptionButton(
                        text = "${question.choices[0]}",
                        type = optionType(question.choices[0], question.answer, selectedAnswer),
                        fontSize = examOptionHeight.value.sp * 0.6f,
                        onClick = { onAnswerSelected(question.choices[0]) },
                        modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                    )
                    KidsOptionButton(
                        text = "${question.choices[1]}",
                        type = optionType(question.choices[1], question.answer, selectedAnswer),
                        fontSize = examOptionHeight.value.sp * 0.6f,
                        onClick = { onAnswerSelected(question.choices[1]) },
                        modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                    )
                }
            }
            if (question.choices.size >= 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                    KidsOptionButton(
                        text = "${question.choices[2]}",
                        type = optionType(question.choices[2], question.answer, selectedAnswer),
                        fontSize = examOptionHeight.value.sp * 0.6f,
                        onClick = { onAnswerSelected(question.choices[2]) },
                        modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                    )
                    KidsOptionButton(
                        text = "${question.choices[3]}",
                        type = optionType(question.choices[3], question.answer, selectedAnswer),
                        fontSize = examOptionHeight.value.sp * 0.6f,
                        onClick = { onAnswerSelected(question.choices[3]) },
                        modifier = Modifier.width(examOptionWidth).height(examOptionHeight)
                    )
                }
            }
        }
    }
}


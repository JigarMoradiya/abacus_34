package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbOptionButton
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.common_ui.LocalPreferencesHelper
import com.jigar.me.ui.view.home.common_ui.sheets.ReviewGateHost
import com.jigar.me.ui.view.home.common_ui.sheets.rememberReviewGateController
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens10
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionHeight
import com.jigar.me.ui.view.home.theme.AppDimens.examOptionWidth

@Composable
fun TableDrillScreen(
    tableNumber: Int,
    tables: List<Int> = listOf(tableNumber),
    onBackClick: () -> Unit,
) {
    val questions = remember { generateTableQuestions(tables) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val prefs = LocalPreferencesHelper.current
    val reviewGate = rememberReviewGateController()

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {
                // Header — hidden when result is showing
                if (!showResult) {
                    ScreenTopBar(title = "Learn & Drill", onBack = onBackClick) {
                        if (questions.isNotEmpty()) {
                            AbProgressBar(
                                filled = currentIndex + 1,
                                total = questions.size,
                                modifier = Modifier.widthIn(max = abResponsive(220f).dp).fillMaxWidth(),
                            )
                            AbButton(
                                text = if (currentIndex < questions.size - 1) "Next" else "Results",
                                tone = AbTone.Primary,
                                size = AbSize.Small,
                                enabled = selectedAnswer != null,
                                icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                iconAtEnd = true,
                                onClick = {
                                    if (currentIndex < questions.size - 1) {
                                        currentIndex++
                                        selectedAnswer = null
                                    } else {
                                        showResult = true
                                    }
                                },
                            )
                        }
                    }
                }

                // Content fills remaining height
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (showResult) {
                        TableResultContent(
                            score = score,
                            total = questions.size,
                            onRetry = {
                                score = 0; currentIndex = 0; selectedAnswer = null
                                showResult = false
                            },
                            onBack = {
                                val pct = if (questions.isNotEmpty()) score.toDouble() / questions.size else 0.0
                                if (pct >= 0.9) reviewGate.attempt(prefs) { onBackClick() } else onBackClick()
                            }
                        )
                    } else if (questions.isNotEmpty()) {
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

            ReviewGateHost(reviewGate, prefs)
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
        // Question card — its height wraps the content
        Box(Modifier.width(tableCardWidth())) {
            AbHeroPanel(Color(0xFF3F5CE0), Modifier.matchParentSize(), padding = 0.dp)
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens8)
            ) {
                AbCountPill(
                    text = "Question ${currentIndex + 1} of $totalCount",
                    icon = Icons.Rounded.Help,
                )
                Text(
                    text = "${question.multiplier} × ${question.multiplicand} = ?",
                    style = abFont(48f, FontWeight.Black),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens24))

        // Options grid — 2×2: fixed examOptionWidth per button
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (question.choices.size >= 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                    question.choices.take(2).forEach { choice ->
                        DrillOption(choice, question.answer, selectedAnswer, onAnswerSelected)
                    }
                }
            }
            if (question.choices.size >= 4) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens10)) {
                    question.choices.drop(2).take(2).forEach { choice ->
                        DrillOption(choice, question.answer, selectedAnswer, onAnswerSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun DrillOption(choice: Int, answer: Int, selectedAnswer: Int?, onAnswerSelected: (Int) -> Unit) {
    AbOptionButton(
        text = "$choice",
        state = optionState(choice, answer, selectedAnswer),
        width = examOptionWidth,
        height = examOptionHeight,
        onClick = { onAnswerSelected(choice) },
    )
}

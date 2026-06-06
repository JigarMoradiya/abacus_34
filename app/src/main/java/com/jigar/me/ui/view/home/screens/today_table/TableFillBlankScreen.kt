package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.PrimaryBlue

@Composable
fun TableFillBlankScreen(
    tableNumber: Int,
    onBackClick: () -> Unit,
) {
    val choicesMap = remember {
        (1..10).associate { m ->
            val correct = tableNumber * m
            val wrong = mutableSetOf<Int>()
            while (wrong.size < 3) {
                val c = tableNumber * (1..10).random()
                if (c != correct) wrong.add(c)
            }
            m to (listOf(correct) + wrong.toList()).shuffled()
        }
    }

    val answers = remember { mutableStateMapOf<Int, Int>() }
    var activeBlank by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            // Header hidden when result is showing
            if (!showResult) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = "Fill the Blanks", onBackClick = onBackClick)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (showResult) {
                    val score = answers.count { (k, v) -> v == tableNumber * k }
                    TableResultContent(
                        score = score,
                        total = 10,
                        onRetry = { answers.clear(); activeBlank = null; showResult = false },
                        onBack = onBackClick
                    )
                } else {
                    FillContent(
                        tableNumber = tableNumber,
                        choicesMap = choicesMap,
                        answers = answers,
                        activeBlank = activeBlank,
                        onToggleBlank = { m ->
                            AudioPlayerManager.playSoundBtnClick()
                            activeBlank = if (activeBlank == m) null else m
                        },
                        onChoiceSelected = { m, choice ->
                            AudioPlayerManager.playSoundBtnClick()
                            answers[m] = choice
                            activeBlank = null
                            if (answers.size == 10) showResult = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FillContent(
    tableNumber: Int,
    choicesMap: Map<Int, List<Int>>,
    answers: Map<Int, Int>,
    activeBlank: Int?,
    onToggleBlank: (Int) -> Unit,
    onChoiceSelected: (Int, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens16)
    ) {
        LinearProgressIndicator(
            progress = { answers.size.toFloat() / 10 },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens12),
            color = PrimaryBlue,
            trackColor = PrimaryBlue.copy(alpha = 0.15f)
        )

        Card(
            shape = RoundedCornerShape(Dimens16),
            elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(AppDimens.Dimens16)) {
                (1..10).forEach { m ->
                    val correct = tableNumber * m
                    val chosen = answers[m]
                    val isAnswered = chosen != null
                    val isCorrect = chosen == correct
                    val isActive = activeBlank == m

                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (m % 2 == 0) PrimaryBlue.copy(alpha = 0.03f)
                                    else Color.White
                                )
                                .padding(vertical = Dimens8),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$tableNumber × $m = ",
                                style = MaterialTheme.typography.bodyLarge.scaled(),
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswered) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$chosen",
                                        style = MaterialTheme.typography.bodyLarge.scaled(),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                                    )
                                    Spacer(modifier = Modifier.width(Dimens6))
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                        modifier = Modifier.size(Dimens20)
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onToggleBlank(m) },
                                    shape = RoundedCornerShape(Dimens8),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isActive) PrimaryBlue else PrimaryBlue.copy(alpha = 0.4f)
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = Dimens16,
                                        vertical = Dimens4
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isActive) PrimaryBlue else Color.Transparent,
                                        contentColor = if (isActive) Color.White else PrimaryBlue
                                    )
                                ) {
                                    Text(
                                        text = "?",
                                        style = MaterialTheme.typography.titleMedium.scaled(),
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isActive && !isAnswered,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = Dimens8),
                                horizontalArrangement = Arrangement.spacedBy(Dimens8)
                            ) {
                                choicesMap[m]?.forEach { choice ->
                                    Button(
                                        onClick = { onChoiceSelected(m, choice) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(Dimens8),
                                        contentPadding = PaddingValues(Dimens8),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryBlue.copy(alpha = 0.1f),
                                            contentColor = PrimaryBlue
                                        )
                                    ) {
                                        Text(
                                            text = "$choice",
                                            style = MaterialTheme.typography.titleSmall.scaled(),
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }

                        if (m < 10) {
                            HorizontalDivider(
                                color = PrimaryBlue.copy(alpha = 0.08f),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppDimens.Dimens24))
    }
}

package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens28
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.PrimaryBlue

data class TableQuestionData(
    val multiplier: Int,
    val multiplicand: Int,
    val choices: List<Int>
) {
    val answer: Int get() = multiplier * multiplicand
}

fun generateTableQuestions(tableNumber: Int, count: Int = 10): List<TableQuestionData> {
    val multiplicands = (1..10).toMutableList().shuffled().take(count)
    return multiplicands.map { m ->
        val correct = tableNumber * m
        val wrong = mutableSetOf<Int>()
        while (wrong.size < 3) {
            val candidate = tableNumber * (1..10).random()
            if (candidate != correct) wrong.add(candidate)
        }
        TableQuestionData(
            multiplier = tableNumber,
            multiplicand = m,
            choices = (listOf(correct) + wrong.toList()).shuffled()
        )
    }
}

@Composable
fun TableDisplayCard(tableNumber: Int) {
    Box(
        modifier = Modifier
            .padding(
                top = AppDimens.Dimens12,
                start = AppDimens.Dimens12,
                end = AppDimens.Dimens12,
                bottom = AppDimens.Dimens12
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // White card — internal spacer at top reserves room for the banner
        Card(
            shape = RoundedCornerShape(Dimens16),
            elevation = CardDefaults.cardElevation(AppDimens.Dimens4),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Spacer(Modifier.height(ToolbarIconSize * 0.55f))
                (1..10).forEach { i ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (i % 2 != 0) PrimaryBlue.copy(alpha = 0.06f)
                                else Color.Transparent
                            )
                            .padding(vertical = Dimens8, horizontal = Dimens16),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$tableNumber × $i",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black.copy(alpha = 0.75f)
                        )
                        Text(
                            text = " = ",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            color = Color.Gray
                        )
                        Text(
                            text = "${tableNumber * i}",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
        // Banner: offset upward so it visually pokes above the card's top edge
        Box(
            modifier = Modifier
                .offset(y = -(ToolbarIconSize * 0.22f))
                .shadow(Dimens4, RoundedCornerShape(Dimens8))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.85f))
                    ),
                    shape = RoundedCornerShape(Dimens8)
                )
                .padding(horizontal = Dimens20, vertical = Dimens4)
        ) {
            Text(
                text = "×$tableNumber",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun TableResultContent(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val pct = if (total > 0) score.toDouble() / total.toDouble() else 0.0
    val starValue = pct * 3.0

    val message = when {
        pct >= 0.9 -> "Perfect! You're a table master! 🏆"
        pct >= 0.6 -> "Great job! Almost there! 🎉"
        pct >= 0.3 -> "Good effort! Keep practicing! 💪"
        else -> "Don't give up! Try again! 🎯"
    }

    val starSize = ToolbarIconSize * 1.4f

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Progressive stars — each animates from 0 with staggered delay (mirrors iOS spring delay)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens16),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (0..2).forEach { idx ->
                val fill = (starValue - idx).coerceIn(0.0, 1.0).toFloat()
                ProgressiveStar(fill = fill, starSize = starSize, delayMs = idx * 120L)
            }
        }

        Spacer(modifier = Modifier.height(Dimens28))

        // Score circle with gradient
        Box(
            modifier = Modifier
                .size(ToolbarIconSize * 2.0f)
                .background(
                    brush = Brush.linearGradient(
                        listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.8f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayMedium.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "out of $total",
                    style = MaterialTheme.typography.bodySmall.scaled(),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens20))

        Text(
            text = message,
            style = MaterialTheme.typography.titleSmall.scaled(),
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppDimens.Dimens24)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons — KidsActionButton centered horizontally (wrapContentWidth)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens16),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = Dimens28)
        ) {
            KidsActionButton(
                text = "Back",
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                type = ButtonType.NEGATIVE,
                isIconStart = true,
                isSmall = true,
                onClick = onBack
            )
            KidsActionButton(
                text = "Try Again",
                icon = Icons.Default.Refresh,
                type = ButtonType.BLUE,
                isIconStart = true,
                isSmall = true,
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun ProgressiveStar(fill: Float, starSize: Dp, delayMs: Long = 0L) {
    var animTarget by remember { mutableFloatStateOf(0f) }

    val animatedFill by animateFloatAsState(
        targetValue = animTarget,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "starFill"
    )
    val scale by animateFloatAsState(
        targetValue = when {
            animTarget >= 1.0f -> 1.18f
            animTarget > 0f -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMedium),
        label = "starScale"
    )

    LaunchedEffect(fill) {
        if (delayMs > 0L) delay(delayMs)
        animTarget = fill
    }

    Box(
        modifier = Modifier
            .size(starSize)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Gray empty star — always visible as background
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.28f),
            modifier = Modifier.size(starSize)
        )
        // Amber filled star — draw-level clip reveals exactly the left fill fraction
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier
                .size(starSize)
                .drawWithContent {
                    if (animatedFill > 0f) {
                        clipRect(right = size.width * animatedFill) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
        )
    }
}

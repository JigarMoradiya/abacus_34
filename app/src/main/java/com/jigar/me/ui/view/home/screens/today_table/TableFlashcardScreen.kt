package com.jigar.me.ui.view.home.screens.today_table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.ToolbarIconSize
import com.jigar.me.ui.view.home.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun TableFlashcardScreen(
    tableNumber: Int,
    onBackClick: () -> Unit,
) {
    val cards = remember {
        mutableStateListOf<Pair<Int, Int>>().also { list ->
            list.addAll((1..10).map { Pair(tableNumber, it) }.shuffled())
        }
    }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }
    val rotation = rotationAnim.value
    val scope = rememberCoroutineScope()

    fun flipCard() {
        if (!isFlipped) {
            scope.launch {
                isFlipped = true
                rotationAnim.animateTo(180f, animationSpec = tween(400))
            }
        }
    }

    fun advance(gotIt: Boolean) {
        if (gotIt) {
            score++
        } else {
            // Re-append card so user retries it later (mirrors iOS behavior)
            cards.add(cards[currentIndex])
        }
        if (currentIndex < cards.size - 1) {
            scope.launch {
                rotationAnim.snapTo(0f)
                isFlipped = false
                currentIndex++
            }
        } else {
            showResult = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomePageBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            // Header hidden when result is showing
            if (!showResult) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BackButtonWithText(title = "Flashcard Quiz", onBackClick = onBackClick)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (showResult) {
                    TableResultContent(
                        score = score,
                        total = cards.size,
                        onRetry = {
                            cards.clear()
                            cards.addAll((1..10).map { Pair(tableNumber, it) }.shuffled())
                            score = 0; currentIndex = 0; isFlipped = false; showResult = false
                            scope.launch { rotationAnim.snapTo(0f) }
                        },
                        onBack = onBackClick
                    )
                } else if (cards.isNotEmpty()) {
                    FlashcardContent(
                        card = cards[currentIndex],
                        currentIndex = currentIndex,
                        totalCards = cards.size,
                        score = score,
                        rotation = rotation,
                        isFlipped = isFlipped,
                        onFlip = {
                            AudioPlayerManager.playSoundBtnClick()
                            flipCard()
                        },
                        onGotIt = {
                            AudioPlayerManager.playSoundBtnClick()
                            advance(true)
                        },
                        onTryAgain = {
                            AudioPlayerManager.playSoundBtnClick()
                            advance(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    card: Pair<Int, Int>,
    currentIndex: Int,
    totalCards: Int,
    score: Int,
    rotation: Float,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onGotIt: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Card ${currentIndex + 1} / $totalCards",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "✓ $score",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }

        Spacer(Modifier.height(Dimens8))

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCards },
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryBlue,
            trackColor = PrimaryBlue.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Flip card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ToolbarIconSize * 2.4f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8 * density
                }
                .clickable { onFlip() },
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front — question
                Card(
                    shape = RoundedCornerShape(Dimens20),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    elevation = CardDefaults.cardElevation(AppDimens.Dimens8),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🤔",
                            style = MaterialTheme.typography.displaySmall.scaled()
                        )
                        Spacer(Modifier.height(Dimens12))
                        Text(
                            text = "${card.first} × ${card.second} = ?",
                            style = MaterialTheme.typography.displaySmall.scaled(),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(Dimens8))
                        Text(
                            text = "Think first, then tap to see the answer",
                            style = MaterialTheme.typography.bodySmall.scaled(),
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Back — answer (counter-rotated to cancel parent flip)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                ) {
                    Card(
                        shape = RoundedCornerShape(Dimens20),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(AppDimens.Dimens8),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00695C), Color(0xFF00897B))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${card.first} × ${card.second} =",
                                    style = MaterialTheme.typography.bodyLarge.scaled(),
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "${card.first * card.second}",
                                    style = MaterialTheme.typography.displayLarge.scaled(),
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Did you know it?",
                                    style = MaterialTheme.typography.bodySmall.scaled(),
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isFlipped) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens12)
            ) {
                // Try Again
                OutlinedButton(
                    onClick = onTryAgain,
                    modifier = Modifier.weight(1f).height(AppDimens.Dimens64),
                    shape = RoundedCornerShape(Dimens16),
                    border = androidx.compose.foundation.BorderStroke(
                        AppDimens.Dimens2,
                        Color(0xFFF44336)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✗", fontWeight = FontWeight.Bold, color = Color(0xFFF44336),
                            style = MaterialTheme.typography.titleMedium.scaled())
                        Text(text = "Try Again", color = Color(0xFFF44336), fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall.scaled())
                    }
                }
                // Got it
                Button(
                    onClick = onGotIt,
                    modifier = Modifier.weight(1f).height(AppDimens.Dimens64),
                    shape = RoundedCornerShape(Dimens16),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✓", fontWeight = FontWeight.Bold, color = Color.White,
                            style = MaterialTheme.typography.titleMedium.scaled())
                        Text(text = "Got it!", color = Color.White, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall.scaled())
                    }
                }
            }
        } else {
            Text(
                text = "Tap the card to reveal the answer",
                style = MaterialTheme.typography.bodyMedium.scaled(),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(Dimens16))
    }
}

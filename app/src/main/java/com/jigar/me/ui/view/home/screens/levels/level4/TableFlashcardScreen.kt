package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.HomePageBackground
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens20
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens4
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens50
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
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
        if (gotIt) score++
        else cards.add(cards[currentIndex])
        if (currentIndex < cards.size - 1) {
            scope.launch {
                // Complete the flip (180° → 360°) then snap to 0° for the next card
                rotationAnim.animateTo(360f, animationSpec = tween(400))
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
        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

            if (!showResult) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButtonWithText(title = "Flashcard Quiz", onBackClick = onBackClick)
                    // Progress bar — same layout as TableDrillScreen header
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(ToolbarIconSize)
                            .padding(horizontal = Dimens50),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / cards.size.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens8)
                                .clip(RoundedCornerShape(100.dp)),
                            color = Color(0xFF00695C),
                            trackColor = Color.Black.copy(alpha = 0.08f)
                        )
                    }
                    // Got It score badge
                    Row(
                        modifier = Modifier
                            .padding(end = Dimens16)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.14f), RoundedCornerShape(Dimens8))
                            .padding(horizontal = Dimens12, vertical = Dimens6),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens4)
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.bodyLarge.scaled(),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleMedium.scaled(),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
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
                        rotation = rotation,
                        isFlipped = isFlipped,
                        onFlip = { AudioPlayerManager.playSoundBtnClick(); flipCard() },
                        onGotIt = { AudioPlayerManager.playSoundBtnClick(); advance(true) },
                        onTryAgain = { AudioPlayerManager.playSoundBtnClick(); advance(false) }
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
    rotation: Float,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onGotIt: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Dimens12),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Flip card — both front and back always in layout, alpha switches at 90°
        // Mirrors iOS ZStack approach for smooth perspective flip
        Box(
            modifier = Modifier
                .fillMaxWidth(0.80f)
                .weight(1f)
                .padding(horizontal = Dimens16, vertical = Dimens12)
                .clickable { onFlip() },
            contentAlignment = Alignment.Center
        ) {
            // Front: rotates 0 → 180, fades out at 90°
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 8 * density
                        alpha = if (rotation < 90f || rotation > 270f) 1f else 0f
                    }
            ) {
                FrontCard(card = card, currentIndex = currentIndex, totalCards = totalCards)
            }
            // Back: starts at -180°, rotates to 0°, visible between 90° and 270°
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation - 180f
                        cameraDistance = 8 * density
                        alpha = if (rotation >= 90f && rotation <= 270f) 1f else 0f
                    }
            ) {
                BackCard(card = card)
            }
        }

        Spacer(modifier = Modifier.height(Dimens12))

        // Fixed-height bottom area — prevents card from shifting when content switches
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppDimens.Dimens48)
                .padding(horizontal = Dimens16),
            contentAlignment = Alignment.Center
        ) {
            if (isFlipped) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens12)
                ) {
                    OutlinedButton(
                        onClick = onTryAgain,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(Dimens16),
                        border = androidx.compose.foundation.BorderStroke(
                            AppDimens.Dimens2, Color(0xFFF44336)
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens8),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(Dimens20)
                            )
                            Text(
                                text = stringResource(R.string.try_again),
                                style = MaterialTheme.typography.bodyLarge.scaled(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                    Button(
                        onClick = onGotIt,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(Dimens16),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens8),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(Dimens20)
                            )
                            Text(
                                text = stringResource(R.string.got_it),
                                style = MaterialTheme.typography.bodyLarge.scaled(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(PrimaryBlue.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                        .padding(horizontal = Dimens20, vertical = Dimens8)
                ) {
                    Text(
                        text = "👆  Tap the card to reveal the answer",
                        style = MaterialTheme.typography.bodyMedium.scaled(),
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens16))
    }
}

@Composable
private fun FrontCard(card: Pair<Int, Int>, currentIndex: Int, totalCards: Int) {
    val isTablet = DeviceInfo.isTablet
    Card(
        shape = RoundedCornerShape(AppDimens.Dimens28),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens8),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1565C0), Color(0xFF0288D1), Color(0xFF0097A7))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.padding(Dimens16)
            ) {
                // Card counter pill
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(100.dp))
                        .padding(horizontal = Dimens16, vertical = Dimens4)
                ) {
                    Text(
                        text = "Card ${currentIndex + 1} of $totalCards",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "🤔",
                    style = MaterialTheme.typography.displaySmall.scaled(),
                    modifier = Modifier.padding(top = Dimens8)
                )
                Text(
                    text = "${card.first} × ${card.second} = ?",
                    style = MaterialTheme.typography.displayLarge.scaled(),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = Dimens8)
                )
                // "Think first" — orange pill with white text
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF57F17), RoundedCornerShape(100.dp))
                        .padding(horizontal = Dimens12, vertical = Dimens6)
                ) {
                    Text(
                        text = "💡  Think first, then tap to reveal!",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium.scaled() else MaterialTheme.typography.bodySmall.scaled(),
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BackCard(card: Pair<Int, Int>) {
    val isTablet = DeviceInfo.isTablet
    Card(
        shape = RoundedCornerShape(AppDimens.Dimens28),
        elevation = CardDefaults.cardElevation(Dimens8),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxSize()
    ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2E7D32), Color(0xFF388E3C), Color(0xFF43A047))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens8),
                    modifier = Modifier.padding(vertical = Dimens12)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens8)
                    ) {
                        Text(
                            text = "${card.first} × ${card.second} =",
                            style = MaterialTheme.typography.headlineLarge.scaled(),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.82f)
                        )
                        Text(
                            text = "${card.first * card.second}",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 60.sp.scaled()),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    // "Did you know it?" — yellow highlight pill
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF9C4), RoundedCornerShape(100.dp))
                            .padding(horizontal = Dimens12, vertical = Dimens6)
                    ) {
                        Text(
                            text = "⭐  Did you know it?",
                            style = if (isTablet) MaterialTheme.typography.bodyLarge.scaled() else MaterialTheme.typography.bodyMedium.scaled(),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF57F17),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
}

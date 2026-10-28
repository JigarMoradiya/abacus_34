package com.jigar.me.ui.view.home.screens.levels.level4

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbCountPill
import com.jigar.me.ui.view.ab.AbHeroPanel
import com.jigar.me.ui.view.ab.AbProgressBar
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.FitOnPhone
import com.jigar.me.ui.view.home.screens.levels.level3.components.ScreenTopBar
import com.jigar.me.ui.view.home.screens.levels.level3.components.abEmojiStyle
import com.jigar.me.ui.view.home.screens.levels.level3.components.abFont
import com.jigar.me.ui.view.home.screens.levels.level3.components.abResponsive
import com.jigar.me.ui.view.home.screens.levels.level3.components.abTabletSize
import com.jigar.me.ui.view.home.screens.levels.level3.components.screenLeftEdge
import com.jigar.me.ui.view.home.screens.levels.level3.components.systemPrimary
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens24
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens6
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import kotlinx.coroutines.launch

@Composable
fun TableFlashcardScreen(
    tableNumber: Int,
    tables: List<Int> = listOf(tableNumber),
    onBackClick: () -> Unit,
) {
    val cards = remember {
        mutableStateListOf<Pair<Int, Int>>().also { list ->
            val pool = tables.flatMap { t -> (1..10).map { Pair(t, it) } }
            list.addAll(pool.shuffled().take(10))
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

    AbTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Twilight)
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).screenLeftEdge()) {

                if (!showResult) {
                    ScreenTopBar(title = "Flashcard Quiz", onBack = onBackClick) {
                        AbProgressBar(
                            filled = currentIndex + 1,
                            total = maxOf(1, cards.size),
                            modifier = Modifier.widthIn(max = abResponsive(220f).dp).fillMaxWidth(),
                        )
                        // Got It score badge
                        TableScoreBadge(score)
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
                                val pool = tables.flatMap { t -> (1..10).map { Pair(t, it) } }
                                cards.addAll(pool.shuffled().take(10))
                                score = 0; currentIndex = 0; isFlipped = false; showResult = false
                                scope.launch { rotationAnim.snapTo(0f) }
                            },
                            onBack = onBackClick
                        )
                    } else if (cards.isNotEmpty()) {
                        FitOnPhone(Modifier.fillMaxSize()) { flexible ->
                            FlashcardContent(
                                card = cards[currentIndex],
                                currentIndex = currentIndex,
                                totalCards = cards.size,
                                rotation = rotation,
                                isFlipped = isFlipped,
                                flexible = flexible,
                                onFlip = { AudioPlayerManager.playSoundBtnClick(); flipCard() },
                                onGotIt = { advance(true) },
                                onTryAgain = { advance(false) }
                            )
                        }
                    }
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
    flexible: Boolean,
    onFlip: () -> Unit,
    onGotIt: () -> Unit,
    onTryAgain: () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        modifier = if (flexible) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens12)
    ) {
        // Flip card — both faces always in layout (at the size of the bigger one), alpha switches at 90°
        EqualStack(
            Modifier
                .then(if (flexible) Modifier.weight(1f) else Modifier)
                .padding(horizontal = Dimens16)
                .padding(top = Dimens16)
                .width(tableCardWidth())
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onFlip() }
        ) {
            // Front: rotates 0 → 180, fades out at 90°
            FrontCard(
                card = card, currentIndex = currentIndex, totalCards = totalCards,
                modifier = Modifier.graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8 * density
                    alpha = if (rotation < 90f || rotation > 270f) 1f else 0f
                }
            )
            // Back: starts at -180°, rotates to 0°, visible between 90° and 270°
            BackCard(
                card = card,
                modifier = Modifier.graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 8 * density
                    alpha = if (rotation >= 90f && rotation <= 270f) 1f else 0f
                }
            )
        }

        // Fixed-height bottom area — prevents the card from shifting when content switches
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxOf(AppDimens.Dimens60, (70f * s).dp)),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isFlipped, animationSpec = tween(200), label = "flashcard-bottom") { flipped ->
                if (flipped) {
                    Row(
                        // centred in the fixed area, where the tap hint is, so nothing jumps when the card is turned
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens24),
                        horizontalArrangement = Arrangement.spacedBy(Dimens12)
                    ) {
                        AbButton(
                            text = stringResource(R.string.try_again),
                            modifier = Modifier.weight(1f),
                            tone = AbTone.Danger,
                            size = AbSize.Medium,
                            icon = Icons.Rounded.Close,
                            fillWidth = true,
                            onClick = onTryAgain
                        )
                        AbButton(
                            text = stringResource(R.string.got_it),
                            modifier = Modifier.weight(1f),
                            tone = AbTone.Success,
                            size = AbSize.Medium,
                            icon = Icons.Rounded.Check,
                            fillWidth = true,
                            onClick = onGotIt
                        )
                    }
                } else {
                    // Tap hint — highlighted pill
                    Text(
                        text = "👆  Tap the card to reveal the answer",
                        style = abFont(14f),
                        color = colors.systemPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = Dimens16)
                            .background(colors.systemPrimary.copy(alpha = 0.10f), CircleShape)
                            .padding(horizontal = Dimens16, vertical = Dimens8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens16))
    }
}

@Composable
private fun FrontCard(card: Pair<Int, Int>, currentIndex: Int, totalCards: Int, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        AbHeroPanel(Color(0xFF0288D1), Modifier.matchParentSize(), padding = 0.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Dimens16)
        ) {
            // Card counter pill
            AbCountPill(
                text = "Card ${currentIndex + 1} of $totalCards",
                icon = Icons.Rounded.Style,
            )
            Text(
                text = "🤔",
                style = abEmojiStyle(abResponsive(40f)),
                modifier = Modifier.padding(top = Dimens8)
            )
            // the line with the sum and the pill under it sit in slots of the same height on both faces
            FlashSlot(flashRowHeight(), Modifier.padding(vertical = Dimens12)) {
                Text(
                    text = "${card.first} × ${card.second} = ?",
                    style = abFont(40f, FontWeight.Black),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
            // "Think first" — orange pill with white text
            FlashSlot(flashPillHeight()) {
                Text(
                    text = "💡  Think first, then tap to reveal!",
                    style = abFixedStyle(abTabletSize(10f, 15f, 18f), FontWeight.ExtraBold),
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFC74500), CircleShape)
                        .padding(horizontal = Dimens12, vertical = Dimens6)
                )
            }
        }
    }
}

@Composable
private fun BackCard(card: Pair<Int, Int>, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        AbHeroPanel(Color(0xFF43A047), Modifier.matchParentSize(), padding = 0.dp)
        // the same skeleton as the front (its pill and emoji unseen), so the answer sits exactly where the question was
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Dimens16)
        ) {
            Box(Modifier.alpha(0f)) { AbCountPill(text = "Card", icon = Icons.Rounded.Style) }
            Text(
                text = "🤔",
                style = abEmojiStyle(abResponsive(40f)),
                modifier = Modifier.padding(top = Dimens8).alpha(0f)
            )
            FlashSlot(flashRowHeight(), Modifier.padding(vertical = Dimens12)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens8)
                ) {
                    Text(
                        text = "${card.first} × ${card.second} =",
                        style = abFont(28f),
                        color = Color.White.copy(alpha = 0.82f)
                    )
                    Text(
                        text = "${card.first * card.second}",
                        style = abFont(60f, FontWeight.Black),
                        color = Color.White
                    )
                }
            }
            // "Did you know it?" — yellow highlight pill
            FlashSlot(flashPillHeight()) {
                Text(
                    text = "⭐  Did you know it?",
                    style = abFixedStyle(abTabletSize(14f, 18f, 22f), FontWeight.ExtraBold),
                    color = Color(0xFFC74500),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0xFFFFF9C4), CircleShape)
                        .padding(horizontal = Dimens12, vertical = Dimens6)
                )
            }
        }
    }
}

/** A slot of exactly [height] for [content], centred in it, so both faces of the card place their lines in the same spots. */
@Composable
private fun FlashSlot(height: Dp, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.height(height), contentAlignment = Alignment.Center) {
        Box(Modifier.wrapContentHeight(unbounded = true)) { content() }
    }
}

/** The height both faces of the flashcard give the line with the sum, and the pill under it (see FrontCard and BackCard). */
@Composable
private fun flashRowHeight() = (abResponsive(60f) * 1.12f).dp

@Composable
private fun flashPillHeight() = (abTabletSize(14f, 18f, 22f) * 1.12f).dp + Dimens6 * 2

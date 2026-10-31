package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import kotlinx.coroutines.delay

// Device scale used across game popups/side panels so tablets/iPad-parity get
// bigger stars, emoji and cards.
fun gameScale(): Float =
    if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1f

// Encouraging lines the mascot shows while playing — one picked at random per game.
val gameCheerLines = listOf(
    "You can do it!",
    "Keep it up! 💪",
    "You've got this!",
    "Nice going! 👍",
    "Stay sharp! ✨",
    "Great focus! 🎯",
    "Keep going! 🚀",
    "You're doing great!",
    "Brain power! 🧠",
    "Smart thinking! 💡"
)
fun randomGameCheer(): String = gameCheerLines.random()

private val BLUE = Color(0xFF0074D5)
private val ORANGE = Color(0xFFE65100)
private val ORANGE_BORDER = Color(0xFFFF8400)

// Floating, springy mascot shared across game play screens.
// ── Characters ───────────────────────────────────────────────────────────────────────────────────────────────────────

/** The friend that cheers a game on. Every game has its own character instead of one shared mascot (iOS twin: GameCharacter). */
enum class GameCharacter(val emoji: String, val charName: String, val tint: Color) {
    Fox("🦊", "Foxy", Color(0xFFF59E0B)),
    Owl("🦉", "Hoot", Color(0xFF8B5CF6)),
    Parrot("🦜", "Polly", Color(0xFF22C55E)),
    Monkey("🐵", "Momo", Color(0xFFD97706)),
    Unicorn("🦄", "Sparkle", Color(0xFFEC4899)),
    Elephant("🐘", "Ellie", Color(0xFF60A5FA)),
    Panda("🐼", "Bamboo", Color(0xFF64748B)),
    Frog("🐸", "Hops", Color(0xFF84CC16)),
    Giraffe("🦒", "Gigi", Color(0xFFEAB308)),
    Rabbit("🐰", "Zippy", Color(0xFF38BDF8)),
    Dog("🐶", "Scout", Color(0xFFF97316));

    companion object {
        /** Which friend belongs to which game. */
        fun of(type: GameCategoryType): GameCharacter = when (type) {
            GameCategoryType.SPEED_COMPARE -> Fox
            GameCategoryType.TRUE_FALSE -> Owl
            GameCategoryType.MATH_BINGO -> Parrot
            GameCategoryType.MISSING_OPERATOR -> Monkey
            GameCategoryType.MAGIC_SQUARE -> Unicorn
            GameCategoryType.CALCUDOKU -> Elephant
            GameCategoryType.MERGE_2048 -> Panda
            GameCategoryType.EQUATION_MATCH -> Frog
            GameCategoryType.PLACE_VALUE -> Giraffe
            GameCategoryType.CLOCK_MASTER -> Rabbit
            GameCategoryType.NUMBER_DETECTIVE -> Dog
            else -> Fox
        }
    }
}

// A game's cartoon friend above a name tag: it bobs, blinks and waves while it waits, jumps with open arms when the answer
// is right (celebrate), and bounces with a wink when tapped.
@Composable
fun GameFloatingMascot(heightDp: Float, celebrate: Boolean, modifier: Modifier = Modifier, character: GameCharacter = GameCharacter.Fox) {
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> t = (now - start) / 1_000_000_000f }
        }
    }
    var tapped by remember { mutableStateOf(false) }
    LaunchedEffect(tapped) { if (tapped) { delay(450); tapped = false } }
    val up = celebrate || tapped
    val bump by animateFloatAsState(if (up) 1.1f else 1f, spring(dampingRatio = 0.45f), label = "bump")
    val lift by animateFloatAsState(
        (if (celebrate) -0.06f else 0f) + (if (tapped) -0.08f else 0f),
        spring(dampingRatio = 0.45f),
        label = "lift",
    )
    val face = character.tint.abReadable(3f)
    Column(
        modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tapped = true },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((heightDp * 0.03f).dp),
    ) {
        CartoonFriend(
            character = character,
            size = heightDp.dp,
            t = t,
            celebrate = up,
            wink = tapped,
            modifier = Modifier.graphicsLayer {
                scaleX = bump
                scaleY = bump
                translationY = lift * heightDp * density
            },
        )
        Text(
            character.charName,
            style = AbTypography.fixed(heightDp * 0.12f, FontWeight.ExtraBold),
            color = Color.White,
            modifier = Modifier
                .abChunkyFace(face, face.abDarkened(0.3f), null, AbShapes.pill, maxOf(2f, heightDp * 0.02f).dp)
                .padding(horizontal = (heightDp * 0.12f).dp, vertical = (heightDp * 0.02f).dp),
        )
    }
}

// Left panel: encouraging speech bubble + floating mascot (fills the left gap).
@Composable
fun GameMascotPanel(cheer: String, celebrate: Boolean, s: Float, modifier: Modifier = Modifier, character: GameCharacter = GameCharacter.Fox) {
    Column(
        modifier = modifier.padding(horizontal = AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(cheer, color = LocalAbColors.current.brandPrimary, style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
            modifier = Modifier.abGamePill()
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8))
        Spacer(Modifier.height(AppDimens.Dimens24))
        GameFloatingMascot(heightDp = 120f * s, celebrate = celebrate, character = character)
    }
}

// Right panel: playful live scoreboard card (fills the right gap).
@Composable
fun GameScoreboardPanel(score: Int, multiplier: Int, best: Int, s: Float, modifier: Modifier = Modifier) {
    val colors = LocalAbColors.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        AbGlassPanel {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
                modifier = Modifier.padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
            ) {
                ScoreRow("⭐", "$score", colors.brandPrimary, s)
                ScoreRow("🔥", "x$multiplier", colors.accentOrange.abReadable(3f), s)
                ScoreRow("🏆", "$best", colors.accentOrange.abReadable(3f), s)
            }
        }
    }
}

@Composable
private fun ScoreRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold))
    }
}

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
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.home.theme.AppDimens

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
@Composable
fun GameFloatingMascot(heightDp: Float, celebrate: Boolean, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "mascot")
    val offsetY by infinite.animateFloat(
        initialValue = 0f, targetValue = -14f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "float"
    )
    val bump by animateFloatAsState(if (celebrate) 1.12f else 1f, spring(dampingRatio = 0.5f), label = "")
    Image(
        painter = painterResource(R.drawable._mascot_),
        contentDescription = null,
        modifier = modifier.height(heightDp.dp)
            .graphicsLayer { translationY = offsetY; scaleX = bump; scaleY = bump }
    )
}

// Left panel: encouraging speech bubble + floating mascot (fills the left gap).
@Composable
fun GameMascotPanel(cheer: String, celebrate: Boolean, s: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(cheer, color = BLUE, fontFamily = FontFamily(Font(R.font.font_bold)), fontSize = (15f * s).sp,
            modifier = Modifier.clip(RoundedCornerShape(100f)).background(Color.White.copy(alpha = 0.9f))
                .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8))
        Spacer(Modifier.height(AppDimens.Dimens24))
        GameFloatingMascot(heightDp = 120f * s, celebrate = celebrate)
    }
}

// Right panel: playful live scoreboard card (fills the right gap).
@Composable
fun GameScoreboardPanel(score: Int, multiplier: Int, best: Int, s: Float, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens12),
            modifier = Modifier.clip(RoundedCornerShape(AppDimens.Dimens16)).background(Color.White.copy(alpha = 0.8f))
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens16)
        ) {
            ScoreRow("⭐", "$score", BLUE, s)
            ScoreRow("🔥", "x$multiplier", ORANGE, s)
            ScoreRow("🏆", "$best", ORANGE_BORDER, s)
        }
    }
}

@Composable
private fun ScoreRow(emoji: String, value: String, color: Color, s: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6), verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = (22f * s).sp)
        Text(value, color = color, fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = (22f * s).sp)
    }
}

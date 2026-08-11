package com.jigar.me.ui.view.home.screens.math_game_zone.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens
import kotlinx.coroutines.delay

// Pops in over the result screen after two strong sessions in a row:
// "🚀 You're ready for Medium!" — a friendly push up the difficulty ladder.
@Composable
fun NextDifficultyBanner(nextDifficultyName: String) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(700); appeared = true }
    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "nudge"
    )

    Box(modifier = Modifier.fillMaxSize().padding(top = AppDimens.Dimens16)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens6),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    scaleX = scale; scaleY = scale
                    alpha = if (appeared) 1f else 0f
                    rotationZ = -1.5f
                }
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF536DFE))),
                    CircleShape
                )
                .border(2.dp, Color.White.copy(alpha = 0.75f), CircleShape)
                .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
        ) {
            Text("🚀", style = MaterialTheme.typography.titleMedium.scaled())
            Text(
                stringResource(R.string.ready_for_next, nextDifficultyName),
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                style = MaterialTheme.typography.labelLarge.scaled()
            )
        }
    }
}

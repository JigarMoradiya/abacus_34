package com.jigar.me.ui.view.home.screens.math_game_zone.place_value

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayPlaceValueView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.place_value.components.PlaceValueHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

/** The chips (value, place) and the digit each one drops into its slot. */
private val PREVIEW_CHIPS = listOf("400" to 2, "+70" to 1, "+2" to 0)
private val PREVIEW_DIGITS = listOf("4", "7", "2")

@Composable
fun PlaceValueHomeScreen(
    viewModel: PlaceValueHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)
    val s = gameScale()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        GameHomeLayout(
            header = {Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.place_value),
                modifier = Modifier.weight(1f), onBackClick = onBackClick
            )
            GameButton(
                modifier = Modifier.padding(end = AppDimens.Dimens16),
                text = stringResource(R.string.how_to_play),
                icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
                onClick = { showHelp = true }
            )
        }},
            bottom = {

            Row(
                modifier = Modifier.fillMaxWidth().padding(AppDimens.Dimens16),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonDifficultySelectorCompose(
                    selected = selectedDifficulty,
                    onSelect = { viewModel.selectDifficulty(it) }
                )
                GameButton(
                    text = stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            // Preview: candy chips in their place colours drop their digits into the matching sockets.
            PlaceValuePreviewRow(s)

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.place_value_tagline),
                color = ORANGE, style = AbTypography.fixed(22f * s, FontWeight.ExtraBold),
                textAlign = TextAlign.Center,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN, style = AbTypography.fixed(17f * s, FontWeight.Bold),
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayPlaceValueView { showHelp = false }
    }
}

// ── Preview art ───────────────────────────────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlaceValuePreviewRow(s: Float) {
    val colors = LocalAbColors.current
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> t = (now - start) / 1_000_000_000f } }
    }
    val cycle = t % 5f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((10f * s).dp)
    ) {
        for (i in 0 until 3) {
            PlaceValuePreviewChip(i, t, s)
        }
        Text(
            "→", color = colors.accentOrange.abDarkened(0.2f),
            style = AbTypography.fixed(30f * s, FontWeight.Black),
            modifier = Modifier.graphicsLayer { translationX = sin(t * 3f) * 3f * s * density }
        )
        for (i in 0 until 3) {
            PlaceValuePreviewSlot(i, cycle, s)
        }
    }
}

@Composable
private fun PlaceValuePreviewChip(i: Int, t: Float, s: Float) {
    val colors = LocalAbColors.current
    val item = PREVIEW_CHIPS[i]
    val face = colors.toyColor(item.second)
    val wave = sin(t * 2.4f + i * 0.6f)
    val bob = wave * 3f * s
    val pulse = 1f + wave * 0.06f
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = pulse; scaleY = pulse; translationY = -bob * density }
            .abToyTile(face, (14f * s).dp, 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            item.first, color = Color.White,
            style = AbTypography.fixed(22f * s, FontWeight.Black).copy(
                shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.55f), Offset(0f, 3f), 0f)
            ),
            maxLines = 1, softWrap = false,
            modifier = Modifier.padding(horizontal = (12f * s).dp, vertical = (8f * s).dp)
        )
    }
}

@Composable
private fun PlaceValuePreviewSlot(i: Int, cycle: Float, s: Float) {
    val colors = LocalAbColors.current
    val place = 2 - i
    val face = colors.toyColor(place)
    val side = 46f * s
    val edge = 4f
    val age = cycle - (1.2f + 0.7f * i)
    val filled = age >= 0f && cycle < 4.4f
    // 0 -> overshoot -> 1: the digit thumps into its socket and settles
    val pop = if (filled) (1f - exp(-9f * age) * cos(age * 14f)).coerceAtLeast(0f) else 0f
    Box(Modifier.size(side.dp)) {
        Box(Modifier.size(side.dp, (side - edge).dp).placeValueSocket(face, (14f * s).dp))
        Box(
            modifier = Modifier
                .size(side.dp)
                .graphicsLayer { scaleX = pop; scaleY = pop; alpha = if (filled) 1f else 0f }
                .abToyTile(face, (14f * s).dp, edge.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                PREVIEW_DIGITS[i], color = Color.White,
                style = AbTypography.fixed(26f * s, FontWeight.Black).copy(
                    shadow = Shadow(face.abDarkened(0.5f).copy(alpha = 0.5f), Offset(0f, 3f), 0f)
                )
            )
        }
    }
}

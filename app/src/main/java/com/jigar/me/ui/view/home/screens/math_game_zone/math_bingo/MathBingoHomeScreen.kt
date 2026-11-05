package com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMathBingoView
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.math_bingo.components.MathBingoHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)
private val MATH_BINGO_PREVIEW_NUMBERS = listOf("12", "7", "45", "9", "28", "6")

@Composable
fun MathBingoHomeScreen(
    viewModel: MathBingoHomeViewModel,
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
                title = stringResource(R.string.math_bingo),
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

            // Preview: a candy bingo card that floats, stamping a bouncy star down the diagonal.
            MathBingoPreviewCard(s)

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.math_bingo_tagline),
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
        HowToPlayMathBingoView { showHelp = false }
    }
}

// ── Preview art ───────────────────────────────────────────────────────────────────────────────────────────────────────

/** A candy bingo card in the game's tray that floats up and down while a star stamps itself down the diagonal. */
@Composable
private fun MathBingoPreviewCard(s: Float) {
    val accent = gameCardStyle(GameCategoryType.MATH_BINGO).light
    var t by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> t = (now - start) / 1_000_000_000f } }
    }
    val bob = sin(t * 1.7f) * 3f * s
    Column(
        verticalArrangement = Arrangement.spacedBy((5f * s).dp),
        modifier = Modifier
            .graphicsLayer { translationY = bob * density }
            .abToyTray(accent, (9f * s).dp)
    ) {
        for (r in 0 until 3) {
            Row(horizontalArrangement = Arrangement.spacedBy((5f * s).dp)) {
                for (c in 0 until 3) {
                    MathBingoPreviewCell(r, c, t, s)
                }
            }
        }
    }
}

/** How far along (in seconds) the star stamp on diagonal cell [k] is; negative while it has not landed yet. */
private fun mathBingoStampAge(k: Int, t: Float): Float {
    val phase = t % 4.6f
    if (phase > 4.1f) return -1f
    return phase - (0.5f + 0.8f * k)
}

@Composable
private fun MathBingoPreviewCell(r: Int, c: Int, t: Float, s: Float) {
    val colors = LocalAbColors.current
    val i = r * 3 + c
    val onDiagonal = r == c
    val age = if (onDiagonal) mathBingoStampAge(r, t) else -1f
    val stamped = age >= 0f
    val candy = colors.toyColor(i)
    val face = if (stamped) colors.accentGreen.abReadable(3f) else candy.abBlended(Color.White, 0.74f)
    // 0 -> overshoot -> 1: the stamp thumps down and settles
    val pop = if (stamped) (1f - exp(-9f * age) * cos(age * 14f)).coerceAtLeast(0f) else 0f
    val edge = 3f * s
    val side = 42f * s
    Box(
        modifier = Modifier.size(side.dp).abToyTile(face, (side * 0.24f).dp, edge.dp),
        contentAlignment = Alignment.Center
    ) {
        if (onDiagonal) {
            MathBingoStar(
                size = (22f * s).dp, tint = Color.White, edgeColor = colors.accentGreen.abDarkened(0.45f),
                modifier = Modifier.graphicsLayer {
                    scaleX = pop
                    scaleY = pop
                    rotationZ = (1f - min(pop, 1f)) * -40f
                    alpha = if (stamped) 1f else 0f
                }
            )
        }
        if (!stamped) {
            Text(
                MATH_BINGO_PREVIEW_NUMBERS[i % 6],
                style = AbTypography.fixed(17f * s, FontWeight.Black),
                color = candy.abDarkened(0.25f),
                maxLines = 1
            )
        }
    }
}

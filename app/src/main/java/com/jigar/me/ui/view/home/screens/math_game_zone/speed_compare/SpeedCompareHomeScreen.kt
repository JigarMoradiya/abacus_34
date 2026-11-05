package com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlaySpeedCompareView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.speed_compare.components.SpeedCompareHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

@Composable
fun SpeedCompareHomeScreen(
    viewModel: SpeedCompareHomeViewModel,
    onStartGame: () -> Unit,
    onStartDuel: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            header = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(
                    title = stringResource(R.string.speed_compare_game),
                    modifier = Modifier.weight(1f),
                    onBackClick = onBackClick
                )
                GameButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    type = ButtonType.PINK,
                    isSmall = true,
                    onClick = { showHelp = true }
                )
            }

            },
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
                    icon = Icons.Rounded.PlayArrow,
                    type = ButtonType.ORANGE,
                    isIconStart = false,
                    onClick = onStartGame
                )
                // Face-to-face duel needs the big screen.
                if (com.jigar.me.data.local.data.DeviceInfo.isTablet) {
                    GameButton(
                        text = stringResource(R.string.duel_two_players),
                        type = ButtonType.BLUE,
                        onClick = onStartDuel
                    )
                }
            }
        },
        ) {

            // Preview: 7 > 5 candy cards on a see-saw, rocking up/down — the game's hook, animated
            SpeedCompareHomePreview()

            // space before the tagline card (the see-saw hangs below the cards)
            Spacer(Modifier.height(AppDimens.Dimens30 + 12.dp))

            SpeedCompareTaglinePill(text = stringResource(R.string.speed_compare_tagline))

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                SpeedCompareBestPill(text = "🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore")
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlaySpeedCompareView { showHelp = false }
    }
}

/** The tagline on a soft white pill. */
@Composable
private fun SpeedCompareTaglinePill(text: String) {
    Text(
        text = text,
        color = ORANGE,
        style = AbTypography.fixed(22f, FontWeight.ExtraBold).scaled(),
        modifier = Modifier
            .abGamePill()
            .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
    )
}

/** The best score on a soft white pill. */
@Composable
private fun SpeedCompareBestPill(text: String) {
    Text(
        text = text,
        color = GREEN,
        style = AbTypography.fixed(17f, FontWeight.Bold).scaled(),
        modifier = Modifier
            .abGamePill()
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
    )
}

/**
 * Two candy number cards rocking on a little see-saw. The plank is drawn behind the cards and follows their offsets, so it always
 * sits under them. Clock-driven (own composable, so only the preview recomposes each frame).
 */
@Composable
private fun SpeedCompareHomePreview() {
    val colors = LocalAbColors.current
    val s = gameScale()
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }
    val bob: Float = sin(time * 1.6f) * 10f
    val cardW: Float = 74f * s
    val signW: Float = 40f * s
    val gap: Float = AppDimens.Dimens10.value
    // distance between the two card centres (card, gap, sign, gap, card)
    val span: Float = cardW + signW + 2f * gap
    val tilt: Float = atan2(2f * bob, span) * 180f / PI.toFloat()
    val plankFace: Color = colors.accentOrange
    val postFace: Color = colors.accentYellow

    Row(
        horizontalArrangement = Arrangement.spacedBy(gap.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.drawBehind {
            val d = density
            val plankW = size.width + 26f * d
            val plankH = 12f * d
            val cx = size.width / 2f
            val cy = size.height + 6f * d
            val postMid = cy + 11f * d
            val post = Path().apply {
                moveTo(cx, postMid - 9f * d)
                lineTo(cx - 17f * d, postMid + 9f * d)
                lineTo(cx + 17f * d, postMid + 9f * d)
                close()
            }
            drawPath(post, postFace)
            rotate(degrees = tilt, pivot = Offset(cx, cy)) {
                val topLeft = Offset(cx - plankW / 2f, cy - plankH / 2f)
                val plank = Size(plankW, plankH)
                val corner = CornerRadius(plankH / 2f, plankH / 2f)
                drawRoundRect(plankFace, topLeft = topLeft, size = plank, cornerRadius = corner)
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.Transparent), startY = topLeft.y, endY = cy),
                    topLeft = topLeft, size = plank, cornerRadius = corner
                )
                drawRoundRect(Color.White.copy(alpha = 0.45f), topLeft = topLeft, size = plank, cornerRadius = corner, style = Stroke(width = 1.5f * d))
            }
        }
    ) {
        SpeedCompareHomeCard("7", colors.brandPrimary, s, Modifier.offset(y = (-bob).dp).abPopIn(0))
        SpeedCompareHomeSign(s)
        SpeedCompareHomeCard("5", colors.accentPink, s, Modifier.offset(y = bob.dp).abPopIn(120))
    }
}

@Composable
private fun SpeedCompareHomeCard(text: String, tint: Color, s: Float, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .abToyTile(tint.abReadable(3f), AppDimens.Dimens16, 4.dp)
            .size((74f * s).dp, (74f * s - 4f).dp)
    ) {
        Text(
            text,
            color = Color.White,
            style = AbTypography.fixed(36f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
    }
}

/** The orange ">" between the cards: a little candy ball that breathes. */
@Composable
private fun SpeedCompareHomeSign(s: Float) {
    val colors = LocalAbColors.current
    val signW = 40f * s
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .abBreathe(0.08f, 1200)
            .abToyTile(colors.accentOrange.abReadable(3f), (signW * 0.5f).dp, 3.dp)
            .size(signW.dp, signW.dp)
    ) {
        Text(
            ">",
            color = Color.White,
            style = AbTypography.fixed(30f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
    }
}

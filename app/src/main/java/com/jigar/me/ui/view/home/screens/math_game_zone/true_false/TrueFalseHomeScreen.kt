package com.jigar.me.ui.view.home.screens.math_game_zone.true_false

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameHomeLayout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayTrueFalseView
import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abPopIn
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.true_false.components.TrueFalseHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

@Composable
fun TrueFalseHomeScreen(
    viewModel: TrueFalseHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        GameHomeLayout(
            header = {Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.true_false),
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

            // Two bobbing candy cards: one true, one sneaky lie.
            TrueFalseHomePreview()

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.true_false_tagline),
                color = ORANGE,
                style = AbTypography.fixed(22f, FontWeight.ExtraBold).scaled(),
                modifier = Modifier
                    .abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text(
                    "🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    color = GREEN,
                    style = AbTypography.fixed(17f, FontWeight.Bold).scaled(),
                    modifier = Modifier
                        .abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4)
                )
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayTrueFalseView { showHelp = false }
    }
}

/** Two candy cards that pulse, bob and tilt: one true, one sneaky lie. Clock-driven; its own composable so only it recomposes. */
@Composable
private fun TrueFalseHomePreview() {
    val s = gameScale()
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) { withFrameNanos { now -> time = (now - start) / 1_000_000_000f } }
    }
    Row(horizontalArrangement = Arrangement.spacedBy((16f * s).dp)) {
        TrueFalsePreviewCard(
            text = "3 + 4 = 7", forTrue = true, s = s,
            scale = 1f + sin(time * 2.4f) * 0.06f, bob = sin(time * 2.0f) * 5f, tilt = -3f,
            modifier = Modifier.abPopIn(0)
        )
        TrueFalsePreviewCard(
            text = "6 × 2 = 13", forTrue = false, s = s,
            scale = 1f + sin(time * 2.4f + 1.6f) * 0.06f, bob = sin(time * 2.0f + 1.6f) * 5f, tilt = 3f,
            modifier = Modifier.abPopIn(120)
        )
    }
}

@Composable
private fun TrueFalsePreviewCard(
    text: String, forTrue: Boolean, s: Float, scale: Float, bob: Float, tilt: Float, modifier: Modifier = Modifier
) {
    val colors = LocalAbColors.current
    val face: Color = (if (forTrue) colors.accentGreen else colors.danger).abReadable(3f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((4f * s).dp),
        modifier = modifier
            .offset(y = bob.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = tilt
            }
            .abToyTile(face, (18f * s).dp, 5.dp)
            .padding(start = (18f * s).dp, top = (12f * s).dp, end = (18f * s).dp, bottom = (12f * s - 5f).dp)
    ) {
        Text(
            text,
            color = Color.White,
            style = AbTypography.fixed(22f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f))
        )
        TrueFalseBadge(forTrue = forTrue, face = face, sizeDp = 26f * s)
    }
}

/** A white disc with a tick or a cross cut out of it in the card's colour (the "checkmark.circle.fill" of iOS). */
@Composable
private fun TrueFalseBadge(forTrue: Boolean, face: Color, sizeDp: Float) {
    Canvas(modifier = Modifier.size(sizeDp.dp)) {
        val d = density
        val r = size.minDimension / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color.White, radius = r, center = c)
        val half = r * 0.5f
        val path = Path()
        if (forTrue) {
            path.moveTo(c.x - half * 0.9f, c.y + half * 0.05f)
            path.lineTo(c.x - half * 0.25f, c.y + half * 0.65f)
            path.lineTo(c.x + half * 0.95f, c.y - half * 0.6f)
        } else {
            path.moveTo(c.x - half * 0.7f, c.y - half * 0.7f)
            path.lineTo(c.x + half * 0.7f, c.y + half * 0.7f)
            path.moveTo(c.x + half * 0.7f, c.y - half * 0.7f)
            path.lineTo(c.x - half * 0.7f, c.y + half * 0.7f)
        }
        drawPath(path, face, style = Stroke(width = 3.2f * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

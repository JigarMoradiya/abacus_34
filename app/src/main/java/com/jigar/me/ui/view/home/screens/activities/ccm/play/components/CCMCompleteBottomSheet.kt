package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abBlended
import com.jigar.me.ui.view.ab.abCardEntrance
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.screens.activities.rememberEncouragements

/**
 * The Custom Challenge result: a dimmed scrim and a card with a happy or an encouraging face, the sum with its answer and
 * the two choices (new challenge / close). The scrim only fades while the card pops in.
 */
@Composable
fun CCMCompleteBottomSheetCompose(
    uiState: CCMPlayUiState,
    onContinue: () -> Unit,
    onClose: () -> Unit
) {
    val colors = LocalAbColors.current
    val abScale = LocalAbScale.current
    // a little smaller on a phone, where the screen is short
    val s = abScale.factor * (if (abScale.isTablet) 1f else 0.82f)
    val challenge = uiState.customChallengeData
    val isAnswerTrue = uiState.isAnswerTrue
    val tone = if (isAnswerTrue) AbColors.Light.accentGreen else AbColors.Light.accentOrange
    val shape = RoundedCornerShape(AbShapes.lg * s)
    val cheers = rememberEncouragements()
    val cheer = remember { cheers.random() }
    val sum = challenge?.fullQuestion
        ?.replace("+", " + ")
        ?.replace("-", " - ")
        ?.plus(" = ${challenge.answer}")
        .orEmpty()

    LaunchedEffect(uiState.isShowCompletePopup) {
        if (uiState.isShowCompletePopup) {
            if (isAnswerTrue) AudioPlayerManager.playSoundWin() else AudioPlayerManager.playSoundOptionWrong()
        }
    }

    AbPopupLayer(onScrimTap = onClose) { layerWidth, _, shown ->
        Column(
            modifier = Modifier
                .width(minOf(layerWidth * 0.6f, 420.dp * s))
                .abCardEntrance(shown)
                .shadow(18.dp * s, shape, clip = false, ambientColor = Color.Black, spotColor = Color.Black)
                .padding(bottom = 6.dp * s)
                .drawBehind {
                    val r = (AbShapes.lg * s).toPx()
                    drawRoundRect(tone.abDarkened(0.3f), Offset(0f, 6.dp.toPx() * s), size, CornerRadius(r, r))
                }
                .background(colors.bgSecondary, shape)
                .border(3.dp, tone.copy(alpha = if (colors.isDark) 0.7f else 0.5f), shape)
                .padding(18.dp * s),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp * s),
        ) {
            // the face: a glossy disc with a rim and a 3D edge
            Box(Modifier.padding(bottom = 4.dp * s).size(70.dp * s), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val d = size.minDimension
                    val centre = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(tone.abDarkened(0.3f), d / 2f, centre + Offset(0f, 4.dp.toPx() * s))
                    drawCircle(
                        Brush.verticalGradient(listOf(tone.abBlended(Color.White, 0.3f), tone)),
                        d / 2f, centre,
                    )
                    drawCircle(Color.White.copy(alpha = 0.9f), d / 2f - 1.5.dp.toPx(), centre, style = Stroke(3.dp.toPx()))
                }
                Text(if (isAnswerTrue) "🎉" else "😞", style = AbTypography.fixed(38f * s))
            }

            Text(
                text = if (isAnswerTrue) stringResource(R.string.congratulations) else stringResource(R.string.sorry),
                style = AbTypography.title(s),
                color = tone.abDarkened(if (colors.isDark) 0f else 0.15f),
            )

            Text(
                text = if (isAnswerTrue) cheer else stringResource(R.string.better_luck_for_next_time),
                style = AbTypography.body(s).copy(fontWeight = FontWeight.SemiBold),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )

            Box(
                Modifier
                    .height(38.dp * s)
                    .background(colors.surfaceTint.copy(alpha = if (colors.isDark) 0.5f else 0.7f), RoundedCornerShape(AbShapes.md * s))
                    .padding(horizontal = 16.dp * s),
                contentAlignment = Alignment.Center,
            ) {
                AbShrinkText(
                    text = sum,
                    style = AbTypography.fixed(18f * s, FontWeight.ExtraBold),
                    color = colors.textPrimary,
                    minScale = 0.5f,
                    maxLines = 1,
                )
            }

            AbButton(
                text = stringResource(R.string.start_new_challenge),
                modifier = Modifier.padding(top = 4.dp * s),
                tone = AbTone.Warning,
                size = AbSize.Medium,
                icon = Icons.Default.EmojiEvents,
                onClick = onContinue,
            )

            Text(
                text = stringResource(R.string.no_i_want_to_close),
                style = AbTypography.label(s),
                color = colors.textSecondary,
                modifier = Modifier
                    .abSquash(cornerRadius = AbShapes.sm * s, sound = AbSound.Button, onClick = onClose)
                    .padding(vertical = 4.dp * s),
            )
        }
    }
}

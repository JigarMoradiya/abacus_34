package com.jigar.me.ui.view.home.screens.math_game_zone.number_detective

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayNumberDetectiveView
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.gameScale
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.number_detective.components.NumberDetectiveHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

@Composable
fun NumberDetectiveHomeScreen(
    viewModel: NumberDetectiveHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)
    val s = gameScale()
    val colors = LocalAbColors.current
    val time = rememberNumberDetectiveTime()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        GameHomeLayout(
            header = {Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BackButtonWithText(
                title = stringResource(R.string.number_detective),
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

            // Preview: a magnifying glass hovering over a mystery number, with the tries coins bobbing below.
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy((10f * s).dp)) {
                NumberDetectivePreviewRow(s = s, time = time)
                NumberDetectiveTriesTrail(maxTries = 5, triesUsed = 0, s = s, time = time)
            }

            Spacer(Modifier.height(AppDimens.Dimens24))

            Text(
                text = stringResource(R.string.number_detective_tagline),
                style = AbTypography.fixed(22f * appScale(), FontWeight.ExtraBold),
                color = colors.textPrimary,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + stringResource(R.string.balloon_best_score) + ": $bestScore",
                    style = AbTypography.fixed(18f * appScale(), FontWeight.Bold),
                    color = colors.accentGreenInk,
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayNumberDetectiveView { showHelp = false }
    }
}

// The preview art: a wiggling magnifier, a candy "?" box that breathes and bobs, and a hopping orange hint arrow.
@Composable
private fun NumberDetectivePreviewRow(s: Float, time: androidx.compose.runtime.State<Float>) {
    val colors = LocalAbColors.current
    val boxFace = gameCardStyle(GameCategoryType.NUMBER_DETECTIVE).light.abReadable(3f)
    val arrowFace = colors.accentOrange.abReadable(3f)
    Row(
        horizontalArrangement = Arrangement.spacedBy((14f * s).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "🔍",
            fontSize = (46f * s).sp,
            modifier = Modifier.graphicsLayer {
                val t = time.value
                rotationZ = sin(t * 1.7f) * 12f
                translationY = sin(t * 2.2f) * 4f * density
            }
        )
        Box(
            Modifier
                .graphicsLayer { translationY = sin(time.value * 2f) * 3f * s * density }
                .abToyTile(boxFace, (18f * s).dp, 4.dp)
                .size((72f * s).dp, (58f * s).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "?",
                style = AbTypography.fixed(36f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    val breathe = 1f + 0.07f * sin(time.value * 3f)
                    scaleX = breathe
                    scaleY = breathe
                }
            )
        }
        Box(
            Modifier
                .graphicsLayer { translationY = sin(time.value * 4f + 1f) * -4f * s * density }
                .abToyTile(arrowFace, (17f * s).dp, 3.dp)
                .size((34f * s).dp),
            contentAlignment = Alignment.Center
        ) {
            NumberDetectiveArrow(up = true, size = (18f * s).dp)
        }
    }
}

package com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.common_ui.CommonDifficultySelectorCompose
import com.jigar.me.ui.view.home.common_ui.how_to_play.HowToPlayMissingOperatorView
import com.jigar.me.ui.view.home.screens.math_game_zone.GameCategoryType
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abBreathe
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abGamePill
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTray
import com.jigar.me.ui.view.home.screens.math_game_zone.gameCardStyle
import com.jigar.me.ui.view.home.screens.math_game_zone.missing_operator.components.MissingOperatorHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import kotlin.math.sin

private val ORANGE = Color(0xFFE65100)
private val GREEN = Color(0xFF2E7D32)

@Composable
fun MissingOperatorHomeScreen(
    viewModel: MissingOperatorHomeViewModel,
    onStartGame: () -> Unit,
    onBackClick: () -> Unit
) {
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    var showHelp by remember { mutableStateOf(false) }
    val bestScore = viewModel.bestScore(selectedDifficulty)
    val s = if (DeviceInfo.isLargeTablet) 1.7f else if (DeviceInfo.isTablet) 1.45f else 1.0f
    val colors = LocalAbColors.current
    val time = rememberMissingOperatorTime()

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        GameHomeLayout(
            header = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BackButtonWithText(
                    title = androidx.compose.ui.res.stringResource(R.string.missing_operator_game),
                    modifier = Modifier.weight(1f), onBackClick = onBackClick
                )
                GameButton(
                    modifier = Modifier.padding(end = AppDimens.Dimens16),
                    text = androidx.compose.ui.res.stringResource(R.string.how_to_play),
                    icon = Icons.AutoMirrored.Filled.HelpOutline, type = ButtonType.PINK, isSmall = true,
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
                CommonDifficultySelectorCompose(selected = selectedDifficulty, onSelect = { viewModel.selectDifficulty(it) })
                GameButton(
                    text = androidx.compose.ui.res.stringResource(R.string.lets_start),
                    icon = Icons.Rounded.PlayArrow, type = ButtonType.ORANGE, isIconStart = false, onClick = onStartGame
                )
            }
        },
        ) {

            // Preview: 12 [?] 3 = 4 on a toy board with the slot breathing, the game's hook, animated
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .abBreathe(0.015f, 2800)
                    .abToyTray(gameCardStyle(GameCategoryType.MISSING_OPERATOR).light, AppDimens.Dimens8)
                    .padding(horizontal = AppDimens.Dimens4)
            ) {
                MissingOperatorTile("12", colors.brandPrimary.abReadable(3f), s)
                MissingOperatorSlot(symbol = null, revealed = false, correct = false, s = s)
                MissingOperatorTile("3", colors.brandPrimary.abReadable(3f), s)
                MissingOperatorEquals(s)
                MissingOperatorTile("4", colors.accentPurple.abReadable(3f), s)
            }

            Spacer(Modifier.height(AppDimens.Dimens12))

            // Animated operator signs bobbing in a wave — fills the space + hints the choices
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens12), verticalAlignment = Alignment.CenterVertically) {
                listOf("+", "−", "×", "÷").forEachIndexed { index, sym ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .graphicsLayer { translationY = sin(time.value * 2f + index * 0.6f) * 6f * density }
                            .abToyTile(missingOperatorFace(sym, colors), (16f * s).dp, 4.dp)
                            .size((54f * s).dp)
                    ) {
                        Text(
                            sym,
                            style = AbTypography.fixed(30f * s, FontWeight.Black).copy(shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(AppDimens.Dimens12))

            Text(
                text = androidx.compose.ui.res.stringResource(R.string.missing_operator_tagline),
                style = AbTypography.fixed(22f * appScale(), FontWeight.ExtraBold),
                color = ORANGE,
                modifier = Modifier.abGamePill()
                    .padding(horizontal = AppDimens.Dimens16, vertical = AppDimens.Dimens8)
            )

            if (bestScore > 0) {
                Spacer(Modifier.height(AppDimens.Dimens6))
                Text("🏆 " + androidx.compose.ui.res.stringResource(R.string.balloon_best_score) + ": $bestScore",
                    style = AbTypography.fixed(18f * appScale(), FontWeight.Bold),
                    color = GREEN,
                    modifier = Modifier.abGamePill()
                        .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens4))
            }

            }
    }

    AnimatedVisibility(visible = showHelp, enter = fadeIn(), exit = fadeOut()) {
        HowToPlayMissingOperatorView { showHelp = false }
    }
}

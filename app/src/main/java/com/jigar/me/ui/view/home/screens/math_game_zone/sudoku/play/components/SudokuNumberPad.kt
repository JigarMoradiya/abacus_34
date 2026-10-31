package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbTonePalette
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abReadable
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abTone
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abToyTile
import com.jigar.me.ui.view.home.screens.math_game_zone.common.toyColor
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPlayViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType


@Composable
fun SudokuNumberPad(vm: SudokuPlayViewModel) {

    val colors = LocalAbColors.current
    val max = vm.puzzle.size.grid
    val columns = if (max == 4) 2 else 3
    val numbers = (1..max).toList()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6)
    ) {

        // ✅ Numbers grid: every digit key is its own rainbow colour
        numbers.chunked(columns).forEach { rowItems ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet)AppDimens.Dimens10 else AppDimens.Dimens6),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { n ->
                    SudokuKey(
                        text = "$n",
                        face = colors.toyColor(n - 1),
                        onClick = { vm.enter(n) }
                    )
                }
            }
        }

        // ✅ Erase button (centered, separate row): a real backspace icon drawn at key size
        SudokuKey(
            iconRes = R.drawable.ic_backspace,
            face = AbTonePalette.of(ButtonType.RED.abTone(), colors).face.abReadable(3f),
            onClick = { vm.eraseSelected() }
        )
    }
}

/**
 * A chunky candy key (Android twin of iOS `KidsKeypadButton` with a `face`): same footprint as the old keypad key, a glossy
 * toy tile with a 3D edge that squashes while pressed. The tap sound stays where it always was (in the view model).
 */
@Composable
private fun SudokuKey(
    text: String? = null,
    iconRes: Int? = null,
    face: Color,
    width: Dp = AppDimens.keyPadWidth,
    height: Dp = AppDimens.keyPadHeight,
    onClick: () -> Unit
) {
    val s = LocalAbScale.current.factor
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val squash by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "sudokuKeySquash"
    )
    val shortSide: Dp = minOf(width, height)

    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .graphicsLayer {
                scaleX = squash
                scaleY = squash
            }
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .abToyTile(face = face, corner = shortSide * 0.3f, edgeHeight = 3.dp * s),
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                style = AbTypography.fixed(shortSide.value * 0.55f, FontWeight.Black).copy(
                    shadow = Shadow(Color.Black.copy(alpha = 0.25f), Offset(0f, 3f), 0f)
                ),
                color = Color.White
            )
        }
        if (iconRes != null) {
            val iconSize: Dp = shortSide * 0.58f
            // soft shadow copy under the icon, then the icon itself
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.25f),
                modifier = Modifier
                    .size(iconSize)
                    .offset(y = 1.5.dp)
            )
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

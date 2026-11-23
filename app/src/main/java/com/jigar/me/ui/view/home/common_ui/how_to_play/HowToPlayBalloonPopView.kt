package com.jigar.me.ui.view.home.common_ui.how_to_play

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.home.screens.math_game_zone.common.abHowToCard

@Composable
fun HowToPlayBalloonPopView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }
    val accentColors = getButtonColors(ButtonType.PINK)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(screenWidth * widthMultiplier)
                .height(screenHeight * heightMultiplier)
                .abHowToCard(ButtonType.PINK)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.balloon_pop_game),
                        fontFamily = AbTypography.Baloo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.jigar.me.ui.view.home.screens.math_game_zone.common.howToInk(accentColors.base)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    GameButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.PINK,
                        onClick = onClose,
                        isSmall = true
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_1)) }
                    item { HowToHeading(stringResource(R.string.htp_balloon_pop_2)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_3)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_4)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_5)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_6)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_7)) }

                    item { HowToHeading(stringResource(R.string.htp_balloon_pop_8)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_9)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_10)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_11)) }

                    item { HowToHeading(stringResource(R.string.htp_balloon_pop_12)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_13)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_14)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_15)) }
                    item { HowToBullet(stringResource(R.string.htp_balloon_pop_16)) }
                }
            }
        }
    }
}

@Composable
private fun HowToBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = LocalAbColors.current.textPrimary, fontFamily = AbTypography.Baloo)
        Text(text, fontSize = 15.sp, color = LocalAbColors.current.textPrimary, fontFamily = AbTypography.Baloo)
    }
}

@Composable
private fun HowToHeading(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = AbTypography.Baloo,
        modifier = Modifier.padding(top = AppDimens.Dimens8)
    )
}

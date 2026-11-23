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
import androidx.compose.ui.res.dimensionResource
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
fun HowToPlayNumberSequenceView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) {
        windowInfo.containerSize.width.toDp()
    }
    val screenHeight = with(LocalDensity.current) {
        windowInfo.containerSize.height.toDp()
    }
    val accentColors = getButtonColors(ButtonType.PURPLE)

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
                .abHowToCard(ButtonType.PURPLE),
            contentAlignment = Alignment.Center
        ){
            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.number_sequence_puzzle),
                        fontFamily = AbTypography.Baloo,
                        fontSize = dimensionResource(id = R.dimen.textSize18).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.jigar.me.ui.view.home.screens.math_game_zone.common.howToInk(accentColors.base)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    GameButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.PURPLE,
                        onClick = onClose,
                        isSmall = true
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                // ---------- SCROLL CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {

                    item {
                        Bullet(stringResource(R.string.htp_number_sequence_puzzle_1))
                    }

                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_2),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { SubBullet(stringResource(R.string.htp_number_sequence_puzzle_3)) }
                    item { SubBullet(stringResource(R.string.htp_number_sequence_puzzle_4)) }
                    item { SubBullet(stringResource(R.string.htp_number_sequence_puzzle_5)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_6),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_7)) }
                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_8)) }
                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_9)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_10),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // ----------- Parent Friendly -----------
                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_11),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check(stringResource(R.string.htp_number_sequence_puzzle_12)) }
                    item { Detail(stringResource(R.string.htp_number_sequence_puzzle_13)) }

                    item { Check(stringResource(R.string.htp_number_sequence_puzzle_14)) }
                    item { Detail(stringResource(R.string.htp_number_sequence_puzzle_15)) }

                    item { Check(stringResource(R.string.htp_number_sequence_puzzle_16)) }
                    item { Detail(stringResource(R.string.htp_number_sequence_puzzle_17)) }

                    item { Check(stringResource(R.string.htp_number_sequence_puzzle_18)) }
                    item { Detail(stringResource(R.string.htp_number_sequence_puzzle_19)) }

                    item { Check(stringResource(R.string.htp_number_sequence_puzzle_20)) }
                    item { Detail(stringResource(R.string.htp_number_sequence_puzzle_21)) }

                    // ----------- Math Explanation -----------
                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_22),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_23)) }
                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_24)) }
                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_25)) }
                    item { Bullet(stringResource(R.string.htp_number_sequence_puzzle_26)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_number_sequence_puzzle_27),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Bullet(text: String) {
    Row(modifier = Modifier.padding(top = AppDimens.Dimens4)) {
        Text("•  ", fontWeight = FontWeight.Bold,fontFamily = AbTypography.Baloo)
        Text(text, fontSize = 16.sp,fontFamily = AbTypography.Baloo)
    }
}

@Composable
fun SubBullet(text: String) {
    Row(modifier = Modifier.padding(start = AppDimens.Dimens12, top = AppDimens.Dimens2)) {
        Text("•  ",fontFamily = AbTypography.Baloo)
        Text(text, fontSize = 16.sp,fontFamily = AbTypography.Baloo)
    }
}

@Composable
fun Check(text: String) {
    Row(modifier = Modifier.padding(top = AppDimens.Dimens6)) {
        Text("✔ ", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold,fontFamily = AbTypography.Baloo)
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp,fontFamily = AbTypography.Baloo)
    }
}

@Composable
fun Detail(text: String) {
    Text(
        text = "   $text",
        color = com.jigar.me.ui.view.ab.LocalAbColors.current.textSecondary,
        fontSize = 15.sp,
        modifier = Modifier.padding(top = AppDimens.Dimens2),
        fontFamily = AbTypography.Baloo
    )
}

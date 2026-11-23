package com.jigar.me.ui.view.home.common_ui.how_to_play

import com.jigar.me.ui.view.home.screens.math_game_zone.common.GameButton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
fun HowToPlayTargetNumberView(
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
    val accentColors = getButtonColors(ButtonType.ORANGE)

    // Dim background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {

        // Popup box
        Box(
            modifier = Modifier
                .width(screenWidth * widthMultiplier)
                .height(screenHeight * heightMultiplier)
                .abHowToCard(ButtonType.ORANGE)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.target_the_number),
                        fontFamily = AbTypography.Baloo,
                        fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.jigar.me.ui.view.home.screens.math_game_zone.common.howToInk(accentColors.base)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    GameButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.ORANGE,
                        onClick = onClose,
                        isSmall = true
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                // ---------- CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {

                    // Intro text
                    item { Bullet(stringResource(R.string.htp_target_number_b_1)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_2)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_3)) }

                    // How it works
                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_target_number_b_4)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_5)) }

                    item { Bullet(stringResource(R.string.htp_target_number_b_6)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_2),
                            fontSize = 16.sp,
                            fontFamily = AbTypography.Baloo
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_3),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    // Example section
                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_4),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            color = Color.Red,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(start = AppDimens.Dimens16),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
                        ) {
                            Text(stringResource(R.string.htp_target_number_5), fontFamily = AbTypography.Baloo)
                            Text(stringResource(R.string.htp_target_number_6), fontFamily = AbTypography.Baloo)
                            Text(stringResource(R.string.htp_target_number_7), fontFamily = AbTypography.Baloo)
                            Text(stringResource(R.string.htp_target_number_8), fontFamily = AbTypography.Baloo)

                            Text(
                                "4 × 6 = 24 ✔",
                                fontSize = 16.sp,
                                fontFamily = AbTypography.Baloo
                            )
                            Text(
                                "then 24 + 2 = 26 ✔",
                                fontSize = 16.sp,
                                fontFamily = AbTypography.Baloo
                            )
                        }
                    }

                    item { Bullet(stringResource(R.string.htp_target_number_b_7)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_8)) }

                    // Parent-friendly section
                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_9),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check(stringResource(R.string.htp_target_number_b_9)) }
                    item { Detail(stringResource(R.string.htp_target_number_b_10)) }

                    item { Check(stringResource(R.string.htp_target_number_b_11)) }
                    item { Detail(stringResource(R.string.htp_target_number_b_12)) }

                    item { Check(stringResource(R.string.htp_target_number_b_13)) }
                    item { Detail(stringResource(R.string.htp_target_number_b_14)) }

                    item { Check(stringResource(R.string.htp_target_number_b_15)) }
                    item { Detail(stringResource(R.string.htp_target_number_b_16)) }

                    item { Check(stringResource(R.string.htp_target_number_b_17)) }
                    item { Detail(stringResource(R.string.htp_target_number_b_18)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_10),
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    // Math-friendly explanation
                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_11),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_target_number_b_19)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_20)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_21)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_22)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_23)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_12),
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_target_number_b_24)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_25)) }
                    item { Bullet(stringResource(R.string.htp_target_number_b_26)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_target_number_13),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }
                }
            }
        }
    }
}

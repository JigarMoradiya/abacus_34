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
fun HowToPlayMathPyramidView(
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
    val accentColors = getButtonColors(ButtonType.RED)

    // Dim background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {

        // Content box
        Box(
            modifier = Modifier
                .width(screenWidth * widthMultiplier)
                .height(screenHeight * heightMultiplier)
                .abHowToCard(ButtonType.RED)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.math_pyramid),
                        fontFamily = AbTypography.Baloo,
                        fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.jigar.me.ui.view.home.screens.math_game_zone.common.howToInk(accentColors.base)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    GameButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.RED,
                        onClick = onClose,
                        isSmall = true
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                // ---------- SCROLL AREA ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {

                    // FIRST BULLETS
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_1)) }

                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_2)) }

                    // SECTION TITLE
                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_3)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_4)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_5)) }

                    // Example Title
                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_2),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            color = Color.Red,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    // Example Pyramid
                    item {
                        Column(
                            modifier = Modifier.padding(start = AppDimens.Dimens16),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
                        ) {
                            Text("    [ ? ]", fontFamily = AbTypography.Baloo)
                            Text("[ 4 ] [ 6 ]", fontFamily = AbTypography.Baloo)
                            Text(stringResource(R.string.htp_math_pyramid_3), fontFamily = AbTypography.Baloo)
                        }
                    }

                    // Your Job
                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_4),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_6)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_7)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_5),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    // PARENT-FRIENDLY SECTION
                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_6),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check(stringResource(R.string.htp_math_pyramid_b_8)) }
                    item { Detail(stringResource(R.string.htp_math_pyramid_b_9)) }

                    item { Check(stringResource(R.string.htp_math_pyramid_b_10)) }
                    item { Detail(stringResource(R.string.htp_math_pyramid_b_11)) }

                    item { Check(stringResource(R.string.htp_math_pyramid_b_12)) }
                    item { Detail(stringResource(R.string.htp_math_pyramid_b_13)) }

                    item { Check(stringResource(R.string.htp_math_pyramid_b_14)) }
                    item { Detail(stringResource(R.string.htp_math_pyramid_b_15)) }

                    item { Check(stringResource(R.string.htp_math_pyramid_b_16)) }
                    item { Detail(stringResource(R.string.htp_math_pyramid_b_17)) }

                    // MATH-FRIENDLY SECTION
                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_7),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AbTypography.Baloo,
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_18)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_19)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_20)) }
                    item { Bullet(stringResource(R.string.htp_math_pyramid_b_21)) }

                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_8),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.htp_math_pyramid_9),
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

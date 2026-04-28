package com.jigar.me.ui.view.home.common_ui.how_to_play

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun HowToPlayNumberSequenceView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

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
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20))
                .padding(AppDimens.Dimens16),
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
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = dimensionResource(id = R.dimen.textSize18).value.sp,
                        color = ColorPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.close),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
                            .clickable { onClose() }
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                // ---------- SCROLL CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {

                    item {
                        Bullet("You get a puzzle grid with one empty box and lots of number tiles.")
                    }

                    item {
                        Text(
                            text = "Depending on the level, the grid size can be:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { SubBullet("3×3 → numbers 1 to 8") }
                    item { SubBullet("4×4 → numbers 1 to 15") }
                    item { SubBullet("5×5 → numbers 1 to 24") }

                    item {
                        Text(
                            text = "👉 How you play:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet("Tap a number only if it is next to the empty box (up, down, left, or right).") }
                    item { Bullet("That number will slide into the empty space.") }
                    item { Bullet("Keep sliding numbers until they are in order from 1 upwards.") }

                    item {
                        Text(
                            text = "It’s like a fun sliding puzzle where every move brings you closer to the correct sequence!",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // ----------- Parent Friendly -----------
                    item {
                        Text(
                            text = "👨‍👩‍👧 How It Helps:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check("Builds strong number sense") }
                    item { Detail("Kids naturally understand before/after in number order.") }

                    item { Check("Develops logical and step-by-step thinking") }
                    item { Detail("Each move must be planned — early coding mindset.") }

                    item { Check("Improves focus and memory") }
                    item { Detail("Kids track sliding tiles and recall positions.") }

                    item { Check("Enhances problem-solving") }
                    item { Detail("Requires strategy, not guessing.") }

                    item { Check("Boosts spatial awareness") }
                    item { Detail("Kids visualize movement on a grid.") }

                    // ----------- Math Explanation -----------
                    item {
                        Text(
                            text = "❤️ Why This Game Matters:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet("Numbers follow a sequence") }
                    item { Bullet("Patterns help solve problems") }
                    item { Bullet("Good solutions come from planning") }
                    item { Bullet("Mistakes are part of the fun learning journey") }

                    item {
                        Text(
                            text = "It is one of the best early-math brain games for developing strong thinking skills without feeling like homework.",
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
        Text("•  ", fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_regular)))
        Text(text, fontSize = 16.sp,fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
fun SubBullet(text: String) {
    Row(modifier = Modifier.padding(start = AppDimens.Dimens12, top = AppDimens.Dimens2)) {
        Text("•  ",fontFamily = FontFamily(Font(R.font.font_regular)))
        Text(text, fontSize = 16.sp,fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
fun Check(text: String) {
    Row(modifier = Modifier.padding(top = AppDimens.Dimens6)) {
        Text("✔ ", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp,fontFamily = FontFamily(Font(R.font.font_bold)))
    }
}

@Composable
fun Detail(text: String) {
    Text(
        text = "   $text",
        color = Color.DarkGray,
        fontSize = 15.sp,
        modifier = Modifier.padding(top = AppDimens.Dimens2),
        fontFamily = FontFamily(Font(R.font.font_regular))
    )
}

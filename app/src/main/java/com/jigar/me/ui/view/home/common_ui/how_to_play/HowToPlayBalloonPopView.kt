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
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun HowToPlayBalloonPopView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }

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
                .padding(AppDimens.Dimens16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.balloon_pop_game),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = 18.sp,
                        color = ColorPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.close),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)),
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
                            .clickable { onClose() }
                    )
                }

                Spacer(modifier = Modifier.height(AppDimens.Dimens8))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {
                    item { HowToBullet("Balloons float up with numbers on them — pop the right one!") }
                    item { HowToHeading("👉 How it works:") }
                    item { HowToBullet("A target shows at the top, like: Make 10 with 7.") }
                    item { HowToBullet("Find the balloon that completes the sum — 7 needs 3!") }
                    item { HowToBullet("Tap it to POP it and earn points.") }
                    item { HowToBullet("Pop 5 in a row and the target changes to a new number.") }
                    item { HowToBullet("Wrong balloon? It just wiggles — try again!") }

                    item { HowToHeading("🧒 Example:") }
                    item { HowToBullet("Target: Make 10 with 6") }
                    item { HowToBullet("Balloons: 2, 4, 7, 4, 9") }
                    item { HowToBullet("6 + 4 = 10 ✔ — pop the 4!") }

                    item { HowToHeading("👨‍👩‍👧 How It Helps:") }
                    item { HowToBullet("Builds number bond fluency — pairs that make 5, 10 and 20.") }
                    item { HowToBullet("Prepares for abacus formulas — the Small Friend & Big Friend pairs.") }
                    item { HowToBullet("Trains quick recognition with gentle motion.") }
                    item { HowToBullet("No punishment for mistakes — wrong taps just wiggle.") }
                }
            }
        }
    }
}

@Composable
private fun HowToBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun HowToHeading(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)),
        modifier = Modifier.padding(top = AppDimens.Dimens8)
    )
}

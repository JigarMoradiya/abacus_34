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
fun HowToPlayEquationMatchView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.width(screenWidth * widthMultiplier).height(screenHeight * heightMultiplier)
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20)).padding(AppDimens.Dimens16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.equation_match),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, color = ColorPrimary)
                    Spacer(Modifier.weight(1f))
                    Text(stringResource(R.string.close), fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)), color = Color.Black,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8).clickable { onClose() })
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { EqBullet("Tap a card to flip it over and see what's hidden.") }
                    item { EqHeading("👉 How it works:") }
                    item { EqBullet("Flip two cards. If they make a matching pair, they stay up!") }
                    item { EqBullet("A pair can be an equation and its answer — like 3×4 and 12.") }
                    item { EqBullet("Sometimes it's two equations that equal the same number — like 3×4 and 6+6.") }
                    item { EqBullet("If the cards don't match, they flip back — remember where they were!") }
                    item { EqBullet("Match every pair to finish the board.") }
                    item { EqHeading("🧒 Example:") }
                    item { EqBullet("You flip 3×4 … then flip 12 → they match! ✔ (3×4 = 12)") }
                    item { EqBullet("Match pairs in a row for a bigger score (🔥 ×2, ×3, ×4).") }
                    item { EqHeading("👨‍👩‍👧 How It Helps:") }
                    item { EqBullet("Builds number sense — an equation and its answer are the same value.") }
                    item { EqBullet("Sharpens quick arithmetic with +, −, × and ÷.") }
                    item { EqBullet("Trains memory — remembering where cards are strengthens focus.") }
                }
            }
        }
    }
}

@Composable
private fun EqBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun EqHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

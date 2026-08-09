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
fun HowToPlayMathBingoView(
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
                    Text(stringResource(R.string.math_bingo),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, color = ColorPrimary)
                    Spacer(Modifier.weight(1f))
                    Text(stringResource(R.string.close), fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)), color = Color.Black,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8).clickable { onClose() })
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { MbBullet("Classic bingo — but the caller shouts MATH! 🎱") }
                    item { MbHeading("👉 How it works:") }
                    item { MbBullet("Your card has 16 answer numbers.") }
                    item { MbBullet("The caller announces an equation, like 7 × 8.") }
                    item { MbBullet("Work it out and tap the answer (56!) on your card — it gets a ⭐ stamp.") }
                    item { MbBullet("Fill a whole row, column, or diagonal → BINGO! Big bonus + a fresh card.") }
                    item { MbBullet("Score as much as you can before the timer runs out.") }
                    item { MbHeading("🧒 Example:") }
                    item { MbBullet("Caller says 6 + 7 … you tap 13 → ⭐") }
                    item { MbBullet("That completes your row → 🎉 BINGO! +50 points!") }
                    item { MbHeading("👨‍👩‍👧 How It Helps:") }
                    item { MbBullet("Scanning the card for answers strengthens number recognition.") }
                    item { MbBullet("Every call is a fresh mental-math problem under friendly pressure.") }
                    item { MbBullet("The bingo hunt keeps kids playing — and calculating — longer.") }
                }
            }
        }
    }
}

@Composable
private fun MbBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun MbHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

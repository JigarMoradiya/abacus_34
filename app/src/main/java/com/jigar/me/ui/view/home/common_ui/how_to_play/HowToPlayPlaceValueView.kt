package com.jigar.me.ui.view.home.common_ui.how_to_play

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

@Composable
fun HowToPlayPlaceValueView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }
    val accentColors = getButtonColors(ButtonType.GREEN)

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.width(screenWidth * widthMultiplier).height(screenHeight * heightMultiplier)
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20))
                .border(AppDimens.Dimens4, accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20))
                .padding(AppDimens.Dimens16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.place_value),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColors.base)
                    Spacer(Modifier.weight(1f))
                    KidsActionButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.GREEN,
                        onClick = onClose,
                        isSmall = true
                    )
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { PvBullet("Build the number from its pieces! 🏗️") }
                    item { PvHeading("👉 How it works:") }
                    item { PvBullet("Purple chips show a number in pieces — like 400 + 70 + 2.") }
                    item { PvBullet("Type the whole number on the keypad: 472!") }
                    item { PvBullet("Each slot shows its place: O = ones, T = tens, H = hundreds, Th = thousands.") }
                    item { PvBullet("Watch out for missing pieces! 400 + 2 is 402 — don't forget the ZERO in the tens place!") }
                    item { PvBullet("Keep answering until the timer runs out.") }
                    item { PvHeading("🧒 Example:") }
                    item { PvBullet("300 + 50 + 9 → type 3, 5, 9 → 359 ✔") }
                    item { PvBullet("500 + 6 → type 5, 0, 6 → 506 ✔ (zero saves the day!)") }
                    item { PvBullet("Answer right in a row for a bigger score (🔥 ×2, ×3).") }
                    item { PvHeading("👨‍👩‍👧 How It Helps:") }
                    item { PvBullet("Place value is the foundation of ALL abacus math.") }
                    item { PvBullet("Zero-trap numbers build real understanding, not just counting.") }
                    item { PvBullet("Higher levels grow to 4 and 5 digit numbers.") }
                }
            }
        }
    }
}

@Composable
private fun PvBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun PvHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

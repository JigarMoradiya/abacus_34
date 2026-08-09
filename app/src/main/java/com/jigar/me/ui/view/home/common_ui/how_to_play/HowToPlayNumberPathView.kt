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
fun HowToPlayNumberPathView(
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
                    Text(stringResource(R.string.number_path),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, color = ColorPrimary)
                    Spacer(Modifier.weight(1f))
                    Text(stringResource(R.string.close), fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)), color = Color.Black,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8).clickable { onClose() })
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { NpBullet("Number Path is a math maze — walk your buddy from the start to the target! 🐻") }
                    item { NpHeading("👉 How it works:") }
                    item { NpBullet("Your buddy starts on the blue tile with a number.") }
                    item { NpBullet("Tap a tile next to your buddy to hop onto it — its math happens right away (+3 adds 3, ×2 doubles...).") }
                    item { NpBullet("Watch the number bubble over your buddy's head change with every hop.") }
                    item { NpBullet("Reach the 🎯 goal tile with EXACTLY the target number to win!") }
                    item { NpBullet("Arrive with the wrong number and the goal bounces you back — rethink your route!") }
                    item { NpBullet("You can't step on the same tile twice. Tap your previous tile (or Undo) to walk back.") }
                    item { NpHeading("🧒 Example:") }
                    item { NpBullet("Start at 5 … hop +3 → 8 … hop −2 → 6 … the goal wants 6 → you win! ✔") }
                    item { NpBullet("Stuck? A 💡 hint makes the next correct tile glow (3 per level).") }
                    item { NpHeading("⭐ Levels & stars:") }
                    item { NpBullet("Follow the road map — finish a level to unlock the next one.") }
                    item { NpBullet("Solve fast with no hints to earn all 3 stars!") }
                    item { NpHeading("👨‍👩‍👧 How It Helps:") }
                    item { NpBullet("Builds mental-math chains — exactly what abacus anzan practice trains.") }
                    item { NpBullet("Wrong branches teach planning ahead, not just calculating.") }
                    item { NpBullet("Higher levels sneak in ×, ÷ and bigger numbers.") }
                }
            }
        }
    }
}

@Composable
private fun NpBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun NpHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

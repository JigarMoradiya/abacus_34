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
fun HowToPlayClockMasterView(
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
                    Text(stringResource(R.string.clock_master),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, color = ColorPrimary)
                    Spacer(Modifier.weight(1f))
                    Text(stringResource(R.string.close), fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.font_medium)), color = Color.Black,
                        modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8).clickable { onClose() })
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { CmkBullet("Read the clock like a real time master! 🕐") }
                    item { CmkHeading("👉 How it works:") }
                    item { CmkBullet("The clock's hands swing to a new time.") }
                    item { CmkBullet("The SHORT dark hand shows the hour, the LONG blue hand shows the minutes.") }
                    item { CmkBullet("Pick the matching digital time from the four cards.") }
                    item { CmkBullet("Wrong picks glow the correct answer for a moment — learn and go!") }
                    item { CmkBullet("Answer as many as you can before the timer runs out.") }
                    item { CmkHeading("🧒 Example:") }
                    item { CmkBullet("Short hand at 3, long hand at 12 → 3:00 ✔") }
                    item { CmkBullet("Short hand past 6, long hand at 6 → 6:30 ✔") }
                    item { CmkBullet("Answer right in a row for a bigger score (🔥 ×2, ×3).") }
                    item { CmkHeading("⏰ Levels:") }
                    item { CmkBullet("Easy: o'clock times · Medium: half hours · Hard: quarter hours · Very Hard: 5-minute times!") }
                    item { CmkHeading("👨‍👩‍👧 How It Helps:") }
                    item { CmkBullet("Telling time is a life skill schools test early.") }
                    item { CmkBullet("Sneaky wrong options (swapped hands!) train careful reading.") }
                }
            }
        }
    }
}

@Composable
private fun CmkBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun CmkHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

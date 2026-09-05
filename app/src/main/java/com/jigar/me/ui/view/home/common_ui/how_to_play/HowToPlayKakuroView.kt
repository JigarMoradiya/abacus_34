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
fun HowToPlayKakuroView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(LocalDensity.current) { windowInfo.containerSize.width.toDp() }
    val screenHeight = with(LocalDensity.current) { windowInfo.containerSize.height.toDp() }
    val accentColors = getButtonColors(ButtonType.TEAL)

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
                    Text(stringResource(R.string.kakuro),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColors.base)
                    Spacer(Modifier.weight(1f))
                    KidsActionButton(
                        text = stringResource(R.string.close),
                        type = ButtonType.TEAL,
                        onClick = onClose,
                        isSmall = true
                    )
                }
                Spacer(Modifier.height(AppDimens.Dimens8))
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = AppDimens.Dimens16)) {
                    item { KkBullet("Kakuro is a crossword of SUMS! 🧮") }
                    item { KkHeading("👉 How it works:") }
                    item { KkBullet("Dark cells show clue numbers — the top-right number is the sum going RIGHT, the bottom-left is the sum going DOWN.") }
                    item { KkBullet("Fill the white boxes with digits 1–9 so each line adds up to its clue.") }
                    item { KkBullet("Secret rule: a digit can't repeat inside the same line!") }
                    item { KkBullet("A finished line turns green when right, red when wrong.") }
                    item { KkHeading("🧒 Example:") }
                    item { KkBullet("Clue 4 over two boxes → only 1+3 works (2+2 repeats!)") }
                    item { KkBullet("Clue 16 over two boxes → 7+9. Clue 3 → 1+2. Small clues are your friends!") }
                    item { KkBullet("Stuck? A 💡 hint fills one correct box (3 per level).") }
                    item { KkHeading("⭐ Levels & stars:") }
                    item { KkBullet("Follow the road map — finish a level to unlock the next one.") }
                    item { KkBullet("Solve fast with no hints to earn all 3 stars!") }
                    item { KkHeading("👨‍👩‍👧 How It Helps:") }
                    item { KkBullet("Addition facts get a real workout — every line is a sum puzzle.") }
                    item { KkBullet("The no-repeat rule builds logical elimination, like Sudoku with math.") }
                    item { KkBullet("Crossing lines teach that one digit must satisfy two sums at once.") }
                }
            }
        }
    }
}

@Composable
private fun KkBullet(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = AppDimens.Dimens4)) {
        Text("•  ", fontSize = 16.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_bold)))
        Text(text, fontSize = 15.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.font_regular)))
    }
}

@Composable
private fun KkHeading(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.font_bold)), modifier = Modifier.padding(top = AppDimens.Dimens8))
}

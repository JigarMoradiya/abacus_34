package com.jigar.me.ui.view.home.common_ui.how_to_play

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
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20))
                .border(AppDimens.Dimens4, accentColors.gradient, RoundedCornerShape(AppDimens.Dimens20))
                .padding(AppDimens.Dimens16)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.math_pyramid),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = dimensionResource(R.dimen.textSize18).value.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColors.base
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    KidsActionButton(
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
                    item { Bullet("Math Pyramid is a fun number-building game where every number helps create the number above it!") }

                    item { Bullet("You will see a pyramid made of rows of boxes. Depending on the level, the pyramid can have 2 to 6 layers.") }

                    // SECTION TITLE
                    item {
                        Text(
                            text = "👉 How it works:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet("Look at the two numbers at the bottom.") }
                    item { Bullet("Add them together.") }
                    item { Bullet("Their sum goes in the box above.") }

                    // Example Title
                    item {
                        Text(
                            text = "Example:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
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
                            Text("    [ ? ]", fontFamily = FontFamily(Font(R.font.font_regular)))
                            Text("[ 4 ] [ 6 ]", fontFamily = FontFamily(Font(R.font.font_regular)))
                            Text("Because 4 + 6 = 10, the top box should be 10.", fontFamily = FontFamily(Font(R.font.font_regular)))
                        }
                    }

                    // Your Job
                    item {
                        Text(
                            text = "👉 Your job:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet("Fill in all the empty boxes by adding the numbers below.") }
                    item { Bullet("Complete the puzzle when all boxes are filled correctly from bottom to top.") }

                    item {
                        Text(
                            text = "It’s like building a number tower — each block supports the block above!",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    // PARENT-FRIENDLY SECTION
                    item {
                        Text(
                            text = "👨‍👩‍👧 How It Helps:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check("Strengthens mental addition") }
                    item { Detail("Kids repeatedly add small and medium numbers — building fluency.") }

                    item { Check("Develops step-by-step reasoning") }
                    item { Detail("Kids see how lower numbers create upper numbers.") }

                    item { Check("Builds problem-solving confidence") }
                    item { Detail("Each correct box gives instant feedback — encouraging independence.") }

                    item { Check("Enhances concentration") }
                    item { Detail("Kids stay focused to complete all layers accurately.") }

                    item { Check("Supports number relationships") }
                    item { Detail("Children understand how numbers combine and grow — essential for algebra.") }

                    // MATH-FRIENDLY SECTION
                    item {
                        Text(
                            text = "❤️ Why This Game Matters:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet("Children see how two values combine to create a new value") }
                    item { Bullet("They practice addition & subtraction patterns, not random sums") }
                    item { Bullet("They understand hierarchy — base numbers build higher numbers") }
                    item { Bullet("The pyramid format helps visualize how parts create a whole") }

                    item {
                        Text(
                            text = "This prepares them for concepts like Pascal’s Triangle, number bonds, and arithmetic patterns.",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    item {
                        Text(
                            text = "It’s a great way to build strong early math skills while solving a fun and satisfying pyramid puzzle.",
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

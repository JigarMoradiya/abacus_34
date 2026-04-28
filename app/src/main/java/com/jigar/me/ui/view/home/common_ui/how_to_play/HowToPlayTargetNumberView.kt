package com.jigar.me.ui.view.home.common_ui.how_to_play

import androidx.compose.foundation.background
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
fun HowToPlayTargetNumberView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Dim background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(AppDimens.Dimens16),
        contentAlignment = Alignment.Center
    ) {

        // Popup box
        Box(
            modifier = Modifier
                .width(screenWidth * widthMultiplier)
                .height(screenHeight * heightMultiplier)
                .background(Color.White, RoundedCornerShape(AppDimens.Dimens20))
                .padding(AppDimens.Dimens16)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.target_the_number),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = dimensionResource(R.dimen.textSize18).value.sp,
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

                // ---------- CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppDimens.Dimens16)
                ) {

                    // Intro text
                    item { Bullet("In this game, you become a little math explorer!") }
                    item { Bullet("You get a target number and a set of smaller numbers.") }
                    item { Bullet("Your mission is to use math operations to reach the target number.") }

                    // How it works
                    item {
                        Text(
                            text = "👉 How it works:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item { Bullet("You will see a target number (for example: 25).") }
                    item { Bullet("You will also see some numbers you can use, like: 5, 5, 6.") }

                    item { Bullet("You can use math operations such as:") }

                    item {
                        Text(
                            text = "➕ Addition, ➖ Subtraction, ✖️ Multiplication, ➗ Division",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_regular))
                        )
                    }

                    item {
                        Text(
                            text = "Combine the given numbers using these operations to make the exact target.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = AppDimens.Dimens4),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    // Example section
                    item {
                        Text(
                            text = "🧒 Example:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            color = Color.Red,
                            modifier = Modifier.padding(top = AppDimens.Dimens8)
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(start = AppDimens.Dimens16),
                            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
                        ) {
                            Text("Target: 26", fontFamily = FontFamily(Font(R.font.font_regular)))
                            Text("Numbers: 4, 2 and 6", fontFamily = FontFamily(Font(R.font.font_regular)))
                            Text("Operations : +  -  ×", fontFamily = FontFamily(Font(R.font.font_regular)))
                            Text("You can do:", fontFamily = FontFamily(Font(R.font.font_regular)))

                            Text(
                                "4 × 6 = 24 ✔",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                            Text(
                                "then 24 + 2 = 26 ✔",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.font_bold))
                            )
                        }
                    }

                    item { Bullet("Try different combinations until you reach the target.") }
                    item { Bullet("When you hit the target correctly — you win!") }

                    // Parent-friendly section
                    item {
                        Text(
                            text = "👨‍👩‍👧 How It Helps:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Check("Strengthens arithmetic fluency") }
                    item { Detail("Kids practice all four operations in a hands-on way.") }

                    item { Check("Encourages flexible thinking") }
                    item { Detail("Kids see there are multiple ways to reach an answer.") }

                    item { Check("Supports creative problem-solving") }
                    item { Detail("Experimentation leads to strategic thinking.") }

                    item { Check("Enhances understanding of number behavior") }
                    item { Detail("Children see how operations change values.") }

                    item { Check("Builds confidence") }
                    item { Detail("Reaching the target gives a sense of achievement.") }

                    item {
                        Text(
                            text = "This game makes math operations meaningful, not just memorized.",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    // Math-friendly explanation
                    item {
                        Text(
                            text = "❤️ Why This Game Matters:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens12)
                        )
                    }

                    item { Bullet("Understanding operation effects (how numbers interact).") }
                    item { Bullet("Practicing mental math in real scenarios.") }
                    item { Bullet("Recognizing number patterns and relationships.") }
                    item { Bullet("Breaking down a goal into smaller steps.") }
                    item { Bullet("Developing strategic reasoning like real word problems.") }

                    item {
                        Text(
                            text = "It trains the brain to think:",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = AppDimens.Dimens4)
                        )
                    }

                    item { Bullet("“Which operation should I use?”") }
                    item { Bullet("“What combination works?”") }
                    item { Bullet("“How do I reach the target logically?”") }

                    item {
                        Text(
                            text = "This is an essential early path toward algebra and problem-solving mastery.",
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

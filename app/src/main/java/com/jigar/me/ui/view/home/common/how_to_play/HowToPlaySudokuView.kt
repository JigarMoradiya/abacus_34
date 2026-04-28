package com.jigar.me.ui.view.home.common.how_to_play

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

@Composable
fun HowToPlaySudokuView(
    widthMultiplier: Float = 0.8f,
    heightMultiplier: Float = 0.9f,
    onClose: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Dimmed background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        // Content box
        Box(
            modifier = Modifier
                .width(screenWidth * widthMultiplier)
                .height(screenHeight * heightMultiplier)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ---------- HEADER ----------
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.sudoku),
                        fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                        fontSize = dimensionResource(R.dimen.textSize18).value.sp,
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
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { onClose() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ---------- SCROLL CONTENT ----------
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {

                    // Intro
                    item {
                        Bullet("Sudoku is a fun number puzzle where you fill in the empty boxes using your thinking skills!")
                    }

                    // Grid sizes
                    item {
                        Text(
                            text = "We have different grid sizes:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item { SubBullet("4×4 (Beginner)") }
                    item { SubBullet("6×6 (Intermediate)") }
                    item { SubBullet("9×9 (Expert)") }

                    // How to play
                    item {
                        Text(
                            text = "👉 How you play:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item { Bullet("Each row must have all different numbers.") }
                    item { Bullet("Each column must have all different numbers.") }
                    item { Bullet("Each small block must also have all different numbers.") }
                    item { Bullet("Tap an empty box and choose a number that does not repeat.") }
                    item { Bullet("You win when the entire grid follows Sudoku rules!") }

                    item {
                        Text(
                            text = "Sudoku is like a secret number pattern game where you become a little detective 🕵️‍♂️.",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Parent friendly
                    item {
                        Text(
                            text = "👨‍👩‍👧 How It Helps:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    item { Check("Logical reasoning") }
                    item { Detail("Kids analyze clues and figure out where numbers can or cannot go.") }

                    item { Check("Decision-making skills") }
                    item { Detail("They test possibilities and choose the correct one based on rules.") }

                    item { Check("Patience and focus") }
                    item { Detail("Completing a Sudoku needs calm thinking — great for attention span development.") }

                    item { Check("Visual and pattern recognition") }
                    item { Detail("Children begin to notice patterns across rows and grids.") }

                    item { Check("Confidence building") }
                    item { Detail("Solving tough puzzles builds confidence and perseverance.") }

                    item {
                        Text(
                            text = "Sudoku teaches how to think, not just how to calculate.",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Math friendly
                    item {
                        Text(
                            text = "❤️ Why This Game Matters:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.font_bold)),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    item { Bullet("Learn structured thinking") }
                    item { Bullet("Understand rules and constraints") }
                    item { Bullet("Strengthen working memory") }
                    item { Bullet("Develop deductive reasoning (If this number cannot go here, it must go there!)") }

                    item {
                        Text(
                            text = "It supports higher-level math learning because it trains the brain to organize information, spot patterns, and solve problems logically.",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    item {
                        Text(
                            text = "Sudoku is not about speed — it's about smart thinking. It turns kids into little mathematicians!",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.font_semibold)),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

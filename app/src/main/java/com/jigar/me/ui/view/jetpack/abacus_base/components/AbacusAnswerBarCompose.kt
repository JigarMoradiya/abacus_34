package com.jigar.me.ui.view.jetpack.abacus_base.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusTheme
import com.jigar.me.ui.view.jetpack.abacus_base.ColorPresetModel
import com.jigar.me.utils.AppConstants

@Composable
fun AbacusAnswerBarCompose(
    answer: String,
    theme: String,
    screenType: String,
    isDisplayAbacusNumber: Boolean,
    abacusType: String? = null,
    isNextButtonEnable: Boolean,
    onReset: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = 1.0

    val horizontalPadding = ((if (screenType == "ccm") 8 else 16) * scale).dp
    val outFrameHeight = ((if (screenType == "ccm") 36 else 48) * scale).dp
    val iconDimensions = ((if (screenType == "ccm") 14 else 18) * scale).dp
    val centerBoxValueTextSize = ((if (screenType == "ccm") 18 else 24) * scale).sp
    val centerBoxHeight = ((if (screenType == "ccm") 48 else 60) * scale).dp

    val theme = AbacusTheme.colorPreset(theme)

    val nextOpacity = when {
        screenType == AppConstants.AbacusScreen.screenTypeFreeMode -> 0.2f
        abacusType == AppConstants.apiParams.answerFormalExam || screenType == "ccm" || isNextButtonEnable -> 1f
        else -> 0.5f
    }

    val resetOpacity = when {
        answer == "0" -> 0.5f
        screenType == "exercise" || isNextButtonEnable -> 1f
        else -> 1f
    }
    val density = LocalDensity.current
    SubcomposeLayout(
        modifier = modifier.zIndex(20f)
    ) { constraints ->

        // 1️⃣ FIRST PASS: Measure CONTENT (Reset + CenterBox + Next)
        val contentMeasurables = subcompose("content") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(horizontalPadding / 2)
            ) {
                ResetButton(resetOpacity, iconDimensions, onReset)
                CenterBox(
                    centerBoxHeight, theme, horizontalPadding,
                    isDisplayAbacusNumber, answer, centerBoxValueTextSize
                )
                NextButton(nextOpacity, iconDimensions, screenType, abacusType, onNext)
            }
        }

        val contentPlaceables = contentMeasurables.map { it.measure(constraints) }
        val contentWidth = contentPlaceables.maxOf { it.width }
        val contentHeight = contentPlaceables.maxOf { it.height }

        // Animate width in dp
        val animatedWidthDp = with(density) { contentWidth.toDp() }

        // 2️⃣ SECOND PASS: Measure BACKGROUND capsule
        val backgroundMeasurable = subcompose("background") {
            Box(
                modifier = Modifier
                    .width(animatedWidthDp)
                    .height(outFrameHeight)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(100.dp),
                        clip = false
                    )
                    .background(theme.buttonColor, RoundedCornerShape(100.dp))
                    .border(4.dp, theme.abacusCenterGradient, RoundedCornerShape(100.dp))
            )
        }.first()

        val backgroundPlaceable = backgroundMeasurable.measure(constraints)

        val layoutWidth = maxOf(backgroundPlaceable.width, contentWidth)
        val layoutHeight = maxOf(backgroundPlaceable.height, contentHeight)

        layout(layoutWidth, layoutHeight) {

            // Place background capsule CENTERED
            backgroundPlaceable.placeRelative(
                x = (layoutWidth - backgroundPlaceable.width) / 2,
                y = (layoutHeight - backgroundPlaceable.height) / 2
            )

            // Place content CENTERED
            var xPos = (layoutWidth - contentWidth) / 2
            contentPlaceables.forEach { child ->
                child.placeRelative(x = xPos, y = (layoutHeight - child.height) / 2)
                xPos += child.width
            }
        }
    }
}


@Composable
fun ResetButton(
    resetOpacity: Float,
    iconDimensions: Dp,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(start = 24.dp)
            .clickable(enabled = resetOpacity > 0.5f) { onReset() }
            .alpha(resetOpacity),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconDimensions)
        )
        Text(
            text = "Reset",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 10.sp,
        )
    }
}

@Composable
fun CenterBox(
    centerBoxHeight: Dp,
    theme: ColorPresetModel,
    horizontalPadding: Dp,
    isDisplayAbacusNumber: Boolean,
    answer: String,
    centerBoxValueTextSize: TextUnit
) {
    Column(
        modifier = Modifier
            .height(centerBoxHeight)
            .requiredWidthIn(min = 140.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(100.dp),  // ⭐ Rounded shadow
                clip = false                          // (same as Swift no clipping)
            )
            .background(theme.displayBGColor, RoundedCornerShape(8.dp))
            .border(
                width = 3.dp,
                color = theme.displayBorderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Current Input",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 10.sp
        )

        if (isDisplayAbacusNumber) {
            Text(
                text = answer,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = centerBoxValueTextSize,
                lineHeight = centerBoxValueTextSize
            )
        } else {
            Text("-", color = Color.White, fontSize = 10.sp,lineHeight = 11.sp)
            Text("Hide From Setting", color = Color.White, fontSize = 9.sp, lineHeight = 10.sp)
        }
    }
}

@Composable
fun NextButton(
    nextOpacity: Float,
    iconDimensions: Dp,
    screenType: String,
    abacusType: String?,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(end = 24.dp)
            .alpha(nextOpacity)
            .clickable(enabled = nextOpacity > 0.2f) { onNext() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (screenType != "ccm" && abacusType != AppConstants.apiParams.answerFormalExam) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconDimensions)
            )
        }

        Text(
            text = when {
                abacusType == AppConstants.apiParams.answerFormalExam -> "Submit"
                screenType == "ccm" -> "Check"
                else -> "Next"
            },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 10.sp,
        )
    }
}

package com.jigar.me.ui.view.base.abacus_base.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.base.abacus_base.AbacusTheme
import com.jigar.me.ui.view.base.abacus_base.ColorPresetModel
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
    val isSmall = screenType == AppConstants.AbacusScreen.screenTypeCCM || screenType == AppConstants.AbacusScreen.screenTypeExercise
    val scale = when {
        DeviceInfo.isLargeTablet -> if (isSmall) 1.35 else 1.5
        DeviceInfo.isTablet -> if (isSmall) 1.2 else 1.3
        else -> 1.0
    }
    val horizontalPadding = ((if (isSmall) 8 else 16) * scale).dp
    val outFrameHeight = ((if (isSmall) 36 else 48) * scale).dp
    val iconDimensions = ((if (isSmall) 14 else 18) * scale).dp
    val buttonLabelTextSize = ((if (isSmall) 10 else 11)).sp.scaled()
    val buttonLabelLineHeight = ((if (isSmall) 12 else 13)).sp.scaled()
    val centerBoxValueTextSize = ((if (isSmall) 18 else 24)).sp.scaled()
    val centerBoxTitleTextSize = ((if (isSmall) 9 else 10)).sp.scaled()
    val centerBoxTitleLineHeight = ((if (isSmall) 10 else 10)).sp.scaled()
    val centerBoxHiddenTextSize = ((if (isSmall) 8 else 9)).sp.scaled()
    val centerBoxHiddenLineHeight = ((if (isSmall) 9 else 10)).sp.scaled()
    val centerBoxHeight = ((if (isSmall) 48 else 60) * scale).dp
    val centerBoxMinWidth = ((if (isSmall) 100 else 140) * scale).dp
    val backgroundBoxBorder = ((if (isSmall) 2 else 4) * scale).dp
    val backgroundBoxPadding = ((if (isSmall) 16 else 24) * scale).dp

    val theme = AbacusTheme.colorPreset(theme)

    val nextOpacity = when {
        screenType == AppConstants.AbacusScreen.screenTypeFreeMode -> 0.2f
        abacusType == AppConstants.apiParams.answerFormalExam || screenType == AppConstants.AbacusScreen.screenTypeCCM || isNextButtonEnable -> 1f
        else -> 0.5f
    }

    val resetOpacity = when {
        answer == "0" -> 0.5f
        screenType == "exercise" || abacusType == AppConstants.apiParams.answerFormalExam -> 1f
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
                ResetButton(
                    resetOpacity,
                    iconDimensions,
                    onReset,
                    backgroundBoxPadding,
                    buttonLabelTextSize,
                    buttonLabelLineHeight
                )
                CenterBox(centerBoxMinWidth,
                    centerBoxHeight, theme, horizontalPadding,
                    isDisplayAbacusNumber, answer, centerBoxValueTextSize,
                    centerBoxTitleTextSize, centerBoxTitleLineHeight,
                    centerBoxHiddenTextSize, centerBoxHiddenLineHeight
                )
                Box(
                    contentAlignment = Alignment.Center
                ) {

                    NextButton(
                        nextOpacity,
                        iconDimensions,
                        screenType,
                        abacusType,
                        onNext,
                        backgroundBoxPadding,
                        buttonLabelTextSize,
                        buttonLabelLineHeight
                    )

                    if (screenType == AppConstants.AbacusScreen.screenTypeAbacusPractice){
                        HandLeftRightIndicator(
                            isVisible = isNextButtonEnable,
                            modifier = Modifier.offset(x = AppDimens.Dimens28)
                        )
                    }
                }
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
                        elevation = AppDimens.Dimens8,
                        shape = RoundedCornerShape(AppDimens.Dimens100),
                        clip = false
                    )
                    .background(theme.buttonColor, RoundedCornerShape(AppDimens.Dimens100))
                    .border(backgroundBoxBorder, theme.abacusCenterGradient, RoundedCornerShape(AppDimens.Dimens100))
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
    onReset: () -> Unit,
    backgroundBoxPadding: Dp,
    buttonLabelTextSize: TextUnit,
    buttonLabelLineHeight: TextUnit
) {
    Column(
        modifier = Modifier
            .padding(start = backgroundBoxPadding)
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
            text = stringResource(R.string.reset),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.font_bold)),
            style = MaterialTheme.typography.labelSmall.scaled().copy(fontSize = buttonLabelTextSize),
            lineHeight = buttonLabelLineHeight,
        )
    }
}

@Composable
fun CenterBox(
    centerBoxMinWidth: Dp,
    centerBoxHeight: Dp,
    theme: ColorPresetModel,
    horizontalPadding: Dp,
    isDisplayAbacusNumber: Boolean,
    answer: String,
    centerBoxValueTextSize: TextUnit,
    centerBoxTitleTextSize: TextUnit,
    centerBoxTitleLineHeight: TextUnit,
    centerBoxHiddenTextSize: TextUnit,
    centerBoxHiddenLineHeight: TextUnit
) {
    Column(
        modifier = Modifier
            .height(centerBoxHeight)
            .requiredWidthIn(min = centerBoxMinWidth)
            .shadow(
                elevation = AppDimens.Dimens4,
                shape = RoundedCornerShape(AppDimens.Dimens100),  // ⭐ Rounded shadow
                clip = false                          // (same as Swift no clipping)
            )
            .background(theme.displayBGColor, RoundedCornerShape(AppDimens.Dimens8))
            .border(
                width = AppDimens.Dimens3,
                color = theme.displayBorderColor,
                shape = RoundedCornerShape(AppDimens.Dimens8)
            )
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.current_input),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.font_bold)),
            fontSize = centerBoxTitleTextSize,
            lineHeight = centerBoxTitleLineHeight
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
            Text("-", color = Color.White, fontSize = centerBoxTitleTextSize, lineHeight = centerBoxTitleLineHeight)
            Text(
                stringResource(R.string.hide_from_setting),
                color = Color.White,
                fontSize = centerBoxHiddenTextSize,
                lineHeight = centerBoxHiddenLineHeight
            )
        }
    }
}

@Composable
fun NextButton(
    nextOpacity: Float,
    iconDimensions: Dp,
    screenType: String,
    abacusType: String?,
    onNext: () -> Unit,
    backgroundBoxPadding: Dp,
    buttonLabelTextSize: TextUnit,
    buttonLabelLineHeight: TextUnit
) {
    val (pulseScale, pulseAlpha) = if (screenType == AppConstants.AbacusScreen.screenTypeAbacusPractice){
        rememberPulseEffect(nextOpacity == 1f)
    }else{
        1f to 1f
    }
    Column(
        modifier = Modifier
            .padding(end = backgroundBoxPadding)
            .alpha(nextOpacity)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
                alpha = pulseAlpha
            }
            .clickable(enabled = nextOpacity == 1f) { onNext() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (screenType != AppConstants.AbacusScreen.screenTypeCCM && abacusType != AppConstants.apiParams.answerFormalExam) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconDimensions)
            )
        }

        Text(
            text = when {
                abacusType == AppConstants.apiParams.answerFormalExam -> stringResource(R.string.submit_answer)
                screenType == AppConstants.AbacusScreen.screenTypeCCM -> stringResource(R.string.check_answer_)
                else -> stringResource(R.string.next)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.scaled().copy(
                color = Color.White,
                fontSize = buttonLabelTextSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            ),
            lineHeight = buttonLabelLineHeight,
        )
    }
}

@Composable
fun rememberPulseEffect(isEnabled: Boolean): Pair<Float, Float> {
    if (!isEnabled) {
        return 1f to 1f
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    ).value

    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    ).value

    return scale to alpha
}

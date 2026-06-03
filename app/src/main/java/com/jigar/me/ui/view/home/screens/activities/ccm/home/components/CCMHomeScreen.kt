package com.jigar.me.ui.view.home.screens.activities.ccm.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryDark
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.jetpack.utils.ui.slider.RangeSlider
import com.jigar.me.ui.jetpack.utils.ui.slider.SingleSlider
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.AppConstants

private val ColorBlue   = Color(0xFF1E88E5)
private val ColorGreen  = Color(0xFF43A047)
private val ColorPurple = Color(0xFF8E24AA)
private val ColorTeal   = Color(0xFF00897B)
private val ColorOrange = Color(0xFFFF9800)

@Composable
fun CCMHomeScreen(
    uiState: CCMHomeUiState,
    viewModel: CCMHomeViewModel,
    onStartClick: () -> Unit,
) {
    val gradientBrush = Brush.linearGradient(colors = listOf(ColorPrimaryDark, ColorPrimary))

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DeviceInfo.screenHorizontalPadding()),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24)
    ) {
        // ── LEFT PANEL: icon + title + display toggles ─────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DisplaySettings,
                contentDescription = null,
                modifier = Modifier
                    .size(AppDimens.ExamCCMIconHeight)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = gradientBrush, size = size, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )

            Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens12 else AppDimens.Dimens6))

            Text(
                text = stringResource(R.string.create_custom_challenge_as_per_your_kid_s_ability),
                textAlign = TextAlign.Center,
                style = (if (DeviceInfo.isTablet) MaterialTheme.typography.titleLarge
                         else MaterialTheme.typography.bodyLarge).scaled().copy(
                    color = Color.Black, fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            )

            Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens16 else AppDimens.Dimens12))

            Text(
                text = stringResource(R.string.question_display_options),
                style = MaterialTheme.typography.bodyMedium.scaled().copy(
                    color = Color.DarkGray, fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            )

            Spacer(Modifier.height(AppDimens.Dimens8))

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                DisplayToggle(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = stringResource(R.string.ccm_speak),
                    isOn = uiState.isQuestionSpeak,
                    accentColor = ColorBlue,
                    onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionSpeak, !uiState.isQuestionSpeak) }
                )
                DisplayToggle(
                    icon = Icons.Default.Tag,
                    label = stringResource(R.string.ccm_number),
                    isOn = uiState.isQuestionShowNumber,
                    accentColor = ColorGreen,
                    onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionShowNumber, !uiState.isQuestionShowNumber) }
                )
                DisplayToggle(
                    icon = Icons.Default.TextFields,
                    label = stringResource(R.string.ccm_word),
                    isOn = uiState.isQuestionShowWord,
                    accentColor = ColorPurple,
                    onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionShowWord, !uiState.isQuestionShowWord) }
                )
            }
        }

        // ── RIGHT PANEL: sliders + start button ────────────────────────────
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingCard(
                icon = Icons.Default.Tune,
                iconColor = ColorTeal,
                label = stringResource(R.string.number_of_questions),
                value = uiState.totalQuestion.toString()
            ) {
                SingleSlider(
                    isShowText = true,
                    value = uiState.totalQuestion,
                    range = 5f..20f,
                    step = 5,
                    onValueChange = { viewModel.updateValues(AppConstants.CCM.totalQuestion, it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens8)
                )
            }

            Spacer(Modifier.height(AppDimens.Dimens8))

            SettingCard(
                icon = Icons.Default.Timer,
                iconColor = ColorOrange,
                label = stringResource(R.string.gap_between_two_question_sec),
                value = "${uiState.questionGap}s"
            ) {
                SingleSlider(
                    isShowText = true,
                    value = uiState.questionGap,
                    range = 1f..10f,
                    onValueChange = { viewModel.updateValues(AppConstants.CCM.questionGap, it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens8)
                )
            }

            Spacer(Modifier.height(AppDimens.Dimens8))

            SettingCard(
                icon = Icons.Default.TextFields,
                iconColor = ColorPurple,
                label = stringResource(R.string.question_min_max_length),
                value = "${uiState.questionMinLength}–${uiState.questionMaxLength}"
            ) {
                RangeSlider(
                    startValue = uiState.questionMinLength,
                    endValue = uiState.questionMaxLength,
                    range = 1f..6f,
                    step = 1,
                    onValueChange = { start, end ->
                        viewModel.updateValues(AppConstants.CCM.questionMinLength, start)
                        viewModel.updateValues(AppConstants.CCM.questionMaxLength, end)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimens.Dimens8)
                )
            }

            Spacer(Modifier.height(AppDimens.Dimens16))

            KidsActionButton(
                text = stringResource(R.string.let_s_start),
                icon = Icons.Default.RocketLaunch,
                type = ButtonType.ORANGE,
                isSmall = true,
                onClick = onStartClick
            )
        }
    }
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(iconColor.copy(alpha = 0.08f))
            .border(AppDimens.Dimens1, iconColor.copy(alpha = 0.18f), RoundedCornerShape(AppDimens.Dimens12))
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimens.Dimens28)
                    .background(iconColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, contentDescription = null,
                    tint = iconColor, modifier = Modifier.size(AppDimens.Dimens14)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.scaled().copy(
                    color = Color.DarkGray, fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                ),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.scaled().copy(
                    color = ColorPrimary, fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            )
        }
        content()
    }
}

@Composable
private fun DisplayToggle(
    icon: ImageVector,
    label: String,
    isOn: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimens.Dimens12))
            .background(if (isOn) accentColor else Color.White)
            .border(AppDimens.Dimens1, if (isOn) accentColor else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(AppDimens.Dimens12))
            .clickable { onClick() }
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens4)
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            tint = if (isOn) Color.White else accentColor,
            modifier = Modifier.size(AppDimens.Dimens18)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.scaled().copy(
                color = if (isOn) Color.White else Color.DarkGray,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.font_bold))
            )
        )
    }
}

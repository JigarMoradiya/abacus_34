package com.jigar.me.ui.view.home.screens.activities.ccm.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFitOnPhone
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbRangeSlider
import com.jigar.me.ui.view.ab.AbSectionLabel
import com.jigar.me.ui.view.ab.AbSelectCard
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSlider
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeUiState
import com.jigar.me.ui.view.home.screens.activities.ccm.home.viewmodels.CCMHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
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
    /** The height of the header laid over the top of this screen: the left panel sits under it, the right one does not. */
    headerHeight: Dp,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit,
) {
    val isTablet = LocalAbScale.current.isTablet
    val s = LocalAbScale.current.factor
    val colors = LocalAbColors.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens24),
    ) {
        // ── LEFT PANEL: icon + title + display toggles, centred in the room under the header ──
        // (on a phone the block shrinks to that room instead of overflowing it)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = headerHeight + if (isTablet) 0.dp else 30.dp),
            contentAlignment = Alignment.Center,
        ) {
            AbFitOnPhone {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AbHeroBadge(icon = Icons.Default.Tune, size = ActivityDimens.ccmHomeTopIcon)

                    Text(
                        text = stringResource(R.string.create_custom_challenge_as_per_your_kid_s_ability),
                        modifier = Modifier.padding(top = ActivityDimens.homeTitleTopSpacing),
                        textAlign = TextAlign.Center,
                        style = AbTypography.fixed(18f * s, FontWeight.Black),
                        color = colors.textPrimary,
                    )

                    Spacer(Modifier.height(AppDimens.Dimens12))

                    AbSectionLabel(stringResource(R.string.question_display_options))

                    Spacer(Modifier.height(AppDimens.Dimens8))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)) {
                        AbSelectCard(
                            title = stringResource(R.string.ccm_speak),
                            accent = ColorBlue,
                            isSelected = uiState.isQuestionSpeak,
                            width = ActivityDimens.ccmToggleCardWidth,
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionSpeak, !uiState.isQuestionSpeak) },
                        )
                        AbSelectCard(
                            title = stringResource(R.string.ccm_number),
                            accent = ColorGreen,
                            isSelected = uiState.isQuestionShowNumber,
                            width = ActivityDimens.ccmToggleCardWidth,
                            icon = Icons.Default.Tag,
                            onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionShowNumber, !uiState.isQuestionShowNumber) },
                        )
                        AbSelectCard(
                            title = stringResource(R.string.ccm_word),
                            accent = ColorPurple,
                            isSelected = uiState.isQuestionShowWord,
                            width = ActivityDimens.ccmToggleCardWidth,
                            symbol = "Abc", // as large as the iOS icon: the Material "Abc" vector is tiny
                            onClick = { viewModel.updateValues(AppConstants.CCM.isQuestionShowWord, !uiState.isQuestionShowWord) },
                        )
                    }
                }
            }
        }

        // ── RIGHT PANEL: sliders + start button, centred in the whole height of the screen ──
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            AbFitOnPhone {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens8),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SettingCard(
                        icon = Icons.Default.Speed,
                        iconColor = ColorTeal,
                        label = stringResource(R.string.number_of_questions),
                        value = uiState.totalQuestion.toString(),
                    ) {
                        AbSlider(
                            value = uiState.totalQuestion.toDouble(),
                            onValueChange = { viewModel.updateValues(AppConstants.CCM.totalQuestion, it.toInt()) },
                            range = 5.0..20.0,
                            step = 5.0,
                            accent = ColorTeal,
                        )
                    }

                    SettingCard(
                        icon = Icons.Default.Timer,
                        iconColor = ColorOrange,
                        label = stringResource(R.string.gap_between_two_question_sec),
                        value = "${uiState.questionGap}s",
                    ) {
                        AbSlider(
                            value = uiState.questionGap.toDouble(),
                            onValueChange = { viewModel.updateValues(AppConstants.CCM.questionGap, it.toInt()) },
                            range = 1.0..10.0,
                            accent = ColorOrange,
                        )
                    }

                    SettingCard(
                        icon = Icons.Default.TextFields,
                        iconColor = ColorPurple,
                        label = stringResource(R.string.question_min_max_length),
                        value = "${uiState.questionMinLength}–${uiState.questionMaxLength}",
                    ) {
                        AbRangeSlider(
                            minValue = uiState.questionMinLength.toDouble(),
                            maxValue = uiState.questionMaxLength.toDouble(),
                            onMinChange = { viewModel.updateValues(AppConstants.CCM.questionMinLength, it.toInt()) },
                            onMaxChange = { viewModel.updateValues(AppConstants.CCM.questionMaxLength, it.toInt()) },
                            range = 1.0..6.0,
                            accent = ColorPurple,
                        )
                    }

                    Spacer(Modifier.height(AppDimens.Dimens8))

                    AbButton(
                        text = stringResource(R.string.let_s_start),
                        tone = AbTone.Warning,
                        size = AbSize.Medium,
                        minHeightOverride = 40f, // same text, a lower face
                        icon = Icons.Default.Bolt,
                        onClick = onStartClick,
                    )
                }
            }
        }
    }
}

/** One setting: a coloured icon, its name, its value in a pill, and the slider under them. */
@Composable
private fun SettingCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    content: @Composable () -> Unit,
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.md * s)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 3.dp * s)
            .drawBehind {
                val r = (AbShapes.md * s).toPx()
                drawRoundRect(colors.outline, Offset(0f, 3.dp.toPx() * s), size, CornerRadius(r, r))
            }
            .background(colors.bgSecondary.copy(alpha = if (colors.isDark) 0.96f else 0.94f), shape)
            .border(2.dp, colors.outline, shape)
            .padding(horizontal = 12.dp * s, vertical = 8.dp * s),
        verticalArrangement = Arrangement.spacedBy(4.dp * s),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp * s),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp * s)
                    .background(iconColor, CircleShape)
                    .border(1.5.dp, Color.White.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, Modifier.size((13f * AB_SF_TO_VECTOR).dp * s), tint = Color.White)
            }
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = AbTypography.fixed(13f * s, FontWeight.ExtraBold),
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Box(
                modifier = Modifier
                    .heightIn(min = 24.dp * s)
                    .background(iconColor, CircleShape)
                    .padding(horizontal = 10.dp * s),
                contentAlignment = Alignment.Center,
            ) {
                Text(value, style = AbTypography.fixed(14f * s, FontWeight.ExtraBold), color = Color.White, maxLines = 1)
            }
        }
        content()
    }
}

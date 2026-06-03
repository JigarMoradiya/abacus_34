package com.jigar.me.ui.view.home.screens.activities.exam.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimary
import com.jigar.me.ui.jetpack.core.presentation.theme.ColorPrimaryDark
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels.ExamHomeUiState
import com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels.ExamHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.AppConstants

private val ColorGreen  = Color(0xFF43A047)
private val ColorBlue   = Color(0xFF1E88E5)
private val ColorPurple = Color(0xFF8E24AA)
private val ColorPink   = Color(0xFFE91E63)
private val ColorOrange = Color(0xFFFF9800)

@Composable
fun ExamHomeScreen(
    uiState: ExamHomeUiState,
    viewModel: ExamHomeViewModel,
    isSubscribed: Boolean = true,
    onLockedClick: () -> Unit = {},
    onStartClick: () -> Unit,
) {
    val gradientBrush = Brush.linearGradient(colors = listOf(ColorPrimaryDark, ColorPrimary))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = DeviceInfo.screenHorizontalPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Icon ──────────────────────────────────────────────────────────────
        Icon(
            imageVector = Icons.Default.AccessTimeFilled,
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
            text = stringResource(R.string.child_level),
            style = (if (DeviceInfo.isTablet) MaterialTheme.typography.headlineLarge
                     else MaterialTheme.typography.titleLarge).scaled().copy(
                color = Color.Black, fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font_extra_bold))
            )
        )

        Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens16 else AppDimens.Dimens10))

        // ── Operation Types ────────────────────────────────────────────────────
        SectionLabel(stringResource(R.string.select_exam_types))
        Spacer(Modifier.height(AppDimens.Dimens8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10, Alignment.CenterHorizontally)
        ) {
            ExamTypeCard(
                symbol = "+", symbolColor = ColorGreen,
                label = stringResource(R.string.Addition),
                isSelected = uiState.isAdditionSelected, isLocked = false,
                onLockedClick = onLockedClick,
                onClick = { viewModel.updateValues(AppConstants.EXAM.isAdditionSelected, !uiState.isAdditionSelected) }
            )
            ExamTypeCard(
                symbol = "−", symbolColor = ColorBlue,
                label = stringResource(R.string.Subtraction),
                isSelected = uiState.isSubtractionSelected, isLocked = !isSubscribed,
                onLockedClick = onLockedClick,
                onClick = { viewModel.updateValues(AppConstants.EXAM.isSubtractionSelected, !uiState.isSubtractionSelected) }
            )
            ExamTypeCard(
                symbol = "×", symbolColor = ColorPurple,
                label = stringResource(R.string.Multiplication),
                isSelected = uiState.isMultiplicationSelected, isLocked = !isSubscribed,
                onLockedClick = onLockedClick,
                onClick = { viewModel.updateValues(AppConstants.EXAM.isMultiplicationSelected, !uiState.isMultiplicationSelected) }
            )
            ExamTypeCard(
                symbol = "÷", symbolColor = ColorPink,
                label = stringResource(R.string.Division),
                isSelected = uiState.isDivisionSelected, isLocked = !isSubscribed,
                onLockedClick = onLockedClick,
                onClick = { viewModel.updateValues(AppConstants.EXAM.isDivisionSelected, !uiState.isDivisionSelected) }
            )
        }

        Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens16 else AppDimens.Dimens10))

        // ── Difficulty ─────────────────────────────────────────────────────────
        SectionLabel(stringResource(R.string.select_exam_difficulty))
        Spacer(Modifier.height(AppDimens.Dimens8))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10, Alignment.CenterHorizontally)
        ) {
            DifficultyCard(
                stars = 1, accentColor = ColorGreen,
                label = stringResource(R.string.beginner),
                isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyBeginner,
                isLocked = false, onLockedClick = onLockedClick,
                onClick = { viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyBeginner) }
            )
            DifficultyCard(
                stars = 2, accentColor = ColorOrange,
                label = stringResource(R.string.intermediate),
                isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyIntermediate,
                isLocked = !isSubscribed, onLockedClick = onLockedClick,
                onClick = { viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyIntermediate) }
            )
            DifficultyCard(
                stars = 3, accentColor = Color(0xFFE53935),
                label = stringResource(R.string.expert),
                isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyExpert,
                isLocked = !isSubscribed, onLockedClick = onLockedClick,
                onClick = { viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyExpert) }
            )
        }

        Spacer(Modifier.height(if (DeviceInfo.isTablet) AppDimens.Dimens16 else AppDimens.Dimens12))

        // ── Start Button ───────────────────────────────────────────────────────
        KidsActionButton(
            text = stringResource(R.string.let_s_start),
            icon = Icons.Default.RocketLaunch,
            type = ButtonType.ORANGE,
            isSmall = true,
            onClick = onStartClick
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = (if (DeviceInfo.isTablet) MaterialTheme.typography.titleMedium
                 else MaterialTheme.typography.bodyMedium).scaled().copy(
            color = Color.DarkGray, fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.font_bold))
        )
    )
}

@Composable
private fun ExamTypeCard(
    symbol: String,
    symbolColor: Color,
    label: String,
    isSelected: Boolean,
    isLocked: Boolean,
    onLockedClick: () -> Unit,
    onClick: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(AppDimens.Dimens12))
                .background(if (isSelected) ColorPrimary else Color.White)
                .border(AppDimens.Dimens1, if (isSelected) ColorPrimary else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(AppDimens.Dimens12))
                .clickable { if (isLocked) onLockedClick() else onClick() }
                .alpha(if (isLocked) 0.55f else 1f)
                .padding(horizontal = AppDimens.Dimens16)
                .padding(top = AppDimens.Dimens4, bottom = AppDimens.Dimens6)
                .width(AppDimens.examTypeCardWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleLarge.scaled().copy(
                    color = if (isSelected) Color.White else symbolColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold))
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.scaled().copy(
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            )
        }
        if (isLocked) LockBadge(Modifier.align(Alignment.TopEnd).offset(x = AppDimens.Dimens4, y = -AppDimens.Dimens4))
    }
}

@Composable
private fun DifficultyCard(
    stars: Int,
    accentColor: Color,
    label: String,
    isSelected: Boolean,
    isLocked: Boolean,
    onLockedClick: () -> Unit,
    onClick: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(AppDimens.Dimens12))
                .background(if (isSelected) accentColor else Color.White)
                .border(AppDimens.Dimens1, if (isSelected) accentColor else Color.Black.copy(alpha = 0.1f), RoundedCornerShape(AppDimens.Dimens12))
                .clickable { if (isLocked) onLockedClick() else onClick() }
                .alpha(if (isLocked) 0.55f else 1f)
                .padding(horizontal = AppDimens.Dimens20, vertical = AppDimens.Dimens8)
                .width(AppDimens.examDiffCardWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = when {
                            isSelected -> Color.White
                            index < stars -> accentColor
                            else -> Color.LightGray
                        },
                        modifier = Modifier.size(AppDimens.Dimens14)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.scaled().copy(
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            )
        }
        if (isLocked) LockBadge(Modifier.align(Alignment.TopEnd).offset(x = AppDimens.Dimens4, y = -AppDimens.Dimens4))
    }
}

@Composable
private fun LockBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(AppDimens.Dimens16)
            .background(ColorOrange, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(AppDimens.Dimens10)
        )
    }
}

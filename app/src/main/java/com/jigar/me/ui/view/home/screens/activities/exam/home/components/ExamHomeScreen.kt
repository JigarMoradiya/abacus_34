package com.jigar.me.ui.view.home.screens.activities.exam.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFitOnPhone
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbSectionLabel
import com.jigar.me.ui.view.ab.AbSelectCard
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels.ExamHomeUiState
import com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels.ExamHomeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

private val ColorGreen  = Color(0xFF43A047)
private val ColorBlue   = Color(0xFF1E88E5)
private val ColorPurple = Color(0xFF8E24AA)
private val ColorPink   = Color(0xFFE91E63)
private val ColorOrange = Color(0xFFFF9800)
private val ColorRed    = Color(0xFFE53935)

@Composable
fun ExamHomeScreen(
    uiState: ExamHomeUiState,
    viewModel: ExamHomeViewModel,
    isSubscribed: Boolean = true,
    onLockedClick: () -> Unit = {},
    onStartClick: () -> Unit,
) {
    val colors = LocalAbColors.current
    val scale = LocalAbScale.current
    val s = scale.factor

    // On a phone the whole block shrinks to the room it is given instead of overflowing it. It is centred, with the same
    // small margin above the icon and below the button.
    AbFitOnPhone(Modifier.padding(vertical = if (scale.isTablet) 0.dp else AppDimens.Dimens8)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Icon + title ───────────────────────────────────────────────────
            AbHeroBadge(icon = Icons.Default.AccessTimeFilled, size = ActivityDimens.examHomeTopIcon)

            Text(
                text = stringResource(R.string.child_level),
                modifier = Modifier.padding(top = ActivityDimens.homeTitleTopSpacing / 8),
                textAlign = TextAlign.Center,
                style = AbTypography.fixed(22f * s, FontWeight.Black),
                // the system's primary label colour: black on the light theme, white on the dark one
                color = if (colors.isDark) Color.White else Color.Black,
            )

            Spacer(Modifier.height(AppDimens.Dimens6))

            // ── Operation Types ────────────────────────────────────────────────
            AbSectionLabel(stringResource(R.string.select_exam_types))
            Spacer(Modifier.height(AppDimens.Dimens4))

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.Addition), symbol = "+", accent = ColorGreen,
                    isSelected = uiState.isAdditionSelected, width = ActivityDimens.examTypeCardWidth,
                    onClick = { viewModel.updateValues(AppConstants.EXAM.isAdditionSelected, !uiState.isAdditionSelected) },
                )
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.Subtraction), symbol = "−", accent = ColorBlue,
                    isSelected = uiState.isSubtractionSelected, isLocked = !isSubscribed, width = ActivityDimens.examTypeCardWidth,
                    onClick = { if (isSubscribed) viewModel.updateValues(AppConstants.EXAM.isSubtractionSelected, !uiState.isSubtractionSelected) else onLockedClick() },
                )
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.Multiplication), symbol = "×", accent = ColorPurple,
                    isSelected = uiState.isMultiplicationSelected, isLocked = !isSubscribed, width = ActivityDimens.examTypeCardWidth,
                    onClick = { if (isSubscribed) viewModel.updateValues(AppConstants.EXAM.isMultiplicationSelected, !uiState.isMultiplicationSelected) else onLockedClick() },
                )
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.Division), symbol = "÷", accent = ColorPink,
                    isSelected = uiState.isDivisionSelected, isLocked = !isSubscribed, width = ActivityDimens.examTypeCardWidth,
                    onClick = { if (isSubscribed) viewModel.updateValues(AppConstants.EXAM.isDivisionSelected, !uiState.isDivisionSelected) else onLockedClick() },
                )
            }

            Spacer(Modifier.height(AppDimens.Dimens6))

            // ── Difficulty ─────────────────────────────────────────────────────
            AbSectionLabel(stringResource(R.string.select_exam_difficulty))
            Spacer(Modifier.height(AppDimens.Dimens4))

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens10)) {
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.beginner), stars = 1, accent = ColorGreen,
                    isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyBeginner,
                    width = ActivityDimens.examDiffCardWidth,
                    onClick = { viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyBeginner) },
                )
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.intermediate), stars = 2, accent = ColorOrange,
                    isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyIntermediate,
                    isLocked = !isSubscribed, width = ActivityDimens.examDiffCardWidth,
                    onClick = { if (isSubscribed) viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyIntermediate) else onLockedClick() },
                )
                AbSelectCard(
                    compact = true,
                    title = stringResource(R.string.expert), stars = 3, accent = ColorRed,
                    isSelected = uiState.selectedDifficulty == AppConstants.EXAM.examDifficultyExpert,
                    isLocked = !isSubscribed, width = ActivityDimens.examDiffCardWidth,
                    onClick = { if (isSubscribed) viewModel.updateRadioValue(AppConstants.EXAM.examDifficultyExpert) else onLockedClick() },
                )
            }

            Spacer(Modifier.height(ActivityDimens.homeStartButtonTopSpacing * 0.6f))

            // ── Start Button ───────────────────────────────────────────────────
            AbButton(
                text = stringResource(R.string.let_s_start),
                tone = AbTone.Warning,
                size = AbSize.Medium,
                icon = Icons.Default.PlayArrow,
                iconAtEnd = true,
                minHeightOverride = 40f, // same text, a lower face
                onClick = onStartClick,
            )
        }
    }
}

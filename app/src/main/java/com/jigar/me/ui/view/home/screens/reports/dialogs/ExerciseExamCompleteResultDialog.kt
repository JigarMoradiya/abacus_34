package com.jigar.me.ui.view.home.screens.reports.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbFitOnPhone
import com.jigar.me.ui.view.ab.AbPopupLayer
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbSound
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abCardEntrance
import com.jigar.me.ui.view.ab.abSquash
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.home.screens.activities.exam.play.exam_generator.toQuestionResultList
import com.jigar.me.ui.view.home.screens.reports.dialogs.common.QuestionExamColumnItemHorizontal
import com.jigar.me.ui.view.home.screens.reports.dialogs.common.StatItem
import com.jigar.me.ui.view.home.screens.reports.dialogs.common.buildExamResult
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.secToCountDown
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExerciseExamCompleteResultDialog(
    selectedTheme: String,
    request: SubmitAllExamDataRequest,
    isFromHistory: Boolean = false,
    saveResults: Boolean = true,
    onLoginToSave: (() -> Unit)? = null,
    onClose: () -> Unit,
    onGiveAgain: () -> Unit,
) {

    val context = LocalContext.current
    val questionList: List<QuestionDataRequest> = request.questions?:arrayListOf()
    val questionResults: List<QuestionResult> = questionList.toQuestionResultList()

    LaunchedEffect(Unit) {
        if (!isFromHistory) AudioPlayerManager.playSoundClap()
    }

    AbTheme {
        // the dimmed backdrop only fades (tapping it closes); the card alone pops in
        AbPopupLayer(onScrimTap = onClose, scrim = Color.Black.copy(alpha = 0.45f)) { layerWidth, layerHeight, shown ->
            val colors = LocalAbColors.current
            val s = LocalAbScale.current.factor
            val shape = RoundedCornerShape(AbShapes.lg * s)
            val rim = colors.brandPrimary.copy(alpha = if (colors.isDark) 0.8f else 0.5f)
            // the card stays inside the safe area (a phone's cutout side), centred in it, like the iOS card
            val insets = WindowInsets.safeDrawing.asPaddingValues()
            val insetStart = insets.calculateLeftPadding(LocalLayoutDirection.current)
            val insetEnd = insets.calculateRightPadding(LocalLayoutDirection.current)

            Column(
                modifier = Modifier
                    .offset(x = (insetStart - insetEnd) / 2)
                    .width(minOf(900.dp * s, layerWidth - insetStart - insetEnd - (DeviceInfo.screenHorizontalPadding() + 8.dp) * 2))
                    .height(layerHeight * (if (DeviceInfo.isTablet) 0.8f else 0.86f))
                    .abCardEntrance(shown, from = 0.94f)
                    .pointerInput(Unit) { detectTapGestures(onTap = { }) }
                    .shadow(16.dp * s, shape, clip = false, ambientColor = Color.Black.copy(alpha = 0.3f), spotColor = Color.Black.copy(alpha = 0.3f))
                    .clip(shape)
                    .background(colors.bgSecondary)
                    .drawBehind {
                        // a 3 dp rim drawn inside the card (the header band covers it where it touches)
                        val inset = 1.5.dp.toPx()
                        val r = AbShapes.lg.toPx() * s - inset
                        drawRoundRect(
                            rim, Offset(inset, inset), Size(size.width - inset * 2, size.height - inset * 2),
                            CornerRadius(r, r), style = Stroke(3.dp.toPx()),
                        )
                    }
            ) {
                // HEADER: a coloured band with the title and a close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(colors.brandPrimary, colors.brandPrimaryDark)))
                        .padding(horizontal = AppDimens.Dimens16, vertical = 10.dp * s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val title = when (request.type) {
                        AppConstants.EXAM.type_Exercise -> {
                            stringResource(R.string.result_of_exercise)
                        }
                        AppConstants.EXAM.type_Exam -> {
                            stringResource(R.string.result_of_exam)
                        }
                        AppConstants.apiParams.answerFormalExam -> {
                            stringResource(R.string.result_of_formal_exam_set)
                        }
                        else -> {
                            ""
                        }
                    }
                    Text(
                        text = title,
                        style = AbTypography.title(s),
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(34.dp * s)
                            .abSquash(
                                face = Color.White.copy(alpha = 0.25f), cornerRadius = AbShapes.pill, edgeHeight = 0.dp,
                                sound = AbSound.Back, onClick = onClose,
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Close, contentDescription = stringResource(R.string.close),
                            tint = Color.White, modifier = Modifier.size((14f * AB_SF_TO_VECTOR).dp * s)
                        )
                    }
                }

                Row(Modifier.fillMaxWidth().weight(1f)) {
                    // LEFT PANEL: time, score, the verdict and the buttons. On a phone it shrinks to fit rather than pushing the button off the card.
                    Box(Modifier.weight(0.4f).fillMaxHeight()) {
                        AbFitOnPhone(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp * s), stretchWidth = true) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp * s)
                            ) {
                                Row(Modifier.fillMaxWidth()) {
                                    var timeLabel = R.string.TotalTakeTime
                                    var timeValue = ""
                                    when (request.type) {
                                        AppConstants.EXAM.type_Exercise -> {
                                            timeLabel = R.string.TotalTakeTime
                                            timeValue = (request.total_time_taken?:0).secToCountDown()+" / "+(request.allowed_max_time?:0).secToCountDown()
                                        }
                                        AppConstants.EXAM.type_Exam, AppConstants.apiParams.answerFormalExam -> {
                                            timeLabel = R.string.TotalTime
                                            timeValue = (request.total_time_taken?:0).secToTimeFormat()
                                        }
                                        else -> {
                                            ""
                                        }
                                    }
                                    Spacer(Modifier.weight(1f))
                                    StatItem(
                                        icon = Icons.Rounded.Alarm,
                                        value = timeValue,
                                        label = stringResource(timeLabel),
                                        color = colors.accentPink
                                    )
                                    Spacer(Modifier.weight(1f))
                                    StatItem(
                                        icon = Icons.Rounded.CheckCircle,
                                        value = request.no_of_right_answers.toString()+" / "+questionList.size.toString(),
                                        label = stringResource(R.string.RightQue),
                                        color = colors.accentGreen
                                    )
                                    Spacer(Modifier.weight(1f))
                                }

                                if (!isFromHistory) {
                                    val result = buildExamResult(
                                        context = context,
                                        totalRight = request.no_of_right_answers?:0,
                                        totalQuestion = questionList.size
                                    )
                                    Text(
                                        text = result.title,
                                        modifier = Modifier.padding(horizontal = AppDimens.Dimens16),
                                        textAlign = TextAlign.Center,
                                        style = AbTypography.headline(s),
                                        color = result.titleColor
                                    )

                                    // Not-saved banner with a button to sign in
                                    if (!saveResults && onLoginToSave != null) {
                                        NotSavedBanner(onLoginToSave)
                                    }

                                    val btnLabel = when (request.type) {
                                        AppConstants.EXAM.type_Exercise -> {
                                            stringResource(R.string.do_exercise_again)
                                        }
                                        AppConstants.EXAM.type_Exam -> {
                                            stringResource(R.string.give_exam_again)
                                        }
                                        AppConstants.apiParams.answerFormalExam -> {
                                            stringResource(R.string.ok_please_close)
                                        }
                                        else -> {
                                            ""
                                        }
                                    }
                                    AbButton(
                                        text = btnLabel,
                                        tone = AbTone.Primary,
                                        size = AbSize.Small,
                                        labelSize = 16f,
                                        labelWeight = FontWeight.ExtraBold,
                                        icon = if (request.type == AppConstants.apiParams.answerFormalExam) Icons.Rounded.Check else Icons.Rounded.Refresh,
                                        onClick = onGiveAgain
                                    )
                                }
                            }
                        }
                    }

                    // RIGHT PANEL: every question with the answer given
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            items = questionResults,
                            key = { item -> "${item.que}_${item.hashCode()}" }
                        ) { item ->
                            QuestionExamColumnItemHorizontal(item,selectedTheme)
                        }
                    }
                }
            }
        }
    }
}

/** "Your results are not saved yet": a warm strip with a button to sign in. */
@Composable
private fun NotSavedBanner(onLoginToSave: () -> Unit) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val warn = colors.accentOrange
    val shape = RoundedCornerShape(AbShapes.sm * s)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppDimens.Dimens16)
            .background(warn.copy(alpha = if (colors.isDark) 0.2f else 0.14f), shape)
            .border(2.dp, warn.copy(alpha = 0.6f), shape)
            .padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens8)
    ) {
        Text(
            text = stringResource(R.string.results_not_saved_to_account),
            style = AbTypography.fixed(12f * s, FontWeight.ExtraBold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        AbButton(
            text = stringResource(R.string.login_now),
            tone = AbTone.Warning,
            size = AbSize.Compact,
            onClick = onLoginToSave
        )
    }
}

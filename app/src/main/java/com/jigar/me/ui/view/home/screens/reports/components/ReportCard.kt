package com.jigar.me.ui.view.home.screens.reports.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.ui.view.ab.AB_SF_TO_VECTOR
import com.jigar.me.ui.view.ab.AbButton
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbHeroBadge
import com.jigar.me.ui.view.ab.AbShapes
import com.jigar.me.ui.view.ab.AbShrinkText
import com.jigar.me.ui.view.ab.AbSize
import com.jigar.me.ui.view.ab.AbTone
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.ab.abChunkyFace
import com.jigar.me.ui.view.ab.abDarkened
import com.jigar.me.utils.AppConstants

/** One report of the history: what it was, when, the button to open it and its figures. */
@Composable
fun ReportCard(
    item: AllExamData,
    onCheckResultTapped: (AllExamData) -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val shape = RoundedCornerShape(AbShapes.lg * s)
    // Each kind of report has its own colour and icon, so a long list is easy to scan.
    val tone = reportTone(item.type)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .abChunkyFace(
                face = colors.bgSecondary.copy(alpha = if (colors.isDark) 0.97f else 0.95f),
                edge = tone.abDarkened(0.25f).copy(alpha = 0.55f), stroke = null,
                cornerRadius = AbShapes.lg * s, edgeHeight = 4.dp * s,
            )
            .border(2.5.dp, tone.copy(alpha = if (colors.isDark) 0.7f else 0.45f), shape)
            .padding(14.dp * s),
        verticalArrangement = Arrangement.spacedBy(10.dp * s)
    ) {
        ReportHeaderRow(item, tone, onCheckResultTapped)

        when (item.type) {
            AppConstants.EXAM.type_CCM -> ReportCCMSection(item)

            AppConstants.EXAM.type_Exercise,
            AppConstants.EXAM.type_Exam,
            AppConstants.apiParams.answerFormalExam -> ReportExerciseExamSection(item)
        }
    }
}

private fun reportTone(type: String?): Color = when (type) {
    AppConstants.EXAM.type_Exam -> AbColors.Light.danger
    AppConstants.EXAM.type_Exercise -> AbColors.Light.accentGreen
    AppConstants.EXAM.type_CCM -> AbColors.Light.brandSecondary
    else -> AbColors.Light.accentPurple
}

private fun reportIcon(type: String?): ImageVector = when (type) {
    AppConstants.EXAM.type_Exam -> Icons.Rounded.AccessTimeFilled
    AppConstants.EXAM.type_Exercise -> Icons.Rounded.Bolt
    AppConstants.EXAM.type_CCM -> Icons.Rounded.Tune
    else -> Icons.Rounded.School
}

@Composable
private fun ReportHeaderRow(
    item: AllExamData,
    tone: Color,
    onCheckResultTapped: (AllExamData) -> Unit
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val title = remember(item.type) {
        when (item.type) {
            AppConstants.apiParams.answerFormalExam ->
                "Practice set of Formal Exam"
            AppConstants.EXAM.type_CCM ->
                "Custom Challenge Mode"
            AppConstants.EXAM.type_Exercise ->
                "Exercise"
            AppConstants.EXAM.type_Exam ->
                "Exam"
            else -> ""
        }
    }
    val hasResult = item.type == AppConstants.apiParams.answerFormalExam ||
        item.type == AppConstants.EXAM.type_Exercise ||
        item.type == AppConstants.EXAM.type_Exam

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp * s),
        modifier = Modifier.fillMaxWidth()
    ) {
        AbHeroBadge(reportIcon(item.type), accent = tone, size = 38.dp * s)

        Text(
            text = title,
            style = AbTypography.headline(s),
            color = colors.textPrimary
        )

        Spacer(Modifier.weight(1f))

        AbShrinkText(
            text = item.dateTimeFormat() ?: "",
            style = AbTypography.fixed(12f * s, FontWeight.ExtraBold),
            color = colors.textSecondary,
            minScale = 0.7f,
            maxLines = 1
        )

        if (hasResult) {
            AbButton(
                text = stringResource(R.string.checkResult),
                tone = AbTone.Success,
                size = AbSize.Compact,
                icon = Icons.Rounded.FindInPage,
                onClick = { onCheckResultTapped(item) }
            )
        }
    }
}

/** "Level of Exam : 3" -- a small grey label and its value on the same line. */
@Composable
private fun ReportLine(label: String, value: String) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp * s)) {
        Text(
            text = label,
            style = AbTypography.fixed(14f * s, FontWeight.ExtraBold),
            color = colors.textSecondary,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = value,
            style = AbTypography.fixed(15f * s, FontWeight.ExtraBold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f, fill = false).alignByBaseline()
        )
    }
}

/** The exam level as the Exam screen names it: an exam taken on iOS is saved with the key ("exam_beginner"), one taken on Android with the name. */
@Composable
private fun examLevelName(level: String?): String = when (level?.lowercase()?.removePrefix("exam_")) {
    "beginner" -> stringResource(R.string.beginner)
    "intermediate" -> stringResource(R.string.intermediate)
    "expert" -> stringResource(R.string.expert)
    else -> level ?: ""
}

@Composable
private fun ReportExerciseExamSection(item: AllExamData) {
    val s = LocalAbScale.current.factor

    Column(verticalArrangement = Arrangement.spacedBy(2.dp * s)) {
        when (item.type) {
            AppConstants.EXAM.type_Exercise -> {
                ReportLine(stringResource(R.string.type_of_exercise), item.category ?: "")
                ReportLine(stringResource(R.string.exercise_in), item.label ?: "")
            }
            AppConstants.EXAM.type_Exam -> {
                ReportLine(stringResource(R.string.level_of_exam), examLevelName(item.level))
                ReportLine(stringResource(R.string.type_of_questions), item.sub_type?.replace(",", ", ") ?: "")
            }
            AppConstants.apiParams.answerFormalExam -> {
                ReportLine(stringResource(R.string.level_of_set), item.level ?: "")
            }
        }
    }

    val right = item.no_of_right_answers ?: 0
    val total = item.no_of_questions ?: 0
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp * s), modifier = Modifier.fillMaxWidth()) {
        ReportStat(Icons.Rounded.Description, total.toString(), stringResource(R.string.TotalQue), AbColors.Light.accentPurple)
        if (item.type == AppConstants.EXAM.type_Exercise) {
            ReportStat(Icons.Rounded.Watch, item.maxTimeFormat(), stringResource(R.string.maximum_time), AbColors.Light.brandSecondary)
        }
        ReportStat(Icons.Rounded.Alarm, item.totalTimeFormat(), stringResource(R.string.TotalTakeTime), AbColors.Light.accentOrange)
        ReportStat(Icons.Rounded.CheckCircle, right.toString(), stringResource(R.string.RightQue), AbColors.Light.accentGreen)
        ReportStat(Icons.Rounded.Cancel, (total - right).toString(), stringResource(R.string.WrongQue), AbColors.Light.danger)
    }
}

@Composable
private fun ReportCCMSection(item: AllExamData) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    val question = remember(item.questions) { item.fullQuestion() }
    val correctAnswer = remember(item.questions) { item.correctAns() }
    val isCorrect = remember(item.questions) { item.isAnswerCorrect() }
    val userAnswer = remember(item.questions) { item.userAns() }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp * s)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp * s)) {
            Text(
                text = stringResource(R.string.question),
                style = AbTypography.fixed(14f * s, FontWeight.ExtraBold),
                color = colors.textSecondary,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = "$question = $correctAnswer",
                style = AbTypography.fixed(16f * s, FontWeight.ExtraBold),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f, fill = false).alignByBaseline()
            )
        }
        if (!isCorrect) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp * s),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Cancel, null, Modifier.size((12f * AB_SF_TO_VECTOR).dp * s), tint = colors.danger)
                Text(
                    text = stringResource(R.string.sorry_your_answer_is_incorrect),
                    style = AbTypography.fixed(12f * s, FontWeight.ExtraBold),
                    color = colors.danger,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Your answer is : $userAnswer",
                    style = AbTypography.fixed(12f * s, FontWeight.ExtraBold),
                    color = colors.danger
                )
            }
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp * s), modifier = Modifier.fillMaxWidth()) {
        ReportStat(
            Icons.Rounded.VolumeUp, if (item.is_question_speak == true) stringResource(R.string.yes) else stringResource(R.string.no),
            stringResource(R.string.is_question_speak), AbColors.Light.brandSecondary
        )
        ReportStat(
            Icons.Rounded.Tag, if (item.is_question_show_in_number == true) stringResource(R.string.yes) else stringResource(R.string.no),
            stringResource(R.string.is_question_show_in_number), AbColors.Light.accentGreen
        )
        ReportStat(
            Icons.Rounded.Abc, if (item.is_question_show_in_word == true) stringResource(R.string.yes) else stringResource(R.string.no),
            stringResource(R.string.is_question_show_in_word), AbColors.Light.accentPurple
        )
        ReportStat(
            Icons.Rounded.Timer, (item.gap_between_two_question ?: 0).toString() + " sec",
            stringResource(R.string.gap_between_each_question_number), AbColors.Light.accentOrange
        )
        ReportStat(
            Icons.Rounded.FormatSize, (item.question_min_length ?: 0).toString() + "-" + (item.question_max_length ?: 0),
            stringResource(R.string.question_min_max_length), AbColors.Light.accentPink
        )
    }
}

/** One figure of a report: a coloured icon, the value and a small label, on a soft plate of the same colour. */
@Composable
private fun RowScope.ReportStat(
    icon: ImageVector,
    value: String,
    label: String,
    tone: Color
) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                tone.copy(alpha = if (colors.isDark) 0.20f else 0.12f),
                RoundedCornerShape(AbShapes.sm * s)
            )
            .padding(horizontal = 6.dp * s, vertical = 8.dp * s),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp * s)
    ) {
        Box(Modifier.height(22.dp * s), contentAlignment = Alignment.Center) {
            Icon(icon, null, Modifier.size((18f * AB_SF_TO_VECTOR).dp * s), tint = tone)
        }
        Box(Modifier.height(20.dp * s), contentAlignment = Alignment.Center) {
            AbShrinkText(
                text = value,
                style = AbTypography.fixed(16f * s, FontWeight.ExtraBold),
                color = colors.textPrimary,
                minScale = 0.6f,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
        AbShrinkText(
            text = label,
            style = AbTypography.fixed(10f * s, FontWeight.Bold),
            color = colors.textSecondary,
            minScale = 0.7f,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

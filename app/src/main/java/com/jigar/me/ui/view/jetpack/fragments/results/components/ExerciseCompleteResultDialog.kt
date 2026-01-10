package com.jigar.me.ui.view.jetpack.fragments.results.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.QuestionResult
import com.jigar.me.ui.view.jetpack.fragments.exam.play.exam_generator.toQuestionResultList
import com.jigar.me.ui.view.jetpack.fragments.results.components.common.QuestionExamColumnItemHorizontal
import com.jigar.me.ui.view.jetpack.fragments.results.components.common.StatItem
import com.jigar.me.ui.view.jetpack.fragments.results.components.common.buildExamResult
import com.jigar.me.utils.extensions.secToCountDown

@Composable
fun ExerciseCompleteResultDialog(
    request: SubmitAllExamDataRequest,
    onClose: () -> Unit,
    onGiveAgain: () -> Unit,
) {

    val context = LocalContext.current
    val questionList: List<QuestionDataRequest> =
        request.questions
            ?.mapNotNull { it as? QuestionDataRequest }
            ?.toCollection(ArrayList())
            ?: arrayListOf()

    val questionResults: List<QuestionResult> = questionList.toQuestionResultList()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClose() }) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(dimensionResource(R.dimen.activity_padding16)), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // Header
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(start = dimensionResource(R.dimen.activity_padding16)).padding(end = dimensionResource(R.dimen.activity_padding12)),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.result_of_exercise),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onClose,
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.DarkGray,modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding4)))
                        Text(
                            stringResource(R.string.close), style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold)))
                        )
                    }
                }

                HorizontalDivider(
                    Modifier, color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp
                )

                Row{
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                icon = R.mipmap.ic_time,
                                value = (request.total_time_taken?:0).secToCountDown()+" / "+(request.allowed_max_time?:0).secToCountDown(),
                                label = R.string.TotalTakeTime,
                                color = Color(0xFFD81B60)
                            )
                            StatItem(
                                icon = R.mipmap.ic_question_right,
                                value = request.no_of_right_answers.toString()+" / "+questionList.size.toString(),
                                label = R.string.RightQue,
                                color = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding16)))
                        val result = buildExamResult(
                            context = context,
                            totalRight = request.no_of_right_answers?:0,
                            totalQuestion = questionList.size
                        )
                        Text(
                            text = result.title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall.copy(color = result.titleColor, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily(Font(R.font.font_medium))))

                        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding12)))

                        PrimaryButton(text = stringResource(R.string.do_exercise_again), onClick = onGiveAgain,)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding8)),
                        contentPadding = PaddingValues(dimensionResource(R.dimen.activity_padding12))
                    ) {
                        items(
                            items = questionResults,
                            key = { item -> "${item.que}_${item.hashCode()}" }
                        ) { item ->
                            QuestionExamColumnItemHorizontal(item)
                        }
                    }
                }
            }
        }
    }
}



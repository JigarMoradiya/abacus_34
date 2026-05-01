package com.jigar.me.ui.view.home.screens.category.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.AppConstants

@Composable
fun SetItem(
    set: Set,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(modifier = Modifier.padding(0.dp)) {

        // 🔹 CARD
        Card(
            shape = RoundedCornerShape(AppDimens.Dimens8),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = setCardColor(set.answer_setting)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.Dimens4)
                .clip(RoundedCornerShape(AppDimens.Dimens8))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        ) {

        Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = AppDimens.Dimens8, vertical = AppDimens.Dimens2)
            ) {

                if (set.show_time_setting) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Timer",
                        modifier = Modifier.size(AppDimens.Dimens16),
                        tint = Color.White
                    )

                }

                Text(
                    text = set.getSetTitle(),
                    modifier = Modifier.padding(
                        start = if (set.show_time_setting) AppDimens.Dimens6 else 0.dp
                    ),
                    style = MaterialTheme.typography.titleMedium.scaled().copy(color = Color.White, fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = (2).dp), // 👈 key part
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens2)
        ) {

            if (set.is_completed_set) {
                Box(
                    modifier = Modifier
                        .size(AppDimens.Dimens8)
                        .background(
                            colorResource(R.color.green_A400),
                            CircleShape
                        )
                )
            }

            if (set.is_running_set) {
                BlinkingDot(
                    color = colorResource(R.color.red_A400),
                    size = AppDimens.Dimens8
                )
            }
        }
    }
}




@Composable
private fun setCardColor(answerSetting: String): Color {
    return when (answerSetting) {
        AppConstants.apiParams.answerStepByStep ->
            colorResource(R.color.step_by_step_answer_light)

        AppConstants.apiParams.answerFinalAnswer ->
            colorResource(R.color.final_answer_light)

        AppConstants.apiParams.answerFormalExam ->
            colorResource(R.color.formal_exam_light)

        else ->
            colorResource(R.color.grey_100)
    }
}


@Composable
fun BlinkingDot(
    color: Color,
    size: Dp = AppDimens.Dimens8
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

package com.jigar.me.ui.view.jetpack.fragments.exam.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.jigar.me.R

@Composable
fun ExamProgressBar(
    progress: Int,
    max: Int,
    modifier: Modifier = Modifier,
    height: Dp = dimensionResource(R.dimen.quiz_progress_height),
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = colorResource(R.color.colorAccent),
    cornerRadius: Dp = height
) {
    val progressFraction = (progress.toFloat() / max.coerceAtLeast(1))
        .coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressFraction)
                .clip(RoundedCornerShape(cornerRadius))
                .background(progressColor)
        )
    }
}

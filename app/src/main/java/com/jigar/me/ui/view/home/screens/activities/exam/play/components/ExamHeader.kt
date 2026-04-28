package com.jigar.me.ui.view.home.screens.activities.exam.play.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.activities.exam.play.viewmodels.ExamPlayUiState
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.utils.extensions.secToTimeFormat

@Composable
fun ExamHeader(
    uiState: ExamPlayUiState,
    elapsedSeconds: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimens.Dimens16),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExamProgressBar(
                progress = uiState.currentIndex + 1,
                max = uiState.examPaper.size,
                modifier = Modifier
                    .width(AppDimens.Dimens240)
                    .padding(horizontal = AppDimens.Dimens16)
            )
        }

        Spacer(Modifier.weight(1f))

        Text("Time : "+elapsedSeconds.secToTimeFormat(),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold)))
        )
    }
}

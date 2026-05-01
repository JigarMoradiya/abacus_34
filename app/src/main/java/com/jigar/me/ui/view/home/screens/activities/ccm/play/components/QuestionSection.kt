package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState

@Composable
fun QuestionSection(
    uiState: CCMPlayUiState,
    modifier: Modifier = Modifier,
) {
    val showListenImage = uiState.isQuestionSpeak
    val showNumber = uiState.isQuestionShowNumber
    val showWord = uiState.isQuestionShowWord
    val numberText = uiState.currentNumberText
    val wordText = uiState.currentWordText
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Listen Image
        if (showListenImage) {
            Image(
                painter = painterResource(R.drawable.ic_kid_listen),
                contentDescription = null,
                modifier = Modifier
                    .height(AppDimens.Dimens150)
                    .wrapContentWidth()
                    .padding(bottom = AppDimens.Dimens16)
            )
        }

        // Number Text
        if (showNumber) {
            Text(
                text = numberText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                fontSize = if (showListenImage) dimensionResource(R.dimen.textSizeCCMNumber).value.sp else dimensionResource(R.dimen.textSizeCCMNumber).value.sp * 2,
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge.scaled().copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 1f
                    )
                )
            )
        }

        // Number in Words
        if (showWord) {
            Text(
                text = wordText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimens.Dimens20),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.textSizeCCMWord).value.sp,
                fontFamily = FontFamily(Font(R.font.font_bold)),
                color = Color.DarkGray
            )
        }
    }
}

package com.jigar.me.ui.view.jetpack.fragments.activities.exercise.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel


@Composable
fun UseWhichHandTextUi( highlightColor: Color,
                        modifier: Modifier = Modifier,) {
    val normalColor = Color.Black.copy(alpha = 0.7f)

    val annotatedText = remember(highlightColor) {
        buildAnnotatedString {
            append("Use ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = highlightColor,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            ) {
                append("the keypad")
            }
            append(" or ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = highlightColor,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            ) {
                append("the abacus")
            }
            append(" to give your answer!")
        }
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodySmall.copy(
            color = normalColor,
            fontFamily = FontFamily(Font(R.font.font_medium))
        ),
        modifier = modifier
    )
}
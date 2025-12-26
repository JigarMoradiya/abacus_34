package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.components.subitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun UseWhichHandTextUi(uiState: AbacusDoPracticeUiState,viewModel : AbacusDoPracticeViewModel) {
    // use left or right hand text
    Column {
        Spacer(Modifier.weight(1f))
        val highlightColor = uiState.currentColorPresetModel.buttonColor
        val normalColor = Color.Black.copy(alpha = 0.7f)

        val annotatedText = buildAnnotatedString {
            append("Use your ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = highlightColor,
                    fontFamily = FontFamily(Font(R.font.font_bold))
                )
            ) {
                append(
                    if (viewModel.isAbacusOnLeftHand) "Left Hand"
                    else "Right Hand"
                )
            }

            append(" to move beads")
        }
        Row(modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.activity_padding24))
            .padding(vertical = dimensionResource(R.dimen.activity_padding12))) {
            if (viewModel.isAbacusOnLeftHand) {
                Spacer(Modifier.weight(1f))
            }
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.titleSmall,
                color = normalColor, // default color for non-highlighted text
                fontFamily = FontFamily(Font(R.font.font_medium))
            )

            if (!viewModel.isAbacusOnLeftHand) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
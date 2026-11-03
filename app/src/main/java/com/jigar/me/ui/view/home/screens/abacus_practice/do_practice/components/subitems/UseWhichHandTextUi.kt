package com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.subitems

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.abFixedStyle
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.components.practiceResponsive
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeUiState
import com.jigar.me.ui.view.home.screens.abacus_practice.do_practice.viewmodels.AbacusDoPracticeViewModel
import com.jigar.me.ui.view.home.theme.AppDimens
import androidx.compose.ui.res.stringResource
import com.jigar.me.R

/**
 * The line at the foot of the screen that says which hand moves the beads. It sits on the side of the abacus; its top
 * edge is reported (in root coordinates) through [onTopChanged] so the question panel can stop above it.
 */
@Composable
fun UseWhichHandTextUi(
    uiState: AbacusDoPracticeUiState,
    viewModel: AbacusDoPracticeViewModel,
    onTopChanged: (Float) -> Unit,
) {
    val colors = LocalAbColors.current
    val highlightColor = uiState.currentColorPresetModel.buttonColor
    val handSize = abFixedStyle(13f).fontSize

    // one whole sentence per hand; the hand's name sits between <b> and </b> and is drawn in the highlight colour
    val template = stringResource(if (viewModel.isAbacusOnLeftHand) R.string.practice_use_left_hand else R.string.practice_use_right_hand)
    val annotatedText = buildAnnotatedString {
        val highlight = SpanStyle(fontWeight = FontWeight.Bold, color = highlightColor, fontSize = handSize)
        var rest = template
        while (true) {
            val open = rest.indexOf("<b>")
            val close = rest.indexOf("</b>")
            if (open < 0 || close < open) { append(rest); break }
            append(rest.substring(0, open))
            withStyle(highlight) { append(rest.substring(open + 3, close)) }
            rest = rest.substring(close + 4)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Dimens20)
            .padding(bottom = AppDimens.Dimens12)
    ) {
        if (viewModel.isAbacusOnLeftHand) {
            Spacer(Modifier.weight(1f))
        }
        Text(
            text = annotatedText,
            modifier = Modifier.onGloballyPositioned { onTopChanged(it.positionInRoot().y) },
            style = abFixedStyle(practiceResponsive(12f), FontWeight.SemiBold),
            color = colors.textPrimary,
        )

        if (!viewModel.isAbacusOnLeftHand) {
            Spacer(Modifier.weight(1f))
        }
    }
}

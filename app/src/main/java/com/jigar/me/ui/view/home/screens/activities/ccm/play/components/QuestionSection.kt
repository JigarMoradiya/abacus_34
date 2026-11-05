package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.view.ab.AbColors
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.home.screens.activities.ActivityDimens
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun QuestionSection(
    uiState: CCMPlayUiState,
    modifier: Modifier = Modifier,
) {
    val showListenImage = uiState.isQuestionSpeak
    val showNumber = uiState.isQuestionShowNumber
    val showWord = uiState.isQuestionShowWord
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
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(ActivityDimens.ccmKidTopIcon)
            )
        }

        // Only the voice is on: nothing to read, so a hint says what to do instead of an empty panel
        if (!showNumber && !showWord) {
            if (showListenImage) {
                Text(
                    text = stringResource(R.string.ccm_listen_hint),
                    modifier = Modifier.padding(top = AppDimens.Dimens16, start = AppDimens.Dimens16, end = AppDimens.Dimens16),
                    style = AbTypography.fixed(ActivityDimens.ccmWordTextSize, FontWeight.ExtraBold),
                    color = com.jigar.me.ui.view.ab.LocalAbColors.current.brandPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            return@Column
        }

        // The sum so far, on a calm panel: the number and / or its words (they cross-fade as the next one arrives)
        AbGlassPanel(Modifier.padding(top = AppDimens.Dimens16), padding = 18.dp) {
            Column(
                Modifier.widthIn(min = ActivityDimens.ccmNumberTextSize.dp * 4),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (showNumber) {
                    Crossfade(uiState.currentNumberText, animationSpec = tween(350), label = "ccmNumber") {
                        Text(
                            text = it,
                            style = AbTypography.fixed(ActivityDimens.ccmNumberTextSize, FontWeight.ExtraBold),
                            color = com.jigar.me.ui.view.ab.LocalAbColors.current.brandPrimary,
                        )
                    }
                }
                if (showWord) {
                    Crossfade(uiState.currentWordText, animationSpec = tween(350), label = "ccmWord") {
                        Text(
                            text = it,
                            style = AbTypography.fixed(ActivityDimens.ccmWordTextSize, FontWeight.ExtraBold),
                            color = com.jigar.me.ui.view.ab.LocalAbColors.current.accentPink,
                        )
                    }
                }
            }
        }
    }
}

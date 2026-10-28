package com.jigar.me.ui.view.home.screens.whats_learning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.htmlToAnnotatedString
import com.jigar.me.ui.view.ab.AbGlassPanel
import com.jigar.me.ui.view.ab.AbTypography
import com.jigar.me.ui.view.ab.LocalAbColors
import com.jigar.me.ui.view.ab.LocalAbScale
import com.jigar.me.ui.view.home.screens.whats_learning.viewmodels.WhatsLearnNewUiState
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
@UnstableApi
fun WhatsLearnNewScreen(
    uiState: WhatsLearnNewUiState,
    onPageChanged: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(
                start = DeviceInfo.screenHorizontalPadding(),
                end = AppDimens.Dimens16,
                top = AppDimens.Dimens8,
                bottom = AppDimens.Dimens12
            ),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.Dimens16)
    ) {
        // LEFT SIDE
        Box(modifier = Modifier.weight(1.2f).fillMaxHeight()) {
            VideoPager(
                videoList = uiState.videoList,
                currentPosition = uiState.currentPosition,
                onPageChanged = onPageChanged,
                modifier = Modifier.fillMaxSize().align(Alignment.Center)
            )
        }

        // RIGHT SIDE
        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            WhatsLearnPoints(points = uiState.videoList[uiState.currentPosition].pointsList)
        }
    }
}

/** What the child learns in the module shown on the left: its points on a calm card that scrolls when they are many. */
@Composable
private fun WhatsLearnPoints(points: List<String>) {
    val colors = LocalAbColors.current
    val s = LocalAbScale.current.factor
    AbGlassPanel(Modifier.fillMaxWidth().heightIn(max = 320.dp * s), padding = 14.dp) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp * s)
        ) {
            points.forEach {
                Text(
                    text = it.boldAsExtraBold(),
                    style = AbTypography.fixed(15f * s, FontWeight.SemiBold),
                    color = colors.textPrimary
                )
            }
        }
    }
}

/** The point's `<b>` runs in the kit's heaviest weight, so they stand out from the rest of the line. */
private fun String.boldAsExtraBold(): AnnotatedString {
    val html = htmlToAnnotatedString()
    return buildAnnotatedString {
        append(html.text)
        html.spanStyles.forEach { addStyle(it.item.copy(fontWeight = FontWeight.ExtraBold), it.start, it.end) }
    }
}

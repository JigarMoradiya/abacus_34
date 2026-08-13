package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.ButtonType

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CCMCompleteBottomSheetCompose(
    uiState: CCMPlayUiState,
    onContinue: () -> Unit,
    onClose: () -> Unit
) {
    if (!uiState.isShowCompletePopup) return

    val challenge = uiState.customChallengeData
    val isAnswerTrue = uiState.isAnswerTrue
    val configuration = LocalConfiguration.current
    val popupWidth = (configuration.screenWidthDp * 0.6f).dp

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LocalContext.current
    LaunchedEffect(Unit) {
        if (isAnswerTrue) AudioPlayerManager.playSoundWin()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(shouldDismissOnClickOutside = false),
        containerColor = Color.Transparent,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = null
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            // This is the ACTUAL popup
            Column(
                modifier = Modifier
                    .width(popupWidth)
                    .background(Color.White, shape = RoundedCornerShape(
                        topStart = AppDimens.Dimens24,
                        topEnd = AppDimens.Dimens24,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ))
                    .padding(horizontal = Dimens16)
                    .padding(top = Dimens16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HANDLE (visual only)

                Box(
                    modifier = Modifier
                        .width(AppDimens.Dimens40)
                        .height(AppDimens.Dimens4)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(AppDimens.Dimens2)
                        )
                )

                Spacer(Modifier.height(Dimens12))

                Image(
                    painter = painterResource(
                        if (isAnswerTrue)
                            R.drawable.ic_complete
                        else
                            R.drawable.ic_not_complete_smiley
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(AppDimens.Dimens60)
                )

                Spacer(Modifier.height(Dimens12))

                Text(
                    text = if (isAnswerTrue)
                        stringResource(R.string.congratulations)
                    else
                        stringResource(R.string.sorry),
                    style = MaterialTheme.typography.titleLarge.scaled(),
                    color = if (isAnswerTrue)
                        Color.Black
                    else
                        Color.Black
                )

                Spacer(Modifier.height(AppDimens.Dimens4))

                Text(
                    text = if (isAnswerTrue) {
                        listOf(
                            stringResource(R.string.good_job),
                            stringResource(R.string.you_are_clever),
                            stringResource(R.string.you_are_glorious),
                            stringResource(R.string.you_are_brilliant),
                            stringResource(R.string.you_are_so_genius),
                            stringResource(R.string.you_are_so_intelligent)
                        ).random()
                    } else {
                        stringResource(R.string.better_luck_for_next_time)
                    },
                    textAlign = TextAlign.Center,
                    color = if (
                        isAnswerTrue)
                        colorResource(R.color.colorGreen)
                    else
                        colorResource(R.color.red)
                )

                Spacer(Modifier.height(AppDimens.Dimens8))

                // Answer (HTML)
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                            textSize = 14f * appScale()
                            typeface = ResourcesCompat.getFont(context, R.font.font_regular)
                        }
                    },
                    update = {
                        val html = challenge?.fullQuestion
                            ?.replace("+", " + ")
                            ?.replace("-", " - ")
                            ?.plus(" = <b>${challenge.answer}</b>")

                        it.text = HtmlCompat.fromHtml(html ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                )

                Spacer(Modifier.height(Dimens16))

                KidsActionButton(
                    modifier = Modifier.padding(horizontal = Dimens16),
                    text = stringResource(R.string.start_new_challenge),
                    icon = Icons.Default.RocketLaunch,
                    type = ButtonType.ORANGE,
                    onClick = onContinue
                )

                TextButton(
                    onClick = onClose,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.colorPrimary)
                    )
                ) {
                    Text(stringResource(R.string.no_i_want_to_close))
                }
            }
        }
    }
}

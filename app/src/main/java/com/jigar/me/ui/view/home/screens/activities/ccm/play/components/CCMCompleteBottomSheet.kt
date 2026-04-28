package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.utils.PlaySound

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

    val activity = LocalContext.current
    LaunchedEffect(Unit) {
        PlaySound.playWin(activity)
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
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ))
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HANDLE (visual only)

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(Modifier.height(12.dp))

                Image(
                    painter = painterResource(
                        if (isAnswerTrue)
                            R.drawable.ic_complete
                        else
                            R.drawable.ic_not_complete_smiley
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(60.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (isAnswerTrue)
                        stringResource(R.string.congratulations)
                    else
                        stringResource(R.string.sorry),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isAnswerTrue)
                        Color.Black
                    else
                        Color.Black
                )

                Spacer(Modifier.height(4.dp))

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

                Spacer(Modifier.height(8.dp))

                // Answer (HTML)
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                            setTextColor(ContextCompat.getColor(context, R.color.black))
                            textSize = 14f
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

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.colorPrimary),
                        contentColor = Color.White                           
                    )
                ) {
                    Text(stringResource(R.string.start_new_challenge))
                }

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

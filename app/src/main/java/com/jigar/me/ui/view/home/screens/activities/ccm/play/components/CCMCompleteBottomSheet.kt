package com.jigar.me.ui.view.home.screens.activities.ccm.play.components

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.appScale
import com.jigar.me.ui.view.home.common_ui.sheets.KidsBottomSheet
import com.jigar.me.ui.view.home.screens.activities.ccm.play.viewmodels.CCMPlayUiState
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.getButtonColors

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CCMCompleteBottomSheetCompose(
    uiState: CCMPlayUiState,
    onContinue: () -> Unit,
    onClose: () -> Unit
) {
    val challenge = uiState.customChallengeData
    val isAnswerTrue = uiState.isAnswerTrue

    LaunchedEffect(uiState.isShowCompletePopup) {
        if (uiState.isShowCompletePopup) {
            if (isAnswerTrue) AudioPlayerManager.playSoundWin() else AudioPlayerManager.playSoundOptionWrong()
        }
    }

    // "Candy Pop" theme -- gradient card (green on success, red on a miss),
    // icon in a white circle badge, bubble-style title, white pill CTA.
    // Built on the shared KidsBottomSheet -- same rounded-all-corners, floating
    // bottom margin, and thin sticker border as every other sheet in the app.
    val accentColors = getButtonColors(if (isAnswerTrue) ButtonType.GREEN else ButtonType.RED)

    KidsBottomSheet(
        visible = uiState.isShowCompletePopup,
        onDismiss = onClose,
        widthFraction = 0.6f,
        containerBackground = accentColors.gradient,
        handleColor = Color.White.copy(alpha = 0.5f),
        borderColor = Color.White.copy(alpha = 0.3f),
        showHandle = false
    ) {
        Spacer(Modifier.height(Dimens12))

        // Icon in a white circle badge with a dashed sticker ring -- matches iOS exactly.
        Box(
            modifier = Modifier
                .size(88.dp)
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = size.minDimension / 2f - 1.5.dp.toPx(),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 5f), 0f)
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        if (isAnswerTrue) R.drawable.ic_complete else R.drawable.ic_not_complete_smiley
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(AppDimens.Dimens48)
                )
            }
        }

        Text(
            text = if (isAnswerTrue) stringResource(R.string.congratulations) else stringResource(R.string.sorry),
            style = MaterialTheme.typography.titleLarge.scaled(),
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.padding(top = Dimens12)
        )

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
            color = Color.White.copy(alpha = 0.92f),
            modifier = Modifier.padding(top = AppDimens.Dimens4)
        )

        // Answer (HTML) -- white text so it stays readable on the gradient card
        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    setTextColor(ContextCompat.getColor(context, R.color.white))
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
            },
            modifier = Modifier.padding(top = AppDimens.Dimens4, bottom = Dimens16)
        )

        Box(
            modifier = Modifier
                .shadow(6.dp, RoundedCornerShape(percent = 50))
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.White)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    AudioPlayerManager.playSoundBtnClick()
                    onContinue()
                }
                .padding(horizontal = AppDimens.Dimens24, vertical = AppDimens.Dimens12),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = accentColors.base,
                    modifier = Modifier.size(AppDimens.Dimens20)
                )
                Spacer(Modifier.width(AppDimens.Dimens6))
                Text(
                    text = stringResource(R.string.start_new_challenge),
                    color = accentColors.base,
                    style = MaterialTheme.typography.bodyLarge.scaled(),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        TextButton(
            onClick = onClose,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White.copy(alpha = 0.85f)
            )
        ) {
            Text(stringResource(R.string.no_i_want_to_close))
        }

        Spacer(Modifier.height(AppDimens.Dimens8))
    }
}

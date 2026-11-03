package com.jigar.me.ui.view.home.common_ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.home.viewmodels.getFreeTrialUi
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.CommonUtils
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun FreeTrialDialog(
    remainingDays: Int, discountPer: Int, discountPerLifetime: Int,manualFreeTrialDays: Int, onYes: () -> Unit, onNo: () -> Unit, onDismiss: () -> Unit
) {
    val ui = remember { getFreeTrialUi(remainingDays,manualFreeTrialDays) }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(AppDimens.Dimens12), elevation = CardDefaults.cardElevation(AppDimens.Dimens8), modifier = Modifier
                    .fillMaxSize(0.8f)
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.bg_free_trial), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                    )
                    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(0.35f).fillMaxHeight().align(Alignment.CenterVertically)) {
                            if (ui.showNow) {
                                TrialNowSticker(
                                    Modifier
                                        .padding(AppDimens.Dimens8)
                                        .align(Alignment.TopStart)
                                        .graphicsLayer { rotationZ = -10f })
                            }

                            if (ui.showTrialStart) {
                                TrialBadge(
                                    days = if (manualFreeTrialDays == 3) 3 else 7,
                                    modifier = Modifier
                                        .size(AppDimens.Dimens260)
                                        .align(Alignment.Center)
                                        .padding(AppDimens.Dimens16)
                                )
                            }

                            if (ui.showDayLeft && ui.numberRes != null) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(ui.numberRes), contentDescription = null, modifier = Modifier.height(AppDimens.Dimens120)
                                    )
                                    Spacer(Modifier.height(AppDimens.Dimens8))
                                    TrialDaysLeftPill(remainingDays.coerceAtLeast(0), Modifier.height(AppDimens.Dimens72))
                                }
                            }
                        }

                        Spacer(Modifier.width(AppDimens.Dimens12))

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.65f).padding(end = AppDimens.Dimens16)) {

                            Image(
                                painter = painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.height(AppDimens.Dimens64)
                            )

                            Spacer(Modifier.height(AppDimens.Dimens8))

                            Text(
                                text = stringResource(ui.title), textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium.scaled().copy(color = Color.Black,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                            )

                            Text(
                                text = stringResource(ui.desc), textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.scaled().copy(color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                            )

                            Spacer(Modifier.height(AppDimens.Dimens4))

                            Text(
                                text = stringResource(R.string.ft_no_payment),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.scaled().copy(color = Color.DarkGray,
                                    fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
                            )

                            if (discountPer > 0 || discountPerLifetime > 0) {
                                Text(
                                    text = CommonUtils.htmlToAnnotatedString(stringResource(R.string.ft_limited_deal)),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall.scaled().copy(color = colorResource(R.color.red_700),
                                        fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
                                )
                            }

                            Spacer(Modifier.height(AppDimens.Dimens8))

                            KidsActionButton(
                                text = stringResource(ui.yesText),
                                icon = Icons.Default.RemoveRedEye,
                                type = ButtonType.BLUE,
                                onClick = onYes,
                                isSmall = true
                            )

                            if (ui.showNoButton && ui.noText != null) {
                                Text(
                                    text = stringResource(ui.noText), modifier = Modifier
                                        .padding(AppDimens.Dimens8)
                                        .clickable { onNo() },
                                    style = MaterialTheme.typography.bodySmall.scaled().copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }

                            Spacer(Modifier.height(AppDimens.Dimens8))
                        }
                    }
                }
            }
        }
    }
}

// ---- The trial pictures, drawn here so their words follow the app language (the old pictures had English baked in).

/** The round "N days free trial" badge: the number big, the words under it. */
@Composable
private fun TrialBadge(days: Int, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val side = maxWidth
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            drawCircle(Color(0xFFDCEBFF), r)
            drawCircle(
                androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFF4985F), Color(0xFFB871C9), Color(0xFF3F7BD8))),
                r * 0.9f,
            )
            drawCircle(Color.White, r * 0.9f, style = androidx.compose.ui.graphics.drawscope.Stroke(r * 0.045f))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$days",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = (side.value * 0.40f).sp, fontWeight = FontWeight.Black, color = Color.White,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), lineHeight = (side.value * 0.46f).sp,
                    shadow = androidx.compose.ui.graphics.Shadow(Color(0x55000000), androidx.compose.ui.geometry.Offset(2f, 3f), 4f),
                ),
            )
            com.jigar.me.ui.view.ab.AbShrinkText(
                text = androidx.compose.ui.res.pluralStringResource(R.plurals.ft_badge_days_free_trial, days),
                modifier = Modifier.fillMaxWidth(0.56f),
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = (side.value * 0.085f).sp, fontWeight = FontWeight.Black,
                    fontFamily = FontFamily(Font(R.font.font_extra_bold)), lineHeight = (side.value * 0.095f).sp,
                ),
                color = Color(0xFFFFE066), minScale = 0.5f, maxLines = 2,
            )
        }
    }
}

/** The yellow "days left" pill under the big number. */
@Composable
private fun TrialDaysLeftPill(count: Int, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFFE066), Color(0xFFF5A623))), shape)
            .border(4.dp, Color(0xFF1B2A5B), shape)
            .padding(horizontal = AppDimens.Dimens20),
        contentAlignment = Alignment.Center,
    ) {
        com.jigar.me.ui.view.ab.AbShrinkText(
            text = "\u2B50 " + androidx.compose.ui.res.pluralStringResource(R.plurals.ft_days_left, count),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily(Font(R.font.font_extra_bold)),
                shadow = androidx.compose.ui.graphics.Shadow(Color(0xFF1B2A5B), androidx.compose.ui.geometry.Offset(2f, 2f), 3f),
            ),
            color = Color.White, minScale = 0.5f, maxLines = 1,
        )
    }
}

/** The red "NOW" sticker on the 6-days-left page. */
@Composable
private fun TrialNowSticker(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.ft_now), modifier,
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 26.sp, fontWeight = FontWeight.Black, color = Color(0xFFE53935), fontFamily = FontFamily(Font(R.font.font_extra_bold)),
            shadow = androidx.compose.ui.graphics.Shadow(Color.White, androidx.compose.ui.geometry.Offset(2f, 2f), 3f),
        ),
    )
}

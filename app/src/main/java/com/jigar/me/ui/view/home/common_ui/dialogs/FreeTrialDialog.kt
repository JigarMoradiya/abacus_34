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
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.screens.home.viewmodels.getFreeTrialUi
import com.jigar.me.ui.view.home.theme.AppDimens
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.CommonUtils

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
                                Image(
                                    painter = painterResource(R.drawable.ic_now), contentDescription = null, modifier = Modifier
                                        .padding(AppDimens.Dimens8)
                                        .size(AppDimens.Dimens40)
                                        .align(Alignment.TopStart)
                                        .graphicsLayer { rotationZ = -10f })
                            }

                            if (ui.showTrialStart) {
                                Image(
                                    painter = if (manualFreeTrialDays == 3) painterResource(R.drawable.ic_free_trial_3) else painterResource(R.drawable.ic_free_trial_7), contentDescription = null, modifier = Modifier
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
                                    Image(
                                        painter = painterResource(R.drawable.free_trial_day_left), contentDescription = null, modifier = Modifier.height(AppDimens.Dimens72)
                                    )
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
                                text = ui.title, textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium.copy(color = Color.Black,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_extra_bold))),
                            )

                            Text(
                                text = ui.desc, textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.font_bold))),
                            )

                            Spacer(Modifier.height(AppDimens.Dimens4))

                            Text(
                                text = "No payment or subscription setup needed during your free trial.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.DarkGray,
                                    fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
                            )

                            if (discountPer > 0 || discountPerLifetime > 0) {
                                Text(
                                    text = CommonUtils.htmlToAnnotatedString("<strong>Limited time deal - </strong> unlock premium at a discounted price!"),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall.copy(color = colorResource(R.color.red_700),
                                        fontWeight = FontWeight.Medium,fontFamily = FontFamily(Font(R.font.font_medium))),
                                )
                            }

                            Spacer(Modifier.height(AppDimens.Dimens8))

                            KidsActionButton(
                                text = ui.yesText,
                                icon = Icons.Default.RemoveRedEye,
                                type = ButtonType.BLUE,
                                onClick = onYes,
                                isSmall = true
                            )

                            if (ui.showNoButton && ui.noText != null) {
                                Text(
                                    text = ui.noText, modifier = Modifier
                                        .padding(AppDimens.Dimens8)
                                        .clickable { onNo() },
                                    style = MaterialTheme.typography.bodySmall.copy(
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

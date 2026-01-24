package com.jigar.me.ui.view.jetpack.fragments.common.dialogs

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jigar.me.R
import com.jigar.me.ui.view.jetpack.core.presentation.components.PrimaryButton
import com.jigar.me.ui.view.jetpack.fragments.home.viewmodels.getFreeTrialUi
import com.jigar.me.utils.CommonUtils

@Composable
fun FreeTrialDialog(
    remainingDays: Int, discountPer: Int, discountPerLifetime: Int, onYes: () -> Unit, onNo: () -> Unit, onDismiss: () -> Unit
) {
    val ui = remember { getFreeTrialUi(remainingDays) }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(8.dp), modifier = Modifier
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
                                        .padding(8.dp)
                                        .size(40.dp)
                                        .align(Alignment.TopStart)
                                        .graphicsLayer { rotationZ = -10f })
                            }

                            if (ui.showTrialStart) {
                                Image(
                                    painter = painterResource(R.drawable.ic_free_trial), contentDescription = null, modifier = Modifier
                                        .size(260.dp)
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            }

                            if (ui.showDayLeft && ui.numberRes != null) {
                                Column(
                                    modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(ui.numberRes), contentDescription = null, modifier = Modifier.height(120.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Image(
                                        painter = painterResource(R.drawable.free_trial_day_left), contentDescription = null, modifier = Modifier.height(72.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.65f).padding(end = 16.dp)) {

                            Image(
                                painter = painterResource(R.drawable.logo_banner), contentDescription = null, modifier = Modifier.height(64.dp)
                            )

                            Spacer(Modifier.height(8.dp))

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

                            Spacer(Modifier.height(4.dp))

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

                            Spacer(Modifier.height(8.dp))

                            PrimaryButton(text = ui.yesText, onClick = onYes)

                            if (ui.showNoButton && ui.noText != null) {
                                Text(
                                    text = ui.noText, modifier = Modifier
                                        .padding(8.dp)
                                        .clickable { onNo() },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily(Font(R.font.font_bold))
                                    )
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

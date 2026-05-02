package com.jigar.me.ui.view.home.screens.purchase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.htmlToAnnotatedString
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.theme.AppDimens


@Composable
fun InfoSection(
    modifier: Modifier = Modifier,
    infoList: List<String>,title : String? = null
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painterResource(R.drawable.crown),
            contentDescription = null,
            modifier = Modifier.size(AppDimens.PurchaseCrownSize),
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(AppDimens.Dimens8))

        title?.let{
            Text(
                text = title,
                style = (if (DeviceInfo.isTablet) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge).scaled().copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = AppDimens.Dimens16)
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE7)),
            shape = RoundedCornerShape(AppDimens.Dimens12),
            modifier = Modifier
                .padding(vertical = AppDimens.Dimens12)
                .padding(start = AppDimens.Dimens2, end = AppDimens.Dimens16)
                .heightIn(max = AppDimens.Dimens320),
            elevation = CardDefaults.cardElevation(AppDimens.Dimens2)

        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimens.Dimens12)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(if (DeviceInfo.isTablet) AppDimens.Dimens8 else AppDimens.Dimens4)
            ) {
                infoList.forEach {
                    Text(
                        text = it.htmlToAnnotatedString(), style = MaterialTheme.typography.labelLarge.scaled().copy(fontWeight = FontWeight.Normal, fontFamily = FontFamily(Font(R.font.font_regular)))
                    )
                }
            }
        }
    }
}

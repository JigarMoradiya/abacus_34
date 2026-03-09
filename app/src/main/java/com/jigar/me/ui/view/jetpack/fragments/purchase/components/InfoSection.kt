package com.jigar.me.ui.view.jetpack.fragments.purchase.components

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
import com.jigar.me.ui.view.jetpack.utils.ui.extensions.htmlToAnnotatedString


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
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.activity_padding8)))

        title?.let{
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_padding16))
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE7)), shape = RoundedCornerShape(12.dp), modifier = Modifier
                .padding(dimensionResource(R.dimen.activity_padding12))
                .heightIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.activity_padding12))
                    .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding4))
            ) {
                infoList.forEach {
                    Text(
                        text = it.htmlToAnnotatedString(), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal, fontFamily = FontFamily(Font(R.font.font_regular)))
                    )
                }
            }
        }
    }
}

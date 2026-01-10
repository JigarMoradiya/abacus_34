package com.jigar.me.ui.view.jetpack.fragments.my_account.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.utils.DateTimeUtils.at_dd_mmm_yy_hh_mm_a
import com.jigar.me.utils.DateTimeUtils.formatTo
import com.jigar.me.utils.DateTimeUtils.toDate
import java.util.Locale

@Composable
fun StatisticsCard(
    title: String,
    count: String,
    time: String,
    backgroundColor: Color,
    countColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.activity_padding12), vertical = dimensionResource(R.dimen.activity_padding6))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontFamily = FontFamily(Font(R.font.font_semibold))),
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.activity_padding4)))
                Text(
                    count,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = countColor,
                        fontFamily = FontFamily(Font(R.font.font_bold))
                    )
                )
            }
            Text(time.toDate().formatTo(at_dd_mmm_yy_hh_mm_a).lowercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        }
    }
}

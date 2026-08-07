package com.jigar.me.ui.view.home.screens.my_account.components

import com.jigar.me.ui.view.home.theme.AppDimens

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
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jigar.me.R
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
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
        shape = RoundedCornerShape(AppDimens.Dimens12),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(AppDimens.Dimens2)
    ) {
        Column(modifier = Modifier.padding(horizontal = AppDimens.Dimens12, vertical = AppDimens.Dimens6)) {
            // Tight line boxes: no Android font padding + line height collapsed to
            // the font size, so the "last attend" line sits right under the count row
            val titleBase = MaterialTheme.typography.bodyMedium.scaled()
            val countBase = MaterialTheme.typography.titleLarge.scaled()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = titleBase.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily(Font(R.font.font_semibold)),
                        lineHeight = titleBase.fontSize,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    ),
                )
                Spacer(Modifier.width(AppDimens.Dimens4))
                Text(
                    count,
                    style = countBase.copy(
                        fontWeight = FontWeight.Bold,
                        color = countColor,
                        fontFamily = FontFamily(Font(R.font.font_bold)),
                        lineHeight = countBase.fontSize,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
            }
            val timeFormat = if (time.isEmpty()){
                "-"
            }else{
                time.toDate().formatTo(at_dd_mmm_yy_hh_mm_a).lowercase()
            }
            val timeBase = MaterialTheme.typography.labelMedium.scaled()
            Text(timeFormat,
                style = timeBase.copy(
                    lineHeight = timeBase.fontSize,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                ),
                fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        }
    }
}

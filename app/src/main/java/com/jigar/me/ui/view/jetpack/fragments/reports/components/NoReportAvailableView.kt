package com.jigar.me.ui.view.jetpack.fragments.reports.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.jigar.me.R

@Composable
fun NoReportAvailableView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_report_card),
                contentDescription = null,
                modifier = Modifier.size(
                    dimensionResource(R.dimen.image_size_no_data_view)
                )
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.activity_padding16)))

            Text(
                text = stringResource(R.string.no_report_available_yet),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.font_semibold)),
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

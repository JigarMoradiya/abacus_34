package com.jigar.me.ui.view.jetpack.fragments.my_account.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
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
import com.jigar.me.ui.view.jetpack.fragments.my_account.viewmodels.MyAccountMenu


@Composable
fun MyAccountChildRow(
    item: MyAccountMenu, onClick: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick(item.tag) }
        .padding(horizontal = dimensionResource(R.dimen.activity_padding12), vertical = dimensionResource(R.dimen.activity_padding2)), verticalAlignment = Alignment.CenterVertically) {

        // Icon (20dp x 32dp with vertical padding)
        item.menuIcon?.let {
            Icon(
                imageVector = it, contentDescription = null, modifier = Modifier
                    .size(width = 20.dp, height = 32.dp)
                    .padding(vertical = dimensionResource(R.dimen.activity_padding6))
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.activity_padding10)))

        // Title
        Text(
            text = item.menuTitle, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.font_semibold))
            )
        )

        // Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
            modifier = Modifier.size(16.dp), tint = Color.Gray
        )
    }
}

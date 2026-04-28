package com.jigar.me.ui.view.home.screens.category.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.ui.view.home.theme.AppDimens

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(AppDimens.Dimens6)
    ) {

        Card(
            shape = RoundedCornerShape(AppDimens.Dimens8),
            border = if (isSelected)
                BorderStroke(2.dp, colorResource(R.color.purple_700))
            else null,
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.size(80.dp).clickable { onClick() }
        ) {
            AsyncImage(
                model = category.icon,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_right_arrow),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = AppDimens.Dimens6)
                    .size(AppDimens.Dimens16),
                tint = Color.Unspecified
            )
        }
    }
}

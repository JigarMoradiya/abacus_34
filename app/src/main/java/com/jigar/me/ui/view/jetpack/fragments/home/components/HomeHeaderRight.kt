package com.jigar.me.ui.view.jetpack.fragments.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.jigar.me.R
import com.jigar.me.utils.AppConstants

@Composable
fun HomeHeaderRight(
    onMenuClick: (String) -> Unit
) {
    Row(modifier = Modifier
        .padding(horizontal = dimensionResource(R.dimen.activity_padding16))
        .padding(top = dimensionResource(R.dimen.activity_padding12)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.activity_padding12))) {
        MenuIconCard(
            type = AppConstants.HomeClicks.Menu_My_Account,
            icon = Icons.Default.AccountBox,
            onClick = { menuType ->
                onMenuClick(menuType)
            }
        )
        MenuIconCard(
            type = AppConstants.HomeClicks.Menu_Settings,
            icon = Icons.Default.Settings,
            onClick = { menuType ->
                onMenuClick(menuType)
            }
        )
        MenuIconCard(
            type = AppConstants.HomeClicks.Menu_Purchase_Store,
            icon = Icons.Default.Store,
            onClick = { menuType ->
                onMenuClick(menuType)
            }
        )
        MenuIconCard(
            type = AppConstants.HomeClicks.Menu_Video_Tutorial,
            icon = Icons.Default.OndemandVideo,
            onClick = { menuType ->
                onMenuClick(menuType)
            }
        )
    }
}


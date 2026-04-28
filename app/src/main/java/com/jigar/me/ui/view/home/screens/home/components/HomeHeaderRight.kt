package com.jigar.me.ui.view.home.screens.home.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.buttons.KidsIconButton
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.KidIconMedium
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.utils.AppConstants

@Composable
fun HomeHeaderRight(
    onMenuClick: (String) -> Unit
) {
    Row(modifier = Modifier
        .padding(horizontal = Dimens16),
        horizontalArrangement = Arrangement.spacedBy(Dimens12)) {

        KidsIconButton(
            icon = Icons.Default.AccountBox,
            onClick = {
                onMenuClick(AppConstants.HomeClicks.Menu_My_Account)
            },
            type = ButtonType.PINK,
            size = KidIconMedium
        )
        KidsIconButton(
            icon = Icons.Default.Settings,
            onClick = {
                onMenuClick(AppConstants.HomeClicks.Menu_Settings)
            },
            type = ButtonType.PINK,
            size = KidIconMedium
        )
        KidsIconButton(
            icon = Icons.Default.Store,
            onClick = {
                onMenuClick(AppConstants.HomeClicks.Menu_Purchase_Store)
            },
            type = ButtonType.PINK,
            size = KidIconMedium
        )
        KidsIconButton(
            icon = Icons.Default.OndemandVideo,
            onClick = {
                onMenuClick(AppConstants.HomeClicks.Menu_Video_Tutorial)
            },
            type = ButtonType.PINK,
            size = KidIconMedium
        )
    }
}


package com.jigar.me.ui.view.home.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.ui.extensions.scaled
import com.jigar.me.ui.view.home.common_ui.buttons.KidsActionButton
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens12
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens16
import com.jigar.me.ui.view.home.theme.AppDimens.Dimens8
import com.jigar.me.ui.view.home.theme.AppDimens.HomePageLogo
import com.jigar.me.ui.view.home.theme.ButtonType
import com.jigar.me.ui.view.home.theme.PrimaryBlue

@Composable
fun HomeHeaderLeft(
    onMyAccountClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = DeviceInfo.screenTopPadding(), bottom = Dimens8, start = DeviceInfo.screenHorizontalPadding(), end = Dimens16)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Abacus",
            modifier = Modifier.size(HomePageLogo)
        )

        Column(modifier = Modifier.padding(start = Dimens12)) {
            Text(
                text = stringResource(R.string.welcome_to_abacus_application),
                style = MaterialTheme.typography.bodyMedium.scaled().copy(color = PrimaryBlue, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.font_bold))),
            )

            KidsActionButton(
                text = stringResource(R.string.my_account),
                type = ButtonType.BLUE,
                onClick = onMyAccountClick,
                isSmall = true
            )

        }
    }
}


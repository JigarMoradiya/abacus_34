package com.jigar.me.ui.view.home.screens.whats_learning.components

import com.jigar.me.ui.view.home.theme.AppDimens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.buttons.KidsIconButton
import com.jigar.me.ui.view.home.screens.whats_learning.viewmodels.WhatsLearnNewUiState
import com.jigar.me.ui.view.home.screens.purchase.components.InfoSection
import com.jigar.me.ui.view.home.theme.AppDimens.KidIconMedium
import com.jigar.me.ui.view.home.theme.ButtonType

@Composable
@UnstableApi
fun WhatsLearnNewScreen(
    uiState: WhatsLearnNewUiState,
    onPageChanged: (Int) -> Unit,
    onClose: () -> Unit
) {
    Box {
        Row {
            // LEFT SIDE
            Box(modifier = Modifier.weight(1.2f).fillMaxSize()) {
                VideoPager(
                    videoList = uiState.videoList,
                    currentPosition = uiState.currentPosition,
                    onPageChanged = onPageChanged,
                    modifier = Modifier.fillMaxSize().align(Alignment.Center).padding(start = DeviceInfo.screenHorizontalPadding())
                )
            }

            Spacer(Modifier.padding(end = AppDimens.Dimens16))

            // RIGHT SIDE
            InfoSection(
                modifier = Modifier.weight(1f),
                infoList = uiState.videoList[uiState.currentPosition].pointsList,
            )

        }

        KidsIconButton(
            icon = Icons.Default.Close,
            onClick = {
                onClose()
            },
            type = ButtonType.BLUE,
            size = KidIconMedium,
            isPlayBackSound = true,
            modifier = Modifier.align(Alignment.TopStart).padding(start = DeviceInfo.screenHorizontalPadding(),top = DeviceInfo.screenTopPadding())
        )
    }
}

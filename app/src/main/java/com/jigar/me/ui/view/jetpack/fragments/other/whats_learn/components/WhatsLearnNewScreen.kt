package com.jigar.me.ui.view.jetpack.fragments.other.whats_learn.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.jigar.me.ui.view.jetpack.fragments.other.whats_learn.viewmodels.WhatsLearnNewUiState
import com.jigar.me.ui.view.jetpack.fragments.purchase.components.InfoSection

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
                    modifier = Modifier.fillMaxSize().align(Alignment.Center)
                )
            }

            // RIGHT SIDE
            InfoSection(
                modifier = Modifier.weight(1f),
                infoList = uiState.videoList[uiState.currentPosition].pointsList,
            )

        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dimensionResource(R.dimen.activity_padding16))
                .clickable { onClose() }
        )
    }
}

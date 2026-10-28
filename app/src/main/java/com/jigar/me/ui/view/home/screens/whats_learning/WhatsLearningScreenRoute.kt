package com.jigar.me.ui.view.home.screens.whats_learning

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.jigar.me.R
import com.jigar.me.data.local.data.DeviceInfo
import com.jigar.me.ui.view.ab.AbBackground
import com.jigar.me.ui.view.ab.AbTheme
import com.jigar.me.ui.view.ab.AbTopBar
import com.jigar.me.ui.view.ab.AbWorld
import com.jigar.me.ui.view.home.screens.whats_learning.components.WhatsLearnNewScreen
import com.jigar.me.ui.view.home.screens.whats_learning.viewmodels.WhatsLearnNewViewModel
import com.jigar.me.ui.view.home.theme.AppDimens

@OptIn(UnstableApi::class)
@Composable
fun WhatsLearningScreenRoute(
    onClose: () -> Unit,
) {
    val viewModel: WhatsLearnNewViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AbTheme {
        Box(Modifier.fillMaxSize()) {
            AbBackground(AbWorld.Lagoon, Modifier.fillMaxSize())

            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                AbTopBar(
                    title = stringResource(R.string.what_s_learn_in_application),
                    onBack = onClose,
                    modifier = Modifier.padding(
                        start = DeviceInfo.screenHorizontalPadding(),
                        end = AppDimens.Dimens16,
                        top = DeviceInfo.screenTopPadding()
                    )
                )

                WhatsLearnNewScreen(
                    uiState = uiState,
                    onPageChanged = { viewModel.changePager(it) }
                )
            }
        }
    }
}

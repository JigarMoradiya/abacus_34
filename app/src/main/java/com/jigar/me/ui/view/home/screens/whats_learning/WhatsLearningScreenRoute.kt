package com.jigar.me.ui.view.home.screens.whats_learning

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.jigar.me.ui.view.jetpack.fragments.other.whats_learn.components.WhatsLearnNewScreen
import com.jigar.me.ui.view.jetpack.fragments.other.whats_learn.viewmodels.WhatsLearnNewViewModel

@OptIn(UnstableApi::class)
@Composable
fun WhatsLearningScreenRoute(
    onClose: () -> Unit,
) {
    val viewModel: WhatsLearnNewViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        WhatsLearnNewScreen(
            uiState = uiState,
            onPageChanged = { viewModel.changePager(it) },
            onClose = onClose
        )
    }
}

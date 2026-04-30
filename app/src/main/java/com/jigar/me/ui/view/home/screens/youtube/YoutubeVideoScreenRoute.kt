package com.jigar.me.ui.view.home.screens.youtube

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jigar.me.R
import com.jigar.me.ui.view.home.common_ui.BackButtonWithText
import com.jigar.me.ui.view.home.screens.youtube.components.YoutubeVideoGrid
import com.jigar.me.ui.view.home.screens.youtube.viewmodels.YoutubeVideoViewModel

@Composable
fun YoutubeVideoScreenRoute(
    onBackClick: () -> Unit,
) {
    val viewModel: YoutubeVideoViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        BackButtonWithText(
            title = stringResource(R.string.video_tutorials),
            onBackClick = onBackClick
        )
        YoutubeVideoGrid(uiState.videoList)
    }
}

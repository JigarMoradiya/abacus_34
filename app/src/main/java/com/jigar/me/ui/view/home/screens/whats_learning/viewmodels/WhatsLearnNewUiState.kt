package com.jigar.me.ui.view.home.screens.whats_learning.viewmodels

import com.jigar.me.data.local.data.VideoTutorial

data class WhatsLearnNewUiState(
    val error: Int? = null,
    val currentPosition: Int = 0,
    val videoList : List<VideoTutorial> = emptyList()
)
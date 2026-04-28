package com.jigar.me.ui.view.home.screens.youtube.viewmodels

import com.jigar.me.data.model.VideoData

data class YoutubeVideoUiState(
    val error: Int? = null,
    val videoList: List<VideoData> = emptyList(),
)
package com.jigar.me.ui.view.jetpack.fragments.other.youtube_video.viewmodels

import com.jigar.me.data.model.VideoData

data class YoutubeVideoUiState(
    val error: Int? = null,
    val videoList: List<VideoData> = emptyList(),
)
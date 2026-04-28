package com.jigar.me.ui.view.home.screens.youtube.viewmodels

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.VideoData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class YoutubeVideoViewModel @Inject constructor(
    prefs: AppPreferencesHelper,
) : StatefulViewModel<YoutubeVideoUiState>() {

    override val TAG = "YoutubeVideoViewModel"

    override fun getInitialState() = YoutubeVideoUiState()

    init {
        val type = object : TypeToken<ArrayList<VideoData>>() {}.type
        val videoList: List<VideoData> = Gson().fromJson(prefs.getCustomParam(AppConstants.RemoteConfig.videoList,""),type)
        val sortedList = videoList.sortedWith { videoList1, videoList2 -> videoList1.so - videoList2.so }
        updateState_ {
            copy(videoList = sortedList)
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
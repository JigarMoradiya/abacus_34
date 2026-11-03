package com.jigar.me.ui.view.home.screens.youtube.viewmodels

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.VideoData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.jigar.me.utils.RemoteText

@HiltViewModel
class YoutubeVideoViewModel @Inject constructor(
    prefs: AppPreferencesHelper,
) : StatefulViewModel<YoutubeVideoUiState>() {

    override val TAG = "YoutubeVideoViewModel"

    override fun getInitialState() = YoutubeVideoUiState()

    init {
        val type = object : TypeToken<ArrayList<VideoData>>() {}.type
        // The list comes from Remote Config and is stored as "" when the app has not received it yet (first launch offline);
        // Gson returns null for "" (that crashed here), and a damaged value throws, so both give an empty list.
        val videoList: List<VideoData> = runCatching {
            Gson().fromJson<List<VideoData>>(RemoteText.read(prefs, AppConstants.RemoteConfig.videoList), type)
        }.getOrNull() ?: emptyList()
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
package com.jigar.me.ui.view.home.screens.whats_learning.viewmodels

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.VideoData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.view.home.screens.youtube.viewmodels.YoutubeVideoUiState
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class WhatsLearnNewViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    prefs: AppPreferencesHelper,
) : StatefulViewModel<WhatsLearnNewUiState>() {

    override val TAG = "WhatsLearnNewViewModel"

    override fun getInitialState() = WhatsLearnNewUiState()

    init {
        updateState_ {
            copy(videoList = DataProvider.getVideoPreviewList(context))
        }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }

    fun changePager(it: Int) {
        updateState_ {
            copy(currentPosition = it)
        }
    }
}
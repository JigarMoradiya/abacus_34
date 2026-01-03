package com.jigar.me.ui.view.jetpack.fragments.exam.play.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExamPlayViewModel @Inject constructor(
    prefs: AppPreferencesHelper,
) : StatefulViewModel<ExamPlayUiState>() {

    override val TAG = "CCMPlayViewModel"

    override fun getInitialState() = ExamPlayUiState()

    init {

    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
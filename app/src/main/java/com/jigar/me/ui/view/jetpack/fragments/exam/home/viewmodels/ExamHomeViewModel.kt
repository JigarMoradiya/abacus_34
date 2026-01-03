package com.jigar.me.ui.view.jetpack.fragments.exam.home.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExamHomeViewModel @Inject constructor(
    prefs: AppPreferencesHelper,
) : StatefulViewModel<ExamHomeUiState>() {

    override val TAG = "CCMHomeViewModel"

    override fun getInitialState() = ExamHomeUiState()

    init {

    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
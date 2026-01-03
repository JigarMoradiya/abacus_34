package com.jigar.me.ui.view.jetpack.fragments.exercise.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    prefs: AppPreferencesHelper,
) : StatefulViewModel<ExerciseUiState>() {

    override val TAG = "CCMHomeViewModel"

    override fun getInitialState() = ExerciseUiState()

    init {

    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
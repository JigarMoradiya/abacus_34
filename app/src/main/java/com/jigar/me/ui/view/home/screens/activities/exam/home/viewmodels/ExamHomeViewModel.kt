package com.jigar.me.ui.view.home.screens.activities.exam.home.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExamHomeViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
) : StatefulViewModel<ExamHomeUiState>() {

    override val TAG = "ExamHomeViewModel"

    override fun getInitialState() = ExamHomeUiState(
        isAdditionSelected = prefs.getCustomParamBoolean(AppConstants.EXAM.isAdditionSelected, true),
        isSubtractionSelected = prefs.getCustomParamBoolean(AppConstants.EXAM.isSubtractionSelected, false),
        isMultiplicationSelected = prefs.getCustomParamBoolean(AppConstants.EXAM.isMultiplicationSelected, false),
        isDivisionSelected = prefs.getCustomParamBoolean(AppConstants.EXAM.isDivisionSelected, false),
        selectedDifficulty = prefs.getCustomParam(AppConstants.EXAM.examDifficulty, AppConstants.EXAM.examDifficultyBeginner),
    )

    fun updateValues(type : String, value: Boolean) {
        prefs.setCustomParamBoolean(type, value)
        when (type) {
            AppConstants.EXAM.isAdditionSelected -> {
                updateState_ { copy(isAdditionSelected = value) }
            }
            AppConstants.EXAM.isSubtractionSelected -> {
                updateState_ { copy(isSubtractionSelected = value) }
            }
            AppConstants.EXAM.isMultiplicationSelected -> {
                updateState_ { copy(isMultiplicationSelected = value) }
            }
            AppConstants.EXAM.isDivisionSelected -> {
                updateState_ { copy(isDivisionSelected = value) }
            }
        }
    }

    fun updateRadioValue(type : String) {
        prefs.setCustomParam(AppConstants.EXAM.examDifficulty, type)
        updateState_ { copy(selectedDifficulty = type) }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
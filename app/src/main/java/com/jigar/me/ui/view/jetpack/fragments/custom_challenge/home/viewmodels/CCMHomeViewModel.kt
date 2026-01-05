package com.jigar.me.ui.view.jetpack.fragments.custom_challenge.home.viewmodels

import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.core.StatefulViewModel
import com.jigar.me.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CCMHomeViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
) : StatefulViewModel<CCMHomeUiState>() {

    override val TAG = "CCMHomeViewModel"

    override fun getInitialState() = CCMHomeUiState(
        totalQuestion = prefs.getCustomParamInt(AppConstants.CCM.totalQuestion, 10),
        questionGap = prefs.getCustomParamInt(AppConstants.CCM.questionGap, 3),
        questionMinLength = prefs.getCustomParamInt(AppConstants.CCM.questionMinLength, 2),
        questionMaxLength = prefs.getCustomParamInt(AppConstants.CCM.questionMaxLength, 3),
        isQuestionSpeak = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionSpeak, true),
        isQuestionShowNumber = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionShowNumber, true),
        isQuestionShowWord = prefs.getCustomParamBoolean(AppConstants.CCM.isQuestionShowWord, true),
    )

    fun updateValues(type : String, value: Int) {
        prefs.setCustomParamInt(type, value)
        when (type) {
            AppConstants.CCM.totalQuestion -> {
                updateState_ { copy(totalQuestion = value) }
            }
            AppConstants.CCM.questionGap -> {
                updateState_ { copy(questionGap = value) }
            }
            AppConstants.CCM.questionMinLength -> {
                updateState_ { copy(questionMinLength = value) }
            }
            AppConstants.CCM.questionMaxLength -> {
                updateState_ { copy(questionMaxLength = value) }
            }
        }
    }

    fun updateValues(type : String, value: Boolean) {
        prefs.setCustomParamBoolean(type, value)
        when (type) {
            AppConstants.CCM.isQuestionSpeak -> {
                updateState_ { copy(isQuestionSpeak = value) }
            }
            AppConstants.CCM.isQuestionShowNumber -> {
                updateState_ { copy(isQuestionShowNumber = value) }
            }
            AppConstants.CCM.isQuestionShowWord -> {
                updateState_ { copy(isQuestionShowWord = value) }
            }
        }
    }



    override fun onFailure(throwable: Throwable) {
        updateState_ {
            copy(error = localizeCommonFailure(throwable))
        }
    }
}
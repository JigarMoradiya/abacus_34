package com.jigar.me.ui.view.jetpack.fragments.activities.custom_challenge.play.viewmodels

import com.jigar.me.data.local.data.CustomChallengeData
import com.jigar.me.data.local.data.CustomChallengeQuestion


data class CCMPlayUiState(
    val error: Int? = null,
    val isLoading: Boolean = false,
    val isShowCompletePopup: Boolean = false,
    val isShowNoInternet: Boolean = false,
    val noInternetMessage : String = "",

    val isAnswerTrue: Boolean = false,
    val isAbacusOnLeftHand: Boolean = false,

    val totalQuestion: Int = 10,
    val questionGap: Int = 3,
    val questionMinLength: Int = 1,
    val questionMaxLength: Int = 3,
    val isQuestionSpeak: Boolean = false,
    val isQuestionShowNumber: Boolean = true,
    val isQuestionShowWord: Boolean = false,

    val customChallengeData: CustomChallengeData? = null,
    val currentIndex: Int = 0,
    val questionList: List<CustomChallengeQuestion> = emptyList(),

    val currentNumberText: String = "",
    val currentWordText: String = "",
    val isQuestionPhase: Boolean = true,   // question vs answer phase

    val answerDigits: List<String> = emptyList(),   // replaces listKeyboardAnswer
    val answerText: String = "",   // answer
)
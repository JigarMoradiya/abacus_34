package com.jigar.me.ui.view.jetpack.fragments.activities.exercise.viewmodels

import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.view.jetpack.abacus_base.ColorPresetModel
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.Exercise
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.ExerciseQuestionList
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.GridItemModel
import com.jigar.me.ui.view.jetpack.fragments.activities.exercise.exercise_generator.exercisesViewPageData


data class ExerciseUiState(
    val error: Int? = null,

    val currentColorPresetModel: ColorPresetModel = ColorPresetModel(),
    val isAbacusOnLeftHand: Boolean = false,

    val isLoading: Boolean = false,
    val isExerciseStarted: Boolean = false,


    val exercises: List<Exercise> = exercisesViewPageData,
    val exerciseQuestionList: List<ExerciseQuestionList> = emptyList(),
    val currentPage: Int = 0,
    val currentQueIndex: Int = 0,
    val selectedItems: Map<Int, GridItemModel> = emptyMap(),

    val answerDigits: List<String> = emptyList(),   // replaces listKeyboardAnswer
    val answerText: String = "",   // answer

    val elapsedSeconds: Int = 0,
    val totalCorrect: Int = 0,

    val isShowCompletePopup: Boolean = false,
    val isLeavePage: Boolean = false,
    val isShowNoInternet: Boolean = false,
    val noInternetMessage : String = "",

    val submitExerciseRequest : SubmitAllExamDataRequest? = null,
)
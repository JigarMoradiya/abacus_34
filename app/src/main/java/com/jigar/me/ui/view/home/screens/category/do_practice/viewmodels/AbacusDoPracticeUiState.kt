package com.jigar.me.ui.view.home.screens.category.do_practice.viewmodels

import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.ui.view.base.abacus_base.ColorPresetModel
import com.jigar.me.utils.AppConstants

data class AbacusDoPracticeUiState(
    val isLoading: Boolean = false,
    val isShowCompletePopup: Boolean = false,
    val currentColorPresetModel: ColorPresetModel = ColorPresetModel(),

    val setDetail: Set? = null,
    val setProgress: SetProgress? = null,
    val abacus: List<Abacus> = emptyList(),

    val currentIndexOfAbacus: Int = 0, // index of question list
    val currentIndexOfOperation: Int = 0, // index of question operation for addition and subtraction
    val currentIndexNum1: Int = 0, // index of 1st question operation for multiplication
    val currentIndexNum2: Int = 0, // index of 2nd question operation for multiplication
    val currentAbacusType: String = AppConstants.extras_Comman.AbacusTypeNumber,
    val currentAbacus: Abacus? = null,
    val currentAbacusFormula: List<ExamProvider.FormulaStep> = emptyList(),
    val currentSetTime: Long? = null,
    val isTimerRunning: Boolean = false,
    val isSumComplete: Boolean = false,

    val isStepByStep: Boolean = false ,
    val isFinalAnswer: Boolean = false ,
    val isShowSubmitAnswer: Boolean? = null,
    val isNextButtonEnable: Boolean = false,
    val submitExerciseRequest : SubmitAllExamDataRequest? = null,

    val error: Int? = null
)
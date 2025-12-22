package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.do_practice.viewmodels

import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.utils.AppConstants

data class AbacusDoPracticeUiState(
    val isLoading: Boolean = false,
    val isShowCompletePopup: Boolean = false,

    val setDetail: Set? = null,
    val setProgress: SetProgress? = null,
    val abacus: List<Abacus> = emptyList(),

    val currentIndexOfAbacus: Int = 0,
    val currentAbacusType: String = AppConstants.extras_Comman.AbacusTypeNumber,
    val currentAbacus: Abacus? = null,
    val currentSetTime: Long? = null,

    val isStepByStep: Boolean? = null,
    val isShowSubmitAnswer: Boolean? = null,
    val isNextButtonEnable: Boolean = false,

    val error: Int? = null
)
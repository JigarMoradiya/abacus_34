package com.jigar.me.ui.view.other.contactus.viewmodels

import androidx.annotation.StringRes
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand

data class ContactUsUiState(
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val description: String = "",
    val countryNameCode: String = "",
    val countryName: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val mobileError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val descriptionError: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val finishActivity: ConsumableCommand<Unit>? = null,
)

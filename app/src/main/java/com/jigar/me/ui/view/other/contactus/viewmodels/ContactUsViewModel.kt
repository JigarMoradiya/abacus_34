package com.jigar.me.ui.view.other.contactus.viewmodels

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.UserApiRepository
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val prefs: AppPreferencesHelper,
    private val apiRepository: UserApiRepository,
) : StatefulViewModel<ContactUsUiState>() {

    override val TAG = "ContactUsViewModel"

    private val type: String? = savedStateHandle[AppConstants.extras_Comman.type]

    override fun getInitialState() = ContactUsUiState()

    init {
        prefillFromLoginData()
    }

    private fun prefillFromLoginData() {
        val loginJson = prefs.getLoginData()
        if (loginJson.isNullOrEmpty()) return
        val loginData = runCatching { Gson().fromJson(loginJson, LoginData::class.java) }.getOrNull()
            ?: return
        updateState_ {
            copy(
                email = loginData.email.orEmpty(),
                countryNameCode = loginData.country.orEmpty(),
            )
        }
    }

    fun onNameChange(value: String) = updateState_ { copy(name = value, nameError = null) }
    fun onMobileChange(value: String) = updateState_ { copy(mobile = value, mobileError = null) }
    fun onEmailChange(value: String) = updateState_ { copy(email = value, emailError = null) }
    fun onDescriptionChange(value: String) = updateState_ { copy(description = value, descriptionError = null) }

    fun onCountrySelected(nameCode: String, name: String) {
        updateState_ { copy(countryNameCode = nameCode, countryName = name) }
    }

    fun onSubmit() {
        val current = state()
        val name = current.name.trim()
        val mobile = current.mobile.trim()
        val email = current.email.trim()
        val description = current.description.trim()

        val nameError: Int? = if (name.isEmpty()) R.string.please_enter_kid_name else null
        val mobileError: Int? = when {
            mobile.isEmpty() -> R.string.please_enter_mobile_number
            !Patterns.PHONE.matcher(mobile).matches() -> R.string.please_enter_valid_mobile_number
            else -> null
        }
        val emailError: Int? = when {
            email.isEmpty() -> R.string.please_enter_email_id
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.please_enter_valid_email_id
            else -> null
        }
        val descriptionError: Int? = when {
            description.isEmpty() -> R.string.please_enter_descritpion
            description.length < 50 -> R.string.please_enter_descritpion_minimum_50
            else -> null
        }

        if (nameError != null || mobileError != null || emailError != null || descriptionError != null) {
            updateState_ {
                copy(
                    nameError = nameError,
                    mobileError = mobileError,
                    emailError = emailError,
                    descriptionError = descriptionError,
                )
            }
            return
        }

        viewModelScope.launch {
            updateState_ { copy(isLoading = true) }
            val request = ContactUsRequest(
                type = type,
                name = name,
                email = email,
                phone = mobile,
                desription = description,
                country = current.countryName.ifEmpty { current.countryNameCode },
                city = null,
            )
            when (val response = apiRepository.contactUs(request)) {
                is Resource.Success -> {
                    if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                        updateState_ { copy(isLoading = false, showSuccessDialog = true) }
                    } else {
                        updateState_ {
                            copy(isLoading = false, errorMessage = response.value.error?.message)
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState_ { copy(isLoading = false, errorMessage = response.errorBody) }
                }
                else -> Unit
            }
        }
    }

    fun onSuccessDialogDismissed() {
        updateState_ { copy(showSuccessDialog = false, finishActivity = ConsumableCommand(Unit)) }
    }

    fun consumeError() = updateState_ { copy(errorMessage = null) }

    fun onBack() = updateState_ { copy(finishActivity = ConsumableCommand(Unit)) }

    override fun onFailure(throwable: Throwable) {
        updateState_ { copy(isLoading = false, errorMessage = throwable.localizedMessage) }
    }
}

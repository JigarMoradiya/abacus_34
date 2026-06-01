package com.jigar.me.ui.view.login.screens.login.viewmodels

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.view.login.data.PostLoginHandler
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
    private val apiRepository: StudentApiRepository,
    private val postLoginHandler: PostLoginHandler,
) : StatefulViewModel<LoginUiState>() {

    override val TAG = "LoginViewModel"

    override fun getInitialState() = LoginUiState()

    init {
        updateState_ {
            copy(privacyPolicyUrl = prefs.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl, ""))
        }
    }

    fun onEmailChange(value: String) {
        updateState_ { copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        updateState_ { copy(password = value, passwordError = null) }
    }

    fun onSubmit() {
        val current = state()
        val email = current.email.trim()
        val password = current.password
        val emailError: Int? = when {
            email.isEmpty() -> R.string.please_enter_email_id
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> R.string.please_enter_valid_email_id
            else -> null
        }
        val passwordError: Int? = if (password.isEmpty()) R.string.please_enter_password else null

        if (emailError != null || passwordError != null) {
            updateState_ { copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            updateState_ { copy(isLoading = true) }
            when (val response = apiRepository.login(LoginRequest(email, password))) {
                is Resource.Success -> {
                    if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                        handlePostLogin(response.value.data)
                    } else {
                        updateState_ { copy(isLoading = false, errorMessage = response.value.error?.message) }
                    }
                }
                is Resource.Failure -> {
                    updateState_ { copy(isLoading = false, errorMessage = response.errorBody) }
                }
                else -> Unit
            }
        }
    }

    private suspend fun handlePostLogin(data: JsonObject?) {
        when (val outcome = postLoginHandler.fetchProgressSetData(data)) {
            is PostLoginHandler.Outcome.NavigateHome -> {
                updateState_ { copy(isLoading = false, navigateToHome = ConsumableCommand(Unit)) }
            }
            is PostLoginHandler.Outcome.Failure -> {
                updateState_ { copy(isLoading = false, errorMessage = outcome.message) }
            }
        }
    }

    fun consumeError() {
        updateState_ { copy(errorMessage = null) }
    }

    override fun onFailure(throwable: Throwable) {
        updateState_ { copy(isLoading = false, errorMessage = throwable.localizedMessage) }
    }
}

package com.jigar.me.ui.view.login.screens.login_home.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.ui.jetpack.core.StatefulViewModel
import com.jigar.me.ui.jetpack.core.domain.ConsumableCommand
import com.jigar.me.ui.view.login.data.GoogleSignInHelper
import com.jigar.me.ui.view.login.data.PostLoginHandler
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginHomeViewModel @Inject constructor(
    private val prefs: AppPreferencesHelper,
    private val apiRepository: StudentApiRepository,
    private val googleSignInHelper: GoogleSignInHelper,
    private val postLoginHandler: PostLoginHandler,
) : StatefulViewModel<LoginHomeUiState>() {

    override val TAG = "LoginHomeViewModel"

    override fun getInitialState() = LoginHomeUiState()

    init {
        loadRemoteContent()
        viewModelScope.launch { googleSignInHelper.signOut() }
    }

    private fun loadRemoteContent() {
        updateState_ {
            copy(
                notesHtml = prefs.getCustomParam(AppConstants.RemoteConfig.newVersionNotes, ""),
                bulkLoginHtml = prefs.getCustomParam(AppConstants.RemoteConfig.bulkLogin, ""),
                privacyPolicyUrl = prefs.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl, ""),
            )
        }
    }

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            updateState_ { copy(isLoading = true) }
            when (val outcome = googleSignInHelper.signIn(activityContext)) {
                is GoogleSignInHelper.Outcome.Success -> {
                    socialLogin(outcome.email, outcome.idToken)
                }
                GoogleSignInHelper.Outcome.Cancelled -> {
                    updateState_ { copy(isLoading = false) }
                }
                is GoogleSignInHelper.Outcome.Failure -> {
                    updateState_ {
                        copy(
                            isLoading = false,
                            errorMessage = outcome.throwable.localizedMessage,
                        )
                    }
                }
            }
        }
    }

    private suspend fun socialLogin(email: String?, idToken: String) {
        val response = apiRepository.socialLogin(SocialLoginRequest(email, idToken))
        when (response) {
            is Resource.Success -> {
                if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                    handlePostLogin(response.value.data?.let { it } )
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

    private suspend fun handlePostLogin(data: com.google.gson.JsonObject?) {
        when (val outcome = postLoginHandler.fetchAbacusDataAndContinue(data)) {
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

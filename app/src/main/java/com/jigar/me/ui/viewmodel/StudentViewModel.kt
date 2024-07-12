package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.ChangePasswordRequest
import com.jigar.me.data.model.data.ForgotPasswordRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.data.model.data.ResetPasswordRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
import com.jigar.me.data.repositories.Result
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(private val apiRepository: StudentApiRepository) :
    ViewModel() {
    private val _signInWithGoogle = MutableLiveData<Boolean>()
    val signInWithGoogle: LiveData<Boolean>
        get() = _signInWithGoogle

    fun signInWithGoogle() {
        _signInWithGoogle.value = true
    }

    var googleSignInClient: GoogleSignInClient? = null

    init {
        initGoogleAuthentication()
    }

    fun initGoogleAuthentication() {
        val idToken = MyApplication.instance?.getString(R.string.web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(idToken ?: "")
            .build()
        googleSignInClient = MyApplication.instance?.let { GoogleSignIn.getClient(it, gso) }
        googleSignInClient?.signOut()
    }

    suspend fun signInWithGoogle(task: Task<GoogleSignInAccount>): Result<FirebaseUser?> {
        return try {
            val account: GoogleSignInAccount =
                task.getResult(ApiException::class.java)
            val credential =
                GoogleAuthProvider.getCredential(account.idToken, null)
            val result =
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
            Result.Success(result.user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    private val _signupResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val signupResponse: LiveData<Resource<MainAPIResponse>> get() = _signupResponse
    fun signup(request: SignupV2Request) = viewModelScope.launch {
        _signupResponse.value = Resource.Loading
        _signupResponse.value = apiRepository.signup(request)
    }

    private val _verificationResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val verificationResponse: LiveData<Resource<MainAPIResponse>> get() = _verificationResponse
    fun verification(request: VerifyEmailRequest) = viewModelScope.launch {
        _verificationResponse.value = Resource.Loading
        _verificationResponse.value = apiRepository.verification(request)
    }

    private val _loginResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<MainAPIResponse>> get() = _loginResponse
    fun login(request: LoginRequest) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = apiRepository.login(request)
    }

    fun socialLogin(request: SocialLoginRequest) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = apiRepository.socialLogin(request)
    }

    private val _handleExistingPurchaseResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val handleExistingPurchaseResponse: LiveData<Resource<MainAPIResponse>> get() = _handleExistingPurchaseResponse
    fun handleExistingPurchase(request: PurchasedPlanCheckRequest) = viewModelScope.launch {
        _handleExistingPurchaseResponse.value = Resource.Loading
        _handleExistingPurchaseResponse.value = apiRepository.handleExistingPurchase(request)
    }

    private val _changePlanResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val changePlanResponse: LiveData<Resource<MainAPIResponse>> get() = _changePlanResponse
    fun changePlan(request: PurchasedPlanCheckRequest) = viewModelScope.launch {
        _changePlanResponse.value = Resource.Loading
        _changePlanResponse.value = apiRepository.changePlan(request)
    }

    private val _resendOTPResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val resendOTPResponse: LiveData<Resource<MainAPIResponse>> get() = _resendOTPResponse
    fun resendOTP(request: ResendOTPRequest) = viewModelScope.launch {
        _resendOTPResponse.value = Resource.Loading
        _resendOTPResponse.value = apiRepository.resendOTP(request)
    }


    private val _forgotPasswordResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val forgotPasswordResponse: LiveData<Resource<MainAPIResponse>> get() = _forgotPasswordResponse
    fun forgotPassword(request: ForgotPasswordRequest) = viewModelScope.launch {
        _forgotPasswordResponse.value = Resource.Loading
        _forgotPasswordResponse.value = apiRepository.forgotPassword(request)
    }

    private val _resetPasswordResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val resetPasswordResponse: LiveData<Resource<MainAPIResponse>> get() = _resetPasswordResponse
    fun resetPassword(request: ResetPasswordRequest) = viewModelScope.launch {
        _resetPasswordResponse.value = Resource.Loading
        _resetPasswordResponse.value = apiRepository.resetPassword(request)
    }

    private val _changePasswordResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val changePasswordResponse: LiveData<Resource<MainAPIResponse>> get() = _changePasswordResponse
    fun changePassword(request: ChangePasswordRequest) = viewModelScope.launch {
        _changePasswordResponse.value = Resource.Loading
        _changePasswordResponse.value = apiRepository.changePassword(request)
    }

    private val _updateProfileResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val updateProfileResponse: LiveData<Resource<MainAPIResponse>> get() = _updateProfileResponse
    fun updateProfile(request: UpdateProfileRequest) = viewModelScope.launch {
        _updateProfileResponse.value = Resource.Loading
        _updateProfileResponse.value = apiRepository.updateProfile(request)
    }
}
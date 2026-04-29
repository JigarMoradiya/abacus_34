package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(private val apiRepository: StudentApiRepository) :
    ViewModel() {

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

    private val _getAbacusDataResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val getAbacusDataResponse: LiveData<Resource<MainAPIResponse>> get() = _getAbacusDataResponse
    fun getAbacusData(request: FetchAbacusDataRequest) = viewModelScope.launch {
        _getAbacusDataResponse.value = Resource.Loading
        _getAbacusDataResponse.value = apiRepository.getAbacusData(request)
    }
    private val _appReviewsListResponse: MutableLiveData<Resource<MainAPIResponse>> =
        MutableLiveData()
    val appReviewsListResponse: LiveData<Resource<MainAPIResponse>> get() = _appReviewsListResponse
    fun appReviewsList() = viewModelScope.launch {
        _appReviewsListResponse.value = Resource.Loading
        _appReviewsListResponse.value = apiRepository.appReviewsList()
    }


    private val _submitReviewResult: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val submitReviewResult: LiveData<Resource<MainAPIResponse>> get() = _submitReviewResult
    fun submitReview(plan_id: RequestBody,description: RequestBody, image_1: MultipartBody.Part?) = viewModelScope.launch {
        _submitReviewResult.value = Resource.Loading
//        _submitReviewResult.value = apiRepository.submitReview(plan_id,description, image_1)
    }
}
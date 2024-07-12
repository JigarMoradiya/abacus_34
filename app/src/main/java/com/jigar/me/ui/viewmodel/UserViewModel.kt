package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.repositories.UserApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val apiRepository: UserApiRepository) :
    ViewModel() {

    private val _contactUsResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val contactUsResponse: LiveData<Resource<MainAPIResponse>> get() = _contactUsResponse
    fun contactUs(request: ContactUsRequest) = viewModelScope.launch {
        _contactUsResponse.value = Resource.Loading
        _contactUsResponse.value = apiRepository.contactUs(request)
    }
}
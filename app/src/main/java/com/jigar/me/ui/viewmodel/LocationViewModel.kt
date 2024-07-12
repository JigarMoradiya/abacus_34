package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.repositories.LocationApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val apiRepository: LocationApiRepository) : ViewModel() {

    private val _getStateResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val getStateResponse: LiveData<Resource<MainAPIResponse>> get() = _getStateResponse
    fun getStateList(countryCode : String? = null, stateCode : String? = null) = viewModelScope.launch {
        _getStateResponse.value = Resource.Loading
        _getStateResponse.value = apiRepository.getLocation(countryCode, stateCode)
    }

    private val _getCityResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val getCityResponse: LiveData<Resource<MainAPIResponse>> get() = _getCityResponse
    fun getCityList(countryCode : String? = null, stateCode : String? = null) = viewModelScope.launch {
        _getCityResponse.value = Resource.Loading
        _getCityResponse.value = apiRepository.getLocation(countryCode, stateCode)
    }
}
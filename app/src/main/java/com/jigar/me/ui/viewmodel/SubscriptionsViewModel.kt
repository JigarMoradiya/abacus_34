package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.CancelReactivePlanRequest
import com.jigar.me.data.model.data.PurchasePlanCreateRequest
import com.jigar.me.data.model.data.PurchaseSuccessRequest
import com.jigar.me.data.repositories.SubscriptionsApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(private val apiRepository: SubscriptionsApiRepository) : ViewModel() {
    private val _getPlanListResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val getPlanListResponse: LiveData<Resource<MainAPIResponse>> get() = _getPlanListResponse
    fun getPlanList() = viewModelScope.launch {
        _getPlanListResponse.value = Resource.Loading
        _getPlanListResponse.value = apiRepository.getPlansList()
    }

    private val _purchasePlanCreateResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val purchasePlanCreateResponse: LiveData<Resource<MainAPIResponse>> get() = _purchasePlanCreateResponse
    fun purchasePlanCreate(request : PurchasePlanCreateRequest) = viewModelScope.launch {
        _purchasePlanCreateResponse.value = Resource.Loading
        _purchasePlanCreateResponse.value = apiRepository.purchasePlanCreate(request)
    }

    private val _purchaseSuccessResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val purchaseSuccessResponse: LiveData<Resource<MainAPIResponse>> get() = _purchaseSuccessResponse
    fun purchaseSuccess(request : PurchaseSuccessRequest) = viewModelScope.launch {
        _purchaseSuccessResponse.value = Resource.Loading
        _purchaseSuccessResponse.value = apiRepository.purchaseSuccess(request)
    }

    private val _cancelReactivePlanResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val cancelReactivePlanResponse: LiveData<Resource<MainAPIResponse>> get() = _cancelReactivePlanResponse
    fun cancelReactivePlan(planId : String?, request : CancelReactivePlanRequest) = viewModelScope.launch {
        _cancelReactivePlanResponse.value = Resource.Loading
        _cancelReactivePlanResponse.value = apiRepository.cancelReactivePlan(planId, request)
    }

    private val _accountDetailResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val accountDetailResponse: LiveData<Resource<MainAPIResponse>> get() = _accountDetailResponse
    fun getAccountDetail() = viewModelScope.launch {
        _accountDetailResponse.value = Resource.Loading
        _accountDetailResponse.value = apiRepository.getAccountDetail()
    }
}
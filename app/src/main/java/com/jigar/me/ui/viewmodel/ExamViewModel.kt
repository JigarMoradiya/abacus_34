package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.data.repositories.ExamApiRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(private val apiRepository: ExamApiRepository) : ViewModel() {
    private val _submitAllExamResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val submitAllExamResponse: LiveData<Resource<MainAPIResponse>> get() = _submitAllExamResponse
    fun submitAllExam(request : SubmitAllExamDataRequest) = viewModelScope.launch {
        _submitAllExamResponse.value = Resource.Loading
        _submitAllExamResponse.value = apiRepository.submitAllExam(request)
    }

    private val _getAllExamResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val getAllExamResponse: LiveData<Resource<MainAPIResponse>> get() = _getAllExamResponse
    fun getAllExam(type : String? = null,from_date : String? = null, to_date : String? = null, from : Int = 0) = viewModelScope.launch {
        _getAllExamResponse.value = Resource.Loading
        _getAllExamResponse.value = apiRepository.getAllExam(type,from_date, to_date, from)
    }
    private val _getStatisticsResponse: MutableLiveData<Resource<MainAPIResponse>> = MutableLiveData()
    val getStatisticsResponse: LiveData<Resource<MainAPIResponse>> get() = _getStatisticsResponse
    fun getStatistics() = viewModelScope.launch {
        _getStatisticsResponse.value = Resource.Loading
        _getStatisticsResponse.value = apiRepository.getStatistics()
    }
}
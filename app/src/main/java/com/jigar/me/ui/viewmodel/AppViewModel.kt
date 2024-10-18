package com.jigar.me.ui.viewmodel

import androidx.lifecycle.*
import com.jigar.me.data.model.MainAPIResponseArray
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.repositories.ApiRepository
import com.jigar.me.data.repositories.DBRepository
import com.jigar.me.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val apiRepository: ApiRepository,private val dbRepository: DBRepository) : ViewModel() {
    private val _getPracticeMaterialResponse: MutableLiveData<Resource<MainAPIResponseArray>> = MutableLiveData()
    val getPracticeMaterialResponse: LiveData<Resource<MainAPIResponseArray>> get() = _getPracticeMaterialResponse
    fun getPracticeMaterial(type : String) = viewModelScope.launch {
        _getPracticeMaterialResponse.value = Resource.Loading
        _getPracticeMaterialResponse.value = apiRepository.getPracticeMaterial(type)
    }

    suspend fun getPurchasesSku() = dbRepository.getPurchasesSku()
    fun getInAppSKU(displayList : ArrayList<String>) = dbRepository.getInAppSKU(displayList)
    fun getInAppSKUPurchasedLive() = dbRepository.getInAppSKUPurchasedLive()
    suspend fun deleteInAppSKU() = dbRepository.deleteInAppSKU()
    suspend fun deleteInAppPurchase() = dbRepository.deleteInAppPurchase()

    fun getInAppSKUDetail(sku : String) : LiveData<List<InAppSkuDetails>>{
        val result = MutableLiveData<List<InAppSkuDetails>>()
        viewModelScope.launch(Dispatchers.IO) {
            val list = dbRepository.getInAppSKUDetail(sku)
            result.postValue(list)
        }
        return result
    }

    suspend fun saveExamResultDB(data: ExamHistory) = dbRepository.saveExamResultDB(data)
    fun getExamHistoryList(examType :String) = dbRepository.getExamHistoryList(examType)

    // abacus all data
    fun getLevel() = dbRepository.getLevel()
    suspend fun getCategory(id: String) = dbRepository.getCategory(id)
    suspend fun getPages(id: String) = dbRepository.getPages(id)
    suspend fun getSet(id: String) = dbRepository.getSet(id)
    suspend fun getSetDetail(setId : String) = dbRepository.getSetDetail(setId)
    suspend fun getAllSet() = dbRepository.getAllSet()
    suspend fun getAbacus(id: String) = dbRepository.getAbacus(id)
    suspend fun updateSetProgress(setId : String,currentAbacusId : String) = dbRepository.updateSetProgress(setId,currentAbacusId)
    suspend fun updateSetTimer(setId : String,time : Long) = dbRepository.updateSetTimer(setId,time)

}
package com.jigar.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.DBRepository
import com.jigar.me.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val dbRepository: DBRepository,private val prefManager : AppPreferencesHelper) : ViewModel() {

    var tempPuzzleSize: Int = 0
    private val _permissionResult = MutableLiveData<Boolean?>()
    val permissionResult: LiveData<Boolean?> = _permissionResult

    fun checkAllowPermission() {
        viewModelScope.launch {
            val purchasedSKU = dbRepository.getInAppSKUPurchased()
            val isPurchased = CommonUtils.checkPurchaseForExerciseExamCCM(prefManager, purchasedSKU)
            _permissionResult.postValue(isPurchased)
//            _permissionResult.postValue(false)
        }
    }
    fun resetNavigation() {
        _permissionResult.value = null
    }


    suspend fun getPurchasesSku() = dbRepository.getPurchasesSku()
    fun getInAppSKU(displayList : ArrayList<String>) = dbRepository.getInAppSKU(displayList)
    suspend fun getInAppSKUPurchased() = dbRepository.getInAppSKUPurchased()
    suspend fun getInAppSKUPurchasedLiveExclude(excludeIds : ArrayList<String>) = dbRepository.getInAppSKUPurchasedLiveExclude(excludeIds)
    suspend fun getInAppSKUPurchased(ids : ArrayList<String>) = dbRepository.getInAppSKUPurchased(ids)

    fun getExamHistoryList(examType :String) = dbRepository.getExamHistoryList(examType)

    // abacus all data
    suspend fun insertLevel(data : List<Level>) = dbRepository.insertLevel(data)
    suspend fun insertAllData(dataLevel: ArrayList<Level>, dataCategory: ArrayList<Category>, dataPages: ArrayList<Pages>, dataSet: ArrayList<com.jigar.me.data.model.dbtable.abacus_all_data.Set>, dataAbacus: ArrayList<Abacus>)  = dbRepository.insertAllData(dataLevel,dataCategory,dataPages,dataSet,dataAbacus)
    fun getLevel() = dbRepository.getLevel()
    suspend fun getLevel(list : List<String>) = dbRepository.getLevel(list)
    suspend fun getCategory(id: String) = dbRepository.getCategory(id)
    suspend fun getPages(id: String, isGetAllData: Boolean)= dbRepository.getPages(id,isGetAllData)
    suspend fun getSetDetail(setId : String) = dbRepository.getSetDetail(setId)
    suspend fun getSetProgress(setId : String) = dbRepository.getSetProgress(setId)
    suspend fun getAllSet() = dbRepository.getAllSet()
    suspend fun getAbacus(id: String) = dbRepository.getAbacus(id)
    suspend fun insertSetProgress(data : List<SetProgress>) = dbRepository.insertSetProgress(data)
    suspend fun updateUserAnswer(abacusId : String,userAnswer : String) = dbRepository.updateUserAnswer(abacusId,userAnswer)
    suspend fun removeUserAnswer(setId : String) = dbRepository.removeUserAnswer(setId)
}
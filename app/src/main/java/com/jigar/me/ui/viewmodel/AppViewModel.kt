package com.jigar.me.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.DBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val dbRepository: DBRepository,private val prefManager : AppPreferencesHelper) : ViewModel() {

    fun getInAppSKU(displayList : ArrayList<String>) = dbRepository.getInAppSKU(displayList)
    suspend fun getInAppSKUPurchasedLiveExclude(excludeIds : ArrayList<String>) = dbRepository.getInAppSKUPurchasedLiveExclude(excludeIds)
    suspend fun getInAppSKUPurchased(ids : ArrayList<String>) = dbRepository.getInAppSKUPurchased(ids)

    // abacus all data
    suspend fun insertLevel(data : List<Level>) = dbRepository.insertLevel(data)

    suspend fun insertSetProgress(data : List<SetProgress>) = dbRepository.insertSetProgress(data)
}
package com.jigar.me.data.repositories

import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDB
import com.jigar.me.data.local.db.exam.ExamHistoryDB
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDB
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDB
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import javax.inject.Inject

class DBRepository @Inject constructor(
    private val inAppPurchaseDB: InAppPurchaseDB,
    private val inAppSKUDB: InAppSKUDB,
    private val examHistoryDB: ExamHistoryDB,
    private val abacusAllDataDB: AbacusAllDataDB,
) : SafeApiCall {

    suspend fun getPurchasesSku() = inAppPurchaseDB.getPurchasesSku()
    suspend fun deleteInAppPurchase() = inAppPurchaseDB.deleteInAppPurchase()

    fun getInAppSKU(displayList : ArrayList<String>) = inAppSKUDB.getInAppSKU(displayList)
    suspend fun getInAppSKUPurchased() = inAppSKUDB.getInAppSKUPurchased()
    suspend fun getInAppSKUPurchasedLiveExclude(excludeIds : ArrayList<String>) = inAppSKUDB.getInAppSKUPurchasedLiveExclude(excludeIds)
    suspend fun deleteInAppSKU() = inAppSKUDB.deleteInAppSKU()
    fun getExamHistoryList(examType: String) = examHistoryDB.getExamHistoryList(examType)

    // abacus all data
    suspend fun insertLevel(data : List<Level>) = abacusAllDataDB.insertLevel(data)
    suspend fun insertAllData(dataLevel: ArrayList<Level>, dataCategory: ArrayList<Category>, dataPages: ArrayList<Pages>, dataSet: ArrayList<com.jigar.me.data.model.dbtable.abacus_all_data.Set>, dataAbacus: ArrayList<Abacus>)  = abacusAllDataDB.insertAllData(dataLevel,dataCategory,dataPages,dataSet,dataAbacus)
    fun getLevel() = abacusAllDataDB.getLevel()
    suspend fun getCategory(id : String) = abacusAllDataDB.getCategory(id)
    suspend fun getPages(id: String, isGetAllData: Boolean) = abacusAllDataDB.getPages(id,isGetAllData)
    suspend fun getSet(id : String) = abacusAllDataDB.getSet(id)
    suspend fun getSetDetail(setId : String) = abacusAllDataDB.getSetDetail(setId)
    suspend fun getSetProgress(setId : String) = abacusAllDataDB.getSetProgress(setId)
    suspend fun getAllSet() = abacusAllDataDB.getAllSet()
    suspend fun getAbacus(id : String) = abacusAllDataDB.getAbacus(id)
    suspend fun insertSetProgress(data : List<SetProgress>) = abacusAllDataDB.insertSetProgress(data)
    suspend fun deleteSetProgress(setId : String) = abacusAllDataDB.deleteSetProgress(setId)
    suspend fun deleteAllData() = abacusAllDataDB.deleteAllData()
    suspend fun updateSetTimer(setId : String,time : Long) = abacusAllDataDB.updateSetTimer(setId,time)
    suspend fun updateUserAnswer(abacusId : String,userAnswer : String) = abacusAllDataDB.updateUserAnswer(abacusId,userAnswer)
    suspend fun removeUserAnswer(setId : String) = abacusAllDataDB.removeUserAnswer(setId)
}
package com.jigar.me.data.repositories

import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.local.db.exam.ExamHistoryDB
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDB
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDB
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import javax.inject.Inject

class DBRepository @Inject constructor(
    private val inAppPurchaseDB: InAppPurchaseDB,
    private val inAppSKUDB: InAppSKUDB,
    private val examHistoryDB: ExamHistoryDB
) : SafeApiCall {

    suspend fun getPurchasesSku() = inAppPurchaseDB.getPurchasesSku()
    suspend fun deleteInAppPurchase() = inAppPurchaseDB.deleteInAppPurchase()

    fun getInAppSKU(displayList : ArrayList<String>) = inAppSKUDB.getInAppSKU(displayList)
    fun getInAppSKUPurchasedLive() = inAppSKUDB.getInAppSKUPurchasedLive()
    suspend fun deleteInAppSKU() = inAppSKUDB.deleteInAppSKU()
    fun getInAppSKUDetail(sku : String) = inAppSKUDB.getInAppSKUDetail(sku)

    suspend fun saveExamResultDB(data: ExamHistory) = examHistoryDB.insert(data)
    fun getExamHistoryList(examType: String) = examHistoryDB.getExamHistoryList(examType)
}
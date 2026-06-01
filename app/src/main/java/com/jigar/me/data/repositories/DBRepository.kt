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
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import javax.inject.Inject

class DBRepository @Inject constructor(
    private val inAppSKUDB: InAppSKUDB,
    private val abacusAllDataDB: AbacusAllDataDB,
) : SafeApiCall {

    fun getInAppSKU(displayList : ArrayList<String>) = inAppSKUDB.getInAppSKU(displayList)
    suspend fun getInAppSKUPurchasedLiveExclude(excludeIds : ArrayList<String>) = inAppSKUDB.getInAppSKUPurchasedLiveExclude(excludeIds)
    suspend fun getInAppSKUPurchased(ids : ArrayList<String>) = inAppSKUDB.getInAppSKUPurchased(ids)

    // abacus all data
    suspend fun insertLevel(data : List<Level>) = abacusAllDataDB.insertLevel(data)
    suspend fun insertSetProgress(data : List<SetProgress>) = abacusAllDataDB.insertSetProgress(data)
    suspend fun insertAllData(
        levels: ArrayList<Level>,
        categories: ArrayList<Category>,
        pages: ArrayList<Pages>,
        sets: ArrayList<Set>,
        abacus: ArrayList<Abacus>
    ) = abacusAllDataDB.insertAllData(levels, categories, pages, sets, abacus)
}
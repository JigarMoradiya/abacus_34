package com.jigar.me.data.local.db.inapp.sku

import androidx.lifecycle.LiveData
import com.android.billingclient.api.ProductDetails
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InAppSKUDB @Inject constructor(private val dao: InAppSKUDao,private val preferencesHelper: AppPreferencesHelper) {
    suspend fun saveInAppSKU(data: MutableList<ProductDetails>) = withContext(Dispatchers.IO) {
        dao.insertOrUpdate(data)
    }
    suspend fun getInAppSKUPurchased(): List<InAppSkuDetails> {
        return dao.getInAppSKUPurchased()
    }
    suspend fun getInAppSKUPurchasedLiveExclude(excludeIds : ArrayList<String>): List<InAppSkuDetails> {
        return dao.getInAppSKUPurchasedLiveExclude(excludeIds)
    }
    suspend fun getInAppSKUPurchased(ids : ArrayList<String>): List<InAppSkuDetails> {
        return dao.getInAppSKUPurchased(ids)
    }
    fun getInAppSKU(displayList : ArrayList<String>): LiveData<List<InAppSkuDetails>> {
        return dao.getInAppSku(displayList)
    }
    suspend fun deleteInAppSKU() = withContext(Dispatchers.IO) {
        dao.deleteInAppSKU()
    }
}

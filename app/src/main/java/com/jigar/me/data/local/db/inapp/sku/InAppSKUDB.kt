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
    fun getInAppSKUPurchasedLive(): LiveData<List<InAppSkuDetails>> {
        return dao.getInAppSKUPurchasedLive()
    }
    fun getInAppSKU(displayList : ArrayList<String>): LiveData<List<InAppSkuDetails>> {
        return dao.getInAppSku(displayList)
//        return if (preferencesHelper.getCustomParam(AppConstants.AbacusProgress.Ads,"").equals("Y",true)){
//            dao.getInAppSku()
//        }else{
//            dao.getInAppSkuNoAds()
//        }

    }
    fun getInAppSKUDetail(sku : String): List<InAppSkuDetails> {
        return dao.getInAppSkuDetail(sku)
    }
    suspend fun deleteInAppSKU() = withContext(Dispatchers.IO) {
        dao.deleteInAppSKU()
    }
}

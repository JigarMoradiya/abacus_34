package com.jigar.me.data.local.db.inapp.purchase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.Purchase
import com.google.gson.Gson
import com.jigar.me.data.model.dbtable.inapp.InAppPurchaseDetails
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old

@Dao
interface InAppPurchaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(inAppPurchaseDetails: InAppPurchaseDetails)

    @Transaction
    fun insert(vararg purchases: Purchase) {
        purchases.map {
            if (it.products.isNotEmpty()){
                val sku = if (it.products.first() == PRODUCT_ID_All_lifetime_old){
                    PRODUCT_ID_All_lifetime
                }else{
                    it.products.first()
                }
                it.orderId?.let { orderId ->
                    insertData(InAppPurchaseDetails(orderId,sku,it.developerPayload,it.purchaseToken,
                        it.purchaseTime,it.purchaseState,it.isAcknowledged,it.signature,it.originalJson,it.isAutoRenewing))
                }
            }
        }
    }

    @Query("SELECT sku FROM tableInAppPurchase WHERE purchaseState = 1")
    suspend fun getPurchasesSku(): List<String>

    @Query("DELETE FROM tableInAppPurchase")
    suspend fun deleteInAppPurchase()

}
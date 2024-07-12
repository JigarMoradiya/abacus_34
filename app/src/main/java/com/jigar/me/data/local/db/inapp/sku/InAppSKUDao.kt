package com.jigar.me.data.local.db.inapp.sku

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.ui.view.base.inapp.BillingRepository


@Dao
interface InAppSKUDao {
    @Query("SELECT SKU.*,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE SKU.sku IN (:displayList) ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    fun getInAppSku(displayList : ArrayList<String>): LiveData<List<InAppSkuDetails>>

    @Query("SELECT SKU.*,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE P.orderId IS NOT NULL ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    fun getInAppSKUPurchasedLive(): LiveData<List<InAppSkuDetails>>

    @Query("SELECT SKU.*,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE SKU.sku != '${BillingRepository.AbacusSku.PRODUCT_ID_ads}' ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    fun getInAppSkuNoAds(): LiveData<List<InAppSkuDetails>>

    @Query("SELECT SKU.*,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE SKU.sku = :sku AND SKU.type = '${BillingClient.ProductType.INAPP}' ORDER BY SKU.price_amount_micros DESC")
    fun getInAppSkuDetail(sku: String): List<InAppSkuDetails>

    @Transaction
    fun insertOrUpdate(skuDetails: MutableList<ProductDetails>) = skuDetails.apply {
        skuDetails.map {
            val originalJson = Gson().toJson(it)
            if (it.productType == BillingClient.ProductType.INAPP){
                val detail = InAppSkuDetails(it.productId, it.productType, it.oneTimePurchaseOfferDetails?.formattedPrice,
                    it.oneTimePurchaseOfferDetails?.priceAmountMicros, it.oneTimePurchaseOfferDetails?.priceCurrencyCode,
                    it.title, it.description, originalJson, offerToken = "")
                insert(detail)
            }else{
                if (!it.subscriptionOfferDetails.isNullOrEmpty()){
                    val offerDetail = it.subscriptionOfferDetails?.first()
                    val pricingPhaseList = offerDetail?.pricingPhases?.pricingPhaseList
                    if (!pricingPhaseList.isNullOrEmpty()){
                        val data = pricingPhaseList.first()
                        val originalPrice = if (pricingPhaseList.size > 1){
                            val data2 = pricingPhaseList[1]
                            data2?.formattedPrice
                        }else{""}
                        val detail = InAppSkuDetails(it.productId, it.productType, data?.formattedPrice,
                            data?.priceAmountMicros, data?.priceCurrencyCode,
                            it.title, it.description, originalJson, offerToken = offerDetail.offerToken, billingPeriod = data?.billingPeriod, originalPrice = originalPrice)
                        insert(detail)
                    }
                }
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inAppSkuDetails: InAppSkuDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(inAppSkuDetails: List<InAppSkuDetails>)

    @Query("DELETE FROM tableInAppSKU")
    suspend fun deleteInAppSKU()
}
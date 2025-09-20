package com.jigar.me.data.local.db.inapp.sku

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.model.dbtable.inapp.PricingPhasesCustom
import com.jigar.me.data.model.dbtable.inapp.SubscriptionOfferDetailsCustom
import com.jigar.me.ui.view.base.inapp.BillingRepository


@Dao
interface InAppSKUDao {
    @Query("SELECT SKU.sku,SKU.type,SKU.price,SKU.price_amount_micros,SKU.price_currency_code,SKU.title,SKU.originalJson,SKU.description,SKU.offerToken,SKU.billingPeriod,SKU.originalPrice,SKU.discountPer,SKU.sortOrder,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE SKU.sku IN (:displayList) ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    fun getInAppSku(displayList : ArrayList<String>): LiveData<List<InAppSkuDetails>>

    @Query("SELECT SKU.sku,SKU.type,SKU.price,SKU.price_amount_micros,SKU.price_currency_code,SKU.title,SKU.originalJson,SKU.description,SKU.offerToken,SKU.billingPeriod,SKU.originalPrice,SKU.discountPer,SKU.sortOrder,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE P.orderId IS NOT NULL ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    fun getInAppSKUPurchasedLive(): LiveData<List<InAppSkuDetails>>

    @Query("SELECT SKU.sku,SKU.type,SKU.price,SKU.price_amount_micros,SKU.price_currency_code,SKU.title,SKU.originalJson,SKU.description,SKU.offerToken,SKU.billingPeriod,SKU.originalPrice,SKU.discountPer,SKU.sortOrder,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE P.orderId IS NOT NULL AND SKU.sku NOT IN (:displayList) ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    suspend fun getInAppSKUPurchasedLiveExclude(displayList : ArrayList<String>): List<InAppSkuDetails>

    @Query("SELECT SKU.sku,SKU.type,SKU.price,SKU.price_amount_micros,SKU.price_currency_code,SKU.title,SKU.originalJson,SKU.description,SKU.offerToken,SKU.billingPeriod,SKU.originalPrice,SKU.discountPer,SKU.sortOrder,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE P.orderId IS NOT NULL ORDER BY SKU.type DESC, SKU.price_amount_micros DESC")
    suspend fun getInAppSKUPurchased(): List<InAppSkuDetails>

    @Query("SELECT SKU.sku,SKU.type,SKU.price,SKU.price_amount_micros,SKU.price_currency_code,SKU.title,SKU.originalJson,SKU.description,SKU.offerToken,SKU.billingPeriod,SKU.originalPrice,SKU.discountPer,SKU.sortOrder,CASE WHEN (P.orderId IS NULL) THEN 0 ELSE 1 END as isPurchase,CASE WHEN (P.orderId IS NULL) THEN '' ELSE P.orderId END as orderId,P.purchaseTime FROM tableInAppSKU as SKU LEFT JOIN tableInAppPurchase as P ON (SKU.sku = P.sku AND P.purchaseState = 1) WHERE SKU.sku = :sku AND SKU.type = '${BillingClient.ProductType.INAPP}' ORDER BY SKU.price_amount_micros DESC")
    fun getInAppSkuDetail(sku: String): List<InAppSkuDetails>

    @Transaction
    fun insertOrUpdate(skuDetails: MutableList<ProductDetails>) = skuDetails.apply {
        skuDetails.map {
            if (it.productType == BillingClient.ProductType.INAPP){
                val detail = InAppSkuDetails(it.productId, it.productType, it.oneTimePurchaseOfferDetails?.formattedPrice,
                    it.oneTimePurchaseOfferDetails?.priceAmountMicros, it.oneTimePurchaseOfferDetails?.priceCurrencyCode,
                     it.name, it.description, null, offerToken = "")
                insert(detail)
            }else{
                if (!it.subscriptionOfferDetails.isNullOrEmpty()){
                    val offerDetail = it.subscriptionOfferDetails?.first()
                    offerDetail?.let{ offer ->
                        with(offer){
                            val pricingPhasesCustom: ArrayList<PricingPhasesCustom> = arrayListOf()
                            pricingPhases.pricingPhaseList.map {
                                with(it){
                                    pricingPhasesCustom.add(PricingPhasesCustom(priceAmountMicros,priceCurrencyCode,formattedPrice,billingPeriod,recurrenceMode,billingCycleCount))
                                }
                            }
                            val customDetail = SubscriptionOfferDetailsCustom(offerToken,basePlanId,offerId,pricingPhasesCustom)
                            val originalJson = Gson().toJson(customDetail)
                            val pricingPhaseList = pricingPhases.pricingPhaseList
                            if (!pricingPhaseList.isNullOrEmpty()){
                                pricingPhaseList.find { it.priceAmountMicros > 0 }.also { findData ->
                                    val data = findData ?: pricingPhaseList.first()
                                    val originalPrice = if (pricingPhaseList.size > 1){
                                        val data2 = pricingPhaseList[1]
                                        data2?.formattedPrice
                                    }else{""}
                                    val detail = InAppSkuDetails(it.productId, it.productType, data?.formattedPrice,
                                        data?.priceAmountMicros, data?.priceCurrencyCode,
                                        it.name, it.description, originalJson, offerToken = offerDetail.offerToken, billingPeriod = data?.billingPeriod, originalPrice = originalPrice)
                                    insert(detail)
                                }

                            }
                        }
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
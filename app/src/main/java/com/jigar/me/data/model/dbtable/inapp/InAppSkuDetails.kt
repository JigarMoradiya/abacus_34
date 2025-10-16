package com.jigar.me.data.model.dbtable.inapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.google.gson.Gson
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old

@Entity(tableName = "tableInAppSKU")
data class InAppSkuDetails(
    @PrimaryKey val sku: String,
    val type: String?,
    val price: String?,
    val price_amount_micros: Long?,
    val price_currency_code: String?,
    val title: String?,
    val description: String?,
    val originalJson: String?,
    val isPurchase: Boolean = false, /* Not in SkuDetails; it's the augmentation */
    val orderId: String = "", /* Not in SkuDetails; it's the augmentation */
    val purchaseTime: Long = 0L, /* Not in SkuDetails; it's the augmentation */
    val offerToken: String? = null,
    val billingPeriod: String? = null,
    val originalPrice: String? = null,
    val discountPer: String? = null,
    var sortOrder: Int = 0
){
    fun getDurationTxt() : String{
        return if (isSubscriptionPlan()) {
            if (billingPeriod.equals("p1m",true)){
                "Monthly Plan"
            }else if (billingPeriod.equals("p3m",true)){
                "For 3 Month"
            }else if (billingPeriod.equals("p6m",true)){
                "For 6 Month"
            }else if (billingPeriod.equals("p1y",true)){
                "Yearly Plan"
            }else if (billingPeriod.equals("p1w",true)){
                "Weekly Plan"
            }else{
                "-"
            }
        } else {
            "Lifetime Plan"
        }
    }
    fun getTrialTxt() : String{
        return if (isSubscriptionPlan()) {
            var text = ""
            val offerDetail = Gson().fromJson(originalJson, SubscriptionOfferDetailsCustom::class.java)
            val pricingPhaseList = offerDetail?.pricingPhases
            pricingPhaseList?.find { it.priceAmountMicros == 0L }.also { findData ->
                if (findData != null){
                    if (findData.billingPeriod.equals("p1w",true)){
                        text = "charged after 7-day free trial"
                    }else if (findData.billingPeriod.equals("p3d",true)){
                        text = "charged after 3-day free trial"
                    }
                }
            }
            text
        } else {
            ""
        }
    }
    fun calculateSavings(monthlyMicros: Long, yearlyMicros: Long): Int {
        val monthlyPrice = monthlyMicros / 1_000_000.0
        val yearlyPrice = yearlyMicros / 1_000_000.0

        val monthlyCostForYear = monthlyPrice * 12
        val savings = monthlyCostForYear - yearlyPrice
        val percentage = (savings / monthlyCostForYear) * 100

        return percentage.toInt() // round down
    }
    fun getDisplayPrice() : String{
        return if (isSubscriptionPlan()) {
            if (billingPeriod.equals("p1m",true)){
                "$price / month"
            }else if (billingPeriod.equals("p3m",true)){
                "$price / 3 month"
            }else if (billingPeriod.equals("p6m",true)){
                "$price / 6 month"
            }else if (billingPeriod.equals("p1y",true)){
                "$price / year"
            }else if (billingPeriod.equals("p1w",true)){
                "$price / week"
            }else{
                "-"
            }
        } else {
            "$price / one-time purchase"
        }
    }
    fun isSubscriptionPlan() = type == BillingClient.ProductType.SUBS
    fun getDesc() = "$description"
}

data class SubscriptionOfferDetailsCustom(
    val offerIdToken: String? = null,
    val basePlanId: String? = null,
    val offerId: String? = null,
    val pricingPhases: ArrayList<PricingPhasesCustom>? = null,
)

data class PricingPhasesCustom(
    val priceAmountMicros: Long = 0,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    val billingPeriod: String,
    val recurrenceMode: Int = 0,
    val billingCycleCount: Int = 0
)
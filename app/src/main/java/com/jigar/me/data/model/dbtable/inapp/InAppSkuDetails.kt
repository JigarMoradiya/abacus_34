package com.jigar.me.data.model.dbtable.inapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.billingclient.api.BillingClient
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
            if (billingPeriod.equals("p3m",true)){
                "For 3 Month"
            }else if (billingPeriod.equals("p6m",true)){
                "For 6 Month"
            }else if (billingPeriod.equals("p1y",true)){
                "For 1 Year"
            }else{
                "-"
            }
        } else {
            "Life Time"
        }
    }
    fun isSubscriptionPlan() = type == BillingClient.ProductType.SUBS
    fun getDesc() = "$description"
//        if (sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1 || sku == BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month6){
//        "$description<br/><br/><b>Including Practice Material.</b><br/><b><font color='#000000'>Offline not supported.</font></b>"
//    }else if (isSubscriptionPlan()){
//        "$description<br/><br/><b><font color='#000000'>Practice Material not included.<br/>Offline not supported.</font></b>"
//    }else if(sku == PRODUCT_ID_All_lifetime || sku == PRODUCT_ID_All_lifetime_old) {
//        "$description<br/><br/><b>Including Practice Material.</b><br/><b>Offline Supported.</b>"
//    } else{
//        "$description"
//    }
}
package com.jigar.me.data.model.data

import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month6
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Weekly
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_ads
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_level1_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_level2_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_level3_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_maths
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_material_nursery
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.extensions.format


data class SubscribedList(
    var plan_id: String? = null,
    var start_date: String? = null,
    var end_date: String? = null,
    var is_cancelled: Boolean = false,
    var is_expired: Boolean = false,
    var purchase_price: String? = null,
    var purchase_currency: String? = null,
    var master_plan: Plans? = null,
){
    fun getPriceWithSymbol() : String{
        return if (purchase_currency.equals("inr",true)){
            AppConstants.APP_PLAN_DATA.Symbol_INR+" "+(purchase_price?:"0").toDouble().format()
        }else{
            AppConstants.APP_PLAN_DATA.Symbol_USD+" "+(purchase_price?:"0").toDouble().format()
        }
    }

    fun getStartDateFormat() : String?{
        return start_date?.let { "Purchased on : "+DateTimeUtils.convertDateFormat(it,DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,DateTimeUtils.dd_MMM_yyyy) }
    }


    fun getEndDateFormat() : String?{
        return if (!master_plan?.google_plan_id.isNullOrBlank() &&
            (master_plan?.google_plan_id == PRODUCT_ID_All_lifetime_old || master_plan?.google_plan_id == PRODUCT_ID_All_lifetime
                    || master_plan?.google_plan_id == PRODUCT_ID_level1_lifetime || master_plan?.google_plan_id == PRODUCT_ID_level2_lifetime
                    || master_plan?.google_plan_id == PRODUCT_ID_level3_lifetime || master_plan?.google_plan_id == PRODUCT_ID_ads
                    || master_plan?.google_plan_id == PRODUCT_ID_material_maths || master_plan?.google_plan_id == PRODUCT_ID_material_nursery)) {
            "No Expiry Date"
        } else {
            if (is_expired){
                end_date?.let { "Expired on : "+DateTimeUtils.convertDateFormat(it,DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,DateTimeUtils.dd_MMM_yyyy) }
            }else{
                end_date?.let { "Expiring on : "+DateTimeUtils.convertDateFormat(it,DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,DateTimeUtils.dd_MMM_yyyy) }
            }
        }

    }

    fun getBtnTextIsRenew() : Boolean{
        return is_expired
    }
    fun isBtnVisible() : Boolean{
        return if (is_expired){
            true
        }else master_plan?.isTrialPlan == true
    }

    fun isCancelBtnVisible() = !is_expired && !is_cancelled && master_plan?.google_plan_id.isNullOrEmpty()
    fun isReActiveBtnVisible() = !is_expired && is_cancelled
    fun getStatusText() : String{
        return if (is_expired){
            "Expired"
        }else if (is_cancelled){
            "Cancelled"
        }else{
            "Actived"
        }
    }
}

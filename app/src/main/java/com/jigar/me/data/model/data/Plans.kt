package com.jigar.me.data.model.data

import com.google.gson.annotations.SerializedName
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.format
import java.util.Locale

data class Plans(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("price_in_inr") var priceINR: String? = null,
    @SerializedName("price_in_usd") var priceUSD: String? = null,
    @SerializedName("term") var term: String? = null,
    @SerializedName("tag") var tag: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("is_trial_plan") var isTrialPlan: Boolean = false,
    @SerializedName("isSelected") var isSelected: Boolean = false,
    @SerializedName("trial_period") var trial_period: Int = 0,
    @SerializedName("google_plan_id") var google_plan_id: String? = null,
){
    fun getPlanName() : String?{
        return if (isTrialPlan){
            "Trial Plan ($trial_period Days)"
        }else{
            name
        }
    }
    fun getPlanBilled() : String{
        return if (isTrialPlan){
            "$trial_period Days"
        }else{
            "Billed "+ name.toString().lowercase(Locale.getDefault())
        }
    }
    fun getPriceWithSymbol(isCurrencyINR : Boolean) : String{
        return if (isCurrencyINR){
            AppConstants.APP_PLAN_DATA.Symbol_INR+" "+(priceINR?:"0").toDouble().format()
        }else{
            AppConstants.APP_PLAN_DATA.Symbol_USD+" "+(priceUSD?:"0").toDouble().format()
        }
    }
}
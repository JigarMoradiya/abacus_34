package com.jigar.me.utils

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.data.ReviewData
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1
import com.jigar.me.utils.extensions.show
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


object CommonUtils {
    fun getCurrentCurrency(isCurrencyINR: Boolean) = if (isCurrencyINR){ AppConstants.APP_PLAN_DATA.Currency_INR }else{ AppConstants.APP_PLAN_DATA.Currency_USD }
    fun getCurrentCurrencySymbol(isCurrencyINR: Boolean) = if (isCurrencyINR){ AppConstants.APP_PLAN_DATA.Symbol_INR }else{ AppConstants.APP_PLAN_DATA.Symbol_USD }
    @SuppressLint("RestrictedApi")
    fun setErrorToEditText(textInputLayout: TextInputLayout, validation_message: String?) {
        textInputLayout.error = validation_message
        textInputLayout.requestFocus()
    }
    fun removeError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
    }
    fun applySpeechSettings(prefManager : AppPreferencesHelper,tts: TextToSpeech){
        // Update the Setting to latest.
        val currentLanguage = prefManager.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, "")
        val localLanguage = if (currentLanguage.isEmpty()){
            Locale(AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE)
        }else{
            Gson().fromJson(currentLanguage, Locale::class.java)
        }
        val currentVoice = prefManager.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE, AppPreferencesHelper.DEFAULT_TTS_VOICE_VALUE)
        val currentSpeed = prefManager.getDefaultTTSSpeed()
        val currentPitch = prefManager.getDefaultTTSPitch()
        tts.voice = Voice(currentVoice, localLanguage, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, false, setOf(""))
//        tts.language = localLanguage
        tts.setPitch(currentPitch)
        tts.setSpeechRate(currentSpeed)
    }
    fun getCurrentTimeMessage(context: Context):String{
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        //        6 AM - 12 PM Morning slots
//        12 PM - 5 PM Afternoon slots
//        5 PM - 9 PM Evening Slots
//        9 PM - 6 AM Night Slots
        return when (timeOfDay) {
            in 6..11 -> context.getString(R.string.good_morning)
            in 12..16 -> context.getString(R.string.good_afternoon)
//                in 17..20 -> "Good Evening"
            else -> context.getString(R.string.good_evening)
        }
    }
    fun calculateNoOfColumns(columnWidthDp: Float, parentWidth : Float): Int {
        // For example columnWidthdp=180
//        val displayMetrics = context.resources.displayMetrics
//        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (parentWidth / columnWidthDp + 0.5).toInt() // +0.5 for correct rounding to int.
    }
    fun getQuestionHighLighterColor(context: Context,abacusType : AbacusContent?): Int {
        return if (abacusType != null){
            if (abacusType.equals(AppConstants.Settings.theam_Poligon_Silver) || abacusType.equals(AppConstants.Settings.theam_Poligon_Brown)){
                ContextCompat.getColor(context,R.color.black)
            }else{
//                mixTwoColors(ContextCompat.getColor(context,abacusType.dividerColor1), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.40f)
                ContextCompat.getColor(context,abacusType.resetBtnColor8)
            }
        }else{
            ContextCompat.getColor(context, R.color.red)
        }
    }
    fun getQuestionBorderColor(context: Context,abacusType : AbacusContent?): Int {
        return if (abacusType != null){
            mixTwoColors(ContextCompat.getColor(context,R.color.white), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.75f)
        }else{
            ContextCompat.getColor(context, R.color.red_600)
        }
    }
    fun mixTwoColors(color1: Int, color2: Int, amount: Float): Int {
        val ALPHA_CHANNEL: Byte = 24
        val RED_CHANNEL: Byte = 16
        val GREEN_CHANNEL: Byte = 8
        //final byte BLUE_CHANNEL = 0;
        val inverseAmount = 1.0f - amount
        val r = ((color1 shr RED_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr RED_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val g = ((color1 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * amount + (color2 shr GREEN_CHANNEL.toInt() and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val b = ((color1 and 0xff).toFloat() * amount + (color2 and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        return 0xff shl ALPHA_CHANNEL.toInt() or (r shl RED_CHANNEL.toInt()) or (g shl GREEN_CHANNEL.toInt()) or b
    }
    fun removeTrailingZero(formattingInput: String): String {
        if (!formattingInput.contains(".")) {
            return formattingInput
        }
        val dotPosition = formattingInput.indexOf(".")
        val newValue = formattingInput.substring(dotPosition, formattingInput.length)
        return if (newValue == ".0") {
            formattingInput.substring(0, dotPosition)
        } else formattingInput
    }
    fun blinkView(view: View, repeatCount : Int? = null){
        view.show()
        val animation: Animation = AlphaAnimation(1F, AppConstants.BLINK_ICON_ANIMATION_ALPHA) //to change visibility from visible to invisible
        animation.duration = AppConstants.BLINK_ICON_ANIMATION_DURATION //duration for each animation cycle
        animation.interpolator = LinearInterpolator()
        if (repeatCount == null){
            animation.repeatCount = Animation.INFINITE //repeating indefinitely
        }else{
            animation.repeatCount = repeatCount
        }
        animation.repeatMode = Animation.REVERSE //animation will start from end point once ended.
        view.startAnimation(animation) //to start animation
    }
    fun AppPreferencesHelper.getCurrentSumFromPref(pageId : String) : Int? {
        var currentPos : Int? = null
        try {
            val pageSum: String = getCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM, "{}")
            val objJson = JSONObject(pageSum)
            if (objJson.has(pageId)) {
                currentPos = objJson.getInt(pageId)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return currentPos
    }
    fun Context.getCurrentSumFromPref(pageId : String) : Int? {
        var currentPos : Int? = null
        try {
            val pageSum: String = AppPreferencesHelper(this, AppConstants.PREF_NAME)
                .getCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM, "{}")
            val objJson = JSONObject(pageSum)
            if (objJson.has(pageId)) {
                currentPos = objJson.getInt(pageId)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return currentPos
    }
    fun AppPreferencesHelper.saveCurrentSum(pageId : String, current_pos : Int) {
        try {
            val pageSum: String = getCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM, "{}")
            val objJson = JSONObject(pageSum)
            objJson.put(pageId, current_pos)
            setCustomParam(AppConstants.AbacusProgress.PREF_PAGE_SUM,objJson.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun get2Decimal(value: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(value)
    }

    fun checkLevelIsPurchase(
        purchasedSKU: List<InAppSkuDetails>,
        data: Category,
        prefManager: AppPreferencesHelper
    ): Boolean {
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        var isPurchased = false
        if (loginData?.email.equals("abacus@yopmail.com")){
            isPurchased = true
        }else{
            if (data.name.contains("free",true)){
                isPurchased = true
            }else{
                purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime || it.sku == PRODUCT_ID_All_lifetime_old
                        || it.sku == PRODUCT_ID_Subscription_Month3
                        || it.sku == PRODUCT_ID_Subscription_Year1
                        || (it.sku.contains(data.name)) }.also {
                    isPurchased = it != null
                }
                if (!isPurchased){
                    if (prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"").isNotEmpty()) {
                        val planListData : List<PlanAssignFromAdminData> = Gson().fromJson(
                            prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, ""),
                            object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
                        )
                        planListData.find { it.google_order_id == null && (it.google_plan_id?.contains(data.name) == true) || (it.google_plan_id?.equals(PRODUCT_ID_Subscription_Year1) == true) }.also {
                            isPurchased = it != null
                        }
                    }
                }
            }
        }
        return isPurchased
    }

    fun checkPurchaseForExerciseExamCCM(prefManager: AppPreferencesHelper,purchasedSKU: List<InAppSkuDetails>): Boolean {
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        var isPurchased = false
        if (loginData?.email.equals("abacus@yopmail.com")){
            isPurchased = true
        }else{
            purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime
                    || it.sku == PRODUCT_ID_All_lifetime_old
                    || it.sku == PRODUCT_ID_Subscription_Month3
                    || it.sku == PRODUCT_ID_Subscription_Year1
                    || (it.sku.contains("level3")) || (it.sku.contains("level4"))
                    || (it.sku.contains("level5")) || (it.sku.contains("level6"))
                    || (it.sku.contains("level7")) || (it.sku.contains("level8"))}.also {
                isPurchased = it != null
            }
            if (!isPurchased){
                if (prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"").isNotEmpty()) {
                    val planListData : List<PlanAssignFromAdminData> = Gson().fromJson(
                        prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, ""),
                        object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
                    )
                    planListData.find { it.google_order_id == null && ((it.google_plan_id?.contains("level3") == true) ||
                            (it.google_plan_id?.contains("level3") == true) ||
                            (it.google_plan_id?.contains("level4") == true) ||
                            (it.google_plan_id?.contains("level5") == true) ||
                            (it.google_plan_id?.contains("level6") == true) ||
                            (it.google_plan_id?.contains("level7") == true) ||
                            (it.google_plan_id?.contains("level8") == true) ||
                            (it.google_plan_id?.equals(PRODUCT_ID_Subscription_Year1) == true)) }.also {
                        isPurchased = it != null
                    }
                }
            }
        }

        return isPurchased
    }
    fun checkPurchaseForAllLevel(purchasedSKU: List<InAppSkuDetails>): Boolean {
        purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime
                || it.sku == PRODUCT_ID_All_lifetime_old
                || it.sku == PRODUCT_ID_Subscription_Month3
                || it.sku == PRODUCT_ID_Subscription_Year1
        }.also {
            return it == null
        }
    }
}
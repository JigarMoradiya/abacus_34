package com.jigar.me.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.text.Html
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.android.billingclient.api.BillingClient
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_1Month
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_1Year
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_3Month
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Week
import com.jigar.me.utils.extensions.show
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


object CommonUtils {
    external fun getOneSignalKey() : String
    external fun getOrganizerId() : String
    external fun getDatabaseKey() : String
    external fun getApiBaseUrl() : String

    fun logMultilineString(tag: String, data: String) {
        for (line in data.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            logLargeString(tag, line)
        }
    }

    fun logLargeString(tag: String, data: String) {
        val CHUNK_SIZE = 4076 // Typical max logcat payload.
        var offset = 0
        while (offset + CHUNK_SIZE <= data.length) {
            Log.e(tag, data.substring(offset, CHUNK_SIZE.let { offset += it; offset }))
        }
        if (offset < data.length) {
            Log.e(tag, data.substring(offset))
        }
    }
    fun htmlToAnnotatedString(html: String): AnnotatedString {
        val spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)

        return buildAnnotatedString {
            var start = 0

            spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
                val spanStart = spanned.getSpanStart(span)
                val spanEnd = spanned.getSpanEnd(span)

                if (start < spanStart) {
                    append(spanned.substring(start, spanStart))
                }

                when (span) {
                    is android.text.style.StyleSpan -> {
                        withStyle(
                            SpanStyle(
                                fontWeight = if (span.style == Typeface.BOLD) FontWeight.Bold else FontWeight.Normal,
                                fontFamily =  if (span.style == Typeface.BOLD) FontFamily(Font(R.font.font_bold)) else FontFamily(Font(R.font.font_regular))
                            )
                        ) {
                            append(spanned.substring(spanStart, spanEnd))
                        }
                    }

                    else -> append(spanned.substring(spanStart, spanEnd))
                }

                start = spanEnd
            }

            if (start < spanned.length) {
                append(spanned.substring(start))
            }
        }
    }
    fun getPurchaseTime(productType : String?, purchaseTime: Long,billingPeriod : String? = null) : String?{
        if (purchaseTime > 0) {
            val formatter = SimpleDateFormat(DateTimeUtils.dd_MMMM_yyyy)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = purchaseTime
            return if (productType == BillingClient.ProductType.SUBS){
                val calendarEnd = Calendar.getInstance()
                calendarEnd.timeInMillis = purchaseTime

                val dateDiff = System.currentTimeMillis() - calendar.timeInMillis
                val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)

                if (billingPeriod.equals("p1w",true)){
                    var weeks = day/7
                    weeks += 1
                    calendarEnd.add(Calendar.WEEK_OF_MONTH,weeks.toInt())
                }else if (billingPeriod.equals("p1y",true)){
                    var year = day/365
                    year += 1
                    calendarEnd.add(Calendar.YEAR,year.toInt())
                }else{
                    val months = if (billingPeriod.equals("p1m",true)){
                        var months = day/30
                        months += 1
                        months
                    }else if (billingPeriod.equals("p3m",true)){
                        var months = day/90
                        months += 3
                        months
                    }else{ // if (billingPeriod.equals("p6m",true))
                        var months = day/180
                        months += 6
                        months
                    }

                    calendarEnd.add(Calendar.MONTH,months.toInt())
                }
                "<b>Subscribed ON : </b>${formatter.format(calendar.time)}<br/><b>Expire ON : </b>${formatter.format(calendarEnd.time)}"
            }else{
                "<b>Purchased ON : </b>${formatter.format(calendar.time)}"
            }
        }
        return null
    }

    @SuppressLint("RestrictedApi")
    fun setErrorToEditText(textInputLayout: TextInputLayout, validation_message: String?) {
        textInputLayout.error = validation_message
        textInputLayout.requestFocus()
    }
    fun removeError(textInputLayout: TextInputLayout) {
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
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

    fun checkLevelIsPurchase(purchasedSKU: List<InAppSkuDetails>, data: Category, prefManager: AppPreferencesHelper): Boolean {
//        if (BuildConfig.DEBUG){
//            return true
//        }
        var isPurchased = false
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        if (loginData?.email.equals("abacus@yopmail.com") || prefManager.isUserInFreeTrial()){
            isPurchased = true
        }else{
            purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime
                    || it.sku == PRODUCT_ID_All_lifetime_old
                    || it.sku.contains(PRODUCT_ID_1Year)
                    || it.sku.contains(PRODUCT_ID_Week)
                    || it.sku.contains(PRODUCT_ID_1Month)
                    || it.sku.contains(PRODUCT_ID_3Month)
                    || (it.sku.contains(data.name)) }.also {
                isPurchased = it != null
            }
            if (!isPurchased){
                if (prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"").isNotEmpty()) {
                    val planListData : List<PlanAssignFromAdminData> = Gson().fromJson(
                        prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, ""),
                        object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
                    )
                    planListData.find { it.google_order_id == null &&
                            (it.google_plan_id?.contains(data.name) == true
                                    || it.google_plan_id?.contains(PRODUCT_ID_1Year) == true
                                    || it.google_plan_id?.contains(PRODUCT_ID_Week) == true
                                    || it.google_plan_id?.contains(PRODUCT_ID_1Month) == true
                                    || it.google_plan_id?.contains(PRODUCT_ID_3Month) == true
                                    )
                    }.also {
                        isPurchased = it != null
                    }
                }
            }
        }
        return isPurchased
    }

    fun checkPurchaseForExerciseExamCCM(prefManager: AppPreferencesHelper,purchasedSKU: List<InAppSkuDetails>): Boolean {
//        if (BuildConfig.DEBUG){
//            return true
//        }
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        var isPurchased = false
        if (loginData?.email.equals("abacus@yopmail.com") || prefManager.isUserInFreeTrial()){
            isPurchased = true
        }else{
            purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime
                    || it.sku == PRODUCT_ID_All_lifetime_old
                    || it.sku.contains(PRODUCT_ID_1Year)
                    || it.sku.contains(PRODUCT_ID_Week)
                    || it.sku.contains(PRODUCT_ID_1Month)
                    || it.sku.contains(PRODUCT_ID_3Month)
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
                    planListData.find { it.google_order_id == null && (
                            (it.google_plan_id?.contains("level3") == true) ||
                            (it.google_plan_id?.contains("level4") == true) ||
                            (it.google_plan_id?.contains("level5") == true) ||
                            (it.google_plan_id?.contains("level6") == true) ||
                            (it.google_plan_id?.contains("level7") == true) ||
                            (it.google_plan_id?.contains("level8") == true) ||
                            (it.google_plan_id?.contains(PRODUCT_ID_1Year) == true) ||
                            (it.google_plan_id?.contains(PRODUCT_ID_Week) == true) ||
                            (it.google_plan_id?.contains(PRODUCT_ID_1Month) == true) ||
                            (it.google_plan_id?.contains(PRODUCT_ID_3Month) == true)
                            ) }.also {
                        isPurchased = it != null
                    }
                }
            }
        }

        return isPurchased
    }
}
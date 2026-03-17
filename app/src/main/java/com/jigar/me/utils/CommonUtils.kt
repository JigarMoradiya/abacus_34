package com.jigar.me.utils

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Html
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.android.billingclient.api.BillingClient
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_1Month
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_1Year
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_3Month
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old
import com.jigar.me.ui.view.base.inapp.BillingRepository.AbacusSku.PRODUCT_ID_Week
import java.text.SimpleDateFormat
import java.util.Calendar
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
        if (BuildConfig.DEBUG){
            return true
        }
        var isPurchased = false
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        if (loginData?.email.equals("abacus@yopmail.com") || prefManager.isUserInFreeTrial()){
            isPurchased = true
        }else{
            purchasedSKU.find { it.sku == PRODUCT_ID_All_lifetime_old
                    || it.sku.contains(PRODUCT_ID_All)
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
                                    || it.google_plan_id?.contains(PRODUCT_ID_All) == true
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
        if (BuildConfig.DEBUG){
            return true
        }
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        var isPurchased = false
        if (loginData?.email.equals("abacus@yopmail.com") || prefManager.isUserInFreeTrial()){
            isPurchased = true
        }else{
            purchasedSKU.find {
                it.sku == PRODUCT_ID_All_lifetime_old
                    || it.sku.contains(PRODUCT_ID_All)
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
                            (it.google_plan_id?.contains(PRODUCT_ID_All) == true) ||
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
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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.pref.AppPreferencesHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit


object CommonUtils {
    external fun getOneSignalKey() : String
    external fun getOrganizerId() : String
    external fun getPublicKey() : String
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
            return if (productType == "subs"){
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

    // Checks RC entitlement + admin-assigned plans. Used for level-specific access.
    fun checkLevelIsPurchase(name: String, prefManager: AppPreferencesHelper): Boolean {
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        if (loginData?.email == "abacus@yopmail.com") return true

        // RevenueCat entitlement
        if (RevenueCatHelper.isSubscribed) return true

        // Lifetime plan purchased before RC migration — still grants full access
        if (RevenueCatHelper.isLifetimePurchased) return true

        // Admin-assigned plans
        val adminData = prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, "")
        if (adminData.isNotEmpty()) {
            val plans = Gson().fromJson<List<PlanAssignFromAdminData>>(
                adminData, object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
            )
            val found = plans.find { plan ->
                plan.google_order_id == null && (
                    plan.google_plan_id?.contains(name) == true
                    || plan.google_plan_id?.contains("all") == true
                    || plan.google_plan_id?.contains("1year") == true
                    || plan.google_plan_id?.contains("week") == true
                    || plan.google_plan_id?.contains("1month") == true
                    || plan.google_plan_id?.contains("3month") == true
                )
            }
            if (found != null) return true
        }
        return false
    }

    // Checks RC entitlement + admin-assigned plans. Used for exercise/exam/CCM/games access.
    fun checkPurchaseForExerciseExamCCM(prefManager: AppPreferencesHelper): Boolean {
        val loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        if (loginData?.email == "abacus@yopmail.com") return true

        // RevenueCat entitlement
        if (RevenueCatHelper.isSubscribed) return true

        // Lifetime plan purchased before RC migration — still grants full access
        if (RevenueCatHelper.isLifetimePurchased) return true

        // Admin-assigned plans
        val adminData = prefManager.getCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, "")
        if (adminData.isNotEmpty()) {
            val plans = Gson().fromJson<List<PlanAssignFromAdminData>>(
                adminData, object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
            )
            val found = plans.find { plan ->
                plan.google_order_id == null && (
                    plan.google_plan_id?.contains("all") == true
                    || plan.google_plan_id?.contains("1year") == true
                    || plan.google_plan_id?.contains("week") == true
                    || plan.google_plan_id?.contains("1month") == true
                    || plan.google_plan_id?.contains("3month") == true
                    || plan.google_plan_id?.contains("level3") == true
                    || plan.google_plan_id?.contains("level4") == true
                    || plan.google_plan_id?.contains("level5") == true
                    || plan.google_plan_id?.contains("level6") == true
                    || plan.google_plan_id?.contains("level7") == true
                    || plan.google_plan_id?.contains("level8") == true
                )
            }
            if (found != null) return true
        }
        return false
    }
}
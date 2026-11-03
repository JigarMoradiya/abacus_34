package com.jigar.me.utils

import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.ui.view.home.screens.reports.viewmodels.DateFilterType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun String.toDate(
    dateFormat: String = DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz,
    timeZone: TimeZone = TimeZone.getTimeZone("UTC"),
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.US)   // text from the server: fixed English-US form, whatever the app language
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(
    dateFormat: String,
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    val formatter = SimpleDateFormat(
        dateFormat, DateTimeUtils.displayLocale()
    )
    formatter.timeZone = timeZone
    return formatter.format(this)
}
object DateTimeUtils {
    /**
     * The locale for text the child or parent reads: the app language, the Gregorian calendar and the digits 0-9 (a Thai or Arabic
     * phone would otherwise write Buddhist years such as 2569, or Arabic-Indic digits). Text that is parsed or sent to the server
     * never uses this, it uses Locale.US.
     */
    fun displayLocale(): Locale = Locale.Builder()
        .setLanguageTag(LanguageManager.currentTag())
        .setUnicodeLocaleKeyword("nu", "latn")
        .setUnicodeLocaleKeyword("ca", "gregory")
        .build()

    private val isEnglish get() = LanguageManager.currentTag() == "en"

    /** English keeps its own patterns; other languages get the usual pattern of their language for the same fields. */
    private fun localPattern(englishPattern: String, skeleton: String): String =
        if (isEnglish) englishPattern else android.text.format.DateFormat.getBestDateTimePattern(displayLocale(), skeleton)

    private fun format(date: Date, pattern: String, zone: TimeZone = TimeZone.getDefault()): String {
        val f = SimpleDateFormat(pattern, displayLocale())
        f.timeZone = zone
        return f.format(date)
    }

    /** "03 Nov-26 10:30 PM" (English) / the same fields in the pattern of the app language. */
    fun shortDateTime(date: Date): String = format(date, localPattern("dd MMM-yy hh:mm a", "ddMMMyyjmm"))
    var yyyy_MM_dd_T_HH_mm_ss_sssz: String = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"
    var ddMMMyyyyhhmma: String = "dd MMM yyyy hh:mm a"
    var yyyy_MM_dd_HH_mm: String = "yyyy_MM_dd_hh_mm"
    var MMMM_dd_yyyy: String = "MMMM dd, yyyy"
    var dd_MMM_yyyy: String = "dd, MMM yyyy"
    var dd_MMMM_yyyy: String = "dd MMMM yyyy"
    var yyyy_MM_dd: String = "yyyy-MM-dd"
    var hh_mm_a: String = "'at' hh:mm a"
    var at_dd_mmm_yy_hh_mm_a: String = "'At' dd MMM-yy hh:mm a"

    fun getDateRange(type: DateFilterType): Pair<String, String> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)   // sent to the server
        val cal = Calendar.getInstance()

        return when (type) {

            DateFilterType.LAST_30_DAYS -> {
                val end = formatter.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, -30)
                val start = formatter.format(cal.time)
                start to end
            }

            DateFilterType.THIS_MONTH -> {
                // Start = 1st day of current month
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = formatter.format(cal.time)

                // End = today
                val end = formatter.format(Calendar.getInstance().time)
                start to end
            }

            DateFilterType.LAST_MONTH -> {
                // Move to previous month
                cal.add(Calendar.MONTH, -1)

                // Start = 1st day of last month
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = formatter.format(cal.time)

                // End = last day of last month
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = formatter.format(cal.time)

                start to end
            }

            DateFilterType.LAST_3_MONTH -> {
                val end = formatter.format(cal.time)
                cal.add(Calendar.MONTH, -3)
                val start = formatter.format(cal.time)
                start to end
            }

            DateFilterType.LAST_6_MONTH -> {
                val end = formatter.format(cal.time)
                cal.add(Calendar.MONTH, -6)
                val start = formatter.format(cal.time)
                start to end
            }

            DateFilterType.LAST_1_YEAR -> {
                val end = formatter.format(cal.time)
                cal.add(Calendar.YEAR, -1)
                val start = formatter.format(cal.time)
                start to end
            }
        }
    }


    fun String.toDate(dateFormat: String = yyyy_MM_dd_T_HH_mm_ss_sssz, timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.US)
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, displayLocale())
        formatter.timeZone = timeZone
        val text = formatter.format(this)
        return if (isEnglish) text.replace("am","AM").replace("pm","PM") else text
    }
    fun displayDurationHourMinSec(totalSecs: Long): String {
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        val outputStr = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
        return outputStr
    }
    fun convertDateFormat(
        date: String,
        sourceStr: String,
        destinationStr: String
    ): String {
        var strNewDate = date
        val newDate: Date
        val source = SimpleDateFormat(sourceStr, Locale.US)
        val destination = SimpleDateFormat(destinationStr, displayLocale())
        try {
            if (!TextUtils.isEmpty(date)) {
                newDate = source.parse(date)
                strNewDate = destination.format(newDate)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return strNewDate
    }
    private fun isToday(date: Long): Boolean {
        val now = Calendar.getInstance()
        val cdate = Calendar.getInstance()
        cdate.timeInMillis = date
        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH) &&
                now.get(Calendar.DATE) == cdate.get(Calendar.DATE)
    }
    private fun isYesterday(date: Long): Boolean {
        val now = Calendar.getInstance()
        val cdate = Calendar.getInstance()
        cdate.timeInMillis = date
        now.add(Calendar.DATE, -1)
        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH) &&
                now.get(Calendar.DATE) == cdate.get(Calendar.DATE)
    }

    /**
     * Report-history style date: "Today at 10:30 PM", "Yesterday at 10:30 PM" or "At 03 Nov-26 10:30 PM", the words and the patterns in
     * the app language. [destinationStr] is used as given only when [isCheckToday] is false.
     */
    fun convertDateFormatFromUTC(
        date: String?,
        sourceStr: String,
        destinationStr: String, isCheckToday : Boolean = true
    ): String? {
        var strNewDate = date
        val source = SimpleDateFormat(sourceStr, Locale.US)
        source.timeZone = TimeZone.getTimeZone("UTC")
        try {
            if (!TextUtils.isEmpty(date)) {
                val newDate = date?.let { source.parse(it) }
                newDate?.let {
                    val words = MyApplication.getInstance().inAppLanguage()
                    strNewDate = if (!isCheckToday) {
                        format(it, destinationStr)
                    } else if (isToday(it.time)) {
                        words.getString(R.string.date_today_at, format(it, localPattern("hh:mm a", "jmm")))
                    } else if (isYesterday(it.time)) {
                        words.getString(R.string.date_yesterday_at, format(it, localPattern("hh:mm a", "jmm")))
                    } else {
                        words.getString(R.string.date_at, shortDateTime(it))
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return strNewDate
    }

    fun getDateString(date: Date,format : String): String {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        return sdf.format(date)
    }
    fun milliSecondToFormat(millisecond: Long,format : String): String {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        return sdf.format(Date(millisecond))
    }
    fun isTimeAfter(startTime: Date?, endTime: Date): Boolean {
        return !endTime.before(startTime)
    }

    @RequiresApi(Build.VERSION_CODES.O) // 26 api
    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDate? {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
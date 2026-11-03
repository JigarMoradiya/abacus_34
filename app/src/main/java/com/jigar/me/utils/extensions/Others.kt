package com.jigar.me.utils.extensions

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.utils.inAppLanguage

val TAG = "jigarLog"
fun String?.isStringNotBlank() = this!=null && this.isNotBlank()
fun Collection<Any?>?.isNotNullOrEmpty() = this!=null && this.isNotEmpty()
fun Collection<Any?>?.isEmpty() = this!=null && this.isEmpty()

fun Color.mixWith(other: Color, fraction: Float): Color {
    val r = (this.red * (1f - fraction)) + (other.red * fraction)
    val g = (this.green * (1f - fraction)) + (other.green * fraction)
    val b = (this.blue * (1f - fraction)) + (other.blue * fraction)
    val a = (this.alpha * (1f - fraction)) + (other.alpha * fraction)

    return Color(r, g, b, a)
}

/**
 * "06 m 50 s" (English) in the app language ("06 min 50 s", "06分50秒" ...), digits always 0-9.
 * The minutes are the minutes of the hour (an hour and 5 minutes is "01 h : 05 m ...", not 65).
 */
fun Int.secToTimeFormat(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    val words = com.jigar.me.MyApplication.getInstance().inAppLanguage()
    val locale = com.jigar.me.utils.DateTimeUtils.displayLocale()
    return if (hours > 0){
        String.format(locale, words.getString(R.string.time_hms), hours, minutes, seconds)
    }else if(minutes > 0 && seconds > 0 ){
        String.format(locale, words.getString(R.string.time_ms), minutes, seconds)
    }else if(minutes > 0){
        String.format(locale, words.getString(R.string.time_m), minutes)
    }else{
        String.format(locale, words.getString(R.string.time_s), seconds)
    }
}

fun Int.secToCountDown(): String {
    val minutes = this / 60
    val seconds = this % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}

fun Any.log(message: String) {
    if (BuildConfig.DEBUG){
        Log.e(TAG, message)
    }
}
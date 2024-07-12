package com.jigar.me.utils.extensions

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.jigar.me.BuildConfig
import org.apache.commons.text.StringEscapeUtils

val TAG = "jigarLog"
fun String?.isStringNotBlank() = this!=null && this.isNotBlank()
fun Collection<Any?>?.isNotNullOrEmpty() = this!=null && this.isNotEmpty()
fun Collection<Any?>?.isEmpty() = this!=null && this.isEmpty()

fun String?.removeQuotesAndUnescape(): String? {
    val noQuotes = this?.replace("^\"|\"$".toRegex(), "")
    return StringEscapeUtils.unescapeJava(noQuotes)
}

fun Int.secToTimeFormat(): String {
    val hours = this / 3600
    val minutes = this / 60
    val seconds = this % 60
    return if (hours > 0){
        String.format("%d:%d:%02d", hours, minutes, seconds)
    }else{
        String.format("%d:%02d", minutes, seconds)
    }
}

fun Any.log(message: String) {
    if (BuildConfig.DEBUG){
        Log.e(TAG, message)
    }
}
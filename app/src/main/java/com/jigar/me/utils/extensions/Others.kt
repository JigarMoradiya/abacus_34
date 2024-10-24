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
        String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)
    }else if(minutes > 0 && seconds > 0 ){
        String.format("%02d min %02d sec", minutes, seconds)
    }else if(minutes > 0){
        String.format("%02d min", minutes)
    }else{
        String.format("%02d sec", seconds)
    }
}

fun Any.log(message: String) {
    if (BuildConfig.DEBUG){
        Log.e(TAG, message)
    }
}
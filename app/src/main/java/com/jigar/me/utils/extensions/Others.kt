package com.jigar.me.utils.extensions

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.jigar.me.BuildConfig

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
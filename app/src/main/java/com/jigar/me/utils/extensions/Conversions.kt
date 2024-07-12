package com.jigar.me.utils.extensions

import android.content.res.Resources
import java.text.NumberFormat
import java.util.Locale

// dp to pixels
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Int.sp: Float get() =  ( this.dp / Resources.getSystem().displayMetrics.scaledDensity)

fun Double.format(isGrouping: Boolean = true): String {
    val formatter = NumberFormat.getInstance(Locale.getDefault())
    formatter.maximumFractionDigits = 3
    formatter.isGroupingUsed = isGrouping
    return formatter.format(this)
}
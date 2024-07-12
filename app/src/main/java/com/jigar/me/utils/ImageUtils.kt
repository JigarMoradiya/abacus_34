package com.jigar.me.utils

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.jigar.me.R

object ImageUtils {
    fun getCircleProgress(context: Context): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 3f
        circularProgressDrawable.centerRadius = 16f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            circularProgressDrawable.colorFilter = BlendModeColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), BlendMode.SRC_ATOP)
        } else {
            circularProgressDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        }
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
}
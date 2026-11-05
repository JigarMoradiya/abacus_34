package com.jigar.me.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.jigar.me.R
import androidx.core.graphics.createBitmap
import com.jigar.me.data.local.data.AbacusContent
import kotlin.math.cos
import kotlin.math.sin

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

    fun linearGradientForAngle(context : Context, data : AbacusContent, width: Float, height: Float, angle: Float): LinearGradient {
        val rad = Math.toRadians(angle.toDouble())

        // direction vector
        val dx = cos(rad)
        val dy = sin(rad)

        // center → start/end points
        val cx = width / 2f
        val cy = height / 2f

        val startX = (cx - dx * width)
        val startY = (cy - dy * height)
        val endX = (cx + dx * width)
        val endY = (cy + dy * height)

        return LinearGradient(
            startX.toFloat(), startY.toFloat(),
            endX.toFloat(),   endY.toFloat(),
            intArrayOf(
                ContextCompat.getColor(context,data.dividerColor1),   // start color
                ContextCompat.getColor(context,data.resetBtnColor8),   // end color
                ),
            null,
            Shader.TileMode.CLAMP
        )
    }

    fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }

    fun drawGradientDrawableOnCanvas(context: Context,data : AbacusContent, canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        // 1. Load drawable
        val drawable = ContextCompat.getDrawable(context, data.beadImage) ?: return

        val finalBitmap = if (data.type.contains(AppConstants.Settings.theam_Poligon_default,true)){
            // 2. Convert drawable to bitmap with size = bounds
            val width = right - left
            val height = bottom - top
            val baseBitmap = drawableToBitmap(drawable, width, height)

            // 3. Create gradient shader
            val shader = linearGradientForAngle(context,data, width.toFloat(), height.toFloat(), 90F)

            // 4. Paint for SRC_IN (clip gradient inside drawable alpha)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.shader = shader
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            }

            // 5. Draw drawable shape + gradient on top
            val finalBitmap = createBitmap(width, height)
            val tempCanvas = Canvas(finalBitmap)

            // draw original image
            tempCanvas.drawBitmap(baseBitmap, 0f, 0f, null)

            // apply gradient clipped to image
            tempCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            finalBitmap
        }else{
            val width = right - left
            val height = bottom - top
            val baseBitmap = drawableToBitmap(drawable, width, height)
            baseBitmap
        }

        // 6. Draw final result into your main canvas
        canvas.drawBitmap(finalBitmap, left.toFloat(), top.toFloat(), null)
    }

}
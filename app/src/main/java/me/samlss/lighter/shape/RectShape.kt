package me.samlss.lighter.shape

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

class RectShape @JvmOverloads constructor(
    private val mXRadius: Float = 5f,
    private val mYRadius: Float = 5f,
    blurRadius: Float = 15f
) : LighterShape(blurRadius) {

    override fun setPaint(paint: Paint?) {
        this.paint = paint
    }

    override fun setViewRect(rect: RectF) {
        super.setViewRect(rect)
        if (!isViewRectEmpty) {
            path?.reset()
            highlightedViewRect?.let{
                path?.addRoundRect(it, mXRadius, mYRadius, Path.Direction.CW)
            }

        }
    }
}
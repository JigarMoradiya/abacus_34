package me.samlss.lighter.shape

import android.graphics.*


abstract class LighterShape protected constructor(blurRadius: Float) {
    protected var highlightedViewRect: RectF? = null
    protected var path: Path? = null
    @JvmField
    var paint: Paint? = null

    init {
        path = Path()
        paint = Paint()
        paint?.isDither = true
        paint?.isAntiAlias = true
        paint?.color = Color.WHITE
        if (blurRadius > 0) {
            paint?.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID)
        }
    }

    /**
     * Draw the highlighted view.
     */
    fun onDraw(canvas: Canvas) {
        if ((path != null && paint != null)) {
            canvas.drawPath(path!!, paint!!)
        }
    }

    /**
     * Set [RectF] the highlighted view.
     */
    open fun setViewRect(rect: RectF) {
        highlightedViewRect = rect
    }

    /**
     * Get [RectF] the highlighted view.
     */
    open fun getViewRect(): RectF? {
        return highlightedViewRect
    }


    /**
     * Returns true if the view rect is empty (left >= right or top >= bottom)
     */
    val isViewRectEmpty: Boolean
        get() = (highlightedViewRect == null || highlightedViewRect?.isEmpty == true)

    /**
     * Set a custom paint when draw this shape.
     */
    open fun setPaint(paint: Paint?) {
        this.paint = paint
    }

    /**
     * Get shape path.
     */
    open fun getShapePath(): Path? {
        return path
    }
}
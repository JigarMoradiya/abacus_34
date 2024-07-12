package me.samlss.lighter.shape

import android.graphics.Path
import android.graphics.RectF


/**
 * @author SamLeung
 * @e-mail samlssplus@gmail.com
 * @github https://github.com/samlss
 * @description
 */
class CircleShape : LighterShape {
    /**
     * Construct a circle shape object.
     *
     * Will call [.CircleShape] and pass the parameter is (15);
     */
    constructor() : super(15F) {}

    /**
     * Construct a circle shape object.
     */
    constructor(blurRadius: Float) : super(blurRadius) {}

    override fun setViewRect(rect: RectF) {
        super.setViewRect(rect)
        if (!isViewRectEmpty) {
            path?.reset()
            highlightedViewRect?.let {
                path?.addCircle(it.centerX(),it.centerY(),Math.max(it.width(), it.height()) / 2,Path.Direction.CW)
            }
        }
    }
}
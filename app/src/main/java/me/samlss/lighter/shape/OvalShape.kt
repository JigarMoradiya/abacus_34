package me.samlss.lighter.shape

import android.graphics.Path
import android.graphics.RectF

class OvalShape : LighterShape {
    /**
     * Construct a circle shape object.
     *
     * Will call [.OvalShape] and pass the parameter is (15);
     */
    constructor() : super(15f) {}

    /**
     * Construct a oval shape object.
     */
    constructor(blurRadius: Float) : super(blurRadius) {}

    override fun setViewRect(rect: RectF) {
        super.setViewRect(rect)
        if (!isViewRectEmpty) {
            path?.reset()
            path?.addOval(rect, Path.Direction.CW)
        }
    }
}
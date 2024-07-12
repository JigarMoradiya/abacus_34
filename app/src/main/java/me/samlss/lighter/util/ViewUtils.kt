package me.samlss.lighter.util


import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.view.View
import me.samlss.lighter.parameter.LighterParameter
import me.samlss.lighter.view.LighterView


/**
 * @author SamLeung
 * @e-mail samlssplus@gmail.com
 * @github https://github.com/samlss
 * @description The utils of view.
 */
class ViewUtils private constructor() {
    init {
        throw UnsupportedOperationException("Can not be instantiated.")
    }

    companion object {
        const val DEFAULT_HIGHLIGHT_VIEW_BG_COLOR = -0x1b000000
        fun isAttachedToWindow(view: View?): Boolean {
            if (view == null) {
                return false
            }
            return if (Build.VERSION.SDK_INT >= 19) {
                view.isAttachedToWindow
            } else {
                view.windowToken != null
            }
        }

        /**
         * Get the position of the view relative to the screen.
         * @param view The view.
         */
        fun getRectOnScreen(view: View?): RectF {
            if (view == null) {
                Log.e("ViewUtils", "Please pass non-null referParent and child.")
                return RectF()
            }
            val result = RectF()
            val pos = IntArray(2)
            view.getLocationOnScreen(pos)
            result.left = pos[0].toFloat()
            result.top = pos[1].toFloat()
            result.right = result.left + view.measuredWidth
            result.bottom = result.top + view.measuredHeight
            return result
        }

        /**
         * Calculate the rect of the highlighted view
         */
        fun calculateHighlightedViewRect(
            lighterView: LighterView?,
            lighterParameter: LighterParameter?
        ) {
            if (lighterView == null || lighterParameter == null || lighterParameter.highlightedView == null) {
                return
            }
            val highlightedViewRect = getRectOnScreen(lighterParameter.highlightedView)
            if (highlightedViewRect == null
                || highlightedViewRect.isEmpty
            ) {
                return
            }
            val parent = lighterView.getParent() as View
            val rootViewPos = IntArray(2)
            parent.getLocationOnScreen(rootViewPos)
            highlightedViewRect.left -= rootViewPos[0].toFloat()
            highlightedViewRect.right -= rootViewPos[0].toFloat()
            highlightedViewRect.top -= rootViewPos[1].toFloat()
            highlightedViewRect.bottom -= rootViewPos[1].toFloat()
            highlightedViewRect.left -= parent.paddingLeft.toFloat()
            highlightedViewRect.right -= parent.paddingLeft.toFloat()
            highlightedViewRect.top -= parent.paddingTop.toFloat()
            highlightedViewRect.bottom -= parent.paddingTop.toFloat()
            lighterParameter.highlightedViewRect = highlightedViewRect
            lighterParameter.lighterShape!!.setViewRect(
                RectF(
                    highlightedViewRect.left - lighterParameter.shapeXOffset,
                    highlightedViewRect.top - lighterParameter.shapeYOffset,
                    highlightedViewRect.right + lighterParameter.shapeXOffset,
                    highlightedViewRect.bottom + lighterParameter.shapeYOffset
                )
            )
        }
    }
}

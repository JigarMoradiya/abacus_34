package me.samlss.lighter.parameter


import android.graphics.RectF
import android.view.View
import android.view.animation.Animation
import me.samlss.lighter.shape.LighterShape

class LighterParameter private constructor() {
    /**
     * Set the id of the view that will be highlighted. <br></br>
     * If you use [Lighter.with], will call [Activity.findViewById] <br></br>
     * If you use [Lighter.with] )}, will call [ViewGroup.findViewById]
     *
     * @param highlightedViewId
     */
    var highlightedViewId = 0

    /**
     * Set the view that will be highlighted.
     *
     * @param highlightedView The highlighted view.
     */
    var highlightedView: View? = null

    /**
     * Set the rect of highlighted view.
     */
    var highlightedViewRect: RectF? = null

    /**
     * Set the layout id of the tip layout. <br></br>
     *
     * Will call [android.view.LayoutInflater.inflate] to create the
     * tip layout. <br></br>
     *
     * Will attached to [me.samlss.lighter.view.LighterView]
     *
     * @param tipLayoutId The layout id of tip.
     */
    var tipLayoutId = 0

    /**
     * Set the tip view
     *
     * Will attached to [me.samlss.lighter.view.LighterView]
     * @param tipView The tip view.
     */
    var tipView: View? = null

    /**
     * Set the shape of the wrapped highlight view
     *
     * @param lighterShape The highlighted shape.
     */
    var lighterShape: LighterShape? = null

    /**
     * Set the x-axis offset of the shape rect([LighterShape.setViewRect]), default is 0  <br></br>
     *
     * The final rect width is new RectF{rect.left - shapeXOffset, top, rect.right + shapeXOffset, bottom}
     *
     * @param shapeXOffset The x-axis offset value.
     */
    var shapeXOffset = 0f

    /**
     * Set the y-axis offset of the shape rect ([LighterShape.setViewRect]), default is 0 <br></br>
     *
     * The final rect width is new RectF{rect.left, top - shapeYOffset, rect.right, bottom + shapeYOffset}
     * @param shapeYOffset The y-axis offset value.
     */
    var shapeYOffset = 0f

    /**
     * Set the direction of the tip view relative to the highlighted view.
     * @param tipViewRelativeDirection Must be [Direction]
     */
    var tipViewRelativeDirection = 0

    /**
     * Set the offset of the tip view's margin relative to the highlighted view.
     *
     * @param marginOffset The margin offset values.
     */
    var tipViewRelativeMarginOffset: MarginOffset? = null

    /**
     * Set animation of the tip view.
     */
    var tipViewDisplayAnimation: Animation? = null

    /**
     * Help to build [LighterParameter]
     */
    class Builder {
        private val mLighterParameter: LighterParameter

        init {
            mLighterParameter = LighterParameter()
        }

        /**
         * Set the id of the view that will be highlighted. <br></br>
         * If you use [Lighter.with], will call [Activity.findViewById] <br></br>
         * If you use [Lighter.with] )}, will call [ViewGroup.findViewById]
         *
         * @param highlightedViewId
         */
        fun setHighlightedViewId(highlightedViewId: Int): Builder {
            mLighterParameter.highlightedViewId = highlightedViewId
            return this
        }

        /**
         * Set the view that will be highlighted.
         *
         * @param highlightedView The highlighted view.
         */
        fun setHighlightedView(highlightedView: View?): Builder {
            mLighterParameter.highlightedView = highlightedView
            return this
        }

        /**
         * Set the layout id of the tip layout. <br></br>
         *
         * Will call [android.view.LayoutInflater.inflate] to create the
         * tip layout. <br></br>
         *
         * Will attached to [me.samlss.lighter.view.LighterView]
         *
         * @param tipLayoutId The layout id of tip.
         */
        fun setTipLayoutId(tipLayoutId: Int): Builder {
            mLighterParameter.tipLayoutId = tipLayoutId
            return this
        }

        /**
         * Set the tip view
         *
         * Will attached to [me.samlss.lighter.view.LighterView]
         * @param tipView The tip view.
         */
        fun setTipView(tipView: View?): Builder {
            mLighterParameter.tipView = tipView
            return this
        }

        /**
         * Set the shape of the wrapped highlight view
         *
         * @param lighterShape The highlighted shape.
         */
        fun setLighterShape(lighterShape: LighterShape?): Builder {
            mLighterParameter.lighterShape = lighterShape
            return this
        }

        /**
         * Set the x-axis offset of the shape rect([LighterShape.setViewRect]), default is 10  <br></br>
         *
         * The final rect width is new RectF{rect.left - shapeXOffset, top, rect.right + shapeXOffset, bottom}
         *
         * @param xOffset The x-axis offset value.
         */
        fun setShapeXOffset(xOffset: Float): Builder {
            mLighterParameter.shapeXOffset = xOffset
            return this
        }

        /**
         * Set the y-axis offset of the shape rect ([LighterShape.setViewRect]), default is 10 <br></br>
         *
         * The final rect width is new RectF{rect.left, top - shapeYOffset, rect.right, bottom + shapeYOffset}
         * @param yOffset The y-axis offset value.
         */
        fun setShapeYOffset(yOffset: Float): Builder {
            mLighterParameter.shapeYOffset = yOffset
            return this
        }

        /**
         * Set the direction of the tip view relative to the highlighted view.
         * @param tipViewRelativeDirection Must be [Direction]
         */
        fun setTipViewRelativeDirection(tipViewRelativeDirection: Int): Builder {
            mLighterParameter.tipViewRelativeDirection = tipViewRelativeDirection
            return this
        }

        /**
         * Set the offset of the tip view's margin relative to the highlighted view.
         *
         * @param marginOffset The margin offset values.
         */
        fun setTipViewRelativeOffset(marginOffset: MarginOffset?): Builder {
            mLighterParameter.tipViewRelativeMarginOffset = marginOffset
            return this
        }

        /**
         * Set animation of the tip view.
         */
        fun setTipViewDisplayAnimation(tipViewDisplayAnimation: Animation?): Builder {
            mLighterParameter.tipViewDisplayAnimation = tipViewDisplayAnimation
            return this
        }

        fun build(): LighterParameter {
            return mLighterParameter
        }
    }
}
package me.samlss.lighter.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Region
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import me.samlss.lighter.parameter.Direction
import me.samlss.lighter.parameter.LighterParameter
import me.samlss.lighter.shape.LighterShape
import me.samlss.lighter.util.ViewUtils


class LighterView : FrameLayout {
    private var mLighterParameterList: List<LighterParameter>? = null
    private var mBgColor = -1
    private var mInitWidth = 0
    private var mInitHeight = 0

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context!!, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context?,attrs: AttributeSet?,defStyleAttr: Int,defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        //need the exactly width & height
        measureChildren(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(width, height)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLighterParameterList = null
    }

    //    @Override
    //    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    //        super.onLayout(changed, left, top, right, bottom);
    //
    //        if (!changed
    //                || (mInitWidth == 0 && mInitHeight == 0)
    //                || mLighterParameterList == null
    //                || mLighterParameterList.isEmpty()
    //                || (getWidth() == mInitWidth && getHeight() == mInitHeight)){
    //            return;
    //        }
    //
    //        mInitWidth = getWidth();
    //        mInitHeight = getHeight();
    //
    //        reLayout();
    //    }
    fun reLayout() {
        for (i in 0 until childCount) {
            ViewUtils.calculateHighlightedViewRect(
                this@LighterView,
                mLighterParameterList!![i]
            )
            getChildAt(i).layoutParams = calculateLayoutParams(
                mInitWidth, mInitHeight,
                mLighterParameterList!![i], getChildAt(i)
            )
        }
    }

    override fun setBackgroundColor(color: Int) {
        mBgColor = color
    }

    /**
     * Add highlighted view, at the same time, multiple views can be highlighted
     *
     * @param lighterParameters The parameter of highlighted views.
     */
    fun addHighlight(lighterParameters: List<LighterParameter>?) {
        for (i in 0 until childCount) {
            getChildAt(i).clearAnimation()
        }
        removeAllViews()
        if (lighterParameters == null
            || lighterParameters.isEmpty()
        ) {
            return
        }
        mLighterParameterList = lighterParameters
        for (lighterParameter in lighterParameters) {
            //check paramters
            addTipView(lighterParameter)
        }
    }

    /**
     * Add highlighted view
     *
     * @param lighterParameter The parameter of highlighted views.
     */
    fun addTipView(lighterParameter: LighterParameter?) {
        if (lighterParameter == null) {
            return
        }

        //add tip view
        val tipView = lighterParameter.tipView
        val layoutParams = calculateLayoutParams(mInitWidth, mInitHeight, lighterParameter, tipView)
        if (lighterParameter.tipViewDisplayAnimation != null) {
            tipView!!.startAnimation(lighterParameter.tipViewDisplayAnimation)
        }
        addView(tipView, layoutParams)
    }

    private fun calculateLayoutParams(width: Int,height: Int,lighterParameter: LighterParameter,tipView: View?): LayoutParams {
        val highlightedViewRect = lighterParameter.highlightedViewRect
        val marginOffset = lighterParameter.tipViewRelativeMarginOffset
        val layoutParams = tipView?.layoutParams as LayoutParams
        if (highlightedViewRect == null || highlightedViewRect.isEmpty) {
            return layoutParams
        }
        var alignRight = false
        when (lighterParameter.tipViewRelativeDirection) {
            Direction.LEFT -> {
                layoutParams.topMargin = highlightedViewRect.top.toInt() + marginOffset!!.topOffset
                layoutParams.rightMargin =
                    (width - highlightedViewRect.right + marginOffset.rightOffset + highlightedViewRect.width()).toInt()
            }
            Direction.RIGHT -> {
                layoutParams.topMargin = highlightedViewRect.top.toInt() + marginOffset!!.topOffset
                layoutParams.leftMargin =
                    (highlightedViewRect.right + marginOffset.leftOffset).toInt()
            }
            Direction.TOP -> {
                if (highlightedViewRect.left > width / 2) { //on the right
                    alignRight = true
                    layoutParams.rightMargin =
                        (width - highlightedViewRect.right + marginOffset!!.rightOffset).toInt()
                } else { //on the left
                    layoutParams.leftMargin =
                        (highlightedViewRect.left + marginOffset!!.leftOffset).toInt()
                }
                layoutParams.bottomMargin =
                    (height - highlightedViewRect.bottom + highlightedViewRect.height() + marginOffset.bottomOffset).toInt()
            }
            Direction.BOTTOM -> {
                if (highlightedViewRect.left > width / 2) { //on the right
                    alignRight = true
                    layoutParams.rightMargin =
                        (width - highlightedViewRect.right + marginOffset!!.rightOffset).toInt()
                } else { //on the left
                    layoutParams.leftMargin =
                        (highlightedViewRect.left + marginOffset!!.leftOffset).toInt()
                }
                layoutParams.topMargin =
                    (highlightedViewRect.bottom + marginOffset.topOffset).toInt()
            }
            else -> {
                layoutParams.topMargin = highlightedViewRect.top.toInt() + marginOffset!!.topOffset
                layoutParams.rightMargin =
                    (width - highlightedViewRect.right + marginOffset.rightOffset + highlightedViewRect.width()).toInt()
            }
        }
        if (layoutParams.rightMargin != 0 || alignRight) {
            layoutParams.gravity = Gravity.RIGHT
        } else {
            layoutParams.gravity = Gravity.LEFT
        }
        if (layoutParams.bottomMargin != 0) {
            layoutParams.gravity = layoutParams.gravity or Gravity.BOTTOM
        } else {
            layoutParams.gravity = layoutParams.gravity or Gravity.TOP
        }
        return layoutParams
    }

    /**
     * Check if the shape is empty.
     */
    private fun isShapeEmpty(lighterShape: LighterShape?): Boolean {
        return lighterShape == null || (lighterShape.getViewRect() == null) || lighterShape.getViewRect()!!.isEmpty
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mBgColor == -1) {
            mBgColor = ViewUtils.DEFAULT_HIGHLIGHT_VIEW_BG_COLOR
        }

//        canvas.save();
        //firstly, clip the rects of all the highlighted views.
        if (mLighterParameterList != null && mLighterParameterList?.isNotEmpty() == true) {
            for (lighterParameter in mLighterParameterList!!) {
                if (lighterParameter.highlightedView == null || (lighterParameter.lighterShape == null) || lighterParameter.lighterShape?.getViewRect() == null || lighterParameter.lighterShape?.getViewRect()?.isEmpty == true) {
                    continue
                }
                lighterParameter.lighterShape?.getShapePath()?.let { canvas.clipPath(it, Region.Op.DIFFERENCE) }
            }
        }

        //then, draw the bg color
        canvas.drawColor(mBgColor)

        //finally, draw the rects of all the highlighted views.
        if (mLighterParameterList != null && mLighterParameterList?.isNotEmpty() == true) {
            for (lighterParameter in mLighterParameterList!!) {
                if (isShapeEmpty(lighterParameter.lighterShape)) {
                    continue
                }
                lighterParameter.lighterShape?.onDraw(canvas)
            }
        }

//        canvas.restore();
    }

    fun setInitHeight(initHeight: Int) {
        mInitHeight = initHeight
    }

    fun setInitWidth(initWidth: Int) {
        mInitWidth = initWidth
    }
}

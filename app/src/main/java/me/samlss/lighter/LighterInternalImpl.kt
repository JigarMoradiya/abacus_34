package me.samlss.lighter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.jigar.me.R
import me.samlss.lighter.interfaces.LighterInternalAction
import me.samlss.lighter.interfaces.OnLighterListener
import me.samlss.lighter.interfaces.OnLighterViewClickListener
import me.samlss.lighter.parameter.LighterParameter
import me.samlss.lighter.parameter.MarginOffset
import me.samlss.lighter.shape.RectShape
import me.samlss.lighter.util.Preconditions.checkNotNull
import me.samlss.lighter.util.ViewUtils
import me.samlss.lighter.view.LighterView
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author SamLeung
 * @e-mail samlssplus@gmail.com
 * @github https://github.com/samlss
 * @description To handle all the function for [Lighter]
 */
class LighterInternalImpl : LighterInternalAction {
    private var mHighlightedParameterList: MutableList<List<LighterParameter>>? = ArrayList()
    private var mLighterView: LighterView? = null
    private var mRootView: ViewGroup? = null
    private var isReleased = false
    private var isAutoNext = true
    override var isShowing = false
        private set
    private var intercept = false
    private var isDecorView = false
    private var hasDidRootViewGlobalLayout = false
    private var mShowIndex = 0
    private var mOnLighterListener: OnLighterListener? = null
    private var mOutSideLighterClickListener: OnLighterViewClickListener? = null

    constructor(activity: Activity) {
        mLighterView = LighterView(activity)
        mRootView = activity.window.decorView as ViewGroup
        isDecorView = true
        activity.findViewById<View>(R.id.content)
            .addOnLayoutChangeListener(mRootViewLayoutChangeListener)
    }

    constructor(rootView: ViewGroup) {
        mRootView = rootView
        mLighterView = LighterView(rootView.context)
        mRootView!!.addOnLayoutChangeListener(mRootViewLayoutChangeListener)
    }

    fun addHighlight(vararg lighterParameters: LighterParameter?) {
        if (isReleased) {
            return
        }
        if (lighterParameters != null && lighterParameters.size > 0) {
            (mHighlightedParameterList as ArrayList<List<LighterParameter>>).add(Arrays.asList(*lighterParameters) as List<LighterParameter>)
        }
    }

    override fun hasNext(): Boolean {
        return if (isReleased) {
            false
        } else !mHighlightedParameterList!!.isEmpty()
    }

    override fun next() {
        if (isReleased) {
            return
        }

        //To ensure the root view has attached to window
        if (!ViewUtils.isAttachedToWindow(mRootView)) {
            show()
            return
        }
        if (!hasNext()) {
            dismiss()
        } else {
            isShowing = true
            if (mOnLighterListener != null) {
                mOnLighterListener!!.onShow(mShowIndex)
            }
            mShowIndex++
            val lighterParameters = mHighlightedParameterList!![0]
            for (lighterParameter in lighterParameters) {
                checkLighterParameter(lighterParameter)
            }
            mLighterView!!.setInitWidth(mRootView!!.width - mRootView!!.paddingLeft - mRootView!!.paddingRight)
            mLighterView!!.setInitHeight(mRootView!!.height - mRootView!!.paddingTop - mRootView!!.paddingBottom)
            mLighterView!!.addHighlight(lighterParameters)
            mHighlightedParameterList!!.removeAt(0)
        }
    }

    /**
     * Check parameter.
     */
    private fun checkLighterParameter(lighterParameter: LighterParameter) {
        if (lighterParameter.lighterShape == null) {
            lighterParameter.lighterShape = RectShape()
        }
        if (lighterParameter.highlightedView == null) {
            lighterParameter.highlightedView =
                mRootView!!.findViewById(lighterParameter.highlightedViewId)
        }
        if (lighterParameter.tipView == null) {
            lighterParameter.tipView = LayoutInflater.from(mLighterView!!.context).inflate(
                lighterParameter.tipLayoutId,
                mLighterView, false
            )
        }
        if (lighterParameter.highlightedView == null) {
            checkNotNull(
                lighterParameter.highlightedView,
                "Please pass a highlighted view or an id of highlighted."
            )
        }
        if (lighterParameter.tipView == null) {
            checkNotNull(
                lighterParameter.tipView,
                "Please pass a tip view or a layout id of tip view."
            )
        }
        if (lighterParameter.tipViewRelativeMarginOffset == null) {
            lighterParameter.tipViewRelativeMarginOffset = MarginOffset() //use empty offset.
        }
        ViewUtils.calculateHighlightedViewRect(mLighterView, lighterParameter)
    }

    /**
     * Release all when all specified highlights are completed.
     */
    private fun onRelease() {
        if (isReleased) {
            return
        }
        isReleased = true
        if (isDecorView) {
            mRootView!!.findViewById<View>(R.id.content)
                .removeOnLayoutChangeListener(mRootViewLayoutChangeListener)
        } else {
            mRootView!!.removeOnLayoutChangeListener(mRootViewLayoutChangeListener)
        }
        mRootView!!.removeView(mLighterView)
        mLighterView!!.removeAllViews()
        mHighlightedParameterList!!.clear()
        mHighlightedParameterList = null
        mLighterViewClickListener = null
        mOnLighterListener = null
        mRootView = null
        mLighterView = null
    }

    override fun show() {
        if (isReleased) {
            return
        }
        if (!intercept) {
            mLighterView!!.setOnClickListener(mLighterViewClickListener)
        }

        //To ensure the root view has attached to window
        if (ViewUtils.isAttachedToWindow(mRootView)) {
            if (mLighterView!!.parent == null) {
                mRootView!!.addView(
                    mLighterView,
                    ViewGroup.LayoutParams(mRootView!!.width, mRootView!!.height)
                )
            }
            mShowIndex = 0
            next()
        } else {
            mRootView!!.viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
        }
    }

    override fun dismiss() {
        if (mOnLighterListener != null) {
            mOnLighterListener!!.onDismiss()
        }
        isShowing = false
        onRelease()
    }

    /**
     * When you call the [.show] method, maybe the [.mRootView] has not been initialized yet and the highlighted view property gets failed.
     * Therefore, before [.show], will invoke [ViewUtils.isAttachedToWindow] to check if the [.mRootView] has attached to window,
     * if yes, show directly, otherwise will add [ViewTreeObserver.addOnGlobalLayoutListener].
     */
    private val mGlobalLayoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            /**
             * Guaranteed to be called only once.
             */
            if (hasDidRootViewGlobalLayout) {
                return
            }
            hasDidRootViewGlobalLayout = true
            mRootView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            show()
        }
    }
    private val mRootViewLayoutChangeListener =
        View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if ((left == oldLeft) && top == oldTop && right == oldRight && bottom == oldBottom) {
                return@OnLayoutChangeListener
            }
            if (mLighterView == null || mLighterView!!.parent == null) {
                return@OnLayoutChangeListener
            }
            if (!isDecorView) {
                val layoutParams = mLighterView!!.layoutParams
                layoutParams.width = Math.abs(right - left)
                layoutParams.height = Math.abs(bottom - top)
                mLighterView!!.setInitWidth(layoutParams.width)
                mLighterView!!.setInitHeight(layoutParams.height)
                mLighterView!!.layoutParams = layoutParams
            }
            mLighterView!!.reLayout()
        }

    fun setBackgroundColor(color: Int) {
        if (isReleased) {
            return
        }
        mLighterView!!.setBackgroundColor(color)
    }

    private var mLighterViewClickListener: View.OnClickListener? =
        View.OnClickListener { v ->
            if (mOutSideLighterClickListener != null) {
                mOutSideLighterClickListener!!.onClick(v)
            }
            if (isAutoNext) {
                next()
            }
        }

    fun setAutoNext(autoNext: Boolean) {
        isAutoNext = autoNext
    }

    fun setIntercept(intercept: Boolean) {
        this.intercept = intercept
    }

    fun setOnClickListener(clickListener: OnLighterViewClickListener?) {
        mOutSideLighterClickListener = clickListener
    }

    fun setOnLighterListener(lighterListener: OnLighterListener?) {
        mOnLighterListener = lighterListener
    }
}
package com.jigar.me.utils.widget.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager


class CustomViewPagerSpeed(context: Context, attrs: AttributeSet) :
    ViewPager(context, attrs) {
    var isEnable = true
    private var mScroller: FixedSpeedScroller? = null
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnable) {
            return super.onTouchEvent(event)
        }
        init()
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isEnable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isEnable = enabled
    }

    /*
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private fun init() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            mScroller = FixedSpeedScroller(
                context,
                DecelerateInterpolator()
            )
            scroller[this] = mScroller
        } catch (ignored: Exception) {
        }
    }

    /*
     * Set the factor by which the duration will change
     */
    fun setScrollDuration(duration: Int) {
        mScroller?.setScrollDuration(duration)
    }

    private inner class FixedSpeedScroller : Scroller {
        private var mDuration = 800
        constructor(context: Context?) : super(context)
        constructor(context: Context?, interpolator: Interpolator?) : super(context,interpolator)
        constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(context,interpolator,flywheel)

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        fun setScrollDuration(duration: Int) {
            mDuration = duration
        }
    }

    init {
        init()
    }
}
package com.jigar.me.utils.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner


class CustomSpinner : AppCompatSpinner {
    // private static final String TAG = "CustomSpinner";
    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, mode: Int) : super(context,attrs,defStyleAttr,mode) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, mode: Int) : super(context, mode) {}
    constructor(context: Context) : super(context) {}

    interface OnSpinnerEventsListener {
        fun onSpinnerOpened(spin: AppCompatSpinner?)
        fun onSpinnerClosed(spin: AppCompatSpinner?)
    }

    override fun performClick(): Boolean {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true
        if (mListener != null) {
            mListener?.onSpinnerOpened(this)
        }
        return super.performClick()
    }

    fun setSpinnerEventsListener(onSpinnerEventsListener: OnSpinnerEventsListener?) {
        mListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    fun performClosedEvent() {
        mOpenInitiated = false
        if (mListener != null) {
            mListener?.onSpinnerClosed(this)
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasBeenOpened() && hasWindowFocus) {
            performClosedEvent()
        }
    }
}
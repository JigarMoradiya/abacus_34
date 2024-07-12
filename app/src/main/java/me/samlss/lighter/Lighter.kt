package me.samlss.lighter

import android.app.Activity
import android.view.ViewGroup
import me.samlss.lighter.interfaces.LighterInternalAction
import me.samlss.lighter.interfaces.OnLighterListener
import me.samlss.lighter.interfaces.OnLighterViewClickListener
import me.samlss.lighter.parameter.LighterParameter
import me.samlss.lighter.util.Preconditions.checkNotNull

class Lighter : LighterInternalAction {
    private var mInternalImpl: LighterInternalImpl

    private constructor(rootView: ViewGroup) {
        mInternalImpl = LighterInternalImpl(rootView)
    }

    private constructor(activity: Activity) {
        mInternalImpl = LighterInternalImpl(activity)
    }

    /**
     * Add highlight parameter <br></br>>
     *
     * Can highlight multiple parameters simultaneously.
     *
     * @param lighterParameters The specified highlight parameter
     */
    fun addHighlight(vararg lighterParameters: LighterParameter?): Lighter {
        mInternalImpl.addHighlight(*lighterParameters)
        return this
    }

    /**
     * Set background color of the highlighted view.
     *
     * @param color The background color, default is .
     */
    fun setBackgroundColor(color: Int): Lighter {
        mInternalImpl.setBackgroundColor(color)
        return this
    }

    /**
     * Set whether to automatically show the next highlighted view, the default is true.
     * If true, when click the [LighterView] & tip view, will show the next highlighted view automatically.
     * Otherwise you need to invoke [.next] manually.
     */
    fun setAutoNext(autoNext: Boolean): Lighter {
        mInternalImpl.setAutoNext(autoNext)
        return this
    }

    /**
     * Set whether to intercept the click event of the [LighterView]. If it's true, it will not process the click event.
     * You need to manually handle the next event, and the view under the [LighterView] can be clicked.
     * And the [OnLighterViewClickListener] will not be invoked.
     */
    fun setIntercept(intercept: Boolean): Lighter {
        mInternalImpl.setIntercept(intercept)
        return this
    }

    /**
     * Set click listener of [LighterView] & tip view.
     */
    fun setOnClickListener(clickListener: OnLighterViewClickListener?): Lighter {
        mInternalImpl.setOnClickListener(clickListener)
        return this
    }

    /**
     * Set click listener of [LighterView] & tip view.
     */
    fun setOnLighterListener(onLighterListener: OnLighterListener?): Lighter {
        mInternalImpl.setOnLighterListener(onLighterListener)
        return this
    }

    override fun hasNext(): Boolean {
        return mInternalImpl.hasNext()
    }

    override fun next() {
        mInternalImpl.next()
    }

    /**
     * Start to show highlight
     */
    override fun show() {
        mInternalImpl.show()
    }

    override val isShowing: Boolean
        get() = mInternalImpl.isShowing

    override fun dismiss() {
        mInternalImpl.dismiss()
    }

    companion object {
        /**
         * Create a [Lighter] with an [Activity]. The highlighted view will fill the entire window. <br></br>
         * Will call to [android.view.WindowManager.addView] to attached the [LighterView]
         * to the window.
         *
         * @param activity The activity that will be attach by the [LighterView].
         */
//        fun with(activity: Activity): Lighter {
//            checkNotNull(activity,"You can not show a highlight view on a null activity.")
//            return Lighter(activity)
//        }

        /**
         * Create a [Lighter] with an [Activity] br>>
         * The highlighted view will fill the entire root view.
         *
         * @param rootView The root view that will be attach by the [LighterView].
         */
        fun with(rootView: ViewGroup): Lighter {
            checkNotNull(rootView,"You can not show a highlight view on a null root view.")
            return Lighter(rootView)
        }
    }
}
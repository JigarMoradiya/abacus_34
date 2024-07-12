package me.samlss.lighter.interfaces

interface LighterInternalAction {
    /**
     * Check if there is any next highlight
     */
    operator fun hasNext(): Boolean

    /**
     * Make the next highlight.
     */
    operator fun next()

    /**
     * Start to show highlight.
     */
    fun show()

    /**
     * Check if it is showing
     */
    val isShowing: Boolean

    /**
     * Dismiss highlight.
     */
    fun dismiss()
}

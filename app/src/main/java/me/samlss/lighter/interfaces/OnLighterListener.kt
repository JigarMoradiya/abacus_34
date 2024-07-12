package me.samlss.lighter.interfaces

interface OnLighterListener {
    /**
     * When the highlight is displayed, this method will be called back.
     *
     * @param index index of the number of highlights you configured.
     */
    fun onShow(index: Int)

    /**
     * Call back this method when the all highlights has been displayed.
     */
    fun onDismiss()
}
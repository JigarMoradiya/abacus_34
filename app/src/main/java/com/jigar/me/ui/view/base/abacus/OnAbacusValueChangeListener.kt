package com.jigar.me.ui.view.base.abacus

import android.view.View

interface OnAbacusValueChangeListener {
    fun onAbacusValueChange(abacusView: View, sum: Long)
    fun onAbacusValueSubmit(sum: Long)
    fun onAbacusValueDotReset()
}
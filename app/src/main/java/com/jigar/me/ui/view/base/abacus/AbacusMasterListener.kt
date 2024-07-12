package com.jigar.me.ui.view.base.abacus

interface AbacusMasterBeadShiftListener {
    fun onBeadShift(abacusView: AbacusMasterView, rowValue: IntArray)
}

interface OnSingleDrawingCompletedListener {
    fun onSingleDrawingCompleted()
}

abstract class AbacusMasterCompleteListener {
    var noOfTimeCompleted = 0

    abstract fun onSetPositionComplete()
}
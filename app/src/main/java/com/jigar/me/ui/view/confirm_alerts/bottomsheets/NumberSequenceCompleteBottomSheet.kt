package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.databinding.BottomSheetNumberSequencCompleteBinding
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr

object NumberSequenceCompleteBottomSheet {
    fun showPopup(activity: Activity, type: Int, totalMoves: Int, bestMoves: Int, clickListener: NumberSequenceCompleteDialogInterface) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetNumberSequencCompleteBinding = BottomSheetNumberSequencCompleteBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        with(sheetBinding){
            tvDescription.text = String.format(activity.getString(R.string.you_completed_this_puzzle_in_moves,totalMoves.toString()))
            val puzzleType = when (type) {
                4 -> {
                    activity.getString(R.string._4_x_4_puzzle)
                }
                5 -> {
                    activity.getString(R.string._5_x_5_puzzle)
                }
                else -> {
                    activity.getString(R.string._3_x_3_puzzle)
                }
            }
            if (totalMoves <= bestMoves){
                tvSubDescription.text = String.format(activity.getString(R.string.this_is_your_best_moves_in_puzzle, puzzleType))
            }else{
                tvSubDescription.text = String.format(activity.getString(R.string.but_your_best_moves_in_puzzle_is,puzzleType,"$bestMoves"))
                tvSubDescription.setTextColor(ContextCompat.getColor(activity,R.color.red))
            }
            btnYes.onClick {
                bottomSheetDialog.dismiss()
                clickListener.numberSequenceCompleteContinue()
            }
            tvNo.onClick {
                bottomSheetDialog.dismiss()
                clickListener.numberSequenceCompleteClose()
            }
            bottomSheetDialog.setOnCancelListener {
                clickListener.numberSequenceCompleteClose()
            }
            bottomSheetDialog.setContentView(root)
        }

        activity.setBottomSheetDialogAttr(bottomSheetDialog, Constants.bottomSheetWidthBaseOnRatio4)
        bottomSheetDialog.show()
    }

    interface NumberSequenceCompleteDialogInterface {
        fun numberSequenceCompleteClose()
        fun numberSequenceCompleteContinue()
    }

}
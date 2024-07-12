package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.CustomChallengeData
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.BottomSheetCcmCompleteBinding
import com.jigar.me.databinding.BottomSheetNumberSequencCompleteBinding
import com.jigar.me.utils.Constants
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr

object CCMCompleteBottomSheet {
    fun showPopup(activity: Activity, challengeData : CustomChallengeData? = null, isAnswerTrue: Boolean, clickListener: CCMCompleteDialogInterface) {
        PlaySound.play(activity, PlaySound.number_puzzle_win)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetCcmCompleteBinding = BottomSheetCcmCompleteBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        with(sheetBinding){
            tvAnswer.text = HtmlCompat.fromHtml(challengeData?.fullQuestion?.replace("+"," + ")?.replace("-"," - ")?.plus(" = ")+"<b>"+challengeData?.answer+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (isAnswerTrue){
                tvTitle.text = activity.getString(R.string.congratulations)
                val list : ArrayList<String> = arrayListOf()
                list.add(activity.getString(R.string.good_job))
                list.add(activity.getString(R.string.you_are_clever))
                list.add(activity.getString(R.string.you_are_glorious))
                list.add(activity.getString(R.string.you_are_brilliant))
                list.add(activity.getString(R.string.you_are_so_genius))
                list.add(activity.getString(R.string.you_are_so_genius))
                list.add(activity.getString(R.string.you_are_so_intelligent))
                list.shuffle()
                tvSubDescription.text = list.first()
                imgAlert.setImageResource(R.drawable.ic_complete)
            }else{
                tvTitle.text = activity.getString(R.string.sorry)
                tvSubDescription.text = activity.getString(R.string.better_luck_for_next_time)
                tvTitle.setTextColor(ContextCompat.getColor(activity,R.color.black))
                tvSubDescription.setTextColor(ContextCompat.getColor(activity,R.color.red))
                imgAlert.setImageResource(R.drawable.ic_not_complete_smiley)
            }

            btnYes.onClick {
                bottomSheetDialog.dismiss()
                clickListener.ccmCompleteContinue()
            }
            tvNo.onClick {
                bottomSheetDialog.dismiss()
                clickListener.ccmCompleteClose()
            }
            bottomSheetDialog.setOnCancelListener {
                clickListener.ccmCompleteClose()
            }
            bottomSheetDialog.setContentView(root)
        }

        activity.setBottomSheetDialogAttr(bottomSheetDialog, Constants.bottomSheetWidthBaseOnRatio5)
        bottomSheetDialog.show()
    }

    interface CCMCompleteDialogInterface {
        fun ccmCompleteClose()
        fun ccmCompleteContinue()
    }

}
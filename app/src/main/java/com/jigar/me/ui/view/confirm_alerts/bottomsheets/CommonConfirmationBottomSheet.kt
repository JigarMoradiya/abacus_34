package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.databinding.BottomSheetCommanConfimationBinding
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr
import com.jigar.me.utils.extensions.show

object CommonConfirmationBottomSheet {
    fun showPopup(activity: Activity, title : String, msg : String? = null,
                  yesBtn : String? = null, noBtn : String? = null, closeBtn : String? = null, bundle: Bundle? = null,
                  icon : Int? = null, isCancelable : Boolean = true, clickListener : OnItemClickListener? = null) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetCommanConfimationBinding = BottomSheetCommanConfimationBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(isCancelable)
        bottomSheetDialog.setCanceledOnTouchOutside(isCancelable)
        with(sheetBinding){
            tvTitle.text = title
            if (!closeBtn.isNullOrEmpty()){
                tvClose.show()
                tvClose.text = closeBtn
            }
            if (!msg.isNullOrEmpty()){
                tvDescription.show()
                tvDescription.text = msg
            }
            if (!yesBtn.isNullOrEmpty()){
                btnYes.text = yesBtn
            }
            if (!noBtn.isNullOrEmpty()){
                tvNo.text = HtmlCompat.fromHtml(noBtn,HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            if (icon != null){
                sheetBinding.imgAlert.setImageResource(icon)
                sheetBinding.imgAlert.show()
            }
            btnYes.onClick {
                bottomSheetDialog.dismiss()
                clickListener?.onConfirmationYesClick(bundle)
            }
            tvNo.onClick {
                bottomSheetDialog.dismiss()
                clickListener?.onConfirmationNoClick(bundle)
            }
            tvClose.onClick {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setOnCancelListener {
                clickListener?.onConfirmationNoClick(bundle)
            }

            bottomSheetDialog.setContentView(root)
        }

        activity.setBottomSheetDialogAttr(bottomSheetDialog, widthRatio = Constants.bottomSheetWidthBaseOnRatio7)
        bottomSheetDialog.show()
    }

    interface OnItemClickListener {
        fun onConfirmationYesClick(bundle: Bundle?)
        fun onConfirmationNoClick(bundle: Bundle?)
    }
}
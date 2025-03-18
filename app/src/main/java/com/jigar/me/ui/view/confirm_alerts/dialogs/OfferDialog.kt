package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.jigar.me.R
import com.jigar.me.data.model.data.DiscountData
import com.jigar.me.databinding.DialogOfferBinding
import com.jigar.me.utils.extensions.onClick

object OfferDialog {

    var alertdialog: AlertDialog? = null

    fun hideDialog() {
        alertdialog?.dismiss()
    }

    fun showPopup(
        activity: Activity, discountData: DiscountData?, listener: DialogOfferInterface
    ) {
        val alertLayout = DialogOfferBinding.inflate(activity.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(alertLayout.root)
        alertLayout.data = discountData

        alertLayout.btnNo.onClick {
            hideDialog()
        }
        alertLayout.btnYes.onClick {
            hideDialog()
            listener.onSubmitYesClick()
        }
        alertBuilder.setView(alertLayout.root)
        alertBuilder.setCancelable(false)
        alertdialog = alertBuilder.show()
        alertdialog?.setCanceledOnTouchOutside(false)
        val windows = alertdialog?.window
        val colorD = ColorDrawable(Color.TRANSPARENT)
        val insetD = InsetDrawable(colorD, 40, 5, 40, 5)
        windows?.setBackgroundDrawable(insetD)
        // Setting Animation for Appearing from Center
        windows?.attributes?.windowAnimations = R.style.DialogAppearFromCenter
        // Positioning it in Bottom Right
        val wlp = windows?.attributes
//        wlp?.width = WindowManager.LayoutParams.WRAP_CONTENT
//        wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp?.gravity = Gravity.CENTER
        windows?.attributes = wlp
        alertdialog?.show()
    }

    interface DialogOfferInterface {
        fun onSubmitYesClick()
    }

}
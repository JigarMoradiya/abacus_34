package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.jigar.me.R
import com.jigar.me.databinding.DialogExamCompleteBinding
import com.jigar.me.databinding.DialogToddlerRangeBinding
import com.jigar.me.utils.extensions.onClick

object ToddlerRangeDialog {

    var alertdialog: AlertDialog? = null

    fun hideDialog() {
        alertdialog?.dismiss()
    }

    fun showPopup(
        activity: Activity,fromValue: String,toValue: String,listener: ToddlerRangeDialogInterface) {
        val alertLayout = DialogToddlerRangeBinding.inflate(activity.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(alertLayout.root)

        alertLayout.txtFrom.setText(fromValue)
        alertLayout.txtTo.setText(toValue)

        alertLayout.btnNo.onClick {
            hideDialog()
        }
        alertLayout.btnYes.onClick {
            alertLayout.layoutEdtTo.isErrorEnabled = false
            alertLayout.layoutEdtTo.isErrorEnabled = false
            val str_from: String = alertLayout.txtFrom.text.toString().trim()
            val str_to: String = alertLayout.txtTo.text.toString().trim()
            when {
                str_from.isEmpty() -> {
                    alertLayout.layoutEdtFrom.error = activity.resources.getString(R.string.error_range_from)
                    requestFocus(alertLayout.txtFrom, activity)
                }
                str_to.isEmpty() -> {
                    alertLayout.layoutEdtTo.error = activity.resources.getString(R.string.error_range_to)
                    requestFocus(alertLayout.txtTo, activity)
                }
                str_to.toLong() - str_from.toLong() < 50 -> {
                    alertLayout.layoutEdtTo.error = activity.resources.getString(R.string.error_range)
                    requestFocus(alertLayout.txtTo, activity)
                }
                else -> {
                    hideDialog()
                    listener.onSubmitClickToddlerRange(alertLayout.txtFrom.text.toString(),alertLayout.txtTo.text.toString())
                }
            }

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
        wlp?.width = WindowManager.LayoutParams.WRAP_CONTENT
        wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp?.gravity = Gravity.CENTER
        windows?.attributes = wlp
        alertdialog?.show()
    }

    fun requestFocus(view: View, activity: Activity) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    interface ToddlerRangeDialogInterface {
        fun onSubmitClickToddlerRange(fromValue: String, toValue: String)
    }

}
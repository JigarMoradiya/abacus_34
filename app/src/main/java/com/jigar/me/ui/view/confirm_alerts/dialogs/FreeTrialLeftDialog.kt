package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.jigar.me.R
import com.jigar.me.data.model.data.DiscountData
import com.jigar.me.databinding.DialogFreeTrialLeftBinding
import com.jigar.me.databinding.DialogOfferBinding
import com.jigar.me.utils.extensions.onClick
import androidx.core.graphics.drawable.toDrawable
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.show

object FreeTrialLeftDialog {

    var alertdialog: AlertDialog? = null

    fun hideDialog() {
        alertdialog?.dismiss()
    }

    fun showPopup(
        activity: Activity, free_trial_remaining_days : Int, listener: DialogFreeTrialInterface
    ) {
        val alertLayout = DialogFreeTrialLeftBinding.inflate(activity.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(activity)

        with(alertLayout){
            alertBuilder.setView(root)
            btnNo.onClick {
                hideDialog()
            }
            btnYes.onClick {
                hideDialog()
                listener.onSubmitYesClick()
            }

            if (free_trial_remaining_days >= 7){
                linearTrialStart.show()
                linearDayLefts.hide()
                btnNo.hide()
                txtTitle.text = "Welcome to your 7-day free trial!"
                txtDesc.text = "You now have full access — explore, learn, and enjoy your journey."
                btnYes.text = "Start Learning"
            }else{
                imgNow.show()
                linearTrialStart.hide()
                linearDayLefts.show()
                btnNo.show()

                when (free_trial_remaining_days) {
                    6 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_6)
                        txtTitle.text = "6 days left in your free trial."
                        txtDesc.text = "Keep exploring and see all the features waiting for you!"
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Continue Learning"
                    }
                    5 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_5)
                        txtTitle.text = "Your free trial ends in 5 days."
                        txtDesc.text = "Enjoy full access to all lessons during your trial!"
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Keep Going"
                    }
                    4 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_4)
                        txtTitle.text = "Only 4 days left!"
                        txtDesc.text = "You're on the right track - don't stop now, success is just ahead!"
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Continue"
                    }
                    3 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_3)
                        txtTitle.text = "3 days remaining in your free trial."
                        txtDesc.text = "Keep your learning data and achievements safe by unlocking full access before your trial ends."
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Maybe Later"
                    }
                    2 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_2)
                        txtTitle.text = "Your free trial ends soon - only 2 days left!"
                        txtDesc.text = "Continue your learning adventure by subscribing today & get full access to all lessons and interactive modules."
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Remind Me Later"
                    }
                    1 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_1)
                        txtTitle.text = "Last day of your free trial!"
                        txtDesc.text = "Stay on track with your learning - subscribe today to keep full access to all lessons and modules."
                        btnYes.text = "Subscribe Now"
                        btnNo.text = "Maybe Later"
                    }
                    0 -> {
                        imgNumber.setImageResource(R.drawable.ic_number_0)
                        txtTitle.text = "Your free trial has ended."
                        txtDesc.text = "Subscribe now to continue your learning journey and unlock full access."
                        btnYes.text = "View Plans"
                        btnNo.hide()
                    }
                }
            }
        }
        alertBuilder.setCancelable(false)
        alertdialog = alertBuilder.show()
        alertdialog?.setCanceledOnTouchOutside(false)
        val windows = alertdialog?.window
        val colorD = Color.TRANSPARENT.toDrawable()
        val insetD = InsetDrawable(colorD, 40, 5, 40, 5)
        windows?.setBackgroundDrawable(insetD)
        // Setting Animation for Appearing from Center
        windows?.attributes?.windowAnimations = R.style.DialogAppearFromCenter
        // Positioning it in Bottom Right
        val wlp = windows?.attributes
        wlp?.gravity = Gravity.CENTER
        windows?.attributes = wlp
        alertdialog?.show()
    }

    interface DialogFreeTrialInterface {
        fun onSubmitYesClick()
    }

}
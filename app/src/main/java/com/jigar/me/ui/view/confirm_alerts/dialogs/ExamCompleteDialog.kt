package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.DialogExamCompleteBinding
import com.jigar.me.utils.Constants
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.onClick

object ExamCompleteDialog {

    var alertdialog: AlertDialog? = null

    fun showPopup(activity: Activity, totalTime: String, totalSkip: String, totalWrong: String,
                  totalRight: String, totalQuestion: String, listener: TestCompleteDialogInterface,prefManager : AppPreferencesHelper) {

        PlaySound.play(activity, PlaySound.number_puzzle_win)

        val alertLayout = DialogExamCompleteBinding.inflate(activity.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(alertLayout.root)

        alertLayout.txtTotalTime.text = totalTime
        alertLayout.txtWrongQue.text = totalWrong
        alertLayout.txtRightQue.text = totalRight
        alertLayout.txtTotalQue.text = totalQuestion

        val percentage : Float = ((totalRight.toFloat() * 100) / totalQuestion.toFloat())
        val userName = prefManager.getCustomParam(Constants.childName,"")
        var msgPrefix = ""
        if (userName.isNotEmpty()){
            msgPrefix = "$userName, "
        }
        val msg = if (percentage == 100F){
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.green_800))
            val index = DataProvider.generateIndex(3)
             when (index) {
                0 -> {
                    activity.getString(R.string.unstoppable_great_job)
                }
                1 -> {
                    activity.getString(R.string.you_are_so_intelligent)
                }
                else -> { // index = 2
                    activity.getString(R.string.you_are_so_genius)
                }
            }
        }else if (percentage >= 90F){
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.blue_800))
            val index = DataProvider.generateIndex(3)
            when (index) {
                0 -> {
                    activity.getString(R.string.you_are_brilliant)
                }
                1 -> {
                    activity.getString(R.string.you_are_glorious)
                }
                else -> { // index = 2
                    activity.getString(R.string.you_are_clever)
                }
            }
        }else if (percentage >= 80F){
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.purple_800))
            val index = DataProvider.generateIndex(1)
            when (index) {
                0 -> {
                    activity.getString(R.string.you_are_on_the_right_track)
                }
                else -> { // index = 1
                    activity.getString(R.string.you_are_on_the_right_track)
                }
            }
        }else if (percentage >= 70F){
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.lime_800))
            activity.getString(R.string.good_job)
        }else if (percentage >= 60F){
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.orange_800))
            if (userName.isNotEmpty()){
                msgPrefix = "$userName "
            }
            activity.getString(R.string.not_bad)
        }else{
            alertLayout.tvResultTitle.setTextColor(ContextCompat.getColor(activity,R.color.red_800))
            activity.getString(R.string.better_luck_for_next_time)
        }
        if (msgPrefix.isEmpty()){
            alertLayout.tvResultTitle.text = msg
        }else{
            alertLayout.tvResultTitle.text = msgPrefix.plus(msg.lowercase())
        }

        val percentageRating : Float = ((totalRight.toFloat() * 5) / totalQuestion.toFloat())
        alertLayout.simpleRatingBar.rating = percentageRating
        alertLayout.tvClose.onClick {
            alertdialog?.dismiss()
            listener.testCompleteClose()
        }

        alertLayout.btnGiveExamAgain.onClick {
            alertdialog?.dismiss()
            listener.testGiveAgain()
        }

        alertLayout.btnCheckResult.onClick {
            alertdialog?.dismiss()
            listener.testCompleteGotoResult()
        }
        alertBuilder.setView(alertLayout.root)
        alertBuilder.setCancelable(false)
        alertdialog = alertBuilder.show()
        val windows = alertdialog?.window
        val colorD = ColorDrawable(Color.TRANSPARENT)
        val insetD = InsetDrawable(colorD, 100, 5, 100, 5)
        windows?.setBackgroundDrawable(insetD)
        // Setting Animation for Appearing from Center
        windows?.attributes?.windowAnimations = R.style.DialogAppearFromCenter
        // Positioning it in Bottom Right
        val wlp = windows?.attributes
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        wlp?.gravity = Gravity.CENTER
        windows?.attributes = wlp
        alertdialog?.show()
    }

    interface TestCompleteDialogInterface {
        fun testCompleteClose()
        fun testGiveAgain()
        fun testCompleteGotoResult()
    }

}
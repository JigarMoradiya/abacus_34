package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.ExerciseLevel
import com.jigar.me.data.local.data.ExerciseLevelDetail
import com.jigar.me.data.local.data.ExerciseList
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.DialogExerciseCompleteBinding
import com.jigar.me.ui.view.dashboard.fragments.exercise.adapter.ExerciseAdditionSubtractionResultAdapter
import com.jigar.me.ui.view.dashboard.fragments.exercise.adapter.ExerciseMultiplicationDivisionResultAdapter
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.*

object ExerciseCompleteDialog {

    var alertdialog: AlertDialog? = null

    fun showPopup(context: Context,resultType : String?, listExercise : MutableList<ExerciseList>, listener: ExerciseCompleteDialogInterface) {
        val alertLayout = DialogExerciseCompleteBinding.inflate(context.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setView(alertLayout.root)

        if (resultType == AppConstants.apiParams.answerFormalAnswer){
            alertLayout.tvTitle.text = context.getString(R.string.result_of_practice_set)
        }

        if (resultType == AppConstants.apiParams.answerFormalAnswer || listExercise.first().question.contains("x") || listExercise.first().question.contains("*") || listExercise.first().question.contains("/")){
            if (listExercise.size > 5){
                alertLayout.recyclerview.layoutManager = GridLayoutManager(context,2)
            }else{
                alertLayout.recyclerview.layoutManager = GridLayoutManager(context,1)
            }

            val adapter = ExerciseMultiplicationDivisionResultAdapter(listExercise)
            alertLayout.recyclerview.adapter = adapter
        }else{
            alertLayout.recyclerview.layoutManager = GridLayoutManager(context,5)
            val adapter = ExerciseAdditionSubtractionResultAdapter(listExercise)
            alertLayout.recyclerview.adapter = adapter
        }

        alertLayout.tvClose.onClick {
            alertdialog?.dismiss()
            listener.exerciseCompleteCloseDialog()
        }

        alertBuilder.setView(alertLayout.root)
        alertBuilder.setCancelable(false)
        alertdialog = alertBuilder.create()

        val windows = alertdialog?.window
        val colorD = ColorDrawable(Color.TRANSPARENT)
        val insetD = if (listExercise.first().question.contains("x") || listExercise.first().question.contains("*")){
            if (listExercise.size > 5){
                InsetDrawable(colorD, 100.dp, 40.dp, 100.dp, 10.dp)
            }else{
                InsetDrawable(colorD,150.dp, 40.dp, 150.dp, 10.dp)
            }
        }else{
            if (listExercise.size > 5){
                InsetDrawable(colorD, 50.dp, 40.dp, 50.dp, 10.dp)
            }else{
                InsetDrawable(colorD,150.dp, 40.dp, 150.dp, 10.dp)
            }
        }


        windows?.setBackgroundDrawable(insetD)
//         Setting Animation for Appearing from Center
        windows?.attributes?.windowAnimations = R.style.DialogAppearFromCenter
//         Positioning it in Bottom Right
//        val wlp = windows?.attributes
//        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
//        wlp?.height = WindowManager.LayoutParams.MATCH_PARENT
//        windows?.attributes = wlp
        alertdialog?.setOnShowListener {
            alertdialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            alertdialog?.window?.setGravity(Gravity.CENTER)
        }
        alertdialog?.show()
    }

    interface ExerciseCompleteDialogInterface {
        fun exerciseCompleteCloseDialog()
    }

}
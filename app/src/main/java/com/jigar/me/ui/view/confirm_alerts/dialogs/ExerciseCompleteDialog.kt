package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.Gravity
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

        if (resultType == AppConstants.apiParams.answerFormalAnswer || listExercise.first().question.contains("x") || listExercise.first().question.contains("/")){
            alertLayout.recyclerview.show()
            alertLayout.recyclerviewAddition.hide()
            if (listExercise.size > 5){
                alertLayout.recyclerview.layoutManager = GridLayoutManager(context,2)
            }else{
                alertLayout.recyclerview.layoutManager = GridLayoutManager(context,1)
            }

            val adapter = ExerciseMultiplicationDivisionResultAdapter(listExercise)
            alertLayout.recyclerview.adapter = adapter
        }else{
            alertLayout.recyclerview.hide()
            alertLayout.recyclerviewAddition.show()
            alertLayout.recyclerviewAddition.layoutManager = GridLayoutManager(context,listExercise.size)
            val adapter = ExerciseAdditionSubtractionResultAdapter(listExercise)
            alertLayout.recyclerviewAddition.adapter = adapter
        }

        alertLayout.tvClose.onClick {
            alertdialog?.dismiss()
            listener.exerciseCompleteCloseDialog()
        }

        alertBuilder.setView(alertLayout.root)
        alertBuilder.setCancelable(false)
        alertdialog = alertBuilder.show()
        val windows = alertdialog?.window
        val colorD = ColorDrawable(Color.TRANSPARENT)
        val insetD = if (listExercise.first().question.contains("x")){
            if (listExercise.size > 5){
                InsetDrawable(colorD, 100.dp, 0, 100.dp, 0)
            }else{
                InsetDrawable(colorD,250.dp, 0, 250.dp, 0)
            }
        }else{
            if (listExercise.size > 5){
                InsetDrawable(colorD, 50.dp, 0, 50.dp, 0)
            }else{
                InsetDrawable(colorD,150.dp, 0, 150.dp, 0)
            }
        }


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

    interface ExerciseCompleteDialogInterface {
        fun exerciseCompleteCloseDialog()
    }

}
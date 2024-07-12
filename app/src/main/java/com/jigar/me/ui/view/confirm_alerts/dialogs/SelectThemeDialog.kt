package com.jigar.me.ui.view.confirm_alerts.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.*
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.DialogExerciseCompleteBinding
import com.jigar.me.databinding.DialogSelectThemeBinding
import com.jigar.me.databinding.LayoutAbacusExamBinding
import com.jigar.me.ui.view.base.abacus.AbacusUtils
import com.jigar.me.ui.view.dashboard.fragments.settings.AbacusThemeSelectionsAdapter
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.*

object SelectThemeDialog {
    fun showPopup(activity: Activity,prefManager : AppPreferencesHelper,listener : DialogInterface) {
        val alertLayout = DialogSelectThemeBinding.inflate(activity.layoutInflater,null,false)
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setView(alertLayout.root)
        alertBuilder.setCancelable(true)
        val alertdialog = alertBuilder.show()

        with(alertLayout){
            btnDone.onClick {
                alertdialog.dismiss()
                listener.themeCloseDialogClick()
            }
            tvNo.onClick {
                alertdialog.dismiss()
                listener.themeCloseDialogClick()
            }

            var selectedFreePosition = -1
            val freeList = DataProvider.getAbacusThemeFreeTypeList(activity,AbacusBeadType.ExamResult)
            if (prefManager.getCustomParam(AppConstants.Settings.Theam, AppConstants.Settings.theam_Default).contains(AppConstants.Settings.theam_Default,true)){
                val position : Int? = freeList.indexOfFirst { it.type.equals(prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default),true) }
                if (position != null && position != -1){
                    selectedFreePosition = position
                    val theme = freeList[position].type
                    setAbacusBeads(theme,activity,prefManager,alertLayout)
                }

                val abacusThemeFreeAdapter = AbacusThemeSelectionsAdapter(DataProvider.getAbacusThemeFreeTypeList(activity,AbacusBeadType.Exam),object : AbacusThemeSelectionsAdapter.OnItemClickListener{
                    override fun onThemePoligonItemClick(data: AbacusContent) {
                        prefManager.setCustomParam(AppConstants.Settings.Theam,data.type)
                        setAbacusBeads(data.type,activity,prefManager,alertLayout)
                    }
                }, selectedFreePosition)
                recyclerviewAbacusDefault.adapter = abacusThemeFreeAdapter
            }
        }

        val windows = alertdialog?.window
        val colorD = ColorDrawable(Color.TRANSPARENT)
        val insetD = InsetDrawable(colorD, 150.dp, 0, 150.dp, 0)

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

    private fun setAbacusBeads(theme: String,activity: Activity,prefManager: AppPreferencesHelper,sheetBinding: DialogSelectThemeBinding) {
        with(sheetBinding){
            prefManager.setCustomParam(AppConstants.Settings.TheamTempView,theme)
            linearAbacus.removeAllViews()

            val abacusBinding : LayoutAbacusExamBinding = LayoutAbacusExamBinding.inflate(activity.layoutInflater, null, false)
            linearAbacus.addView(abacusBinding.root)

            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0)
            abacusBinding.relAbacus.layoutParams = params

            val themeContent = DataProvider.findAbacusThemeType(activity,theme, AbacusBeadType.Exam)
            themeContent.abacusFrameExam135.let {
                abacusBinding.rlAbacusMain.setBackgroundResource(it)
            }
            themeContent.dividerColor1.let {
                abacusBinding.ivDivider.setBackgroundColor(ContextCompat.getColor(activity,it))
            }
            themeContent.resetBtnColor8.let {
                abacusBinding.ivReset.setColorFilter(ContextCompat.getColor(activity,it), android.graphics.PorterDuff.Mode.SRC_IN)
                txtPreview.setTextColor(ContextCompat.getColor(activity,it))
            }
            AbacusUtils.setAbacusColumnTheme(AbacusBeadType.ExamResult,abacusBinding.abacusTop,abacusBinding.abacusBottom, column = 3)
            val number = DataProvider.generateSingleDigit(1, 998).toString()
            abacusBinding.tvCurrentVal.text = number
            AbacusUtils.setNumber(number,abacusBinding.abacusTop,abacusBinding.abacusBottom)
        }

    }

    interface DialogInterface {
        fun themeCloseDialogClick()
    }
}
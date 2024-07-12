package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.DialogPaidFeatureListBinding
import com.jigar.me.ui.view.dashboard.fragments.profile.purchased.PlanFeaturesAdapter
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr

object PaidFeatureListDialog {
    fun showPopup(activity: Activity) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: DialogPaidFeatureListBinding =
            DialogPaidFeatureListBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        with(sheetBinding) {
            val planFeaturesAdapter = PlanFeaturesAdapter(DataProvider.getAppPaidFeatureList(activity))
            recyclerviewPaidFeatures.adapter = planFeaturesAdapter
            val planFeaturesAdapter1 = PlanFeaturesAdapter(DataProvider.getAppFreeFeatureList(activity))
            recyclerviewFreeFeatures.adapter = planFeaturesAdapter1
            tvClose.onClick {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(root)
        }
        activity.setBottomSheetDialogAttr(bottomSheetDialog, Constants.bottomSheetWidthBaseOnRatio7,isDraggable = false)
        bottomSheetDialog.show()
    }
}
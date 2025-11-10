package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import com.jigar.me.databinding.BottomSheetOldPurchasesBinding
import com.jigar.me.ui.view.dashboard.fragments.purchase.newui.adapter.PurchaseNewAdapter
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr

object OldPurchasesBottomSheet {
    fun showPopup(activity: Activity, oldPurchasedSkuList: List<InAppSkuDetails>,) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetOldPurchasesBinding = BottomSheetOldPurchasesBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        with(sheetBinding){
            bottomSheetDialog.setOnCancelListener {
                bottomSheetDialog.dismiss()
            }
            txtClose.onClick {
                bottomSheetDialog.dismiss()
            }

            val purchaseNewAdapter = PurchaseNewAdapter(oldPurchasedSkuList,arrayListOf(),0,0,recyclerview,isOnlyView = true)
            recyclerview.adapter = purchaseNewAdapter

            bottomSheetDialog.setContentView(root)
        }

        activity.setBottomSheetDialogAttr(bottomSheetDialog, Constants.bottomSheetWidthBaseOnRatio7,isfullScreen = false)
        bottomSheetDialog.show()
    }

    interface CCMCompleteDialogInterface {
        fun ccmCompleteClose()
    }

}
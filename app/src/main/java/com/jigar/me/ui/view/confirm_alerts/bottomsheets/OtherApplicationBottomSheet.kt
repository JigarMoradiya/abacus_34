package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.OtherApps
import com.jigar.me.databinding.BottomSheetOtherApplicationBinding
import com.jigar.me.ui.view.dashboard.fragments.home.OtherAppAdapter
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr

object OtherApplicationBottomSheet {
    fun showPopup(activity: Activity) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: BottomSheetOtherApplicationBinding = BottomSheetOtherApplicationBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        with(sheetBinding){
            recyclerviewOtherApps.post {
                val height = recyclerviewOtherApps.height
                val width = recyclerviewOtherApps.width / 2
                val dimentions = if (width > height){
                    height
                }else{
                    width
                }
                val otherAppAdapter = OtherAppAdapter(DataProvider.getOtherAppList(),object  : OtherAppAdapter.OnItemClickListener{
                    override fun onItemOtherAppClick(data: OtherApps) {
                        bottomSheetDialog.dismiss()
                        activity.openURL(data.url)
                    }
                },dimentions)
                recyclerviewOtherApps.adapter = otherAppAdapter
            }
            tvNo.onClick {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setOnCancelListener {

            }
            bottomSheetDialog.setContentView(root)
        }

        activity.setBottomSheetDialogAttr(bottomSheetDialog, Constants.bottomSheetWidthBaseOnRatio4)
        bottomSheetDialog.show()
    }

}
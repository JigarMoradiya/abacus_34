package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.*
import com.jigar.me.data.model.data.KeyValuePair
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.DialogAvatarProfileBinding
import com.jigar.me.databinding.DialogSingleSelectionCommonBinding
import com.jigar.me.ui.view.confirm_alerts.adapter.SelectAvatarAdapter
import com.jigar.me.ui.view.confirm_alerts.adapter.SingleSelectAdapter
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.*

object SingleSelectionCommonDialog {
    fun showPopup(activity: Activity, list : ArrayList<KeyValuePair>,title : String? = null, selectedId : String? = null, listener: SingleSelectAdapter.ItemSelectInterface,isVertical : Boolean = false) {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: DialogSingleSelectionCommonBinding = DialogSingleSelectionCommonBinding.inflate(activity.layoutInflater)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        with(sheetBinding){
            if (isVertical){
                recyclerView.layoutParams.height = 240.dp
            }
            if (!title.isNullOrEmpty()){
                txtTitle.text = title
            }
            val adapter = SingleSelectAdapter(list,selectedId, object : SingleSelectAdapter.ItemSelectInterface{
                override fun onItemSelectClick(selectedData: KeyValuePair) {
                    listener.onItemSelectClick(selectedData)
                    bottomSheetDialog.dismiss()
                }

            })
            recyclerView.adapter = adapter
            edtSearch.doAfterTextChanged {
                adapter.filter.filter(edtSearch.text.toString())
            }
            txtClose.onClick {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(root)
        }
        if (isVertical){
            activity.setBottomSheetDialogAttr(bottomSheetDialog,Constants.bottomSheetWidthBaseOnRatio5,isVertical = isVertical)
        }else{
            activity.setBottomSheetDialogAttr(bottomSheetDialog,Constants.bottomSheetWidthBaseOnRatio5,isVertical = isVertical)
        }

        bottomSheetDialog.show()
    }

}
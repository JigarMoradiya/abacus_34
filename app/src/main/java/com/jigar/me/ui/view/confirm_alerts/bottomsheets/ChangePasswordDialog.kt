package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import android.text.TextUtils
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.databinding.DialogChangePasswordBinding
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr
import java.util.Objects

object ChangePasswordDialog {
    var bottomSheetDialog : BottomSheetDialog? = null
    fun showPopup(activity: Activity, listener: DialogChangePasswordInterface) {
        bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
        val sheetBinding: DialogChangePasswordBinding = DialogChangePasswordBinding.inflate(activity.layoutInflater)
        bottomSheetDialog?.setCancelable(false)
        bottomSheetDialog?.setCanceledOnTouchOutside(false)
        with(sheetBinding){
            etPassword.doAfterTextChanged {
                CommonUtils.removeError(tilPassword)
            }
            etNewPassword.doAfterTextChanged {
                CommonUtils.removeError(tilNewPassword)
            }
            tvCancel.onClick {
                bottomSheetDialog?.dismiss()
            }
            btnSubmit.onClick {
                val password = Objects.requireNonNull(etPassword.text).toString()
                val newPassword = Objects.requireNonNull(etNewPassword.text).toString()
                if (TextUtils.isEmpty(password)) {
                    CommonUtils.setErrorToEditText(tilPassword,activity.getString(R.string.please_enter_old_password))
                }else if (TextUtils.isEmpty(newPassword)) {
                    CommonUtils.setErrorToEditText(tilNewPassword,activity.getString(R.string.please_enter_new_password))
                }else{
                    listener.changePassword(password,newPassword)
                }
            }
            bottomSheetDialog?.setContentView(root)
        }
        activity.setBottomSheetDialogAttr(bottomSheetDialog!!,Constants.bottomSheetWidthBaseOnRatio5)
        bottomSheetDialog?.show()
    }

    interface DialogChangePasswordInterface {
        fun changePassword(oldPassword : String, newPassword : String)
    }

}
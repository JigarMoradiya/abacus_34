import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.jigar.me.R
import com.jigar.me.databinding.CommonAlertDialogBinding
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setDialogAttr

object CommonAlertDialog {
    fun showPopup(
        activity: Activity, msg : String, positiveBtnTxt : String? = null, listener : CommonAlertDialogInterface
    ) {
        val alertDialog = AlertDialog.Builder(activity, R.style.CustomAlertDialog)
        val dialogBinding = CommonAlertDialogBinding.inflate(activity.layoutInflater)
        alertDialog.setView(dialogBinding.root)
        alertDialog.setCancelable(false)
        val dialog = alertDialog.create()
        with(dialogBinding){
            tvTitle.text = msg
            if (!positiveBtnTxt.isNullOrEmpty()){
                btnYes.text = positiveBtnTxt
            }

            btnYes.onClick {
                dialog.dismiss()
                listener.commonAlertCloseClick()
            }
        }
        dialog.setOnDismissListener {

        }
        activity.setDialogAttr(dialog)
        dialog.show()
    }

    interface CommonAlertDialogInterface {
        fun commonAlertCloseClick()
    }

}
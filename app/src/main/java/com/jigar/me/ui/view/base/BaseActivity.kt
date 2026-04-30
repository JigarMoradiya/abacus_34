package com.jigar.me.ui.view.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.toastS


/**
 * Used for handle common methods of activities
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var prefManager : AppPreferencesHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        prefManager = AppPreferencesHelper(this, AppConstants.PREF_NAME)
        super.onCreate(savedInstanceState)
    }

    fun showLoading() {
        if (progressDialog != null && progressDialog?.isShowing == false) {
            progressDialog?.show()
        } else {
            initProgressDialog()
            progressDialog?.show()
        }
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    private var progressDialog: AlertDialog? = null

    open fun initProgressDialog() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.dialog_loading, null)
        val builder1 = AlertDialog.Builder(this)
        builder1.setView(alertLayout)
        builder1.setCancelable(true)
        progressDialog = builder1.create()
        progressDialog?.setCancelable(true)
        progressDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    // api failure
    fun onFailure(error: String?) {
        error?.let { showToast(it) }
    }

    fun showToast(id : Int){
        toastS(getString(id))
    }
    fun showToast(msg : String){
        toastS(msg)
    }
}
package com.jigar.me.ui.view.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.toastS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), CoroutineScope {
    lateinit var prefManager : AppPreferencesHelper

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        prefManager = AppPreferencesHelper(requireContext(), AppConstants.PREF_NAME)
//        requireContext().setLocale(prefManager.getCustomParam(Constants.appLanguage,"en"))
        super.onCreate(savedInstanceState)
        job = Job()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationGraph()
    }
    private fun navigationGraph() {
        navController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    fun showToast(id : Int){
        requireContext().toastS(getString(id))
    }
    fun showToast(msg : String){
        requireContext().toastS(msg)
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
        val builder1 = AlertDialog.Builder(requireContext())
        builder1.setView(alertLayout)
        builder1.setCancelable(true)
        progressDialog = builder1.create()
        progressDialog?.setCancelable(true)
        progressDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

    }


    // api failure
    fun onFailure(error: String?) {
        error?.let { showToast(it) }
    }
    fun goToInAppPurchase() {
        navController.navigate(R.id.toPurchaseFragment)
    }
    fun goToSetting() {
        navController.navigate(R.id.toSettingsFragmentNew)
    }
}
package com.jigar.me.ui.view.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.local.data.EventBusType
import com.jigar.me.data.local.data.MessageEvent
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.repositories.Result
import com.jigar.me.databinding.FragmentLoginHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.view.other.ContactUsActivity
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import androidx.navigation.findNavController
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.Constants
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class LoginHomeFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginHomeBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()
    private val appViewModel by viewModels<AppViewModel>()
    private var root : View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        if (root == null){
            binding = FragmentLoginHomeBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initView()
            initListener()
        }
        return root
    }

    private fun initView() {
        with(binding){

        }
    }

    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun initListener() {
        with(binding){

            val notes = prefManager.getCustomParam(AppConstants.RemoteConfig.newVersionNotes,"")
            if (notes.isNotEmpty()){
                txtNotes.text = HtmlCompat.fromHtml(notes,HtmlCompat.FROM_HTML_MODE_COMPACT)
                txtNotes.show()
            }else{
                txtNotes.hide()
            }

            val bulkLogin = prefManager.getCustomParam(AppConstants.RemoteConfig.bulkLogin,"")
            if (bulkLogin.isNotEmpty()){
                txtBulkLogin.text = HtmlCompat.fromHtml(bulkLogin,HtmlCompat.FROM_HTML_MODE_COMPACT)
                txtBulkLogin.show()
                txtBulkLogin.onClick {
                    ContactUsActivity.getInstance(requireContext(),AppConstants.extras_Comman.typeBulkLogin)
                }
            }else{
                txtBulkLogin.hide()
            }
            txtTermsCondition.onClick {
                requireContext().openURL(prefManager.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
            btnGoogleLogin.onClick {
                val signInIntent = studentViewModel.googleSignInClient?.signInIntent
                googleLoginLauncher.launch(signInIntent)
            }
            btnLogin.onClick {
                mNavController?.navigate(R.id.toLoginFragment)
            }
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragmentNew)
            }

        }
    }

    private var googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            lifecycleScope.launch(Dispatchers.IO) {
                val result = studentViewModel.signInWithGoogle(task)
                if (result is Result.Success) {
                    // navigate to main page
                    val request = SocialLoginRequest(result.data?.email,task.result.idToken)
                    studentViewModel.socialLogin(request)
                } else {

                    // your error handling
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", e.localizedMessage)
        }
    }

    private fun initObserver() {
        studentViewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else{
                        hideLoading()
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }
        studentViewModel.getAbacusDataResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccessAbacusData(it.value.data)
                    else{
                        hideLoading()
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }

        studentViewModel.appReviewsListResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                    {
                        checkPurchasedPlans(it.value.data)
                    }else{
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
                else -> {}
            }
        }
    }

    private fun onSuccessAbacusData(data: JsonObject?) {
        val response = Gson().fromJson(data, AbacusAllData::class.java)
        lifecycleScope.launch {
            response.levels?.let {
                appViewModel.insertLevel(it)
                response.setProgress?.let {
                    appViewModel.insertSetProgress(it)
                }
                studentViewModel.appReviewsList()
            }
        }
    }
    private fun onSuccess(data: JsonObject?) {
        val response = Gson().fromJson(data, LoginData::class.java)
        prefManager.setAccessToken(response.token)
        prefManager.setLoginData(data.toString())
        val defaultDateTime = Constants.last_sync_default_time
        val dateTime = prefManager.getCustomParam(Constants.last_sync_time,defaultDateTime)
        val request = FetchAbacusDataRequest(true, get_set_progress_report = true,last_sync_time = dateTime)
        studentViewModel.getAbacusData(request)
    }

    private fun checkPurchasedPlans(data: JsonObject?) {
        if (data?.has("plans_purchased_manually") == true){
            if (data.getAsJsonArray("plans_purchased_manually")?.isEmpty == true){
                prefManager.setCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,"")
            }else{
                val list : List<PlanAssignFromAdminData> =  Gson().fromJson(data.getAsJsonArray("plans_purchased_manually"), object : TypeToken<List<PlanAssignFromAdminData>>() {}.type)
                prefManager.setCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA,Gson().toJson(list))
            }
        }

        // trial_ends_at : "2025-10-11T14:00:00.000Z", free_trial_remaining_days, trial_period_offered
        if (data?.has("free_trial_remaining_days") == true){
            val free_trial_remaining_days = data.get("free_trial_remaining_days").asInt
            prefManager.setCustomParamInt(Constants.free_trial_remaining_days,free_trial_remaining_days)
        }
        prefManager.setUserLoggedIn(true)
        MainDashboardActivity.getInstance(requireContext())
    }

}
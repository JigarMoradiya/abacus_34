package com.jigar.me.ui.view.login.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.databinding.FragmentLoginBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils.removeError
import com.jigar.me.utils.CommonUtils.setErrorToEditText
import com.jigar.me.utils.Constants
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Objects

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private var root : View? = null
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()
    private val appViewModel by viewModels<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        if (root == null){
            binding = FragmentLoginBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initView()
            initListener()
        }
        return root
    }

    private fun initView() {

    }

    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun initListener() {
        with(binding){
//            if (BuildConfig.DEBUG){
//                etEmail.setText("j1@yopmail.com")
//                etPassword.setText("123456")
//            }
            txtTermsCondition.onClick {
                requireContext().openURL(prefManager.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
            btnSubmit.onClick {
                if (validate()){
                    studentViewModel.login(LoginRequest(etEmail.text.toString(),etPassword.text.toString()))
                }
            }
            txtGoBack?.onClick {
                mNavController?.navigateUp()
            }
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragmentNew)
            }
            etEmail.doAfterTextChanged {
                removeError(tilEmail)
            }
            etPassword.doAfterTextChanged {
                removeError(tilPassword)
            }
        }
    }


    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            val password = Objects.requireNonNull(etPassword.text).toString()
            if (TextUtils.isEmpty(email)) {
                validate = false
                setErrorToEditText(tilEmail,getString(R.string.please_enter_email_id))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validate = false
                setErrorToEditText(tilEmail,getString(R.string.please_enter_valid_email_id))
            }else if (TextUtils.isEmpty(password)) {
                validate = false
                setErrorToEditText(tilPassword,getString(R.string.please_enter_password))
            }
        }
        return validate
    }

    private fun initObserver() {
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
        prefManager.setLoginData(Gson().toJson(data))

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
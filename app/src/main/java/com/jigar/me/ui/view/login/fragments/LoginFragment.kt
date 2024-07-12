package com.jigar.me.ui.view.login.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.databinding.FragmentLoginBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils.removeError
import com.jigar.me.utils.CommonUtils.setErrorToEditText
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        initListener()
        return binding.root
    }

    private fun initView() {

    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
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
            txtSignup.onClick {
                mNavController?.navigate(R.id.toSignupFragment)
            }
            txtForgotPassword.onClick {
                mNavController?.navigate(R.id.toForgotPasswordFragment)
            }
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragment)
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
        studentViewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else{
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {
                    hideLoading()
                    if (it.errorType == AppConstants.APIStatus.ERROR_CODE_USER_NOT_VERIFIED){
                        val email = Objects.requireNonNull(binding.etEmail.text).toString().trim { it <= ' ' }
                        studentViewModel.resendOTP(ResendOTPRequest(email))
                    }else{
                        onFailure(it.errorBody)
                    }
                }
            }
        }
        studentViewModel.resendOTPResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS){
                        val action = LoginFragmentDirections.toOTPFragment(AppConstants.OTPScreen.signupStep,binding.etEmail.text.toString())
                        mNavController?.navigate(action)
                    }
                    else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }
    }

    private fun onSuccess(data: JsonObject?) {
        val response = Gson().fromJson(data, LoginData::class.java)
        prefManager.setAccessToken(response.token)
        prefManager.setLoginData(Gson().toJson(data))
        response.country?.let {
            prefManager.setCountryCode(it)
            if (it.equals(AppConstants.LoginData.LoginCountry_IN,true)){
                prefManager.setIsCurrencyINR(true)
            }else{
                prefManager.setIsCurrencyINR(false)
            }
        }
        prefManager.setUserLoggedIn(true)
        MainDashboardActivity.getInstance(requireContext())

    }

}
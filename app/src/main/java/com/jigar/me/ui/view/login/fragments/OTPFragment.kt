package com.jigar.me.ui.view.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.VerifyEmailRequest
import com.jigar.me.databinding.FragmentOtpBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class OTPFragment : BaseFragment() {
    private lateinit var binding: FragmentOtpBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()
    private var from : String? = null
    private var email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        from = OTPFragmentArgs.fromBundle(requireArguments()).from
        email = OTPFragmentArgs.fromBundle(requireArguments()).email
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initView() {
        with(binding){
            txtLableEmail.text = email
        }
    }
    private fun initListener() {
        with(binding){
            cardBack.onClick {
                mNavController?.navigateUp()
            }
            btnSubmit.onClick {
                if (validate()){
                    studentViewModel.verification(VerifyEmailRequest(email,from,otpView.text.toString()))
                }
            }
            txtResendOTP.onClick {
                studentViewModel.resendOTP(ResendOTPRequest(email))
            }
        }
    }

    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val otp = Objects.requireNonNull(otpView.text).toString().trim { it <= ' ' }
            if (otp.length != 6) {
                validate = false
            }
        }
        return validate
    }

    private fun initObserver() {
        studentViewModel.verificationResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else
                         (it.value.error?.message)
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
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
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onFailure(getString(R.string.otp_send_on_your_email_id))
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
        if (from == AppConstants.OTPScreen.signupStep){
            val response = Gson().fromJson(data, LoginData::class.java)
            prefManager.setAccessToken(response.token)
            prefManager.setLoginData(data.toString())
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
        }else{
            val action = OTPFragmentDirections.toResetPasswordFragment(binding.otpView.text.toString()?:"",email?:"")
            mNavController?.navigate(action)
        }
    }
}
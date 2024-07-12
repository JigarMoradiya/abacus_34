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
import com.jigar.me.R
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.databinding.FragmentSignupBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.markRequiredInRed
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.openURL
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class SignupFragment : BaseFragment() {
    private lateinit var binding: FragmentSignupBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initListener()
        return binding.root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initListener() {
        with(binding){
//            if (BuildConfig.DEBUG){
//                etEmail.setText("j@yopmail.com")
//                etMobileNumber.setText("9999900000")
//                etName.setText("test student")
//                etPassword.setText("123456")
//                etCPassword.setText("123456")
//            }
            tilName.markRequiredInRed()
            tilMobileNumber.markRequiredInRed()
            tilEmail.markRequiredInRed()
            tilPassword.markRequiredInRed()
            tilCPassword.markRequiredInRed()
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragment)
            }
            txtLogin.onClick {
                mNavController?.navigateUp()
            }
            btnSubmit.onClick {
                if (validate()){
                    studentViewModel.signup(SignupV2Request(etName.text.toString(),etEmail.text.toString(),etPassword.text.toString(),etMobileNumber.text.toString(),ccp.selectedCountryNameCode))
                }
            }
            txtTermsCondition.onClick {
                requireContext().openURL(prefManager.getCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,""))
            }
            etName.doAfterTextChanged {
                CommonUtils.removeError(tilName)
            }
            etEmail.doAfterTextChanged {
                CommonUtils.removeError(tilEmail)
            }
            etMobileNumber.doAfterTextChanged {
                CommonUtils.removeError(tilMobileNumber)
            }
            etPassword.doAfterTextChanged {
                CommonUtils.removeError(tilPassword)
                CommonUtils.removeError(tilCPassword)
            }
            etCPassword.doAfterTextChanged {
                CommonUtils.removeError(tilPassword)
                CommonUtils.removeError(tilCPassword)
            }
        }
    }

    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val name = Objects.requireNonNull(etName.text).toString().trim { it <= ' ' }
            val mobile = Objects.requireNonNull(etMobileNumber.text).toString().trim { it <= ' ' }
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            val password = Objects.requireNonNull(etPassword.text).toString()
            val cPassword = Objects.requireNonNull(etCPassword.text).toString()
            if (TextUtils.isEmpty(name)) {
                validate = false
                CommonUtils.setErrorToEditText(tilName, getString(R.string.please_enter_kid_name))
            } else if (TextUtils.isEmpty(mobile)) {
                validate = false
                CommonUtils.setErrorToEditText(tilMobileNumber, getString(R.string.please_enter_mobile_number))
            } else if (!Patterns.PHONE.matcher(mobile).matches()) {
                validate = false
                CommonUtils.setErrorToEditText(tilMobileNumber,getString(R.string.please_enter_valid_mobile_number))
            } else if (TextUtils.isEmpty(email)) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail, getString(R.string.please_enter_email_id))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail,getString(R.string.please_enter_valid_email_id))
            } else if (TextUtils.isEmpty(password)) {
                validate = false
                CommonUtils.setErrorToEditText(tilPassword,getString(R.string.please_enter_password))
            } else if (TextUtils.isEmpty(cPassword)) {
                validate = false
                CommonUtils.setErrorToEditText(tilCPassword,getString(R.string.please_enter_cpassword))
            } else if (password != cPassword) {
                validate = false
                CommonUtils.setErrorToEditText(tilPassword,getString(R.string.password_not_match))
                CommonUtils.setErrorToEditText(tilCPassword,getString(R.string.password_not_match))
            }
        }
        return validate
    }

    private fun initObserver() {
        studentViewModel.signupResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess()
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

    private fun onSuccess() {
        val action = SignupFragmentDirections.toOTPFragment(AppConstants.OTPScreen.signupStep,binding.etEmail.text.toString())
        mNavController?.navigate(action)
    }
}
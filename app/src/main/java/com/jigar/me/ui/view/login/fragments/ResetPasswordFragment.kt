package com.jigar.me.ui.view.login.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.model.data.ResetPasswordRequest
import com.jigar.me.databinding.FragmentResetPasswordBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentResetPasswordBinding
    private var mNavController: NavController? = null
    private val viewModel by viewModels<StudentViewModel>()
    private var otp : String? = null
    private var email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otp = ResetPasswordFragmentArgs.fromBundle(requireArguments()).otp
        email = ResetPasswordFragmentArgs.fromBundle(requireArguments()).email
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initListener() {
        with(binding){
            cardBack.onClick {
                mNavController?.navigateUp()
            }
            btnSubmit.onClick {
                if (validate()){
                    viewModel.resetPassword(ResetPasswordRequest(email,otp,etPassword.text.toString()))
                }
            }
            etPassword.doAfterTextChanged {
                CommonUtils.removeError(tilPassword)
                CommonUtils.removeError(tilNewPassword)
            }
            etNewPassword.doAfterTextChanged {
                CommonUtils.removeError(tilPassword)
                CommonUtils.removeError(tilNewPassword)
            }
        }
    }

    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val password = Objects.requireNonNull(etPassword.text).toString()
            val cPassword = Objects.requireNonNull(etNewPassword.text).toString()
            if (TextUtils.isEmpty(password)) {
                validate = false
                CommonUtils.setErrorToEditText(tilPassword,getString(R.string.please_enter_password))
            }else if (TextUtils.isEmpty(cPassword)) {
                validate = false
                CommonUtils.setErrorToEditText(tilNewPassword,getString(R.string.please_enter_cpassword))
            }else if (password != cPassword) {
                validate = false
                CommonUtils.setErrorToEditText(tilPassword,getString(R.string.password_not_match))
                CommonUtils.setErrorToEditText(tilNewPassword,getString(R.string.password_not_match))
            }
        }
        return validate
    }

    private fun initObserver() {
        viewModel.resetPasswordResponse.observe(this) {
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
        showToast(R.string.sucess_password_rest)
        mNavController?.navigate(R.id.toLoginFragment)
    }
}
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
import com.jigar.me.data.model.data.ForgotPasswordRequest
import com.jigar.me.databinding.FragmentForgotPasswordBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private var mNavController: NavController? = null
    private val viewModel by viewModels<StudentViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
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
                    viewModel.forgotPassword(ForgotPasswordRequest(etEmail.text.toString()))
                }
            }
            etEmail.doAfterTextChanged {
                CommonUtils.removeError(tilEmail)
            }
        }
    }
    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail, getString(R.string.please_enter_email_id))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail,getString(R.string.please_enter_valid_email_id))
            }
        }
        return validate
    }

    private fun initObserver() {
        viewModel.forgotPasswordResponse.observe(this) {
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
        val action = ForgotPasswordFragmentDirections.toOTPFragment(AppConstants.OTPScreen.forgotPassword,binding.etEmail.text.toString())
        mNavController?.navigate(action)
    }
}
package com.jigar.me.ui.view.dashboard.fragments.profile

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
import com.jigar.me.R
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.databinding.FragmentEditProfileBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.markRequiredInRed
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private var mNavController: NavController? = null
    private val viewModel by viewModels<StudentViewModel>()
    private var loginData: LoginData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        initListener()
        return binding.root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initView() {
        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        with(binding){
            data = loginData
            ccp.setCountryForNameCode(loginData?.country)
        }
    }
    private fun initListener() {
        with(binding){
            tilName.markRequiredInRed()
            tilEmail.markRequiredInRed()
            tilMobileNumber.markRequiredInRed()
            cardBack.onClick {
                mNavController?.navigateUp()
            }

            btnSubmit.onClick {
                if (validate()){
                    viewModel.updateProfile(UpdateProfileRequest(etName.text.toString(),etEmail.text.toString(),etMobileNumber.text.toString()))
                }
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
        }
    }

    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val name = Objects.requireNonNull(etName.text).toString().trim { it <= ' ' }
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            val mobile = Objects.requireNonNull(etMobileNumber.text).toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(name)) {
                validate = false
                CommonUtils.setErrorToEditText(tilName, getString(R.string.please_enter_kid_name))
            } else if (TextUtils.isEmpty(email)) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail, getString(R.string.please_enter_email_id))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                validate = false
                CommonUtils.setErrorToEditText(tilEmail,getString(R.string.please_enter_valid_email_id))
            }  else if (TextUtils.isEmpty(mobile)) {
                validate = false
                CommonUtils.setErrorToEditText(tilMobileNumber, getString(R.string.please_enter_mobile_number))
            } else if (!Patterns.PHONE.matcher(mobile).matches()) {
                validate = false
                CommonUtils.setErrorToEditText(tilMobileNumber,getString(R.string.please_enter_valid_mobile_number))
            }
        }
        return validate
    }

    private fun initObserver() {
        viewModel.updateProfileResponse.observe(this) {
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
        with(binding){
            loginData?.name = etName.text.toString()
            loginData?.email = etEmail.text.toString()
            loginData?.phone_number = etMobileNumber.text.toString()
            prefManager.setLoginData(Gson().toJson(loginData))
        }
        mNavController?.navigateUp()
    }
}
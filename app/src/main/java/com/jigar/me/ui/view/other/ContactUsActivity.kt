package com.jigar.me.ui.view.other

import CommonAlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.model.data.ContactUsRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.databinding.ActivityContactUsBinding
import com.jigar.me.ui.view.base.BaseActivity
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.ui.viewmodel.UserViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hideKeyboard
import com.jigar.me.utils.extensions.markRequiredInRed
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class ContactUsActivity : BaseActivity() {
    lateinit var binding: ActivityContactUsBinding
    private val userViewModel by viewModels<UserViewModel>()
    private var loginData: LoginData? = null
    private var type : String? = null
    companion object {
        fun getInstance(context: Context?,type : String) {
            Intent(context, ContactUsActivity::class.java).apply {
                putExtra(AppConstants.extras_Comman.type,type)
                context?.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactUsBinding.inflate(layoutInflater)
        type = intent.getStringExtra(AppConstants.extras_Comman.type)
        setContentView(binding.root)
        initViews()
        initListener()
        initObserver()

    }

    private fun initObserver() {
        userViewModel.contactUsResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        CommonAlertDialog.showPopup(this@ContactUsActivity,getString(R.string.thank_you_for_reaching_out),
                            getString(R.string.ok_thanks),
                            listener = object : CommonAlertDialog.CommonAlertDialogInterface{
                            override fun commonAlertCloseClick() {
                                finish()
                            }
                        })
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

    private fun initViews() {
        onMainActivityBack()

        loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
        with(binding){
            data = loginData
            ccp.setCountryForNameCode(loginData?.country)
            tilName.markRequiredInRed()
            tilMobileNumber.markRequiredInRed()
            tilEmail.markRequiredInRed()
            tilDescription.markRequiredInRed()
        }
    }

    private fun initListener() = with(binding){
        cardBack.onClick {
            finish()
        }

        etName.doAfterTextChanged {
            CommonUtils.removeError(tilName)
        }
        etEmail.doAfterTextChanged {
            CommonUtils.removeError(tilEmail)
        }
        etDescription.doAfterTextChanged {
            CommonUtils.removeError(tilDescription)
        }
        etMobileNumber.doAfterTextChanged {
            CommonUtils.removeError(tilMobileNumber)
        }
        btnSubmit.onClick {
            if (validate()){
                root.context.hideKeyboard(root)
                userViewModel.contactUs(ContactUsRequest(type,etName.text.toString(),etEmail.text.toString(),
                    etMobileNumber.text.toString(),etDescription.text.toString(),binding.ccp.selectedCountryName,
                    null))
            }
        }
    }
    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val name = Objects.requireNonNull(etName.text).toString().trim { it <= ' ' }
            val mobile = Objects.requireNonNull(etMobileNumber.text).toString().trim { it <= ' ' }
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            val description = Objects.requireNonNull(etDescription.text).toString().trim { it <= ' ' }
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
            } else if (TextUtils.isEmpty(description)) {
                validate = false
                CommonUtils.setErrorToEditText(tilDescription, getString(R.string.please_enter_descritpion))
            } else if (description.length < 50) {
                validate = false
                CommonUtils.setErrorToEditText(tilDescription, getString(R.string.please_enter_descritpion_minimum_50))
            }
        }
        return validate
    }
    private fun onMainActivityBack() {
        onBackPressedDispatcher.addCallback(
            this, // lifecycle owner
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.cardBack.performClick()
                }
            })
    }


}
package com.jigar.me.ui.view.login.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.KeyValuePair
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.databinding.FragmentLoginCompleteProfileBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.adapter.SingleSelectAdapter
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.SingleSelectionCommonDialog
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.LocationViewModel
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.markRequiredInRed
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects

@AndroidEntryPoint
class LoginCompleteProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginCompleteProfileBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()
    private val locationViewModel by viewModels<LocationViewModel>()
    private var stateList : ArrayList<KeyValuePair> = arrayListOf()
    private var cityList : ArrayList<KeyValuePair> = arrayListOf()
    private var selectedCityKey : String? = null
    private var selectedStateKey : String? = null
    private var loginData: LoginData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentLoginCompleteProfileBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initListener()
        return binding.root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initListener() {
        with(binding){
            loginData = Gson().fromJson(prefManager.getLoginData(), LoginData::class.java)
            with(binding){
                data = loginData
                loginData?.country?.let {
                    ccp.setCountryForNameCode(it)
                }

            }
            getStateList()
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
            tilCity.markRequiredInRed()
            tilState.markRequiredInRed()

            ccp.setOnCountryChangeListener {
                selectedCityKey = null
                selectedStateKey = null
                etState.setText("")
                etCity.setText("")
                stateList.clear()
                cityList.clear()
                getStateList()
            }
            etState.doAfterTextChanged {
                selectedCityKey = null
                cityList.clear()
                etCity.setText("")
                getCityList()
            }
            etState.onClick {
                stateDialog()
            }
            etCity.onClick {
                if (!selectedStateKey.isNullOrEmpty()){
                    cityDialog()
                }
            }
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragment)
            }
            btnSubmit.onClick {
                if (validate()){
                    studentViewModel.updateProfile(UpdateProfileRequest(etName.text.toString(),etEmail.text.toString(),binding.ccp.selectedCountryNameCode,etMobileNumber.text.toString(),selectedStateKey,selectedCityKey,binding.ccp.selectedCountryName))
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
            etState.doAfterTextChanged {
                CommonUtils.removeError(tilState)
            }
            etCity.doAfterTextChanged {
                CommonUtils.removeError(tilCity)
            }
        }
    }

    private fun validate(): Boolean {
        var validate = true
        with(binding){
            val name = Objects.requireNonNull(etName.text).toString().trim { it <= ' ' }
            val mobile = Objects.requireNonNull(etMobileNumber.text).toString().trim { it <= ' ' }
            val email = Objects.requireNonNull(etEmail.text).toString().trim { it <= ' ' }
            val state = Objects.requireNonNull(etState.text).toString()
            val city = Objects.requireNonNull(etCity.text).toString()
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
            }else if (TextUtils.isEmpty(state)) {
                validate = false
                CommonUtils.setErrorToEditText(tilState,getString(R.string.please_select_state))
            }else if (TextUtils.isEmpty(city)) {
                validate = false
                CommonUtils.setErrorToEditText(tilCity,getString(R.string.please_select_city))
            }
        }
        return validate
    }

    private fun initObserver() {
        studentViewModel.updateProfileResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    hideLoading()
                    onFailure(it.errorBody)
                }
            }
        }
        locationViewModel.getStateResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        setState(it.value.data)
                    else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    onFailure(it.errorBody)
                }
            }
        }
        locationViewModel.getCityResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        setCity(it.value.data)
                    else
                        onFailure(it.value.error?.message)
                }
                is Resource.Failure -> {
                    onFailure(it.errorBody)
                }
            }
        }
        studentViewModel.signupResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
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

    private fun stateDialog() {
        SingleSelectionCommonDialog.showPopup(requireActivity(),stateList,getString(R.string.select_state),selectedStateKey,object : SingleSelectAdapter.ItemSelectInterface{
            override fun onItemSelectClick(selectedData: KeyValuePair) {
                binding.etState.setText(selectedData.name)
                selectedStateKey = selectedData.key
                Log.e("jigarLogs","selectedStateKey = "+selectedStateKey)
                getCityList()
            }
        },true)
    }

    private fun cityDialog() {
        SingleSelectionCommonDialog.showPopup(requireActivity(),cityList,getString(R.string.select_city),selectedStateKey,object : SingleSelectAdapter.ItemSelectInterface{
            override fun onItemSelectClick(selectedData: KeyValuePair) {
                binding.etCity.setText(selectedData.name)
                selectedCityKey = selectedData.name
                Log.e("jigarLogs","selectedCityKey = "+selectedCityKey)
            }
        },true)
    }

    private fun setCity(data: JsonObject?) {
        if (data?.has("data") == true){
            cityList = Gson().fromJson(
                data.getAsJsonArray("data"), object :
                    TypeToken<ArrayList<KeyValuePair>>() {}.type
            )
        }
    }

    private fun setState(data: JsonObject?) {
        if (data?.has("data") == true){
            stateList = Gson().fromJson(
                data.getAsJsonArray("data"), object :
                    TypeToken<ArrayList<KeyValuePair>>() {}.type
            )
        }
    }

    private fun getStateList() {
        locationViewModel.getStateList(binding.ccp.selectedCountryNameCode)
    }
    private fun getCityList() {
        locationViewModel.getCityList(binding.ccp.selectedCountryNameCode,selectedStateKey)
    }
}
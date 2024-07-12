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
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
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

@AndroidEntryPoint
class LoginHomeFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginHomeBinding
    private var mNavController: NavController? = null
    private val studentViewModel by viewModels<StudentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentLoginHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        initListener()
        initGoogleLogin()
        return binding.root
    }

    private fun initView() {
        with(binding){

        }
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
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
                studentViewModel.signInWithGoogle()
            }
            btnLogin.onClick {
                mNavController?.navigate(R.id.toLoginFragment)
            }
            cardFAQs.onClick {
                mNavController?.navigate(R.id.toFAQsFragment)
            }

        }
    }

    private fun initGoogleLogin() {
        studentViewModel.signInWithGoogle.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val signInIntent = studentViewModel.googleSignInClient?.signInIntent
                    googleLoginLauncher.launch(signInIntent)
                }
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
                    Log.e("jigarLogs","doOnSignInWithGoogle idToken := "+task.result.idToken)
                    Log.e("jigarLogs","doOnSignInWithGoogle data := "+result.data?.displayName)
                    Log.e("jigarLogs","doOnSignInWithGoogle email := "+result.data?.email)
                    Log.e("jigarLogs","doOnSignInWithGoogle phoneNumber := "+result.data?.phoneNumber)
                    Log.e("jigarLogs","doOnSignInWithGoogle photoUrl := "+result.data?.photoUrl)
                    Log.e("jigarLogs","doOnSignInWithGoogle uid := "+result.data?.uid)
                    Log.e("jigarLogs","doOnSignInWithGoogle isEmailVerified := "+result.data?.isEmailVerified)
                    studentViewModel.socialLogin(SocialLoginRequest(result.data?.email,task.result.idToken))
                } else {

                    // your error handling
                    Log.e("jigarLogs","doOnSignInWithGoogle error = "+(result as Result.Error).exception)
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
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        onSuccess(it.value.data)
                    else{
                        onFailure(it.value.error?.message)
                    }
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
        prefManager.setLoginData(data.toString())
        if (response.name.isNullOrEmpty()){
            mNavController?.navigate(R.id.toLoginCompleteProfileFragment)
        }else{
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

}
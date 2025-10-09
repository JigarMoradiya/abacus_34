package com.jigar.me.ui.view.login.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.databinding.FragmentSplashBinding
import com.jigar.me.internal.workmanagers.FetchAbacusDataWorkManager
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog
import com.jigar.me.ui.viewmodel.StudentViewModel
import com.jigar.me.utils.Resource

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    private lateinit var binding: FragmentSplashBinding
    private var mNavController: NavController? = null
    private lateinit var mFirebaseRemoteConfig  : FirebaseRemoteConfig
    private val studentViewModel by viewModels<StudentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setNavigationGraph()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    override fun onResume() {
        super.onResume()
        firebaseConfig()
    }
    private fun initObserver(){
        studentViewModel.appReviewsListResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                    {
                        checkPurchasedPlans(it.value.data)
                    }else{
                        onFailure(it.value.error?.message)
                    }
                }
                is Resource.Failure -> {

                }
                else -> {}
            }
        }
    }

    private fun firebaseConfig() {
        var tries = 0
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(5)
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(requireActivity()) { task ->
                Log.e("jigarSplash","tries = "+tries)
                tries++
                if (tries>3){
                    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings.toBuilder().setFetchTimeoutInSeconds(20).build())
                }
                if (task.isSuccessful) {
                    val versionCode: Long = mFirebaseRemoteConfig.getLong(AppConstants.RemoteConfig.versionCode)
                    checkVersion(versionCode)
                }
            }
    }

    private fun checkVersion(versionCode: Long) {
        try {
            val pInfo =
                requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                pInfo.longVersionCode
            }else{
                pInfo.versionCode.toLong()
            }

            if (versionCode > version){
                CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.app_update),getString(R.string.new_version_msg)
                    ,getString(R.string.yes_i_want_to_update),getString(R.string.no_thanks), icon = R.drawable.ic_alert,
                    clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                        override fun onConfirmationYesClick(bundle: Bundle?) {
                            requireActivity().openURL("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                        }
                        override fun onConfirmationNoClick(bundle: Bundle?){
                            requireActivity().finish()
                        }
                    })
            }else{
                val video: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.videoList)
                val discountData: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.discountData)
                val discountPer: Long = mFirebaseRemoteConfig.getLong(AppConstants.RemoteConfig.discountPer)
                val displayPlan: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.displayPlanList)
                val privacyPolicyUrl: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.privacyPolicyUrl)
                val supportEmail: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.supportEmail)
                val newVersionNotes: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.newVersionNotes)
                val bulkLogin: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.bulkLogin)

                with(prefManager){
                    setCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,privacyPolicyUrl)
                    setCustomParam(AppConstants.RemoteConfig.supportEmail,supportEmail)
                    setCustomParam(AppConstants.RemoteConfig.newVersionNotes,newVersionNotes)
                    setCustomParam(AppConstants.RemoteConfig.bulkLogin,bulkLogin)
                    setCustomParamInt(AppConstants.RemoteConfig.versionCode,versionCode.toInt())
                    setCustomParamInt(AppConstants.RemoteConfig.discountPer,discountPer.toInt())
                    if (discountData.length > 5){
                        setCustomParam(AppConstants.RemoteConfig.discountData,discountData)
                    }else{
                        setCustomParam(AppConstants.RemoteConfig.discountData,"")
                    }
                    if (video.length > 5){
                        setCustomParam(AppConstants.RemoteConfig.videoList,video)
                    }else{
                        setCustomParam(AppConstants.RemoteConfig.videoList,"")
                    }
                    setCustomParam(AppConstants.RemoteConfig.displayPlanList,displayPlan)
                }
                if (requireContext().isNetworkAvailable){
                    if (!prefManager.getAccessToken().isNullOrEmpty() && prefManager.isUserLoggedIn()){
                        studentViewModel.appReviewsList()
                    }else{
                        mNavController?.navigate(R.id.toLoginHomeFragment)
                    }
                }else{
                    requireContext().toastL(resources.getString(R.string.no_internet))
                    requireActivity().finish()
                }

            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

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

        MainDashboardActivity.getInstance(requireContext())
    }
}
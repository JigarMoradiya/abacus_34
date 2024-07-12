package com.jigar.me.ui.view.login.fragments

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.openURL
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    private lateinit var binding: FragmentSplashBinding
    private var mNavController: NavController? = null
    private lateinit var mFirebaseRemoteConfig  : FirebaseRemoteConfig
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() {
        firebaseConfig()
    }
    private fun goToNext() {
        val androidId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        prefManager.setDeviceId(androidId)
        if (requireContext().isNetworkAvailable){
            lifecycleScope.launch {
                delay(3000)
                if (!prefManager.getAccessToken().isNullOrEmpty() && prefManager.isUserLoggedIn()){
                    MainDashboardActivity.getInstance(requireContext())
                }else{
                    mNavController?.navigate(R.id.toLoginHomeFragment)
                }
            }
        }else{
            requireContext().toastL(resources.getString(R.string.no_internet))
            requireActivity().finish()
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
                tries++
                if (tries>3){
                    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings.toBuilder().setFetchTimeoutInSeconds(20).build())
                }
                if (task.isSuccessful) {

                    val video: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.videoList)
                    val displayPlan: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.displayPlanList)
                    val privacyPolicyUrl: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.privacyPolicyUrl)
                    val supportEmail: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.supportEmail)
                    val newVersionNotes: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.newVersionNotes)
                    val bulkLogin: String = mFirebaseRemoteConfig.getString(AppConstants.RemoteConfig.bulkLogin)
                    val versionCode: Long = mFirebaseRemoteConfig.getLong(AppConstants.RemoteConfig.versionCode)
                    val ads: String = mFirebaseRemoteConfig.getString(AppConstants.AbacusProgress.Ads)
                    val baseUrl: String = mFirebaseRemoteConfig.getString(AppConstants.AbacusProgress.baseUrl)
                    val iPath: String = mFirebaseRemoteConfig.getString(AppConstants.AbacusProgress.iPath)
                    val resetImage: Long = mFirebaseRemoteConfig.getLong(AppConstants.AbacusProgress.resetImage)
                    val isAdmob = if (BuildConfig.DEBUG){
                        false
                    }else{
                        mFirebaseRemoteConfig.getBoolean(AppConstants.AbacusProgress.isAdmob)
                    }
                    with(prefManager){
                        if (resetImage.toInt() > getCustomParamInt(AppConstants.AbacusProgress.resetImage, 0)) {
                            setCustomParamInt(AppConstants.AbacusProgress.resetImage, resetImage.toInt())
                            setCustomParam(AppConstants.extras_Comman.DownloadType+"_"+AppConstants.extras_Comman.DownloadType_Maths, "")
                            setCustomParam(AppConstants.extras_Comman.DownloadType+"_"+AppConstants.extras_Comman.DownloadType_Nursery, "")
                        }
                        setBaseUrl(baseUrl)
                        setCustomParam(AppConstants.AbacusProgress.iPath,iPath)
                        setCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,isAdmob)
                        setCustomParam(AppConstants.AbacusProgress.Ads,ads)
                        setCustomParam(AppConstants.RemoteConfig.privacyPolicyUrl,privacyPolicyUrl)
                        setCustomParam(AppConstants.RemoteConfig.supportEmail,supportEmail)
                        setCustomParam(AppConstants.RemoteConfig.newVersionNotes,newVersionNotes)
                        setCustomParam(AppConstants.RemoteConfig.bulkLogin,bulkLogin)
                        setCustomParamInt(AppConstants.RemoteConfig.versionCode,versionCode.toInt())
                        if (video.length > 5){
                            setCustomParam(AppConstants.RemoteConfig.videoList,video)
                        }else{
                            setCustomParam(AppConstants.RemoteConfig.videoList,"")
                        }
                        setCustomParam(AppConstants.RemoteConfig.displayPlanList,displayPlan)
                    }
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
                goToNext()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
}
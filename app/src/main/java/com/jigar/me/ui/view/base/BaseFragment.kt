package com.jigar.me.ui.view.base

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.GooglePurchasedPlanRequest
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.base.inapp.BillingRepository
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSetting
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSettingInterface
import com.jigar.me.ui.viewmodel.ExamViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Resource
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.show
import com.jigar.me.utils.extensions.toastS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), CoroutineScope, VoiceControllerSettingInterface {
    lateinit var prefManager : AppPreferencesHelper

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Default
    private val examViewModel by viewModels<ExamViewModel>()

    //   TTS
    private var textToSpeech: TextToSpeech? = null
    private var speak = false
    var voiceController: VoiceControllerSetting? = null
    var statisticApiResponseListener: StatisticApiResponseListener? = null
    private lateinit var navController: NavController
    companion object{
        interface StatisticApiResponseListener {
            fun statisticApiData(data: JsonObject?)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        prefManager = AppPreferencesHelper(requireContext(), AppConstants.PREF_NAME)
//        requireContext().setLocale(prefManager.getCustomParam(Constants.appLanguage,"en"))
        super.onCreate(savedInstanceState)
        job = Job()
        initCommonObserver()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtToSpeechInit()
        navigationGraph()
    }
    private fun navigationGraph() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    fun getStatisticData(listener : StatisticApiResponseListener){
        statisticApiResponseListener = listener
        examViewModel.getStatistics()
    }
    private fun initCommonObserver() {
        examViewModel.getStatisticsResponse.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    if (it.value.status == AppConstants.APIStatus.SUCCESS)
                        statisticApiResponseListener?.statisticApiData(it.value.data)
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

    fun setCurrentSubscription(data: ArrayList<GooglePurchasedPlanRequest>) {
        // TODO
        with(prefManager) {
            setCustomParam(AppConstants.Purchase.Purchase_All, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Toddler_Single_digit_level1, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Add_Sub_level2, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Mul_Div_level3, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Ads, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "N")

            data.map {
                when (it.google_plan_id) {
                    BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime, BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old -> {
                        setCustomParam(AppConstants.Purchase.Purchase_All, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_material_maths -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_material_nursery -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_ads -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Ads, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_level1_lifetime -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Toddler_Single_digit_level1, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_level2_lifetime -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Add_Sub_level2, "Y")
                    }
                    BillingRepository.AbacusSku.PRODUCT_ID_level3_lifetime -> {
                        setCustomParam(AppConstants.Purchase.Purchase_Mul_Div_level3, "Y")
                    }

                    BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Weekly,
                    BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month1,
                    BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3,
                    BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month6,
                    BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Year1 -> {
                        setCustomParam(AppConstants.Purchase.Purchase_All, "Y")
                    }
                }
            }
//            setCustomParam(AppConstants.Purchase.Purchase_All, "N")
//            Log.e("jigarLogs","Purchase_All New = "+getCustomParam(AppConstants.Purchase.Purchase_All,""))
        }
    }

    fun showToast(id : Int){
        requireContext().toastS(getString(id))
    }
    fun showToast(msg : String){
        requireContext().toastS(msg)
    }


    fun showLoading() {
        if (progressDialog != null && progressDialog?.isShowing == false) {
            progressDialog?.show()
        } else {
            initProgressDialog()
            progressDialog?.show()
        }
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    private var progressDialog: AlertDialog? = null

    open fun initProgressDialog() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.dialog_loading, null)
        val builder1 = AlertDialog.Builder(requireContext())
        builder1.setView(alertLayout)
        builder1.setCancelable(true)
        progressDialog = builder1.create()
        progressDialog?.setCancelable(true)
        progressDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    // TODO Speech
    open fun txtToSpeechInit() {
        if (isAdded){
            textToSpeech = TextToSpeech(requireActivity()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    speak = true
                    textToSpeech?.let {
                        CommonUtils.applySpeechSettings(prefManager, it)
                        voiceController = VoiceControllerSetting(requireActivity(), this, prefManager, it)
                    }

                }
            }
        }
    }


    open fun speakOut(txt: String) {
        if (speak) {
            requireActivity().runOnUiThread {
                if (textToSpeech != null){
                    textToSpeech?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
                }else{
                    txtToSpeechInit()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
    // load Interstitial ads
    fun showAMFullScreenAds(adUnit: String, isShowAdsDirect : Boolean = false) {
        val isShowAd = if (isShowAdsDirect) true else{
            val count = prefManager.getCustomParamInt(AppConstants.Purchase.AdsShowCount, 0)
            if (count == 7) {
                true
            } else {
                val newCount = if (count > 8){
                    1
                }else{
                    count + 1
                }
                prefManager.setCustomParamInt(AppConstants.Purchase.AdsShowCount, newCount)
                false
            }
        }
        if (isShowAd){
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            if (isAdmob){
                newInterstitialAd(adUnit,true)
            }else{
                newAdxInterstitialAd(adUnit,true)
            }
        }

    }

    fun newInterstitialAd(adUnit: String,isAdsCountReset : Boolean = false) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),adUnit, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                // Show the ad if it's ready. Otherwise toast and reload the ad.
                interstitialAd.show(requireActivity())
                if (isAdsCountReset){
                    prefManager.setCustomParamInt(AppConstants.Purchase.AdsShowCount, 0)
                }
            }
        })
    }

    fun newAdxInterstitialAd(adUnit: String,isAdsCountReset : Boolean = false) {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(requireContext(),adUnit, adRequest, object : AdManagerInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                // Show the ad if it's ready. Otherwise toast and reload the ad.
                interstitialAd.show(requireActivity())
                if (isAdsCountReset){
                    prefManager.setCustomParamInt(AppConstants.Purchase.AdsShowCount, 0)
                }
            }
        })
    }
    // load banner ads
    fun showAMBannerAds(adViewLayout: LinearLayoutCompat, adUnit : String) {
        try {
            adViewLayout.gravity = Gravity.CENTER or Gravity.BOTTOM
//            adViewLayout.minimumHeight = resources.displayMetrics.heightPixels / 11
            // Request for Ads
            val adRequest = AdRequest.Builder().build()
            val listener: AdListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adViewLayout.show()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    adViewLayout.hide()
                }
            }
            val isAdmob = prefManager.getCustomParamBoolean(AppConstants.AbacusProgress.isAdmob,true)
            val adView = if (isAdmob){
                AdView(requireContext())
            }else{
                AdManagerAdView(requireContext())
            }
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = adUnit
            if (adView.adUnitId != "NA") {
                adViewLayout.addView(adView)
                adView.loadAd(adRequest)

                adView.adListener = listener
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun updateVoiceSettings(pitch: Float, speed: Float, voice: String, language: Locale) {
        // Update TTS
        prefManager.setCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE, voice)
        prefManager.setCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, Gson().toJson(language))
        prefManager.setCustomParamFloat(AppPreferencesHelper.KEY_DEFAULT_TTS_PITCH,pitch)
        prefManager.setCustomParamFloat(AppPreferencesHelper.KEY_DEFAULT_TTS_SPEECH,speed)
        // Dismiss the Dialog.
        voiceController?.dismiss()
    }

    // api failure
    fun onFailure(error: String?) {
        error?.let { showToast(it) }
    }
    fun goToInAppPurchase() {
        navController.navigate(R.id.toPurchaseFragment)
    }
    fun goToSetting() {
        navController.navigate(R.id.toSettingsFragment)
    }
}
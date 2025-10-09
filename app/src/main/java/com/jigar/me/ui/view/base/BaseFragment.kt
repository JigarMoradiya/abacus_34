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
import androidx.navigation.findNavController
import com.jigar.me.ui.view.confirm_alerts.dialogs.FreeTrialLeftDialog

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
        navController = requireActivity().findNavController(R.id.nav_host_fragment)
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
            setCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "N")
            setCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "N")

            data.map {
                checkPlanAndUpdate(it.google_plan_id)
            }
//            setCustomParam(AppConstants.Purchase.Purchase_All, "N")
//            Log.e("jigarLogs","Purchase_All New = "+getCustomParam(AppConstants.Purchase.Purchase_All,""))


        }
    }

    fun checkPlanAndUpdate(google_plan_id: String) {
        with(prefManager) {
            when (google_plan_id) {
                BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime, BillingRepository.AbacusSku.PRODUCT_ID_All_lifetime_old -> {
                    setCustomParam(AppConstants.Purchase.Purchase_All, "Y")
                }
                BillingRepository.AbacusSku.PRODUCT_ID_material_maths -> {
                    setCustomParam(AppConstants.Purchase.Purchase_Material_Maths, "Y")
                }
                BillingRepository.AbacusSku.PRODUCT_ID_material_nursery -> {
                    setCustomParam(AppConstants.Purchase.Purchase_Material_Nursery, "Y")
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

                BillingRepository.AbacusSku.PRODUCT_ID_Subscription_Month3 -> {
                    setCustomParam(AppConstants.Purchase.Purchase_All, "Y")
                }
            }
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
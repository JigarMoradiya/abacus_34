package com.jigar.me.ui.view.dashboard.fragments.custom_challenge

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.FragmentCustomChallengeHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSetting
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.VoiceControllerSettingInterface
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class CustomChallengeHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentCustomChallengeHomeBinding
    private lateinit var mNavController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCustomChallengeHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        init()
        clickListener()
        return binding.root
    }

    private fun init() {
        binding.rsQuestion.currentValue = prefManager.getCustomParamInt(AppConstants.CCM.totalQuestion,10)
        binding.rsGap.currentValue = prefManager.getCustomParamInt(AppConstants.CCM.questionGap,3)
        binding.rsQuestionLength.currentMinValue = prefManager.getCustomParamInt(AppConstants.CCM.questionMinLength,1)
        binding.rsQuestionLength.currentMaxValue = prefManager.getCustomParamInt(AppConstants.CCM.questionMaxLength,3)
        binding.cbQuestionVoice.isChecked = prefManager.getCustomParamBoolean(AppConstants.CCM.isQuestionSpeak,false)
        binding.cbQuestionNumber.isChecked = prefManager.getCustomParamBoolean(AppConstants.CCM.isQuestionShowNumber,true)
        binding.cbQuestionWord.isChecked = prefManager.getCustomParamBoolean(AppConstants.CCM.isQuestionShowWord,false)
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtStartExam.onClick { onStartClick() }
        binding.cardSettingTop.onClick { goToSetting() }
    }

    private fun onStartClick() {
        if (binding.cbQuestionVoice.isChecked || binding.cbQuestionNumber.isChecked || binding.cbQuestionWord.isChecked){
            prefManager.setCustomParamInt(AppConstants.CCM.totalQuestion,binding.rsQuestion.currentValue)
            prefManager.setCustomParamInt(AppConstants.CCM.questionGap,binding.rsGap.currentValue)
            prefManager.setCustomParamInt(AppConstants.CCM.questionMinLength,binding.rsQuestionLength.currentMinValue)
            prefManager.setCustomParamInt(AppConstants.CCM.questionMaxLength,binding.rsQuestionLength.currentMaxValue)
            prefManager.setCustomParamBoolean(AppConstants.CCM.isQuestionSpeak,binding.cbQuestionVoice.isChecked)
            prefManager.setCustomParamBoolean(AppConstants.CCM.isQuestionShowNumber,binding.cbQuestionNumber.isChecked)
            prefManager.setCustomParamBoolean(AppConstants.CCM.isQuestionShowWord,binding.cbQuestionWord.isChecked)

            if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"").equals("Y",true)){
                gotoNext()
            }else{
                getStatisticData(object : Companion.StatisticApiResponseListener{
                    override fun statisticApiData(data: JsonObject?) {
                        val response = Gson().fromJson(data, Statistics::class.java)
                        if (response.CCM?.can_give_exam == true){
                            gotoNext()
                        }else{
                            canNotAccess()
                        }
                    }
                })
            }

        }else{
            showToast(getString(R.string.please_select_at_least_one_checkbox))
        }

    }

    private fun gotoNext() {
        val action = CustomChallengeHomeFragmentDirections.actionCustomChallengeHomeFragmentToCustomChallengeFragment(
            binding.rsQuestion.currentValue,binding.rsQuestionLength.currentMinValue,binding.rsQuestionLength.currentMaxValue,
            binding.rsGap.currentValue,binding.cbQuestionVoice.isChecked,binding.cbQuestionNumber.isChecked,binding.cbQuestionWord.isChecked)
        mNavController.navigate(action)
    }

    private fun canNotAccess() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.ccm_subcribe_title),getString(R.string.ccm_subcribe_msg)
            ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

}
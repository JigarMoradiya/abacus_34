package com.jigar.me.ui.view.dashboard.fragments.custom_challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.jigar.me.R
import com.jigar.me.databinding.FragmentCustomChallengeHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomChallengeHomeFragment : BaseFragment() {
    private val appViewModel by viewModels<AppViewModel>()
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
        if (prefManager.getCustomParamInt(AppConstants.CCM.questionMaxLength,3) > 6){
            prefManager.setCustomParamInt(AppConstants.CCM.questionMaxLength,6)
        }
        binding.rsQuestionLength.currentMinValue = prefManager.getCustomParamInt(AppConstants.CCM.questionMinLength,1)
        binding.rsQuestionLength.currentMaxValue = prefManager.getCustomParamInt(AppConstants.CCM.questionMaxLength,3)
        binding.cbQuestionVoice.isChecked = prefManager.getCustomParamBoolean(AppConstants.CCM.isQuestionSpeak,false)
        binding.cbQuestionNumber.isChecked = prefManager.getCustomParamBoolean(AppConstants.CCM.isQuestionShowNumber,true)
        binding.cbQuestionWord.isChecked = prefManager. getCustomParamBoolean(AppConstants.CCM.isQuestionShowWord,false)
    }

    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtStartExam.onClick { onStartClick() }
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
            lifecycleScope.launch {
                val purchasedSKU = appViewModel.getInAppSKUPurchased()
                if (CommonUtils.checkPurchaseForExerciseExamCCM(prefManager,purchasedSKU)){
                    gotoNext()
                }else{
                    goToInAppPurchase()
                }
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
}
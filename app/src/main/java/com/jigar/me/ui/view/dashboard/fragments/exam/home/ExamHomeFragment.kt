package com.jigar.me.ui.view.dashboard.fragments.exam.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.databinding.FragmentExamHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.navigation.findNavController

@AndroidEntryPoint
class ExamHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentExamHomeBinding
    private lateinit var mNavController: NavController
    private val appViewModel by viewModels<AppViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        clickListener()
        return binding.root
    }

    private fun initView() {
    }

    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtStartExam.onClick {
            if (!binding.chNumber.isChecked && !binding.chAddition.isChecked && !binding.chSubtraction.isChecked
                && !binding.chMultiplication.isChecked && !binding.chDivision.isChecked ){
                showToast(getString(R.string.please_select_at_least_one_checkbox))
            }else{
                lifecycleScope.launch {
                    val purchasedSKU = appViewModel.getInAppSKUPurchased()
                    if (CommonUtils.checkPurchaseForExerciseExamCCM(prefManager,purchasedSKU)){
                        onExamStartClick()
                    }else{
                        goToInAppPurchase()
                    }
                }
            }
        }
    }

    private fun onExamStartClick() {
        var level = ""
        when {
            binding.rdchildLevelBeginner.isChecked -> {
                level = AppConstants.EXAM.examDifficultyBeginner
            }
            binding.rdchildLevelIntermediate.isChecked -> {
                level = AppConstants.EXAM.examDifficultyIntermediate
            }
            binding.rdchildLevelExpert.isChecked -> {
                level = AppConstants.EXAM.examDifficultyExpert
            }
        }
        if (level.isEmpty()){
            requireContext().toastL(getString(R.string.child_level))
        }else{
            goToNext(level)
        }
    }

    private fun goToNext(level : String) {
        val action = ExamHomeFragmentDirections.actionExamHomeFragmentToExamCommonFragment(level,binding.chNumber.isChecked,
            binding.chAddition.isChecked,binding.chSubtraction.isChecked,binding.chMultiplication.isChecked,binding.chDivision.isChecked)
        mNavController.navigate(action)
    }
}
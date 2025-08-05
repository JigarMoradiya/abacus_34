package com.jigar.me.ui.view.dashboard.fragments.exam.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.databinding.FragmentExamHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.findNavController

@AndroidEntryPoint
class ExamHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentExamHomeBinding
    private lateinit var mNavController: NavController
    private val appViewModel by viewModels<AppViewModel>()
//    private var themeContent : AbacusContent? = null
//    private var theme = AppConstants.Settings.theam_Default
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initView()
        clickListener()
        return binding.root
    }

    private fun initView() {
//        with(prefManager){
//            setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
//            theme = getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
//        }
//
//        themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.Exercise)
//        themeContent?.resetBtnColor8?.let{
//            binding.txtStartExam.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),it))
//        }
    }

    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.cardExamHistory.onClick { mNavController.navigate(R.id.action_examHomeFragment_to_examHistoryHomeFragment) }
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
                        canNotAccess()
                    }
                }
            }
        }
    }

    private fun onExamStartClick() {
        var level = ""
        when {
            binding.rdchildLevelBeginner.isChecked -> {
                level = AppConstants.ExamType.exam_Level_Beginner
            }
            binding.rdchildLevelIntermediate.isChecked -> {
                level = AppConstants.ExamType.exam_Level_Intermediate
            }
            binding.rdchildLevelExpert.isChecked -> {
                level = AppConstants.ExamType.exam_Level_Expert
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

    private fun canNotAccess() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.exam_subcribe_title),getString(R.string.exam_subcribe_msg)
            ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

}
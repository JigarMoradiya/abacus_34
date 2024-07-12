package com.jigar.me.ui.view.dashboard.fragments.exam.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.R
import com.jigar.me.data.model.data.Statistics
import com.jigar.me.databinding.FragmentExamHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.toastL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExamHomeFragment : BaseFragment() {

    private lateinit var binding: FragmentExamHomeBinding
    private lateinit var mNavController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExamHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        clickListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun clickListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.cardExamHistory.onClick { mNavController.navigate(R.id.action_examHomeFragment_to_examHistoryHomeFragment) }
        binding.txtStartExam.onClick {
            if (!binding.chNumber.isChecked && !binding.chAddition.isChecked && !binding.chSubtraction.isChecked
                && !binding.chMultiplication.isChecked && !binding.chDivision.isChecked ){
                showToast(getString(R.string.please_select_at_least_one_checkbox))
            }else{
                onExamStartClick()
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
            if (prefManager.getCustomParam(AppConstants.Purchase.Purchase_All,"").equals("Y",true)){
                goToNext(level)
            }else {
                getStatisticData(object : Companion.StatisticApiResponseListener {
                    override fun statisticApiData(data: JsonObject?) {
                        val response = Gson().fromJson(data, Statistics::class.java)
                        if (response.EXAM?.can_give_exam == true) {
                            goToNext(level)
                        } else {
                            canNotAccess()
                        }
                    }
                })
            }
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
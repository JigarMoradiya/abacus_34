package com.jigar.me.ui.view.dashboard.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.FragmentSettingsBinding
import com.jigar.me.databinding.LayoutAbacusExamBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusUtils
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.*
import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener
import com.mohammedalaa.seekbar.RangeSeekBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), AbacusThemeSelectionsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentSettingsBinding
    private var isPurchased = false
    private lateinit var mNavController: NavController
    private lateinit var abacusThemeFreeAdapter: AbacusThemeSelectionsAdapter
    private lateinit var abacusThemePaidAdapter: AbacusThemeSelectionsAdapter
    private var selectedFreePosition : Int = -1
    private var selectedPaidPosition : Int = -1
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    private fun initViews() {
        with(prefManager){
            isPurchased = (getCustomParam(AppConstants.Purchase.Purchase_All, "") == "Y"
                    || getCustomParam(AppConstants.Purchase.Purchase_Toddler_Single_digit_level1,"") == "Y"
                    || getCustomParam(AppConstants.Purchase.Purchase_Add_Sub_level2,"") == "Y"
                    || getCustomParam(AppConstants.Purchase.Purchase_Mul_Div_level3,"") == "Y")
            binding.isPurchased = isPurchased
        }

        binding.recyclerviewAbacusDefault.post {
            val columnFree = CommonUtils.calculateNoOfColumns((resources.getDimension(R.dimen.bead_column).toInt().dp.toFloat()),binding.recyclerviewAbacusDefault.width.dp.toFloat())
            binding.recyclerviewAbacusDefault.layoutManager = GridLayoutManager(requireContext(),columnFree)
            abacusThemeFreeAdapter = AbacusThemeSelectionsAdapter(DataProvider.getAbacusThemeFreeTypeList(requireContext(),AbacusBeadType.SettingPreview),this@SettingsFragment, selectedFreePosition)
            binding.recyclerviewAbacusDefault.adapter = abacusThemeFreeAdapter

            val columnPaid = CommonUtils.calculateNoOfColumns((resources.getDimension(R.dimen.bead_column_paid).toInt().dp.toFloat()),binding.recyclerviewAbacusDefault.width.dp.toFloat())
            binding.recyclerviewAbacusPaid.layoutManager = GridLayoutManager(requireContext(),columnPaid)
            abacusThemePaidAdapter = AbacusThemeSelectionsAdapter(DataProvider.getAbacusThemePaidTypeList(requireContext(),AbacusBeadType.SettingPreview),this@SettingsFragment, selectedPaidPosition, true)
            binding.recyclerviewAbacusPaid.adapter = abacusThemePaidAdapter

            setTheme()
        }
        setSettings()
        setAbacusAnswer()
    }

    private fun initListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }

        binding.rsBgMusic.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener{
            override fun onProgressChanged(seekBar: RangeSeekBarView?,progress: Int,fromUser: Boolean) {
                prefManager.setCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, progress)
                (activity as MainDashboardActivity).setMusicVolume(progress)
            }
            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?, progress: Int) {
                binding.nsv.requestDisallowInterceptTouchEvent(true)
            }
            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?, progress: Int) {
                binding.nsv.requestDisallowInterceptTouchEvent(false)
            }
        })

        binding.relVoiceSettings.onClick { voiceController?.show() }

        binding.cardSubscribe.onClick { goToInAppPurchase() }

        binding.relHintSound.onClick { onOnOffClick(AppConstants.Settings.Setting__hint_sound,binding.isHintSound) }
        binding.swHintSound.onClick { onOnOffClick(AppConstants.Settings.Setting__hint_sound,binding.isHintSound) }

        binding.relAbacusSound.onClick { onOnOffClick(AppConstants.Settings.Setting_sound,binding.isAbacusSound) }
        binding.swSound.onClick { onOnOffClick(AppConstants.Settings.Setting_sound,binding.isAbacusSound) }

        binding.relAutoReset.onClick { onOnOffClick(AppConstants.Settings.Setting_auto_reset_abacus,binding.isAutoReset) }
        binding.swAutoReset.onClick { onOnOffClick(AppConstants.Settings.Setting_auto_reset_abacus,binding.isAutoReset) }

        binding.relNumberPuzzleSound.onClick { onOnOffClick(AppConstants.Settings.Setting_NumberPuzzleVolume,binding.isNumberPuzzleSound) }
        binding.swNumberPuzzleSound.onClick { onOnOffClick(AppConstants.Settings.Setting_NumberPuzzleVolume,binding.isNumberPuzzleSound) }

        binding.relDisplayAbacusNumber.onClick { onOnOffClick(AppConstants.Settings.Setting_display_abacus_number,binding.isDisplayAbacusNumber) }
        binding.swDisplayAbacusNumber.onClick { onOnOffClick(AppConstants.Settings.Setting_display_abacus_number,binding.isDisplayAbacusNumber) }

        binding.relDisplayHelpMessage.onClick { onOnOffClick(AppConstants.Settings.Setting_display_help_message,binding.isDisplayHelpMessage) }
        binding.swDisplayHelpMessage.onClick { onOnOffClick(AppConstants.Settings.Setting_display_help_message,binding.isDisplayHelpMessage) }

        binding.relHideTable.onClick { onOnOffClick(AppConstants.Settings.Setting_hide_table,binding.isHideTable) }
        binding.swHideTable.onClick { onOnOffClick(AppConstants.Settings.Setting_hide_table,binding.isHideTable) }

        binding.relLeftHand.onClick { onOnOffClick(AppConstants.Settings.Setting_left_hand,binding.isLeftHand) }
        binding.swLeftHand.onClick { onOnOffClick(AppConstants.Settings.Setting_left_hand,binding.isLeftHand) }

        binding.relAnswerStep.onClick { onAbacusAnswerClick(AppConstants.Settings.Setting_answer_Step) }
        binding.relAnswerFinal.onClick { onAbacusAnswerClick(AppConstants.Settings.Setting_answer_Final) }
        binding.relAnswerPhysical.onClick {
            if (isPurchased){
                onAbacusAnswerClick(AppConstants.Settings.Setting_answer_with_tools)
            }else{
                paidPlanDialog()
            }
        }

    }
    private fun paidPlanDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.txt_purchase_alert), getString(R.string.need_paid_plan_msg),
            getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_not_purchased,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun setSettings() {
        with(prefManager){
            binding.rsBgMusic.currentValue = getCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default)
            binding.isDisplayHelpMessage = getCustomParamBoolean(AppConstants.Settings.Setting_display_help_message, true)

            binding.isDisplayAbacusNumber = getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

            binding.isHideTable = getCustomParamBoolean(AppConstants.Settings.Setting_hide_table, false)

            binding.isLeftHand = getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)

            binding.isHintSound = getCustomParamBoolean(AppConstants.Settings.Setting__hint_sound, false)

            binding.isAbacusSound = getCustomParamBoolean(AppConstants.Settings.Setting_sound, true)

            binding.isAutoReset = getCustomParamBoolean(AppConstants.Settings.Setting_auto_reset_abacus, false)

            binding.isNumberPuzzleSound = getCustomParamBoolean(AppConstants.Settings.Setting_NumberPuzzleVolume, true)
        }

    }
    private fun setTheme() {
        selectedFreePosition = -1
        selectedPaidPosition = -1
        val freeList = DataProvider.getAbacusThemeFreeTypeList(requireContext(),AbacusBeadType.SettingPreview)
        if (prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default).contains(AppConstants.Settings.theam_Default,true)){
            val position : Int? = freeList.indexOfFirst { it.type.equals(prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default),true) }
            if (position != null && position != -1){
                selectedFreePosition = position
                setPreviewTheme(freeList[position].type)
            }
        }else{
            val paidList = DataProvider.getAbacusThemePaidTypeList(requireContext(),AbacusBeadType.SettingPreview)
            val position : Int? = paidList.indexOfFirst { it.type.equals(prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default),true) }
            if (position != null && position != -1){
                selectedPaidPosition = position
                setPreviewTheme(paidList[position].type)
            }
        }
        abacusThemeFreeAdapter.selectedPos(selectedFreePosition)
        abacusThemePaidAdapter.selectedPos(selectedPaidPosition)
    }

    private fun setPreviewTheme(theme : String) {
        prefManager.setCustomParam(AppConstants.Settings.TheamTempView,theme)
        binding.linearAbacus.removeAllViews()
        binding.linearAbacusPreview.invisible()

        lifecycleScope.launch {
            val abacusBinding : LayoutAbacusExamBinding = LayoutAbacusExamBinding.inflate(layoutInflater, null, false)
//        val abacusBinding : FragmentAbacusSubBinding = FragmentAbacusSubBinding.inflate(layoutInflater, null, false)

            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0)
            abacusBinding.relAbacus.layoutParams = params

            binding.linearAbacus.addView(abacusBinding.root)
            val themeContent = DataProvider.findAbacusThemeType(requireContext(),theme, AbacusBeadType.SettingPreview)
            themeContent.abacusFrameExam135.let {
                abacusBinding.rlAbacusMain.setBackgroundResource(it)
            }
            themeContent.dividerColor1.let {
                abacusBinding.ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it))
            }
            themeContent.resetBtnColor8.let {
                abacusBinding.ivReset.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.txtPreview.setTextColor(ContextCompat.getColor(requireContext(),it))
            }
            AbacusUtils.setAbacusColumnTheme(AbacusBeadType.SettingPreview,abacusBinding.abacusTop,abacusBinding.abacusBottom, column = 3)
            binding.linearAbacusPreview.show()
            val number = DataProvider.generateSingleDigit(1, 998).toString()
            abacusBinding.tvCurrentVal.text = number
            AbacusUtils.setNumber(number,abacusBinding.abacusTop,abacusBinding.abacusBottom)

        }
    }

    override fun onThemePoligonItemClick(data: AbacusContent) {
        onThemeClick(data.type)
    }

    private fun setAbacusAnswer() {
        binding.isStepByStep = prefManager.getCustomParam(AppConstants.Settings.Setting_answer,AppConstants.Settings.Setting_answer_Step) == AppConstants.Settings.Setting_answer_Step
        binding.isFinalAnswer = prefManager.getCustomParam(AppConstants.Settings.Setting_answer,AppConstants.Settings.Setting_answer_Step) == AppConstants.Settings.Setting_answer_Final
        binding.isAnswerWithTool = prefManager.getCustomParam(AppConstants.Settings.Setting_answer,AppConstants.Settings.Setting_answer_Step) == AppConstants.Settings.Setting_answer_with_tools
    }

    private fun onAbacusAnswerClick(answerType: String) {
        prefManager.setCustomParam(AppConstants.Settings.Setting_answer,answerType)
        setAbacusAnswer()
    }

    private fun onOnOffClick(type: String, isChecked: Boolean?) {
       if (type == AppConstants.Settings.Setting__hint_sound){
            if (isChecked == true){
                prefManager.setCustomParamBoolean(type, false)
            }else{
                if (isPurchased){
                    prefManager.setCustomParamBoolean(type, true)
                }else{
                    prefManager.setCustomParamBoolean(type, false)
                    paidPlanDialog()
                }
            }
        }else{
            if (isChecked == true){
                prefManager.setCustomParamBoolean(type, false)
            }else{
                prefManager.setCustomParamBoolean(type, true)
            }

        }
        setSettings()
    }

    private fun onThemeClick(themeType: String) {
        if (themeType != prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default)){

            when {
                themeType.contains(AppConstants.Settings.theam_Default,true) -> {
                    prefManager.setCustomParam(AppConstants.Settings.Theam,themeType)
                    abacusThemePaidAdapter.selectedPos(-1)
                }
                isPurchased -> {
                    prefManager.setCustomParam(AppConstants.Settings.Theam,themeType)
                    abacusThemeFreeAdapter.selectedPos(-1)
                }
                else -> {
                    abacusThemeFreeAdapter.selectedPos(-1)
                    paidPlanDialog()
                }
            }
            setPreviewTheme(themeType)
        }
    }



}
package com.jigar.me.ui.view.dashboard.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.FragmentSettingsBinding
import com.jigar.me.databinding.LayoutAbacusExamBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusUtils
import com.jigar.me.ui.view.dashboard.MainDashboardActivity
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener
import com.mohammedalaa.seekbar.RangeSeekBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), AbacusThemeSelectionsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var mNavController: NavController
    private lateinit var abacusThemeFreeAdapter: AbacusThemeSelectionsAdapter
    private lateinit var abacusThemePaidAdapter: AbacusThemeSelectionsAdapter
    private var selectedTheme: String = AppConstants.Settings.theam_Default
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }
    private fun setNavigationGraph() {
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }
    private fun initViews() {
        selectedTheme = prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default)
        setPreviewTheme(selectedTheme)

        binding.recyclerviewAbacusDefault.post {
            val columnFree = CommonUtils.calculateNoOfColumns((resources.getDimension(R.dimen.bead_column).toInt().dp.toFloat()),binding.recyclerviewAbacusDefault.width.dp.toFloat())
            binding.recyclerviewAbacusDefault.layoutManager = GridLayoutManager(requireContext(),columnFree)
            abacusThemeFreeAdapter = AbacusThemeSelectionsAdapter(DataProvider.getAbacusThemeFreeTypeList(requireContext(),AbacusBeadType.SettingPreview),this@SettingsFragment, selectedTheme = selectedTheme)
            binding.recyclerviewAbacusDefault.adapter = abacusThemeFreeAdapter

            val columnPaid = CommonUtils.calculateNoOfColumns((resources.getDimension(R.dimen.bead_column_paid).toInt().dp.toFloat()),binding.recyclerviewAbacusDefault.width.dp.toFloat())
            binding.recyclerviewAbacusPaid.layoutManager = GridLayoutManager(requireContext(),columnPaid)
            abacusThemePaidAdapter = AbacusThemeSelectionsAdapter(DataProvider.getAbacusThemePaidTypeList(requireContext(),AbacusBeadType.SettingPreview),this@SettingsFragment, isPaidTheme = true, selectedTheme = selectedTheme)
            binding.recyclerviewAbacusPaid.adapter = abacusThemePaidAdapter
        }
        setSettings()
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

        binding.relHintSound.onClick { onOnOffClick(AppConstants.Settings.Setting__hint_sound,binding.isHintSound) }
        binding.relAbacusDirection.onClick { onOnOffClick(AppConstants.Settings.Setting_direction,binding.isShowBeadDirection) }
        binding.relAbacusSound.onClick { onOnOffClick(AppConstants.Settings.Setting_sound,binding.isAbacusSound) }
        binding.relDisplayAbacusNumber.onClick { onOnOffClick(AppConstants.Settings.Setting_display_abacus_number,binding.isDisplayAbacusNumber) }
        binding.relDisplayHelpMessage.onClick { onOnOffClick(AppConstants.Settings.Setting_display_help_message,binding.isDisplayHelpMessage) }
        binding.relLeftHand.onClick { onOnOffClick(AppConstants.Settings.Setting_left_hand,binding.isLeftHand) }
    }

    private fun setSettings() {
        with(prefManager){
            binding.rsBgMusic.currentValue = getCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default)
            binding.isDisplayHelpMessage = getCustomParamBoolean(AppConstants.Settings.Setting_display_help_message, true)

            binding.isDisplayAbacusNumber = getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

            binding.isLeftHand = getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)

            binding.isHintSound = getCustomParamBoolean(AppConstants.Settings.Setting__hint_sound, false)

            binding.isShowBeadDirection = getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)

            binding.isAbacusSound = getCustomParamBoolean(AppConstants.Settings.Setting_sound, true)
        }

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
        val themeType = data.type
        if (themeType != prefManager.getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default)){

            abacusThemeFreeAdapter.updateSelection(selectedTheme,themeType)
            abacusThemePaidAdapter.updateSelection(selectedTheme,themeType)

            prefManager.setCustomParam(AppConstants.Settings.Theam,themeType)
            selectedTheme = themeType

            setPreviewTheme(themeType)
        }
    }

    private fun onOnOffClick(type: String, isChecked: Boolean?) {
       if (type == AppConstants.Settings.Setting__hint_sound){
            if (isChecked == true){
                prefManager.setCustomParamBoolean(type, false)
            }else{
                prefManager.setCustomParamBoolean(type, true)
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
}
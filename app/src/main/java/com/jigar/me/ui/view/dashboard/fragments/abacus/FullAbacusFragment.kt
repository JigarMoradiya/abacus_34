package com.jigar.me.ui.view.dashboard.fragments.abacus

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.FragmentAbacusSubKidBinding
import com.jigar.me.databinding.FragmentFullAbacusBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterBeadShiftListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterView
import com.jigar.me.ui.view.base.abacus.AbacusUtils
import com.jigar.me.ui.view.base.abacus.OnAbacusValueChangeListener
import com.jigar.me.ui.view.confirm_alerts.bottomsheets.CommonConfirmationBottomSheet
import com.jigar.me.ui.view.confirm_alerts.dialogs.ToddlerRangeDialog
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.samlss.lighter.IntroProvider
import me.samlss.lighter.Lighter
import me.samlss.lighter.parameter.Direction
import java.util.*

@AndroidEntryPoint
class FullAbacusFragment : BaseFragment(), ToddlerRangeDialog.ToddlerRangeDialogInterface,
    AbacusMasterBeadShiftListener,
    OnAbacusValueChangeListener {
    private lateinit var binding: FragmentFullAbacusBinding
    private var abacusBinding: FragmentAbacusSubKidBinding? = null
    private lateinit var themeContent : AbacusContent
    private var values: Float = 1.0F
    private var valuesFinal: Float = 1.0F
    private var random_min: Float = 1F
    private var random_max: Float = 1000F
    private var total_count: Int = 1

    private var currentSumVal = 0L
    private var isTourPageRunning = false
    private var isResetRunning = false
    private var isPurchased = false
    private var is1stTime = false
    private var theme = AppConstants.Settings.theam_Default
    private lateinit var mNavController: NavController
    private var lighter : Lighter? = null
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentFullAbacusBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initViews()
        initListener()
        return binding.root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() {
//        setAbacus()
        with(prefManager){
            if (getCustomParam(AppConstants.Settings.SW_Random,"") != "Y") {
                values = getCustomParamFloat(AppConstants.Settings.Toddler_No,1.0F)
                if (values > 9999999) {
                    values = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1.0F)
                }
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000
                }else{
                    values
                }
            } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                values = genrateRandom().toFloat()
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000
                }else{
                    values
                }
            }
        }
        setSwitchs()
        binding.swDecimalMode.isChecked = prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode, "N") == "Y"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                binding.txtFreeMode.setPadding(0,8.dp,0,0)
                binding.txtResetEveryTime.setPadding(0,12.dp,12.dp,0)
                binding.txtRangeLable.setPadding(0,12.dp,0,0)
                binding.txtRandom.setPadding(0,12.dp,0,0)
                binding.txtStartWith1.setPadding(0,12.dp,0,0)
            } catch (e: Exception) {
            }
        }
    }

    private fun initListener() {
        binding.cardBack.onClick { mNavController.navigateUp() }
        binding.txtShowTour.onClick { setThemeLighterTopBeads() }
        binding.txtShowTourTop.onClick { binding.txtShowTour.performClick() }
        binding.swRandom.onClick { switchRandomClick() }
        binding.swReset.onClick { switchResetClick() }
        binding.swResetStarting.onClick { switchResetStartingClick() }
        binding.swFreeMode.onClick { switchFreeMode() }
        binding.swDecimalMode.onClick { switchDecimalMode() }
        binding.txtRange.onClick { rangeClick() }

        binding.cardSettingTop.onClick { goToSetting() }
        binding.cardYoutube.onClick { requireContext().openYoutube() }
        binding.cardSubscribe.onClick { goToInAppPurchase()  }
    }
    private fun resetClick() {
        if (!isResetRunning) {
            isResetRunning = true
            abacusBinding?.ivReset?.y = 0f
            abacusBinding?.ivReset?.animate()?.setDuration(200)
                ?.translationYBy((abacusBinding?.ivReset?.height!! / 2).toFloat())?.withEndAction {
                    abacusBinding?.ivReset?.animate()?.setDuration(200)
                        ?.translationYBy((-abacusBinding?.ivReset?.height!! / 2).toFloat())!!.withEndAction {
                            isResetRunning = false
                        }.start()
                }?.start()
            onAbacusValueDotReset()
        }
    }

    private fun switchResetClick() {
        if (prefManager.getCustomParam(AppConstants.Settings.SW_Reset,"") == "Y") {
            prefManager.setCustomParam(AppConstants.Settings.SW_Reset, "N")
            binding.swReset.isChecked = false
        } else {
            prefManager.setCustomParam(AppConstants.Settings.SW_Reset, "Y")
            binding.swReset.isChecked = true
        }
    }

    private fun switchResetStartingClick() {
        prefManager.setCustomParamFloat(AppConstants.Settings.Toddler_No,prefManager.getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F))
        setSwitchs()
    }

    private fun switchFreeMode() {
        if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") == "Y") {
            prefManager.setCustomParam(AppConstants.Settings.SW_FreeMode, "N")
            binding.swFreeMode.isChecked = false
        } else {
            prefManager.setCustomParam(AppConstants.Settings.SW_FreeMode, "Y")
            binding.swFreeMode.isChecked = true
        }
        setSwitchs()
    }
    private fun switchDecimalMode() {
        with(prefManager){
            if (getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                prefManager.setCustomParam(AppConstants.Settings.SW_DecimalMode, "N")
                binding.swDecimalMode.isChecked = false
            } else {
                setCustomParam(AppConstants.Settings.SW_DecimalMode, "Y")
                binding.swDecimalMode.isChecked = true
            }
            with(prefManager){
                if (getCustomParam(AppConstants.Settings.SW_Random,"") != "Y") {
                    values = getCustomParamFloat(AppConstants.Settings.Toddler_No,1.0F)
                    if (values > 9999999) {
                        values = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1.0F)
                    }
                    valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                        values / 1000
                    }else{
                        values
                    }
                } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                    random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                    random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                    values = genrateRandom().toFloat()
                    valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                        values / 1000
                    }else{
                        values
                    }
                }
            }
            setNumberValue()
            if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") != "Y") {
                goToNextValue()
            }
            setAbacus()
        }
    }

    private fun rangeClick() {
        ToddlerRangeDialog.showPopup(requireActivity(),
            prefManager.getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F).toInt().toString(),
            (prefManager.getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)-1).toInt().toString(),this)
    }

    private fun switchRandomClick() {
        with(prefManager){
            if (isPurchased) {
                if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                    setCustomParam(AppConstants.Settings.SW_Random,"N")
                } else {
                    setCustomParam(AppConstants.Settings.SW_Random,"Y")
                }
                setSwitchs()
            } else {
                setCustomParam(AppConstants.Settings.SW_Random,"N")
                setSwitchs()
                notPurchaseDialog()
            }
        }

    }
    // not purchased
    private fun notPurchaseDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.txt_purchase_alert),getString(R.string.txt_page_full_abacus_not_purchased)
            ,getString(R.string.yes_i_want_to_purchase),getString(R.string.no_purchase_later), icon = R.drawable.ic_alert_not_purchased,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    goToInAppPurchase()
                }
                override fun onConfirmationNoClick(bundle: Bundle?) = Unit
            })
    }

    private fun setAbacus() {
        with(prefManager){
            isPurchased = (getCustomParam(AppConstants.Purchase.Purchase_All,"") == "Y"
                    || getCustomParam(AppConstants.Purchase.Purchase_Toddler_Single_digit_level1,"") == "Y")
            if (isPurchased){
                setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
            }else{
                if (getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default).contains(AppConstants.Settings.theam_Default,true)){
                    setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
                }else{
                    setCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
                }
            }
            theme = getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)

        }

        binding.linearAbacus.removeAllViews()
        binding.linearAbacusFreeMode.removeAllViews()
        abacusBinding = FragmentAbacusSubKidBinding.inflate(layoutInflater, null, false)
        if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode, "N") == "Y"){
            abacusBinding?.imgDot4?.show()
            abacusBinding?.imgDot7?.show()
            abacusBinding?.txtRodName01Bottom?.show()
            abacusBinding?.txtRodName02Bottom?.show()
            abacusBinding?.txtRodName03Bottom?.show()
            abacusBinding?.txtRodName8Bottom?.hide()
            abacusBinding?.txtRodName7Bottom?.hide()
            abacusBinding?.txtRodName6Bottom?.hide()

            abacusBinding?.txtRodNumber01?.show()
            abacusBinding?.txtRodNumber02?.show()
            abacusBinding?.txtRodNumber03?.show()
            abacusBinding?.txtRodNumber8?.hide()
            abacusBinding?.txtRodNumber7?.hide()
            abacusBinding?.txtRodNumber6?.hide()

            abacusBinding?.txtRodNumber01Bottom?.show()
            abacusBinding?.txtRodNumber02Bottom?.show()
            abacusBinding?.txtRodNumber03Bottom?.show()
            abacusBinding?.txtRodNumber8Bottom?.hide()
            abacusBinding?.txtRodNumber7Bottom?.hide()
            abacusBinding?.txtRodNumber6Bottom?.hide()
        }else{
            abacusBinding?.imgDot1?.show()
            abacusBinding?.imgDot4?.show()
            abacusBinding?.imgDot7?.show()

            abacusBinding?.txtRodName01Bottom?.hide()
            abacusBinding?.txtRodName02Bottom?.hide()
            abacusBinding?.txtRodName03Bottom?.hide()
            abacusBinding?.txtRodName8Bottom?.show()
            abacusBinding?.txtRodName7Bottom?.show()
            abacusBinding?.txtRodName6Bottom?.show()

            abacusBinding?.txtRodNumber01?.hide()
            abacusBinding?.txtRodNumber02?.hide()
            abacusBinding?.txtRodNumber03?.hide()
            abacusBinding?.txtRodNumber8?.show()
            abacusBinding?.txtRodNumber7?.show()
            abacusBinding?.txtRodNumber6?.show()
            abacusBinding?.txtRodNumber01Bottom?.hide()
            abacusBinding?.txtRodNumber02Bottom?.hide()
            abacusBinding?.txtRodNumber03Bottom?.hide()
            abacusBinding?.txtRodNumber8Bottom?.show()
            abacusBinding?.txtRodNumber7Bottom?.show()
            abacusBinding?.txtRodNumber6Bottom?.show()
        }

        val abacusBeadType = if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"){
            binding.linearAbacusFreeMode.addView(abacusBinding?.root)
            binding.linearAbacusFreeMode.show()
            binding.linearAbacus.hide()
            abacusBinding?.viewNumbers?.show()
            abacusBinding?.viewNumbersBottom?.show()
            AbacusBeadType.FreeMode
        }else{
            binding.linearAbacus.addView(abacusBinding?.root)
            binding.linearAbacus.show()
            binding.linearAbacusFreeMode.hide()
            AbacusBeadType.None
        }
        themeContent  = DataProvider.findAbacusThemeType(requireContext(),theme,abacusBeadType)

        if (DataProvider.generateIndex() == 0){
            abacusBinding?.imgKidLeft?.setImageResource(R.drawable.ic_boy_abacus_left)
            abacusBinding?.imgKidHandLeft?.setImageResource(R.drawable.ic_boy_abacus_hand_left)
        }else{
            abacusBinding?.imgKidLeft?.setImageResource(R.drawable.ic_girl_abacus_left)
            abacusBinding?.imgKidHandLeft?.setImageResource(R.drawable.ic_girl_abacus_hand_left)
        }
        abacusBinding?.imgKidLeft?.show()
        abacusBinding?.imgKidHandLeft?.show()

        abacusBinding?.ivReset?.onClick { resetClick()}

        abacusBinding?.rlAbacusMain?.setBackgroundResource(themeContent.abacusFrame135)
        abacusBinding?.ivDivider?.setBackgroundColor(ContextCompat.getColor(requireContext(),themeContent.dividerColor1))
        themeContent.txtColor?.let {
            abacusBinding?.tvCurrentVal?.setTextColor(ContextCompat.getColor(requireContext(),it))
        }
        themeContent.resetBtnColor8.let {
            abacusBinding?.imgDot1?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot4?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot7?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            abacusBinding?.imgDot5?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot8?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.txtShowTourTop.setTextColor(ContextCompat.getColor(requireContext(),it))
            binding.txtShowTour.setTextColor(ContextCompat.getColor(requireContext(),it))
            abacusBinding?.ivReset?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivRight?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivLeft?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        abacusBinding?.abacusTop?.setNoOfRowAndBeads(0, 9, 1,abacusBeadType)
        abacusBinding?.abacusBottom?.setNoOfRowAndBeads(0, 9, 4,abacusBeadType)

        abacusBinding?.abacusTop?.onBeadShiftListener = this@FullAbacusFragment
        abacusBinding?.abacusBottom?.onBeadShiftListener = this@FullAbacusFragment

        if (!prefManager.getCustomParamBoolean(AppConstants.Settings.isFreeModeTourWatch, false)) {
            binding.txtShowTour.hide()
            binding.txtShowTourTop.hide()
            setThemeLighterTopBeads()
        }else{
            setTourVisibility()
        }
    }

    private fun setTourVisibility() {
        if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"){
            binding.txtShowTour.show()
            binding.txtShowTourTop.hide()
        }else{
            binding.txtShowTour.hide()
            binding.txtShowTourTop.show()
        }
    }

    private fun setSwitchs() {
        with(prefManager){
            binding.swFreeMode.isChecked = getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"
            setFreeMode()
        }
    }

    private fun setNumberValue() {
        val setValues = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
            valuesFinal.toString()
        }else{
            valuesFinal.toLong().toString()
        }
        try {
            binding.txtAbacus.text = String.format(requireContext().getString(R.string.txt_set_only_s),setValues)
        } catch (e: UnknownFormatConversionException) {
            binding.txtAbacus.text = "${requireContext().getString(R.string.txt_set)} $setValues"
        } catch (e: Exception) {
            binding.txtAbacus.text = "${requireContext().getString(R.string.txt_set)} $setValues"
        }
    }

    private fun setFreeMode() {
        with(prefManager){
            if (getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"){
                binding.swRandom.hide()
                binding.swResetStarting.hide()
                binding.swReset.hide()
                binding.txtRange.hide()

                binding.txtStartWith1.hide()
                binding.txtRandom.hide()
                binding.txtRangeLable.hide()
                binding.txtResetEveryTime.hide()

                binding.txtAbacus.hide()
                is1stTime = true
            }else{
                binding.swRandom.show()
                binding.swResetStarting.show()
                binding.swReset.show()
                binding.txtRange.show()

                binding.txtStartWith1.show()
                binding.txtRandom.show()
                binding.txtRangeLable.show()
                binding.txtResetEveryTime.show()

                binding.txtAbacus.show()

                binding.swRandom.isChecked = getCustomParam(AppConstants.Settings.SW_Random, "") == "Y"
                binding.swReset.isChecked = getCustomParam(AppConstants.Settings.SW_Reset, "") == "Y"
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                try {
                    binding.txtRange.text = String.format(requireContext().getString(R.string.txt_From_to),random_min,(random_max - 1))
                } catch (e: UnknownFormatConversionException) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} $random_min ${requireContext().getString(R.string.txt_To)} ${(random_max - 1)}"
                } catch (e: Exception) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} $random_min ${requireContext().getString(R.string.txt_To)} ${(random_max - 1)}"
                }
                if (getCustomParam(AppConstants.Settings.SW_Random, "") != "Y") {
                    values = getCustomParamFloat(AppConstants.Settings.Toddler_No, random_min.toFloat())
                } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                    values = genrateRandom().toFloat()
                }
                total_count = getCustomParamInt(AppConstants.Settings.Toddler_No_Count,1)
                setNumberValue()

                lifecycleScope.launch {
                    delay(300)
                    is1stTime = true
                    if (requireContext().isNetworkAvailable){
                        goToNextValue()
                    }else{
                        notOfflineSupportDialog()
                    }
                }
            }

            setAbacus()
        }
    }

    private fun genrateRandom(): Float {
        val r = Random()
        val i1 = r.nextInt(random_max.toInt() - random_min.toInt()) + random_min.toInt()
        return i1.toFloat()
    }

    // ToddlerRangeDialog click listener
    override fun onSubmitClickToddlerRange(fromValue: String, toValue: String) {
        with(prefManager){
            setCustomParamFloat(AppConstants.Settings.SW_Range_min,fromValue.toFloat())
            setCustomParamFloat(AppConstants.Settings.SW_Range_max,toValue.toFloat() + 1)
            setCustomParamFloat(AppConstants.Settings.Toddler_No,getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F))
            setSwitchs()
        }
    }

    // abacus Bead Shift Listener
    override fun onBeadShift(abacusView: AbacusMasterView, rowValue: IntArray) {
        val singleBeadWeight = abacusView.singleBeadValue
        var accumulator = 0L

        when (abacusView.id) {
            R.id.abacusTop -> if (abacusBinding?.abacusBottom?.engine != null) {
                val bottomVal = abacusBinding?.abacusBottom?.engine!!.getValue()
                var i = 0
                while (i < rowValue.size) {
                    accumulator *= 10
                    val rval = rowValue[i]
                    if (rval > -1) accumulator += rval * singleBeadWeight
                    i++
                }
                val intSumVal = bottomVal + accumulator
                currentSumVal = intSumVal
                onAbacusValueChange(abacusView, currentSumVal)
            }
            R.id.abacusBottom -> if (abacusBinding?.abacusTop?.engine != null) {
                val topVal = abacusBinding?.abacusTop?.engine!!.getValue()
                var i = 0
                while (i < rowValue.size) {
                    accumulator *= 10
                    val rval = rowValue[i]
                    if (rval > -1) accumulator += rval * singleBeadWeight
                    i++
                }
                val intSumVal = topVal + accumulator
                currentSumVal = intSumVal
                onAbacusValueChange(abacusView, currentSumVal)
            }
        }
    }
    private fun notOfflineSupportDialog() {
        CommonConfirmationBottomSheet.showPopup(requireActivity(),getString(R.string.no_internet_working),getString(R.string.no_internet)
            ,getString(R.string.continue_working_internet),getString(R.string.no_working_internet), icon = R.drawable.ic_alert_sad_emoji,isCancelable = false,
            clickListener = object : CommonConfirmationBottomSheet.OnItemClickListener{
                override fun onConfirmationYesClick(bundle: Bundle?) {
                    if (requireContext().isNetworkAvailable){
                        goToNextValue()
                    } else{
                        showToast(R.string.still_no_internet)
                        notOfflineSupportDialog()
                    }
                }
                override fun onConfirmationNoClick(bundle: Bundle?){
                    mNavController.navigateUp()
                }
            })
    }
    override fun onAbacusValueChange(abacusView: View, sum1: Long) {
        // TODO
        val sum : Float = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
            val ans : Float = sum1.toFloat() / 1000
            abacusBinding?.tvCurrentVal?.text = ans.toString()
            ans
        }else{
            abacusBinding?.tvCurrentVal?.text = sum1.toString()
            sum1.toFloat()
        }


        with(prefManager){
            if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") == "Y") {
                if (!isTourPageRunning){
                    if (!is1stTime){
                        var count = getCustomParamInt(AppConstants.Settings.Free_Mode_Beads_Move_Count,0)
                        if (count == AppConstants.Settings.Free_Mode_Beads_Move_Count_Limit){
                            count = 0
                            ads(true)
                        }
                        count++
                        setCustomParamInt(AppConstants.Settings.Free_Mode_Beads_Move_Count,count)
                    }else{
                        lifecycleScope.launch {
                            delay(300)
                            is1stTime = false
                        }
                    }
                } else {

                }
            }else{
                if (sum == valuesFinal) {
                    binding.swResetStarting.isChecked = false
                    generateValue()
                    total_count = getCustomParamInt(AppConstants.Settings.Toddler_No_Count,1)
                    total_count++
                    if (total_count > 99999999) {
                        total_count = 1
                    }
                    setCustomParamInt(AppConstants.Settings.Toddler_No_Count,total_count)
                    lifecycleScope.launch {
                        delay(300)
                        if (getCustomParam(AppConstants.Settings.SW_Reset,"") == "Y") {
                            resetAbacus()
                        }
                        is1stTime = false
                        if (requireContext().isNetworkAvailable){
                            goToNextValue()
                        }else{
                            notOfflineSupportDialog()
                        }
                    }
                    setNumberValue()
                } else {

                }
            }
        }

    }

    private fun generateValue() {
        with(prefManager){
            if (getCustomParam(AppConstants.Settings.SW_Random,"") != "Y") {
                values++
                if (values > 9999999) {
                    values = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1.0F)
                }
                setCustomParamFloat(AppConstants.Settings.Toddler_No,values)
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000
                }else{
                    values
                }
            } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                try {
                    binding.txtRange.text = String.format(requireContext().getString(R.string.txt_From_to),random_min,(random_max - 1))
                } catch (e: UnknownFormatConversionException) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} $random_min ${requireContext().getString(R.string.txt_To)} ${(random_max - 1)}"
                } catch (e: Exception) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} $random_min ${requireContext().getString(R.string.txt_To)} ${(random_max - 1)}"
                }
                values = genrateRandom().toFloat()
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000
                }else{
                    values
                }
            } else {

            }
        }
    }

    private fun goToNextValue() {
//        speakOut(String.format(resources.getString(R.string.speech_set), " ${requireContext().convert(values)}"))
        // TODO
//        values = 268.56F
        val setValues = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
            valuesFinal.toString()
        }else{
            valuesFinal.toLong().toString()
        }
        val speakText = try {
            String.format(requireContext().getString(R.string.txt_set_only),setValues)
        } catch (e: UnknownFormatConversionException) {
            "${requireContext().getString(R.string.txt_set)} $setValues"
        } catch (e: Exception) {
            "${requireContext().getString(R.string.txt_set)} $setValues"
        }
        speakOut(speakText)
        if (!is1stTime){
            ads()
        }
    }

    private fun ads(isShowAdDirect : Boolean = false) {
        if (requireContext().isNetworkAvailable && AppConstants.Purchase.AdsShow == "Y" &&
            prefManager.getCustomParam(AppConstants.AbacusProgress.Ads,"") == "Y" &&
            !isPurchased && prefManager.getCustomParam(AppConstants.Purchase.Purchase_Ads,"") != "Y") { // if not purchased
            showAMFullScreenAds(getString(R.string.interstitial_ad_unit_id_abacus_full_screen),isShowAdDirect)
        }
    }

    override fun onAbacusValueSubmit(sum: Long) {

    }

    override fun onAbacusValueDotReset() {
        resetAbacus()
    }

    private fun resetAbacus() {
        abacusBinding?.abacusTop?.reset()
        abacusBinding?.abacusBottom?.reset()
    }

    // TODO abacus tour
    private fun setThemeLighterTopBeads() {
        isTourPageRunning = true
        lighter = Lighter.with(binding.root as ViewGroup)
        abacusBinding?.let {
            IntroProvider.abacusTopBottomBeadsIntro(lighter,it.flAbacusTop,it.flAbacusBottom,object : IntroProvider.IntroCloseClickListener {
                override fun onIntroCloseClick() {
                    setThemeLighterRod1()
                }
            })
        }
    }

    private fun setThemeLighterRod1() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1
            abacusBinding?.relHighLighter?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("9",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.LEFT,R.layout.layout_tip_abacus_rod1,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        setThemeLighterRod2()
                    }
                })
            }
        }

    }

    private fun setThemeLighterRod2() {
        lifecycleScope.launch {
            val paramsView1 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = themeContent.beadWidth + themeContent.beadSpace + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            lighter = Lighter.with(binding.root as ViewGroup)
            abacusBinding?.let {
                AbacusUtils.setNumber("90",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1,Direction.LEFT,R.layout.layout_tip_abacus_rod2,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        setThemeLighterRod3()
                    }
                })
            }
        }
    }
    private fun setThemeLighterRod3() {
        lifecycleScope.launch {
            val paramsView2 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView2.width = themeContent.beadWidth
            paramsView2.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 2) + (themeContent.beadSpace / 2)
            paramsView2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView2

            lighter = Lighter.with(binding.root as ViewGroup)
            abacusBinding?.let {
                AbacusUtils.setNumber("900",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1,Direction.LEFT,R.layout.layout_tip_abacus_rod3,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        setThemeLighterOneColumn()
                    }
                })
            }
        }
    }

    private fun setThemeLighterOneColumn() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("9",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.LEFT,R.layout.layout_tip_abacus_column1,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        setThemeLighterTwoColumn()
                    }
                })
            }
        }

    }

    private fun setThemeLighterTwoColumn() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth + themeContent.beadWidth + themeContent.beadSpace
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("99",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.LEFT,R.layout.layout_tip_abacus_column2,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        setThemeLighterThreeColumn()
                    }
                })
            }
        }

    }

    private fun setThemeLighterThreeColumn() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRod1?.layoutParams as RelativeLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth + ((themeContent.beadWidth + themeContent.beadSpace) * 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("999",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.LEFT,R.layout.layout_tip_abacus_column3,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        abacusBinding?.relHighLighter?.hide()
                        setThemeLighterRod1BottomAdd()
                    }
                })
            }
        }

    }

    private fun setThemeLighterRod1BottomAdd() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRodBottom?.layoutParams as ConstraintLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            abacusBinding?.viewRodBottom?.layoutParams = paramsView1
            abacusBinding?.relHighLighterTop?.hide()
            abacusBinding?.relHighLighterBottom?.show()
            abacusBinding?.linearBottomArrows?.show()
            abacusBinding?.arrowDownRodBottom?.hide()
            abacusBinding?.arrowUpRodBottom?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("0",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.relHighLighterBottom , Direction.LEFT,R.layout.layout_tip_abacus_rod1_bottom_add,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        abacusBinding?.relHighLighterBottom?.hide()
                        setThemeLighterRod1TopAdd()
                    }
                })
            }
        }
    }
    private fun setThemeLighterRod1TopAdd() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRodTop?.layoutParams as ConstraintLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            abacusBinding?.viewRodTop?.layoutParams = paramsView1
            abacusBinding?.relHighLighterBottom?.hide()
            abacusBinding?.relHighLighterTop?.show()
            abacusBinding?.arrowDownRodTop?.show()
            abacusBinding?.arrowUpRodTop?.hide()
            abacusBinding?.linearTopArrows?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("0",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.relHighLighterTop , Direction.LEFT,R.layout.layout_tip_abacus_rod1_top_add,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        abacusBinding?.relHighLighterTop?.hide()
                        setThemeLighterRod1BottomSub()
                    }
                })
            }
        }

    }
    private fun setThemeLighterRod1BottomSub() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRodBottom?.layoutParams as ConstraintLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            abacusBinding?.viewRodBottom?.layoutParams = paramsView1
            abacusBinding?.relHighLighterTop?.hide()
            abacusBinding?.relHighLighterBottom?.show()
            abacusBinding?.linearBottomArrows?.show()
            abacusBinding?.arrowDownRodBottom?.show()
            abacusBinding?.arrowUpRodBottom?.hide()

            abacusBinding?.let {
                AbacusUtils.setNumber("4",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.relHighLighterBottom , Direction.LEFT,R.layout.layout_tip_abacus_rod1_bottom_sub,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        abacusBinding?.relHighLighterBottom?.hide()
                        setThemeLighterRod1TopSub()
                    }
                })
            }
        }
    }
    private fun setThemeLighterRod1TopSub() {
        lifecycleScope.launch {
            lighter = Lighter.with(binding.root as ViewGroup)
            val paramsView1 = abacusBinding?.viewRodTop?.layoutParams as ConstraintLayout.LayoutParams
            paramsView1.width = themeContent.beadWidth
            paramsView1.marginEnd = (themeContent.beadSpace / 2)
            abacusBinding?.viewRodTop?.layoutParams = paramsView1
            abacusBinding?.relHighLighterTop?.show()
            abacusBinding?.relHighLighterBottom?.hide()
            abacusBinding?.linearTopArrows?.show()
            abacusBinding?.arrowDownRodTop?.hide()
            abacusBinding?.arrowUpRodTop?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("5",it.abacusTop,it.abacusBottom,totalLength = 9)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.relHighLighterTop , Direction.LEFT,R.layout.layout_tip_abacus_rod1_top_sub,object : IntroProvider.IntroCloseClickListener {
                    override fun onIntroCloseClick() {
                        abacusBinding?.relHighLighterTop?.hide()
                        resetAbacusLighter()
                    }
                })
            }
        }
    }

    private fun resetAbacusLighter() {
        lighter = Lighter.with(binding.root as ViewGroup)
        abacusBinding?.let {
            IntroProvider.abacusRodIntro(lighter, it.ivReset,Direction.LEFT,R.layout.layout_tip_abacus_reset,object : IntroProvider.IntroCloseClickListener {
                override fun onIntroCloseClick() {
                    AbacusUtils.setNumber("0",it.abacusTop,it.abacusBottom,totalLength = 9)
                    prefManager.setCustomParamBoolean(AppConstants.Settings.isFreeModeTourWatch, true)
                    setTourVisibility()
                    isTourPageRunning = false
                }
            })
        }
    }
}
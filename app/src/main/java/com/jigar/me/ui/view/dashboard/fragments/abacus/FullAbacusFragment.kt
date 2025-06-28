package com.jigar.me.ui.view.dashboard.fragments.abacus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.databinding.ContentAbacusDirectionsBinding
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
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.isNetworkAvailable
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setAbacusResetShakeAnimation
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.samlss.lighter.IntroProvider
import me.samlss.lighter.Lighter
import me.samlss.lighter.parameter.Direction
import java.util.Random
import java.util.UnknownFormatConversionException
import androidx.navigation.findNavController


@AndroidEntryPoint
class FullAbacusFragment : BaseFragment(), ToddlerRangeDialog.ToddlerRangeDialogInterface,
    AbacusMasterBeadShiftListener,
    OnAbacusValueChangeListener {
    private lateinit var binding: FragmentFullAbacusBinding
    private var abacusBinding: FragmentAbacusSubKidBinding? = null
    private lateinit var themeContent : AbacusContent
    private var abacusTotalColumns: Int = 13
    private var values: Float = 1.0F
    private var valuesFinal: Float = 1.0F
    private var random_min: Float = 1F
    private var random_max: Float = 1000F
    private var total_count: Int = 1

    private var currentSumVal = 0L
    private var isTourPageRunning = false
    private var isResetRunning = false
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
        mNavController = requireActivity().findNavController(R.id.nav_host_fragment)
    }

    private fun initViews() {
        with(prefManager){
            binding.spaceNotch.layoutParams.width = getCustomParamInt(AppConstants.NOTCH_HEIGHT,0)

            abacusTotalColumns = getCustomParamInt(AppConstants.Settings.AbacusMaxColumn,13)
            if (getCustomParam(AppConstants.Settings.SW_Random,"") != "Y") {
                values = getCustomParamFloat(AppConstants.Settings.Toddler_No,1.0F)
                if (values > 9999999) {
                    values = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1.0F)
                }
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000000
                }else{
                    values
                }
            } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                values = genrateRandom()
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000000
                }else{
                    values
                }
            }
        }
        setSwitches()
        binding.swDecimalMode.isChecked = prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode, "N") == "Y"
    }

    private fun initListener() = with(binding){
        cardBack.onClick { mNavController.navigateUp() }
        txtShowTour.onClick { setThemeLighterTopBeads() }
        swRandom.onClick { switchRandomClick() }
        swReset.onClick { switchResetClick() }
        swFreeMode.onClick { switchFreeMode() }
//        swDecimalMode.onClick { switchDecimalMode() }
        txtRange.onClick { rangeClick() }
    }
    private fun resetClick() {
        if (!isResetRunning) {
            abacusBinding?.ivReset?.setAbacusResetShakeAnimation(true)
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

    private fun switchFreeMode() {
        if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") == "Y") {
            prefManager.setCustomParam(AppConstants.Settings.SW_FreeMode, "N")
            binding.swFreeMode.isChecked = false
        } else {
            prefManager.setCustomParam(AppConstants.Settings.SW_FreeMode, "Y")
            binding.swFreeMode.isChecked = true
        }
        setSwitches()
    }
//    private fun switchDecimalMode() {
//        with(prefManager){
//            if (getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
//                prefManager.setCustomParam(AppConstants.Settings.SW_DecimalMode, "N")
//                binding.swDecimalMode.isChecked = false
//            } else {
//                setCustomParam(AppConstants.Settings.SW_DecimalMode, "Y")
//                binding.swDecimalMode.isChecked = true
//            }
//            with(prefManager){
//                if (getCustomParam(AppConstants.Settings.SW_Random,"") != "Y") {
//                    values = getCustomParamFloat(AppConstants.Settings.Toddler_No,1.0F)
//                    if (values > 9999999) {
//                        values = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1.0F)
//                    }
//                    valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
//                        values / 1000000
//                    }else{
//                        values
//                    }
//                } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
//                    random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
//                    random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
//                    values = genrateRandom().toFloat()
//                    valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
//                        values / 1000000
//                    }else{
//                        values
//                    }
//                }
//            }
//            setNumberValue()
//            if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") != "Y") {
//                goToNextValue()
//            }
//            setAbacus()
//        }
//    }

    private fun rangeClick() {
        ToddlerRangeDialog.showPopup(requireActivity(),
            prefManager.getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F).toInt().toString(),
            (prefManager.getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)-1).toInt().toString(),this)
    }

    private fun switchRandomClick() {
        with(prefManager){
            if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                setCustomParam(AppConstants.Settings.SW_Random,"N")
            } else {
                setCustomParam(AppConstants.Settings.SW_Random,"Y")
            }
            setSwitches()
        }

    }

    private fun setAbacus() {
        with(prefManager){
            setCustomParam(AppConstants.Settings.TheamTempView,getCustomParam(AppConstants.Settings.Theam,AppConstants.Settings.theam_Default))
            theme = getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
        }

        binding.linearAbacus.removeAllViews()
        binding.linearAbacusFreeMode.removeAllViews()
        abacusBinding = FragmentAbacusSubKidBinding.inflate(layoutInflater, null, false)
        abacusBinding?.imgDot1?.show()
        abacusBinding?.imgDot4?.show()
        abacusBinding?.imgDot7?.show()
        abacusBinding?.imgDot10?.show()
        abacusBinding?.imgDot13?.show()
        abacusBinding?.linearNumber?.show()

        val abacusBeadType = if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"){
            binding.linearAbacusFreeMode.addView(abacusBinding?.root)
            binding.linearAbacusFreeMode.show()
            binding.linearAbacus.hide()
            abacusBinding?.imgKidsTop?.invisible()
            abacusBinding?.viewNumbers?.show()
            abacusBinding?.viewNumbersBottom?.show()
            AbacusBeadType.FreeMode
        }else{
            abacusBinding?.imgKidsTop?.show()
            binding.linearAbacus.addView(abacusBinding?.root)
            binding.linearAbacus.show()
            binding.linearAbacusFreeMode.hide()
            abacusBinding?.viewNumbersBottom?.hide()
            AbacusBeadType.FullMode
        }
        themeContent  = DataProvider.findAbacusThemeType(requireContext(),theme,abacusBeadType)

        abacusBinding?.ivReset?.onClick {
            abacusBinding?.viewDirection?.hide()
            resetClick()

            if (prefManager.getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") != "Y") {
                abacusBinding?.tvCurrentVal?.text = "0"
                goToNextValue()
            }
        }

        abacusBinding?.rlAbacusMain?.setBackgroundResource(themeContent.abacusFrame135)
        abacusBinding?.ivDivider?.setBackgroundColor(ContextCompat.getColor(requireContext(),themeContent.dividerColor1))
        themeContent.txtColor?.let {
            abacusBinding?.tvCurrentVal?.setTextColor(ContextCompat.getColor(requireContext(),it))
        }
        themeContent.resetBtnColor8.let {
            abacusBinding?.imgDot1?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot4?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot7?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot7?.layoutParams?.width = 3.dp
            abacusBinding?.imgDot7?.layoutParams?.height = 3.dp
            abacusBinding?.imgDot10?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.imgDot13?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            abacusBinding?.ivReset?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivRight?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            abacusBinding?.ivLeft?.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        abacusBinding?.abacusTop?.setNoOfRowAndBeads(0, abacusTotalColumns, 1,abacusBeadType,6)
        abacusBinding?.abacusBottom?.setNoOfRowAndBeads(0, abacusTotalColumns, 4,abacusBeadType,6)
        abacusBinding?.abacusTop?.onBeadShiftListener = this@FullAbacusFragment
        abacusBinding?.abacusBottom?.onBeadShiftListener = this@FullAbacusFragment

        if (!prefManager.getCustomParamBoolean(AppConstants.Settings.isFreeModeTourWatch, false)) {
            setThemeLighterTopBeads()
        }
    }

    private fun setSwitches() {
        with(prefManager){
            binding.swFreeMode.isChecked = getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"
            if (getCustomParam(AppConstants.Settings.SW_FreeMode, "Y") == "Y"){
                binding.txtShowTour.show()
            }else{
                binding.txtShowTour.hide()
            }
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
            binding.txtAbacus.text = setValues
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
                binding.swReset.hide()
                binding.txtRange.hide()

                binding.txtRandom.hide()
                binding.txtRangeLable.hide()
                binding.txtResetEveryTime.hide()

                binding.txtAbacus.hide()
                is1stTime = true
            }else{
                binding.swRandom.show()
                binding.swReset.show()
                binding.txtRange.show()

                binding.txtRandom.show()
                binding.txtRangeLable.show()
                binding.txtResetEveryTime.show()

                binding.txtAbacus.show()

                binding.swRandom.isChecked = getCustomParam(AppConstants.Settings.SW_Random, "") == "Y"
                binding.swReset.isChecked = getCustomParam(AppConstants.Settings.SW_Reset, "") == "Y"
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                try {
                    binding.txtRange.text = String.format(requireContext().getString(R.string.txt_From_to),random_min.toInt(),(random_max - 1).toInt())
                } catch (e: UnknownFormatConversionException) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} ${random_min.toInt()} ${requireContext().getString(R.string.txt_To)} ${(random_max - 1).toInt()}"
                } catch (e: Exception) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} ${random_min.toInt()} ${requireContext().getString(R.string.txt_To)} ${(random_max - 1).toInt()}"
                }
                if (getCustomParam(AppConstants.Settings.SW_Random, "") != "Y") {
                    values = getCustomParamFloat(AppConstants.Settings.Toddler_No, random_min)
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
            setSwitches()
            generateNewNumber()
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
        val abacusValue = sum1.toString()
        var newValue = abacusValue
        if (abacusValue.length != abacusTotalColumns){
            for (i in 1..(abacusTotalColumns - abacusValue.length)) {
                newValue = "0$newValue"
            }
        }
        val sb = StringBuilder(newValue)
        sb.insert(7, ".")
        newValue = sb.toString()
        val splitResult = newValue.split(".")
        if (splitResult.size == 2){

            with(prefManager){
                val value1 = splitResult[0].toLong()
                val value2 = splitResult[1]
                val sum = if (value2.toLong() > 0){
                    abacusBinding?.tvCurrentVal?.text = "$value1.$value2"
                    "$value1.$value2"
                }else{
                    abacusBinding?.tvCurrentVal?.text = "$value1"
                    "$value1"
                }
                if (getCustomParam(AppConstants.Settings.SW_FreeMode,"Y") != "Y") {
                    if (sum == (valuesFinal.toInt()).toString()) {
                        generateNewNumber()
                    } else {
                        clearDirection()
                        addDirection(value1.toInt(),valuesFinal.toInt())
                        if (value2.toInt() > 0){
                            addDirectionDivisor(0,value2.toInt())
                        }
                    }
                }
            }
        }else{
            mNavController.navigateUp()
        }

    }

    private fun generateNewNumber() {
        with(prefManager){

            generateValue()
            total_count = getCustomParamInt(AppConstants.Settings.Toddler_No_Count,1)
            total_count++
            if (total_count > 9999999) {
                total_count = 1
            }
            setCustomParamInt(AppConstants.Settings.Toddler_No_Count,total_count)
            lifecycleScope.launch {
                delay(300)
                if (getCustomParam(AppConstants.Settings.SW_Reset,"") == "Y") {
                    abacusBinding?.tvCurrentVal?.text = "0"
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
                    values / 1000000
                }else{
                    values
                }
            } else if (getCustomParam(AppConstants.Settings.SW_Random,"") == "Y") {
                random_min = getCustomParamFloat(AppConstants.Settings.SW_Range_min,1F)
                random_max = getCustomParamFloat(AppConstants.Settings.SW_Range_max,101F)
                try {
                    binding.txtRange.text = String.format(requireContext().getString(R.string.txt_From_to),random_min.toInt(),(random_max - 1).toInt())
                } catch (e: UnknownFormatConversionException) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} ${random_min.toInt()} ${requireContext().getString(R.string.txt_To)} ${(random_max - 1).toInt()}"
                } catch (e: Exception) {
                    binding.txtRange.text = "${requireContext().getString(R.string.txt_From)} ${random_min} ${requireContext().getString(R.string.txt_To)} ${(random_max - 1).toInt()}"
                }
                values = genrateRandom().toFloat()
                valuesFinal = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
                    values / 1000000
                }else{
                    values
                }
            } else {

            }
        }
    }

    private fun goToNextValue() {
        // TODO
//        values = 268.56F
        val setValues = if (prefManager.getCustomParam(AppConstants.Settings.SW_DecimalMode,"N") == "Y") {
            valuesFinal.toString()
        }else{
            valuesFinal.toLong().toString()
        }
        val speakText = try {
            setValues
        } catch (e: UnknownFormatConversionException) {
            "${requireContext().getString(R.string.txt_set)} $setValues"
        } catch (e: Exception) {
            "${requireContext().getString(R.string.txt_set)} $setValues"
        }
        try {
            clearDirection()
            val fromValue = if (prefManager.getCustomParam(AppConstants.Settings.SW_Reset,"") == "Y") {
                "0"
            }else{
                abacusBinding?.tvCurrentVal?.text.toString()
            }
            if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)){
                lifecycleScope.launch {
                    delay(Constants.DELAY_DIRECTION)
                    addDirection(if (fromValue.isEmpty()) 0 else fromValue.toInt(),setValues.toInt())
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        speakOut(speakText)
    }

    override fun onAbacusValueSubmit(sum: Long) {

    }

    override fun onAbacusSubmitValue(userAnswer : String) = Unit
    override fun onAbacusValueDotReset() {
        resetAbacus()
    }

    private fun resetAbacus() {
        abacusBinding?.abacusTop?.reset()
        abacusBinding?.abacusBottom?.reset()
    }

    // TODO abacus tour
    private fun setThemeLighterTopBeads() {
        abacusBinding?.viewDirection?.hide()

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
            paramsView1.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1
            abacusBinding?.relHighLighter?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("9000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
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
            paramsView1.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 7) + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            lighter = Lighter.with(binding.root as ViewGroup)
            abacusBinding?.let {
                AbacusUtils.setNumber("90000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1,Direction.RIGHT,R.layout.layout_tip_abacus_rod2,object : IntroProvider.IntroCloseClickListener {
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
            paramsView2 .marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 8) + (themeContent.beadSpace / 2)
            paramsView2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView2

            lighter = Lighter.with(binding.root as ViewGroup)
            abacusBinding?.let {
                AbacusUtils.setNumber("900000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1,Direction.RIGHT,R.layout.layout_tip_abacus_rod3,object : IntroProvider.IntroCloseClickListener {
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
            paramsView1.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("9000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
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
            paramsView1.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("99000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.RIGHT,R.layout.layout_tip_abacus_column2,object : IntroProvider.IntroCloseClickListener {
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
            paramsView1.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            paramsView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            abacusBinding?.viewRod1?.layoutParams = paramsView1

            abacusBinding?.let {
                AbacusUtils.setNumber("999000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
                delay(300)
                IntroProvider.abacusRodIntro(lighter, it.viewRod1, Direction.RIGHT,R.layout.layout_tip_abacus_column3,object : IntroProvider.IntroCloseClickListener {
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
            abacusBinding?.viewRodBottom?.layoutParams = paramsView1

            val paramsView2 = abacusBinding?.relHighLighterBottom?.layoutParams as ConstraintLayout.LayoutParams
            paramsView2.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            abacusBinding?.relHighLighterBottom?.layoutParams = paramsView2

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
            abacusBinding?.viewRodTop?.layoutParams = paramsView1

            val paramsView2 = abacusBinding?.relHighLighterTop?.layoutParams as ConstraintLayout.LayoutParams
            paramsView2.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            abacusBinding?.relHighLighterTop?.layoutParams = paramsView2

            abacusBinding?.relHighLighterBottom?.hide()
            abacusBinding?.relHighLighterTop?.show()
            abacusBinding?.arrowDownRodTop?.show()
            abacusBinding?.arrowUpRodTop?.hide()
            abacusBinding?.linearTopArrows?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("0",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
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
            abacusBinding?.viewRodBottom?.layoutParams = paramsView1

            val paramsView2 = abacusBinding?.relHighLighterBottom?.layoutParams as ConstraintLayout.LayoutParams
            paramsView2.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            abacusBinding?.relHighLighterBottom?.layoutParams = paramsView2

            abacusBinding?.relHighLighterTop?.hide()
            abacusBinding?.relHighLighterBottom?.show()
            abacusBinding?.linearBottomArrows?.show()
            abacusBinding?.arrowDownRodBottom?.show()
            abacusBinding?.arrowUpRodBottom?.hide()

            abacusBinding?.let {
                AbacusUtils.setNumber("4000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
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
            abacusBinding?.viewRodTop?.layoutParams = paramsView1

            val paramsView2 = abacusBinding?.relHighLighterTop?.layoutParams as ConstraintLayout.LayoutParams
            paramsView2.marginEnd = ((themeContent.beadWidth + themeContent.beadSpace) * 6) + (themeContent.beadSpace / 2)
            abacusBinding?.relHighLighterTop?.layoutParams = paramsView2

            abacusBinding?.relHighLighterTop?.show()
            abacusBinding?.relHighLighterBottom?.hide()
            abacusBinding?.linearTopArrows?.show()
            abacusBinding?.arrowDownRodTop?.hide()
            abacusBinding?.arrowUpRodTop?.show()

            abacusBinding?.let {
                AbacusUtils.setNumber("5000000",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
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
                    AbacusUtils.setNumber("0",it.abacusTop,it.abacusBottom,totalLength = abacusTotalColumns)
                    prefManager.setCustomParamBoolean(AppConstants.Settings.isFreeModeTourWatch, true)
                    isTourPageRunning = false

                    setFreeMode()
                }
            })
        }
    }

    // for draw direction
    private fun clearDirection() {
        abacusBinding?.viewDirection?.removeAllViews()
    }
    private fun addDirectionDivisor(toValue: Int, fromValue : Int) {
        if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)){
            var rods = fromValue.toString().length
            if (toValue.toString().length > rods){
                rods = toValue.toString().length
            }

            if (fromValue != toValue){
                val rodMovement = ExamProvider.calculateRodMovements(requireContext(),from = fromValue, to = toValue, rods = rods)
                if (rodMovement.isNotNullOrEmpty()){
                    val extraHeight = resources.getDimension(R.dimen.height_extra).toInt()
                    val beamHeight = resources.getDimension(R.dimen.four).toInt()
                    val topBeadsHeight = themeContent.beadHeight * 2
                    val topTotalHeightAlways = extraHeight + beamHeight + topBeadsHeight

                    val bottomBeadsHeight = themeContent.beadHeight * 5
                    val bottomTotalHeightAlways = extraHeight + beamHeight + bottomBeadsHeight
//                        val decimalBeadsWidth = (themeContent.beadWidth * 6) + (themeContent.beadSpace * 6) + (themeContent.beadWidth * 0.75).toInt()
                    val decimalBeadsWidth = (themeContent.beadWidth * 0.75).toInt()

                    rodMovement.map { rodData ->
                        if (rodData.movement.lowerUp > 0 || rodData.movement.lowerDown > 0 || rodData.movement.upperDown || rodData.movement.upperUp){
                            val directionBinding = ContentAbacusDirectionsBinding.inflate(layoutInflater, null, false)
                            with(directionBinding){
                                val rightSpace = decimalBeadsWidth + (themeContent.beadWidth * rodData.rodIndex) + (themeContent.beadSpace / 2) + (rodData.rodIndex * themeContent.beadSpace)
                                val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                                layoutParams.marginEnd = rightSpace
                                directionBinding.conDirection.layoutParams = layoutParams

                                if (rodData.movement.lowerUp > 0){
                                    linearDirectionBottom.show()
                                    imgUpArrowBottom.show()
                                    val heightOldBeads = rodData.movement.lowerOldValue * themeContent.beadHeight
                                    val bottomRemainBeadHeight = ((4 - rodData.movement.lowerUp) * themeContent.beadHeight)+extraHeight - heightOldBeads
                                    linearDirectionBottom.setPadding(0,topTotalHeightAlways + heightOldBeads,0,bottomRemainBeadHeight)
                                }else if (rodData.movement.lowerDown > 0){
                                    linearDirectionBottom.show()
                                    imgDownArrowBottom.show()
                                    val heightOldBeads = (rodData.movement.lowerOldValue - rodData.movement.lowerDown) * themeContent.beadHeight
                                    val bottomRemainBeadHeight = if (rodData.movement.lowerOldValue == 4) { extraHeight }else{ extraHeight + ((4 - rodData.movement.lowerOldValue) * themeContent.beadHeight)}
                                    linearDirectionBottom.setPadding(0,topTotalHeightAlways + heightOldBeads,0,bottomRemainBeadHeight)
                                }
                                if (rodData.movement.upperDown){
                                    linearDirectionTop.show()
                                    imgDownArrowTop.show()
                                    linearDirectionTop.setPadding(0,extraHeight,0,bottomTotalHeightAlways)
                                }else if (rodData.movement.upperUp){
                                    linearDirectionTop.show()
                                    imgUpArrowTop.show()
                                    linearDirectionTop.setPadding(0,extraHeight,0,bottomTotalHeightAlways)
                                }
                            }
                            abacusBinding?.viewDirection?.addView(directionBinding.root)
                        }
                    }
                    if ((abacusBinding?.viewDirection?.childCount?:0) > 0){
                        abacusBinding?.viewDirection?.show()
                    }else{
                        abacusBinding?.viewDirection?.hide()
                    }
                }else{

                }
            }else{
            }
        }
    }
    private fun addDirection(fromValue: Int, toValue: Int) {
        if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_direction, true)){


            var rods = fromValue.toString().length
            if (toValue.toString().length > rods){
                rods = toValue.toString().length
            }

            val rodMovement = ExamProvider.calculateRodMovements(requireContext(),from = fromValue, to = toValue, rods = rods)
            val extraHeight = resources.getDimension(R.dimen.height_extra).toInt()
            val beamHeight = resources.getDimension(R.dimen.four).toInt()
            val topBeadsHeight = themeContent.beadHeight * 2
            val topTotalHeightAlways = extraHeight + beamHeight + topBeadsHeight

            val bottomBeadsHeight = themeContent.beadHeight * 5
            val bottomTotalHeightAlways = extraHeight + beamHeight + bottomBeadsHeight
            val decimalBeadsWidth = (themeContent.beadWidth * 6) + (themeContent.beadSpace * 6) + (themeContent.beadWidth * 0.75).toInt()

            rodMovement.map { rodData ->
                if (rodData.movement.lowerUp > 0 || rodData.movement.lowerDown > 0 || rodData.movement.upperDown || rodData.movement.upperUp){
                    val directionBinding = ContentAbacusDirectionsBinding.inflate(layoutInflater, null, false)
                    with(directionBinding){
                        val rightSpace = decimalBeadsWidth + (themeContent.beadWidth * rodData.rodIndex) + (themeContent.beadSpace / 2) + (rodData.rodIndex * themeContent.beadSpace)
                        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                        layoutParams.marginEnd = rightSpace
                        directionBinding.conDirection.layoutParams = layoutParams

                        if (rodData.movement.lowerUp > 0){
                            linearDirectionBottom.show()
                            imgUpArrowBottom.show()
                            val heightOldBeads = rodData.movement.lowerOldValue * themeContent.beadHeight
                            val bottomRemainBeadHeight = ((4 - rodData.movement.lowerUp) * themeContent.beadHeight)+extraHeight - heightOldBeads
                            linearDirectionBottom.setPadding(0,topTotalHeightAlways + heightOldBeads,0,bottomRemainBeadHeight)
                        }else if (rodData.movement.lowerDown > 0){
                            linearDirectionBottom.show()
                            imgDownArrowBottom.show()
                            val heightOldBeads = (rodData.movement.lowerOldValue - rodData.movement.lowerDown) * themeContent.beadHeight
                            val bottomRemainBeadHeight = if (rodData.movement.lowerOldValue == 4) { extraHeight }else{ extraHeight + ((4 - rodData.movement.lowerOldValue) * themeContent.beadHeight)}
                            linearDirectionBottom.setPadding(0,topTotalHeightAlways + heightOldBeads,0,bottomRemainBeadHeight)
                        }
                        if (rodData.movement.upperDown){
                            linearDirectionTop.show()
                            imgDownArrowTop.show()
                            linearDirectionTop.setPadding(0,extraHeight,0,bottomTotalHeightAlways)
                        }else if (rodData.movement.upperUp){
                            linearDirectionTop.show()
                            imgUpArrowTop.show()
                            linearDirectionTop.setPadding(0,extraHeight,0,bottomTotalHeightAlways)
                        }
                    }
                    abacusBinding?.viewDirection?.addView(directionBinding.root)
                }
            }
            if ((abacusBinding?.viewDirection?.childCount?:0) > 0){
                abacusBinding?.viewDirection?.show()
            }else{
                abacusBinding?.viewDirection?.hide()
            }
        }
    }

}
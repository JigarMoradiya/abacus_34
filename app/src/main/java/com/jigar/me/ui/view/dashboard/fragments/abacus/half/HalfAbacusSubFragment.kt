package com.jigar.me.ui.view.dashboard.fragments.abacus.half

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.databinding.FragmentAbacusSubKidBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterBeadShiftListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterCompleteListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterView
import com.jigar.me.ui.view.base.abacus.OnAbacusValueChangeListener
import com.jigar.me.utils.*
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setAbacusResetShakeAnimation
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HalfAbacusSubFragment : BaseFragment(), AbacusMasterBeadShiftListener {
    private lateinit var binding: FragmentAbacusSubKidBinding
    // Settings Constants
    private var isDisplayAbacusNumber = true

    private var abacus_type = 0 // 0 = sum-sub-single  1 = multiplication 2 = divide
    private var abacusTotalColumns = 0
    private var noOfDecimalPlace = 0

    // abacus move
    private var isResetRunning: Boolean = false
    private var currentSumVal = 0L
    private var resetX: Float = 0f
    var questionLength = 0
    var finalAnsLength: Int = 0

    // for division
    private var topSelectedPositions: ArrayList<Int> = arrayListOf()
    private var bottomSelectedPositions: ArrayList<Int> = arrayListOf()

    private var onAbacusValueChangeListener: OnAbacusValueChangeListener? = null

    fun newInstance(column: Int, noOfDecimalPlace: Int, abacus_type: Int): HalfAbacusSubFragment {
        val fragment = HalfAbacusSubFragment()
        fragment.abacusTotalColumns = column
        fragment.noOfDecimalPlace = noOfDecimalPlace
        fragment.abacus_type = abacus_type
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAbacusSubKidBinding.inflate(inflater, container, false)
        initViews()
        initListener()
        return binding.root
    }

    private fun initViews() {
        isDisplayAbacusNumber = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

        binding.imgDot1.show()
        binding.imgDot4.show()
        binding.imgDot7.show()
        binding.imgDot10.show()
        binding.imgDot13.show()
        binding.linearRodName.show()


        val theme = prefManager.getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
        val themeContent = DataProvider.findAbacusThemeType(requireContext(),theme,AbacusBeadType.AbacusPrecise)
        themeContent.abacusFrame135.let { binding.rlAbacusMain.setBackgroundResource(it) }
        themeContent.dividerColor1.let { binding.ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it)) }
        themeContent.resetBtnColor8.let {
            binding.imgDot1.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.imgDot4.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.imgDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.imgDot7.layoutParams?.width = 3.dp
            binding.imgDot7.layoutParams?.height = 3.dp
            binding.imgDot10.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.imgDot13.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            binding.ivReset.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.ivRight.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.ivLeft.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    private fun initListener() {
        binding.ivReset.onClick { onResetClick() }
        binding.resettoContinue.onClick { onResetClick() }
    }
    fun setOnAbacusValueChangeListener(onAbacusValueChangeListener: OnAbacusValueChangeListener?) {
        this.onAbacusValueChangeListener = onAbacusValueChangeListener
    }

    // TODO abacus Move logic
    override fun onResume() {
        if (isAdded) {
            setBead()
        }
        if (abacus_type == 2) {
            setSelectedPositions(topSelectedPositions, bottomSelectedPositions, null)
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.abacusTop.stop()
        binding.abacusBottom.stop()
    }

    private fun setBead() {
//        val isHideTable = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_hide_table, false)
//        if (isHideTable || abacusTotalColumns <= 5){
//            if (prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_left_hand, true)){
//                binding.imgKidRight.show()
//                binding.imgKidHandRight.show()
//                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
//                params.setMargins(0)
//                binding.relAbacus.layoutParams = params
//
//                if (DataProvider.generateIndex() == 0){
//                    binding.imgKidRight.setImageResource(R.drawable.ic_boy_abacus_right)
//                    binding.imgKidHandRight.setImageResource(R.drawable.ic_boy_abacus_hand_right)
//                }else{
//                    binding.imgKidRight.setImageResource(R.drawable.ic_girl_abacus_right)
//                    binding.imgKidHandRight.setImageResource(R.drawable.ic_girl_abacus_hand_right)
//                }
//            }else{
//                binding.imgKidLeft.show()
//                binding.imgKidHandLeft.show()
//
//                if (DataProvider.generateIndex() == 0){
//                    binding.imgKidLeft.setImageResource(R.drawable.ic_girl_abacus_left)
//                    binding.imgKidHandLeft.setImageResource(R.drawable.ic_girl_abacus_hand_left)
//                }else{
//                    binding.imgKidLeft.setImageResource(R.drawable.ic_boy_abacus_left)
//                    binding.imgKidHandLeft.setImageResource(R.drawable.ic_boy_abacus_hand_left)
//                }
//            }
//        }else{
//            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
//            params.setMargins(0)
//            binding.relAbacus.layoutParams = params
//        }


//        if (abacus_type == 2){
//            binding.abacusTop.setNoOfRowAndBeads(0, abacusTotalColumns, 1,AbacusBeadType.AbacusPrecise,unitRodPosition = 6,noOfColumnUsed = 6+questionLength)
//            binding.abacusBottom.setNoOfRowAndBeads(0, abacusTotalColumns, 4,AbacusBeadType.AbacusPrecise,unitRodPosition = 6,noOfColumnUsed = 6+questionLength)
//        }else{
            binding.abacusTop.setNoOfRowAndBeads(0, abacusTotalColumns, 1,AbacusBeadType.AbacusPrecise,unitRodPosition = 6)
            binding.abacusBottom.setNoOfRowAndBeads(0, abacusTotalColumns, 4,AbacusBeadType.AbacusPrecise,unitRodPosition = 6)
//        }

        binding.abacusTop.onBeadShiftListener = this
        binding.abacusBottom.onBeadShiftListener = this
    }

    override fun onBeadShift(abacusView: AbacusMasterView, rowValue: IntArray) {
        val singleBeadWeight = abacusView.singleBeadValue
        var accumulator = 0L

        if (noOfDecimalPlace == 0) {
            when (abacusView.id) {
                R.id.abacusTop -> if (binding.abacusBottom.engine != null && isVisible) {
                    val bottomVal = binding.abacusBottom.engine!!.getValue()
                    var i = 0
                    while (i < rowValue.size) {
                        accumulator *= 10
                        val rval = rowValue[i]
                        if (rval > -1) accumulator += rval * singleBeadWeight
                        i++
                    }
                    currentSumVal = (bottomVal + accumulator)
                    Log.e("jigarLogs","abacusTop currentSumVal = "+currentSumVal)
                    if (abacus_type == 0 || abacus_type == 1 || abacus_type == 2) {
                        setCurrentValue(currentSumVal.toString())
                        onAbacusValueChangeListener?.onAbacusValueChange(
                            abacusView,
                            currentSumVal
                        )
                    }
                }
                R.id.abacusBottom -> if (binding.abacusTop.engine != null && isVisible) {
                    val topVal = binding.abacusTop.engine!!.getValue()
                    var i = 0
                    while (i < rowValue.size) {
                        accumulator *= 10
                        val rval = rowValue[i]
                        if (rval > -1) accumulator += rval * singleBeadWeight
                        i++
                    }
                    currentSumVal = (topVal + accumulator)
                    Log.e("jigarLogs","abacusBottom currentSumVal = "+currentSumVal)
                    if (abacus_type == 0 || abacus_type == 1 || abacus_type == 2) {
                        setCurrentValue(currentSumVal.toString())
                        try {
                            onAbacusValueChangeListener?.onAbacusValueChange(
                                abacusView,
                                currentSumVal
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
//            if (abacus_type == 2) {
//                /*common code to calculate sumvalue*/
//                var currentAns: String = currentSumVal.toString()
//                Log.e("jigarLogs","onBeadShift currentAns = "+currentAns)
//                val iterationCount = questionLength + finalAnsLength - 1 - currentAns.length
//                for (i in 0 until iterationCount) {
//                    currentAns = "0$currentAns"
//                }
//                if (questionLength + finalAnsLength - 1 <= currentAns.length) {
//                    try {
//                        val right = Integer.valueOf(
//                            currentAns.substring(
//                                currentAns.length - questionLength,
//                                currentAns.length
//                            )
//                        )
//                        val left = Integer.valueOf(currentAns.substring(0, finalAnsLength))
//                        setCurrentValue("$left < $right")
//                    } catch (e: Exception) {
//                    }
//                } else {
//                    setCurrentValue(currentSumVal.toString())
//                }
//                onAbacusValueChangeListener?.onAbacusValueChange(abacusView, currentSumVal)
//            }
        } else {
            when (abacusView.id) {
                R.id.abacusTop -> if (binding.abacusBottom.engine != null) {
                    val bottomVal = binding.abacusBottom.engine!!.getValue()
                    var i = 0
                    while (i < rowValue.size) {
                        accumulator *= 10
                        val rval = rowValue[i]
                        if (rval > -1) accumulator += rval * singleBeadWeight
                        i++
                    }
                    val intSumVal = bottomVal + accumulator
                    var strCurVal = intSumVal.toString()
                    strCurVal = if (strCurVal.length < noOfDecimalPlace) {
                        val preFix =
                            "0." + String(CharArray(noOfDecimalPlace - strCurVal.length)).replace(
                                '\u0000',
                                '0')
                        preFix + strCurVal
                    } else if (strCurVal.length == noOfDecimalPlace) {
                        "0.$strCurVal"
                    } else {
                        strCurVal.substring(
                            0,strCurVal.length - noOfDecimalPlace) + "." + strCurVal.substring(
                            strCurVal.length - noOfDecimalPlace,
                            strCurVal.length)
                    }
                    currentSumVal = intSumVal
                    setCurrentValue(strCurVal)
                    try {
                        onAbacusValueChangeListener?.onAbacusValueChange(abacusView, currentSumVal)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.abacusBottom -> if (binding.abacusTop.engine != null) {
                    val topVal = binding.abacusTop.engine!!.getValue()
                    var i = 0
                    while (i < rowValue.size) {
                        accumulator *= 10
                        val rval = rowValue[i]
                        if (rval > -1) accumulator += rval * singleBeadWeight
                        i++
                    }
                    val intSumVal = topVal + accumulator
                    var strCurVal = intSumVal.toString()
                    strCurVal = if (strCurVal.length < noOfDecimalPlace) {
                        val preFix =
                            "0." + String(CharArray(noOfDecimalPlace - strCurVal.length)).replace(
                                '\u0000',
                                '0'
                            )
                        preFix + strCurVal
                    } else if (strCurVal.length == noOfDecimalPlace) {
                        "0.$strCurVal"
                    } else {
                        strCurVal.substring(
                            0,
                            strCurVal.length - noOfDecimalPlace
                        ) + "." + strCurVal.substring(
                            strCurVal.length - noOfDecimalPlace,
                            strCurVal.length
                        )
                    }
                    currentSumVal = intSumVal
                    setCurrentValue(strCurVal)
                    try {
                        onAbacusValueChangeListener?.onAbacusValueChange(abacusView, currentSumVal)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun resetAbacus() {
        binding.abacusTop.reset()
        binding.abacusBottom.reset()
        showResetToContinue(false)
    }

    fun resetButtonEnable(isEnable: Boolean) {
        binding.ivReset.isEnabled = isEnable
        binding.ivReset.isClickable = isEnable
    }
    fun showResetToContinue(type: Boolean) {
        if (type) {
            binding.resettoContinue.show()
            startTimerForToolTips()
        } else {
            binding.resettoContinue.hide()
        }
    }

    private fun startTimerForToolTips() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded && isResumed){
                binding.ivReset.setAbacusResetShakeAnimation()
            }
        },2500)
    }

    private fun onResetClick() {
        if (!isResetRunning) {
//            isResetRunning = true
//            binding.ivReset.y = 0f
//
//            binding.ivReset.animate().setDuration(200)
//                .translationYBy((binding.ivReset.height / 2).toFloat()).withEndAction {
//                    binding.ivReset.animate().setDuration(200)
//                        .translationYBy((-binding.ivReset.height / 2).toFloat()).withEndAction {
//                            isResetRunning = false
//                        }.start()
//                }.start()

            binding.ivReset.setAbacusResetShakeAnimation(true)
            onAbacusValueChangeListener?.onAbacusValueDotReset()
        }
    }

    // TODO for Division
    fun setSelectedPositions(
        topSelectedPositions: ArrayList<Int>,
        bottomSelectedPositions: ArrayList<Int>,
        setPositionCompleteListener: AbacusMasterCompleteListener?
    ) {
        this.topSelectedPositions = topSelectedPositions
        this.bottomSelectedPositions = bottomSelectedPositions

        if (isAdded) {
            //app was crashing if position set before update no of row count. so added this delay.
            binding.abacusBottom.post {
                binding.abacusTop.setSelectedPositions(
                    topSelectedPositions,
                    setPositionCompleteListener
                )
                binding.abacusBottom.setSelectedPositions(
                    bottomSelectedPositions,
                    setPositionCompleteListener
                )
            }
        }
    }

    fun setQuestionAndDividerLength(questionLength: Int, finalAnsLength: Int) {
        this.questionLength = questionLength
        this.finalAnsLength = finalAnsLength
//        abacusTotalColumns = questionLength + finalAnsLength - 1
//        final_column = 7
//        abacusTotalColumns = if (abacusTotalColumns == 1) 2 else abacusTotalColumns

        setAbacusRowCountAndInvalidate(abacusTotalColumns)
    }

    private fun setAbacusRowCountAndInvalidate(column: Int) {
        abacusTotalColumns = column
        if (isAdded) {
            binding.abacusTop.noOfColumn = column
            binding.abacusBottom.noOfColumn = column
        }
    }

    private fun setCurrentValue(abacusValue: String) {
//        Log.e("jigarLogsDivision","setCurrentValue abacusValue = "+abacusValue)
        when {
            isDisplayAbacusNumber -> {
                binding.tvCurrentVal.show()
                var newValue = abacusValue
                if (abacusValue.length != abacusTotalColumns){
                    for (i in 1..(abacusTotalColumns - abacusValue.length)) {
                        newValue = "0$newValue"
                    }
                }
//                Log.e("jigarLogsDivision","setCurrentValue newValue = "+newValue)
                if (abacus_type == 2){
                    val remainQuestion = newValue.replace(".","").takeLast(6).trimStart('0')
                    val answers = newValue.replace(".","").take(7).trimStart('0')
//                    val answer = newValue.take(7)
//                    val reminders = newValue.takeLast(7).take(questionLength)
                    binding.tvCurrentVal.text = (if (answers.isNullOrEmpty()){"0"}else{answers})+" < "+(if (remainQuestion.isNullOrEmpty()){"0"}else{remainQuestion})
                }else{
                    val sb = StringBuilder(newValue)
                    sb.insert(7, ".")
                    newValue = sb.toString()
                    val splitResult = newValue.split(".")
                    if (splitResult.size == 2) {
                        val value1 = splitResult[0].toLong()
                        val value2 = splitResult[1]
                        if (value2.toLong() > 0) {
                            binding.tvCurrentVal.text = "$value1.$value2"
                        } else {
                            binding.tvCurrentVal.text = "$value1"
                        }
                    }
                }

            }
            else -> {
                binding.tvCurrentVal.visibility = View.INVISIBLE
            }
        }
    }
}
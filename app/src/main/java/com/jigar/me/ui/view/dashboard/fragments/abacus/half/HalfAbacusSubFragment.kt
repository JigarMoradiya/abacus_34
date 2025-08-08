package com.jigar.me.ui.view.dashboard.fragments.abacus.half

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.local.data.ExamProvider
import com.jigar.me.databinding.ContentAbacusDirectionsBinding
import com.jigar.me.databinding.FragmentAbacusSubKidBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.view.base.abacus.AbacusMasterBeadShiftListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterCompleteListener
import com.jigar.me.ui.view.base.abacus.AbacusMasterView
import com.jigar.me.ui.view.base.abacus.OnAbacusValueChangeListener
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.dp
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import com.jigar.me.utils.extensions.setIsEnabled

@AndroidEntryPoint
class HalfAbacusSubFragment : BaseFragment(), AbacusMasterBeadShiftListener {
    private lateinit var binding: FragmentAbacusSubKidBinding
    private var root : View? = null
    private lateinit var themeContent : AbacusContent
    // Settings Constants
    private var isDisplayAbacusNumber = true
    private var isShowSubmitAnswer = true

    private var abacus_type = 0 // 0 = sum-sub-single  1 = multiplication 2 = divide
    private var abacusTotalColumns = 0
    private var noOfDecimalPlace = 0

    // abacus move
    private var isResetRunning: Boolean = false
    private var currentSumVal = 0L
    var fromValue = 0
    var questionLength = 0
    var finalAnsLength: Int = 0
    var firstQuestionForDirection: String = ""

    // for division
    private var topSelectedPositions: ArrayList<Int> = arrayListOf()
    private var bottomSelectedPositions: ArrayList<Int> = arrayListOf()

    private var onAbacusValueChangeListener: OnAbacusValueChangeListener? = null

    fun newInstance(column: Int, noOfDecimalPlace: Int, abacus_type: Int, themeContent : AbacusContent, isShowSubmitAnswer : Boolean? = false, firstQuestionForDirection : String = "0", topPositions : ArrayList<Int> = arrayListOf(), bottomPositions : ArrayList<Int> = arrayListOf()): HalfAbacusSubFragment {
        val fragment = HalfAbacusSubFragment()
        fragment.themeContent = themeContent
        fragment.firstQuestionForDirection = firstQuestionForDirection
        fragment.abacusTotalColumns = column
        fragment.noOfDecimalPlace = noOfDecimalPlace
        fragment.abacus_type = abacus_type
        fragment.isShowSubmitAnswer = isShowSubmitAnswer?:false
        fragment.topSelectedPositions = topPositions
        fragment.bottomSelectedPositions = bottomPositions
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (root == null){
            binding = FragmentAbacusSubKidBinding.inflate(inflater, container, false)
            root = binding.root
            initViews()
            initListener()
        }
        return root
    }

    private fun initViews() = with(binding){
        isDisplayAbacusNumber = prefManager.getCustomParamBoolean(AppConstants.Settings.Setting_display_abacus_number, true)

        imgDot1.show()
        imgDot4.show()
        imgDot7.show()
        imgDot10.show()
        imgDot13.show()

        setNextBtn()

        themeContent.abacusFrame135.let { rlAbacusMain.setBackgroundResource(it) }
        themeContent.dividerColor1.let {
            ivDivider.setBackgroundColor(ContextCompat.getColor(requireContext(),it))
            cardAnswerWindowBg.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),it)))
        }
        cardAnswerWindow.setCardBackgroundColor(themeContent.answerWindowBG.toColorInt())
        cardAnswerWindow.strokeColor = themeContent.answerWindowLine.toColorInt()

        themeContent.resetBtnColor8.let {
            cardAnswerWindowBg.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),it)))

            imgDot1.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            imgDot4.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            imgDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            imgDot7.layoutParams?.width = 3.dp
            imgDot7.layoutParams?.height = 3.dp
            imgDot10.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            imgDot13.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)

            ivRight.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
            ivLeft.setColorFilter(ContextCompat.getColor(requireContext(),it), android.graphics.PorterDuff.Mode.SRC_IN)
        }

        setBead()
    }

    private fun setNextBtn() = with(binding){
        if (isShowSubmitAnswer){
            txtNext.setIsEnabled(true,1f)
        }else{
            txtNext.setIsEnabled(false,0.5f)
        }
    }

    private fun initListener() = with(binding){
        txtReset.onClick {
            setNextBtn()
            onResetClick()
        }
        txtNext.onClick {
            setNextBtn()
            onSubmitAnswerClick()
        }
    }

    fun setOnAbacusValueChangeListener(onAbacusValueChangeListener: OnAbacusValueChangeListener?) {
        this.onAbacusValueChangeListener = onAbacusValueChangeListener
    }

    override fun onStop() {
        super.onStop()
        binding.abacusTop.stop()
        binding.abacusBottom.stop()
    }

    private fun setBead() {
        binding.abacusTop.setNoOfRowAndBeads(0, abacusTotalColumns, 1,AbacusBeadType.AbacusPrecise,unitRodPosition = 6)
        binding.abacusBottom.setNoOfRowAndBeads(0, abacusTotalColumns, 4,AbacusBeadType.AbacusPrecise,unitRodPosition = 6)

        binding.abacusTop.onBeadShiftListener = this
        binding.abacusBottom.onBeadShiftListener = this

        lifecycleScope.launch {
            delay(Constants.DELAY_DIRECTION)
            try {
                fromValue = 0
                if (firstQuestionForDirection.toInt() > 0){
                    addDirection(firstQuestionForDirection.toInt())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (abacus_type == 2) { // only for division
            setSelectedPositions(topSelectedPositions, bottomSelectedPositions)
        }
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
                    if (abacus_type == 0 || abacus_type == 1 || abacus_type == 2) {
                        setCurrentValue(currentSumVal.toString())
                        try {
                            onAbacusValueChangeListener?.onAbacusValueChange(abacusView, currentSumVal)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
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
        hideDirection()
    }

    fun nextButtonEnable() {
        binding.txtNext.setIsEnabled(true,1f)
        binding.txtReset.setIsEnabled(false,0.5f)
    }
    private fun onResetClick() {
        if (!isResetRunning) {
            fromValue = 0
            binding.tvCurrentVal.text = "0"
            onAbacusValueChangeListener?.onAbacusValueDotReset()
        }
    }

    private fun onSubmitAnswerClick() {
        val userAnswer = binding.tvCurrentVal.text.toString()
        fromValue = 0
        binding.tvCurrentVal.text = "0"
        onAbacusValueChangeListener?.onAbacusSubmitValue(userAnswer)
    }

    // TODO for Division
    fun setSelectedPositions(
        topSelectedPositions: ArrayList<Int>,
        bottomSelectedPositions: ArrayList<Int>,
        setPositionCompleteListener: AbacusMasterCompleteListener? = null
    ) {
        this.topSelectedPositions = topSelectedPositions
        this.bottomSelectedPositions = bottomSelectedPositions

        if (isAdded) {
            //app was crashing if position set before update no of row count. so added this delay.
            binding.abacusBottom.post {
                binding.abacusTop.setSelectedPositions(topSelectedPositions,setPositionCompleteListener)
                binding.abacusBottom.setSelectedPositions(bottomSelectedPositions,setPositionCompleteListener)
            }
        }
    }

    fun setQuestionAndDividerLength(questionLength: Int, finalAnsLength: Int) {
        this.questionLength = questionLength
        this.finalAnsLength = finalAnsLength
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
        when {
            isDisplayAbacusNumber -> {
                binding.tvCurrentVal.show()
                binding.tvCurrentValHide.hide()
                var newValue = abacusValue
                if (abacusValue.length != abacusTotalColumns){
                    for (i in 1..(abacusTotalColumns - abacusValue.length)) {
                        newValue = "0$newValue"
                    }
                }
                if (abacus_type == 2){
                    val remainQuestion = newValue.replace(".","").takeLast(questionLength).trimStart('0')
                    val answers = newValue.replace(".","").take(7).trimStart('0')
                    fromValue = if (answers.isEmpty()){0}else{answers.toInt()}
                    binding.tvCurrentVal.text = (if (answers.isEmpty()){"0"}else{answers})+" < "+(if (remainQuestion.isNullOrEmpty()){"0"}else{remainQuestion})
                }else{
                    val sb = StringBuilder(newValue)
                    sb.insert(7, ".")
                    newValue = sb.toString()
                    val splitResult = newValue.split(".")
                    if (splitResult.size == 2) {
                        val value1 = splitResult[0].toLong()
                        val value2 = splitResult[1]
                        fromValue = value1.toInt()
                        if (value2.toLong() > 0) {
                            binding.tvCurrentVal.text = "$value1.$value2"
                            binding.txtReset.setIsEnabled(true,1f)
                        } else {
                            binding.tvCurrentVal.text = "$value1"
                            if(value1 == 0L){
                                binding.txtReset.setIsEnabled(false,0.5F)
                            }else{
                                binding.txtReset.setIsEnabled(true,1f)
                            }
                        }
                    }
                }

            }
            else -> {
                binding.tvCurrentVal.hide()
                binding.tvCurrentValHide.show()
            }
        }
    }

    // for draw direction
    fun addDirection(toValue: Int) {
        if (::binding.isInitialized){
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
                        val decimalBeadsWidth = (themeContent.beadWidth * 6) + (themeContent.beadSpace * 6) + (themeContent.beadWidth * 0.75).toInt()

                        rodMovement.map { rodData ->
                            if (rodData.movement.lowerUp > 0 || rodData.movement.lowerDown > 0 || rodData.movement.upperDown || rodData.movement.upperUp){
                                val directionBinding = ContentAbacusDirectionsBinding.inflate(layoutInflater, null, false)
                                with(directionBinding){
                                    val rightSpace = decimalBeadsWidth + (themeContent.beadWidth * rodData.rodIndex) + (themeContent.beadSpace / 2) + (rodData.rodIndex * themeContent.beadSpace)
                                    val layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT)
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
                                binding.viewDirection.addView(directionBinding.root)
                            }
                        }
                        if ((binding.viewDirection.childCount?:0) > 0){
                            binding.viewDirection.show()
                        }else{
                            hideDirection()
                        }
                    }else{
                        hideDirection()
                    }
                }else{
                    hideDirection()
                }
            }
        }
    }
    fun addDirectionDivisor(toValue: Int,fromValue : Int) {
        if (::binding.isInitialized){
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
                                binding.viewDirection.addView(directionBinding.root)
                            }
                        }
                        if ((binding.viewDirection.childCount?:0) > 0){
                            binding.viewDirection.show()
                        }else{
                            hideDirection()
                        }
                    }else{
                        hideDirection()
                    }
                }else{
                    hideDirection()
                }
            }
        }
    }

    fun clearDirection() {
        if (::binding.isInitialized){
            binding.viewDirection.removeAllViews()
        }
    }
    fun hideDirection() {
        if (::binding.isInitialized){
            binding.viewDirection.removeAllViews()
            binding.viewDirection.hide()
        }
    }

}
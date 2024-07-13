package com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowQuestionLayoutBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.ViewUtils
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show
import java.util.*
import kotlin.collections.ArrayList

class AbacusDivisionTypeAdapter(
    private var abacusItems: ArrayList<HashMap<String, String>>, private var isStepByStep: Boolean, private val abacusType : AbacusContent?
) : RecyclerView.Adapter<AbacusDivisionTypeAdapter.FormViewHolder>() {

    private val highlightDetail = HashMap<Int, Pair<Int, Int>>()
    private var isClear = false
    private var isLastTemp = false
    private var nextDivider: Long = 0
    private  var nextDividerIteration:Long = 0
    private var totalRequiredIteration = 0
    private var initialDividerPosition = 2
    private var nextDivider1: Long = 0
    var nextHighlightedPosition = 0
    var currentTablePosition = 0

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowQuestionLayoutBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = abacusItems[position]
        val context = holder.binding.tvQuestion.context
        holder.binding.tvQuestionTemp.text = abacusItems[0][Constants.Que]
        holder.binding.tvSymbol.text = data[Constants.Sign] // intentionally added space before symbol
        holder.binding.tvQuestion.text = data[Constants.Que]
        if (holder.binding.tvSymbol.text.toString().isEmpty()) {
            holder.binding.tvSymbol.invisible()
        } else {
            holder.binding.tvSymbol.show()
        }
        holder.binding.conMain.show()
        if (isStepByStep) {
            val highlightPOs = highlightDetail[position]
            if (highlightPOs != null) {
                if (!isClear) {
                    holder.binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
                    if (!TextUtils.isEmpty(data[Constants.Que])) {
                        holder.binding.tvQuestion.text = span(
                            data[Constants.Que],
                            highlightPOs.first!!, highlightPOs.second!!,context
                        )
                    }
                } else {
                    holder.binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
                    holder.binding.tvQuestion.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
                }
            }
            if (position == 1 && abacusItems.size > 2) {
                val color = if (abacusType != null){
                    CommonUtils.mixTwoColors(ContextCompat.getColor(context,R.color.white), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.75f)
                }else{
                    ContextCompat.getColor(context, R.color.red_600)
                }
                holder.binding.ivDivider.setBackgroundColor(color)
                holder.binding.ivDivider.show()
                holder.binding.spaceBottom.hide()
            } else {
                holder.binding.ivDivider.hide()
                holder.binding.spaceBottom.hide()
            }
        }
    }

    class FormViewHolder(
        itemBinding: RowQuestionLayoutBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowQuestionLayoutBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return abacusItems.size
    }

    fun setDefaultHighlight() {
        if (abacusItems.size >= 2) {
            val first = abacusItems[0][Constants.Que]
            val second = java.lang.Float.valueOf(abacusItems[1][Constants.Que])
            calculateHiglightCount(first, second.toInt(), 1, 1, 0)

            val pair = Pair<Int, Int>(0, nextHighlightedPosition)
            highlightDetail[0] = pair
            val pair1 = Pair(0, abacusItems[1][Constants.Que]!!.length)
            highlightDetail[1] = pair1
            highlightDetail[2] = Pair(0, 0)
            highlightDetail[3] = Pair(0, 0)

        }
    }

    fun isLastStep(): Boolean {
        Log.e("jigarLogsDivision","highlightDetail = "+Gson().toJson(highlightDetail))
        return if (!isLastTemp) {
            if (highlightDetail.size > 0) {
        Log.e("jigarLogsDivision","highlightDetail second = "+highlightDetail[0]!!.second)
                highlightDetail[0]!!.second == abacusItems[0][Constants.Que]!!.length
            } else false
        } else {
            true
        }
    }


    fun getCurrentStep(): Int? {
        return highlightDetail[0]!!.second
    }
    fun clearIterationCount() {
        totalRequiredIteration = 0
    }

    private fun span(text: String?, startPosition: Int, endPosition: Int, context: Context): Spannable {
        if (!TextUtils.isEmpty(text) && startPosition <= text!!.length && endPosition <= text.length) {
            val wordtoSpan = SpannableString(text)
            wordtoSpan.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.abacus_place_holder)),
                0,
                text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val color = if (abacusType != null){
                if (abacusType.equals(AppConstants.Settings.theam_Poligon_Silver) || abacusType.equals(
                        AppConstants.Settings.theam_Poligon_Brown)){
                    ContextCompat.getColor(context,R.color.black)
                }else{
                    CommonUtils.mixTwoColors(ContextCompat.getColor(context,abacusType.dividerColor1), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.40f)
                }
            }else{
                ContextCompat.getColor(context, R.color.red)
            }
            wordtoSpan.setSpan(ForegroundColorSpan(color), startPosition, endPosition, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            return wordtoSpan
        }
        return SpannableString("")
    }
    fun reset() {
        for (i in 2 until abacusItems.size) {
            abacusItems[i][Constants.Que] = ""
            abacusItems[i][Constants.Sign] = ""
            abacusItems[i][Constants.Hint] = ""
        }
        nextDivider = 0L
        initialDividerPosition = 2
        setDefaultHighlight()
        notifyDataSetChanged()
    }

    fun goToNextStep() {
        try {
            val data = abacusItems[0]
            /*Long currentSum = getCurrentSumVal();*/
            /*shift 0 position*/
            val firstEndPostion = highlightDetail[0]!!.second!!
            if (firstEndPostion < data[Constants.Que]!!.length) {
                calculateHiglightCount(
                    abacusItems[0][Constants.Que], abacusItems[1][Constants.Que]!!
                        .toInt(),
                    1, 1, firstEndPostion
                )
                if (firstEndPostion == nextHighlightedPosition) {
//                    current value is 0 so add +1 to position highlight and move next
                    val pair = Pair<Int, Int>(firstEndPostion, nextHighlightedPosition + 1)
                    highlightDetail[0] = pair
                    goToNextStep()
                    return
                }
                if (firstEndPostion > nextHighlightedPosition && data[Constants.Que]!!.endsWith("0")){
                    isLastTemp = true
                    return
                }

                val pair = Pair<Int, Int>(firstEndPostion, nextHighlightedPosition)
                highlightDetail[0] = pair
                if (abacusItems.size > initialDividerPosition) {
                    if (initialDividerPosition > 2) {
                        /*unhighlight previous divider*/
                        val pairInternal = Pair(0, 0)
                        highlightDetail[initialDividerPosition - 1] = pairInternal
                    }
                    val abecuseItem = abacusItems[initialDividerPosition]
                    abecuseItem[Constants.Que] = nextDivider.toString()
                    val pairInternal = Pair(0, abecuseItem[Constants.Que]!!.length)
                    highlightDetail[initialDividerPosition] = pairInternal
                    initialDividerPosition++
                } else {
                    /*unhighlight previous divider*/
                    val pairInternal = Pair(0, 0)
                    highlightDetail[initialDividerPosition - 1] = pairInternal
                }
            } else {
                val pairInternal = Pair(0, 0)
                highlightDetail[initialDividerPosition - 1] = pairInternal
                //                sum completed
                return
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getCurrentSumVal(): Long? {
        if (abacusItems.size >= 2 && highlightDetail.size > 0) {
            val firstItem = abacusItems[0]
            val secondItem = abacusItems[1]
            val firstPair = highlightDetail[0]
            /*first step*/
            val divideBy = Integer.valueOf(secondItem[Constants.Que])
            val fullQuestion = firstItem[Constants.Que]
            currentTablePosition = 0

            /*first step completed
         * now need to take highlight end position from question 1 and need perform same step as above */
            return calculateCurrentSumVal(
                fullQuestion,
                divideBy,
                firstPair!!.second,
                1
            )
        }
        return null
    }
    fun calculateCurrentSumVal(
        fullQuestion: String?,
        divideBy: Int,
        length: Int?,
        currentRecursionCount: Int
    ): Long? {
        var currentsumval = 0L
        //        if (currentRecursionCount >= length) {
//            return currentsumval;
//        }
        for (i in 0..length!!) {
            if (i > fullQuestion!!.length) {
                break
            }
            val que = fullQuestion.substring(0, i)
            if (!TextUtils.isEmpty(que)) {
                val queInt = Integer.valueOf(que)
                if (queInt >= divideBy) {
//                    if (((i + 1) <= fullQuestion.length()) && fullQuestion.substring(0, i + 1).endsWith("0")) {
//                        continue;
//                    }
                    var j = 1
                    while (true) {
                        if (queInt - divideBy * j < divideBy) {
                            break
                        }
                        j++
                    }
                    var curSum = j.toString() + ""
                    for (k in i until fullQuestion.length) {
                        curSum += "0"
                    }
                    currentsumval = java.lang.Long.valueOf(curSum)
                    nextDivider = java.lang.Long.valueOf(fullQuestion) - currentsumval * divideBy
                    if (currentRecursionCount < length && i < length) {
                        var nextQue = nextDivider.toString()
                        curSum = ""
                        //                        String nextQue = String.valueOf(nextDivider1);
                        for (k in 0 until fullQuestion.length - nextQue.length) {
                            curSum += "0"
                        }
                        nextQue =
                            curSum + nextQue // to maintain lenth = full question length we added 0
                        currentsumval += calculateCurrentSumVal(
                            nextQue,
                            divideBy,
                            length,
                            currentRecursionCount + 1
                        )!!
                    }
                    return currentsumval
                }
            }
        }
        return 0L
    }

    fun calculateHiglightCount(
        fullQuestion: String?,
        divideBy: Int,
        position: Int?,
        currentRecursionCount: Int,
        startLength: Int
    )
    {
        if (currentRecursionCount == 1) {
            nextHighlightedPosition = 0
        }
        var currentsumval = 0L

//        if (currentRecursionCount >= fullQuestion!!.length) {
//            return currentsumval;
//        }
//        for (int i = 0; i <= length; i++) {
//        if (position > fullQuestion.length()) {
//            return;
//        }
        nextHighlightedPosition++
        val que = fullQuestion!!.substring(0, position!!)
        if (!TextUtils.isEmpty(que)) {
            val queInt = Integer.valueOf(que)
            if (queInt >= divideBy) {
//                if (((position) <= fullQuestion.length) && fullQuestion.substring(0, position).endsWith("0")) {
//                    return
//                }
                var j = 1
                while (true) {
                    if (queInt - divideBy * j < divideBy) {
                        break
                    }
                    j++
                }
                currentTablePosition = j
                var curSum = j.toString() + ""
                for (k in position until fullQuestion.length) {
                    curSum += "0"
                }
                currentsumval = java.lang.Long.valueOf(curSum)
                nextDivider1 = java.lang.Long.valueOf(fullQuestion) - currentsumval * divideBy
                if (nextDivider1 != 0L && position <= startLength) {
                    curSum = ""
                    var nextQue = nextDivider1.toString()
                    for (k in 0 until fullQuestion.length - nextQue.length) {
                        curSum += "0"
                    }
                    nextQue =
                        curSum + nextQue // to maintain lenth = full question length we added 0
                    calculateHiglightCount(
                        nextQue,
                        divideBy,
                        position + 1,
                        currentRecursionCount + 1,
                        startLength
                    )
                }
            } else {
                calculateHiglightCount(
                    fullQuestion,
                    divideBy,
                    position + 1,
                    currentRecursionCount + 1,
                    startLength
                )
            }
        }

    }
    fun getDivideIterationCount(fullQuestion: String, divideBy: Int): Long {
        var currentsumval = 0L
        for (i in 0..fullQuestion.length) {
            val que = fullQuestion.substring(0, i)
            if (!TextUtils.isEmpty(que)) {
                val queInt = Integer.valueOf(que)
                if (queInt >= divideBy) {
//                    if (((i + 1) <= fullQuestion.length()) && fullQuestion.substring(0, i + 1).endsWith("0")) {
//                        continue;
//                    }
                    var j = 1
                    while (true) {
                        if (queInt - divideBy * j < divideBy) {
                            break
                        }
                        j++
                    }
                    var curSum = j.toString() + ""
                    for (k in i until fullQuestion.length) {
                        curSum += "0"
                    }
                    currentsumval = java.lang.Long.valueOf(curSum)
                    nextDividerIteration =
                        java.lang.Long.valueOf(fullQuestion) - currentsumval * divideBy
                    totalRequiredIteration++
                    if (nextDividerIteration != 0L) {
                        val nextQue = nextDividerIteration.toString()
                        currentsumval += getDivideIterationCount(nextQue, divideBy)
                    }
                    return currentsumval
                }
            }
        }
        return 0L
    }

    fun getNextDivider(): Long {
        return nextDivider
    }
    fun getFinalSumVal(): Double? {

        /*divide have 2 items. other 2 items used for calculation purpose*/
        if (abacusItems.isNotEmpty()) {
            var expression = ""
            for (i in 0..1) {
                val abecuseItem = abacusItems[i]
                expression += abecuseItem[Constants.Sign]!!.trim { it <= ' ' } + abecuseItem[Constants.Que]!!
                    .trim { it <= ' ' }
            }
            val answer = ViewUtils.calculateStringExpression(expression)
            return answer
        }
        return null
    }

    fun clearHighlight() {
        isClear = true
        notifyDataSetChanged()
    }

    fun getTotalRequiredIteration(): Int {
        return totalRequiredIteration
    }
}
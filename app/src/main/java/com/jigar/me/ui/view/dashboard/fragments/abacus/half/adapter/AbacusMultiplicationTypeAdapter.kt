package com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowQuestionLayoutBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.ViewUtils
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show
import java.util.*
import kotlin.collections.ArrayList

class AbacusMultiplicationTypeAdapter(
    private var abacusItems: ArrayList<HashMap<String, String>>, private var isStepByStep: Boolean, private val abacusType : AbacusContent?
) :
    RecyclerView.Adapter<AbacusMultiplicationTypeAdapter.FormViewHolder>() {

    val highlightDetail = HashMap<Int, Int>()
    private var isClear = false
    fun setData(listData: List<HashMap<String, String>>, isStepByStep: Boolean) {
        highlightDetail[0] = 0
        highlightDetail[1] = 0
        this.abacusItems.clear()
        this.abacusItems.addAll(listData)
        this.isStepByStep = isStepByStep
        notifyItemRangeChanged(0,abacusItems.size)
    }


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
        holder.binding.tvSymbol.text =
            Objects.requireNonNull<String>(data[Constants.Sign]).replace("*", "x")
        holder.binding.tvQuestion.text = data[Constants.Que]
        if (holder.binding.tvSymbol.text.toString().isEmpty()) {
            holder.binding.tvSymbol.invisible()
        } else {
            holder.binding.tvSymbol.show()
        }
        if (isStepByStep) {
            val highlightPOs = highlightDetail[position]!!
            if (!isClear) {
                holder.binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
                holder.binding.tvQuestion.text = span(
                    data[Constants.Que],
                    highlightPOs, context
                )
            } else {
                holder.binding.tvSymbol.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
                holder.binding.tvQuestion.setTextColor(ContextCompat.getColor(context, R.color.abacus_place_holder))
            }
        }
    }

    class FormViewHolder( itemBinding: RowQuestionLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowQuestionLayoutBinding = itemBinding

    }

    override fun getItemCount(): Int {
        return abacusItems.size
    }

    fun getTableNew() : String{ // return only current multiplication
        var sum = ""
        if (highlightDetail.size > 1) {
            val position = highlightDetail[1]!!
            val position0 = highlightDetail[0]!!
            val abacusItem = abacusItems[1]
            val abacusItem0 = abacusItems[0]
            val tableOf = Integer.valueOf(abacusItem[Constants.Que]!!.substring(position, position + 1))
            val highlightedPosition = Integer.valueOf(abacusItem0[Constants.Que]!!.substring(position0, position0 + 1))
            var answer = (highlightedPosition * tableOf).toString()
            if (answer.length == 1) {
                answer = "0$answer"
            }
            sum = "$highlightedPosition x $tableOf = $answer"
        }
        return sum
    }
    fun getTable(context: Context,themeContent : AbacusContent? = null): SpannableString? {
        if (highlightDetail.size > 1) {
            val position = highlightDetail[1]!!
            val position0 = highlightDetail[0]!!
            val abacusItem = abacusItems[1]
            val abacusItem0 = abacusItems[0]
            return ViewUtils.getTable(
                context,
                Integer.valueOf(abacusItem[Constants.Que]!!.substring(position, position + 1)),
                Integer.valueOf(abacusItem0[Constants.Que]!!.substring(position0, position0 + 1)),
                themeContent
            )
        }
        return null
    }

    private fun span(text: String?, position: Int, context: Context): Spannable {
        val wordToSpan = SpannableString(text)
        wordToSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.abacus_place_holder)),0,text?.length?:0,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        val color = CommonUtils.getQuestionHighLighterColor(context,abacusType)
        wordToSpan.setSpan(ForegroundColorSpan(color), position, position + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return wordToSpan
    }

    fun getCurrentStep(): HashMap<Int, Int> {
        return highlightDetail
    }

    fun getItem(item: Int): HashMap<String, String> {
        return abacusItems[item]
    }

    fun reset() {
        isClear = false
        highlightDetail[0] = 0
        highlightDetail[1] = 0
        notifyDataSetChanged()
    }

    fun goToNextStep() {
        try {
            val firstItem = abacusItems[0]
            val secondItem = abacusItems[1]
            val currentSum = getCurrentSumVal()
            if (highlightDetail[0]!! < firstItem[Constants.Que]!!.length - 1) {
                highlightDetail[0] = highlightDetail[0]!! + 1
            } else if (highlightDetail[1]!! < secondItem[Constants.Que]!!.length - 1) {
                highlightDetail[1] = highlightDetail[1]!! + 1
                highlightDetail[0] = 0
            } else {
//                sum completed
                return
            }
            val nextSumVal = getCurrentSumVal()
            if (nextSumVal == currentSum) {
                goToNextStep()
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }

    fun getCurrentSumVal(): Double? {
        if (abacusItems.size >= 2) {
            val firstItem = abacusItems[0]
            val secondItem = abacusItems[1]
            var currentsumval = 0.0
            for (i in 0..highlightDetail[1]!!) {
                for (j in 0..(if (i == highlightDetail[1]) highlightDetail[0] else firstItem[Constants.Que]!!.length - 1)!!) {
                    var firstVal = firstItem[Constants.Que]!![j].toString()
                    for (k in j + 1 until firstItem[Constants.Que]!!.length) {
                        firstVal += "0"
                    }
                    var secondVal = secondItem[Constants.Que]!![i].toString()
                    for (k in i + 1 until secondItem[Constants.Que]!!.length) {
                        secondVal += "0"
                    }
                    val expression = "$firstVal*$secondVal"
                    currentsumval += ViewUtils.calculateStringExpression(expression)
                }
            }
            return currentsumval
        }
        return null
    }

    fun getFinalSumVal(): Double? {
        if (abacusItems.size > 0) {
            var expression = ""
            for (i in abacusItems.indices) {
                val abecuseItem = abacusItems[i]
                expression += abecuseItem[Constants.Sign]!!.trim { it <= ' ' } + abecuseItem[Constants.Que]!!
                    .trim { it <= ' ' }
            }
            return ViewUtils.calculateStringExpression(expression)
        }
        return null
    }

    fun clearHighlight() {
        isClear = true
        notifyDataSetChanged()
    }
}
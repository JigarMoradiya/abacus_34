package com.jigar.me.ui.view.dashboard.fragments.abacus.half.adapter

import android.util.Log
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

class AbacusAdditionSubtractionTypeAdapter(
    private var abacusItems: ArrayList<HashMap<String, String>>,
    private val mListener: HintListener, private var isStepByStep: Boolean, private val abacusType : AbacusContent? = null
) :
    RecyclerView.Adapter<AbacusAdditionSubtractionTypeAdapter.FormViewHolder>() {
    var maxQuestion = ""
    interface HintListener {
        fun onCheckHint(hint: String?, que: String?, Sign: String?)
    }

    private var currentStep = 0

    fun setData(listData: List<HashMap<String, String>>,isStepByStep : Boolean) {
        this.abacusItems.clear()
        this.abacusItems.addAll(listData)
        this.isStepByStep = isStepByStep
        listData.map {
            if (maxQuestion.length < (it[Constants.Que]?.length ?: 0)){
                maxQuestion = it[Constants.Que]?:""
            }
        }
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

        holder.binding.tvSymbol.text = data[Constants.Sign]
        holder.binding.tvQuestion.text = data[Constants.Que]
        holder.binding.tvQuestionTemp.text = maxQuestion

        if (holder.binding.tvSymbol.text.toString().isEmpty()) {
            holder.binding.tvSymbol.invisible()
        } else {
            holder.binding.tvSymbol.show()
        }
        if (isStepByStep) {
            if (currentStep == position) {
                val color = if (abacusType != null){
                    if (abacusType.equals(AppConstants.Settings.theam_Poligon_Silver) || abacusType.equals(AppConstants.Settings.theam_Poligon_Brown)){
                        ContextCompat.getColor(context,R.color.black)
                    }else{
                        CommonUtils.mixTwoColors(ContextCompat.getColor(context,abacusType.dividerColor1), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.40f)
                    }
                }else{
                    ContextCompat.getColor(context, R.color.red)
                }
                holder.binding.tvSymbol.setTextColor(color)
                holder.binding.tvQuestion.setTextColor(color)

            } else {
                holder.binding.tvSymbol.setTextColor(
                    ContextCompat.getColor(context, R.color.abacus_place_holder)
                )
                holder.binding.tvQuestion.setTextColor(
                    ContextCompat.getColor(context, R.color.abacus_place_holder)
                )
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

    fun getCurrentStep(): Int {
        return currentStep
    }

    fun reset() {
        currentStep = 0
        notifyDataSetChanged()
    }

    fun goToNextStep() {
        this.currentStep++
        if (currentStep < abacusItems.size) {
            val data = abacusItems[currentStep]
            if (data[Constants.Que]!!.trim { it <= ' ' } == "0") {
                goToNextStep()
            } else {
                mListener.onCheckHint(
                    data[Constants.Hint],
                    data[Constants.Que], data[Constants.Sign]
                )
            }
        }
        notifyDataSetChanged()
    }

    fun getCurrentSumVal(): Double? {
        if (abacusItems.size > currentStep) {
            var expression = ""
            for (i in 0..currentStep) {
                val data = abacusItems[i]
                expression += data[Constants.Sign]!!.trim { it <= ' ' } + data[Constants.Que]!!.trim { it <= ' ' }
            }
            return ViewUtils.calculateStringExpression(expression)
        }
        return null
    }

    fun getFinalSumVal(): Double? {
        if (abacusItems.isNotEmpty()) {
            var expression = ""
            for (i in abacusItems.indices) {
                val data = abacusItems[i]
                expression += data[Constants.Sign]!!.trim { it <= ' ' } + data[Constants.Que]!!.trim { it <= ' ' }
            }
            return ViewUtils.calculateStringExpression(expression)
        }
        return null
    }

}
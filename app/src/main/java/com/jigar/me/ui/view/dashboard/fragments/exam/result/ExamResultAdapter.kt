package com.jigar.me.ui.view.dashboard.fragments.exam.result

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.exam.DailyExamData
import com.jigar.me.databinding.RawExamResultBinding
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class ExamResultAdapter(
    private var listData: List<DailyExamData>
) : RecyclerView.Adapter<ExamResultAdapter.FormViewHolder>() {

    private var mCalculator: Calculator = Calculator()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawExamResultBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = listData[position]
        val resultObject: String = mCalculator.getResult(data.questions,data.questions)
        val correctAns: String = CommonUtils.removeTrailingZero(resultObject)
        holder.binding.txtAnswer.text = correctAns
        if (data.userAnswer.isEmpty()) {
            holder.binding.img.hide()
            holder.binding.txtYourAnswer.show()
            holder.binding.txtYourAnswer.text =
                holder.binding.txtYourAnswer.context.getText(R.string.SKipped)
        } else {
            holder.binding.img.show()
            if (correctAns.equals(data.userAnswer, ignoreCase = true)) {
                holder.binding.img.setBackgroundResource(R.drawable.ic_answer_right)
                holder.binding.txtYourAnswer.hide()
            } else {
                holder.binding.img.setBackgroundResource(R.drawable.ic_answer_wrong)
                holder.binding.txtYourAnswer.show()
                holder.binding.txtYourAnswer.text =
                    holder.binding.txtYourAnswer.context.getText(R.string.YourAnswer)
                        .toString() + " : " + data.userAnswer
            }
        }

        holder.binding.txtAbacus.setText(
            data.questions
                .replace("+", "\n+")
                .replace("-", "\n-")
                .replace("x", "\nx ")
                .replace("/", "\n÷ ")
        )

    }

    class FormViewHolder(itemBinding: RawExamResultBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawExamResultBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
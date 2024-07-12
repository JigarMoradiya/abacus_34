package com.jigar.me.ui.view.dashboard.fragments.exercise.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.ExerciseList
import com.jigar.me.databinding.RowExerciseResultMulDivBinding
import com.jigar.me.utils.extensions.*

class ExerciseMultiplicationDivisionResultAdapter(private var questions: List<ExerciseList> )
    : RecyclerView.Adapter<ExerciseMultiplicationDivisionResultAdapter.FormViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowExerciseResultMulDivBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = questions[position]
        with(holder.binding){
            val context = root.context
            val questions = if (data.question.contains("x")){
                data.question.split("x")
            }else{
                data.question.split("/")
            }

            if (questions.size == 2){
                tvQuestion1.text = questions[0]
                tvQuestion2.text = questions[1]
            }

            if (data.question.contains("x")){
                imgSymbol.setImageResource(R.drawable.cal_mul)
                imgSymbol.show()
            }else if (data.question.contains("/")){
                imgSymbol.setImageResource(R.drawable.cal_divide)
                imgSymbol.show()
            }else{
                imgSymbol.invisible()
            }

            if (data.userAnswer == -1){
                txtYourAnswer.text = "0"
                txtYourAnswer.setTextColor(ContextCompat.getColor(context,R.color.orange_800))
                img.setBackgroundResource(R.drawable.ic_answer_skip)
            }else{
                txtYourAnswer.text = "".plus(data.userAnswer.toString())
                if (data.userAnswer == data.answer){
                    img.setBackgroundResource(R.drawable.ic_answer_right)
                    txtYourAnswer.setTextColor(ContextCompat.getColor(context,R.color.green_900))
                }else{
                    img.setBackgroundResource(R.drawable.ic_answer_wrong)
                    txtYourAnswer.setTextColor(ContextCompat.getColor(context,R.color.redDark))
                }
            }
        }

    }

    class FormViewHolder(itemBinding: RowExerciseResultMulDivBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowExerciseResultMulDivBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}
package com.jigar.me.ui.view.dashboard.fragments.exercise.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.ExerciseList
import com.jigar.me.databinding.RowExerciseQuestionResultLayoutBinding
import com.jigar.me.databinding.RowExerciseResultBinding
import com.jigar.me.utils.extensions.*

class ExerciseAdditionSubtractionResultAdapter(
    private var questions: List<ExerciseList>
) : RecyclerView.Adapter<ExerciseAdditionSubtractionResultAdapter.FormViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowExerciseResultBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = questions[position]
        with(holder.binding){
            val context = root.context
            val question = data.question.replace("+"," +").replace("-"," -").replace("x"," x")
            val list = question.split(" ")
            val listExerciseAdditionSubtractionQuestion = arrayListOf<String>()
            list.map {
                listExerciseAdditionSubtractionQuestion.add(it)
            }
            val exerciseAdditionSubtractionAdapter = ExerciseAdditionSubtractionResultQuestionsAdapter(listExerciseAdditionSubtractionQuestion)
            recyclerview.adapter = exerciseAdditionSubtractionAdapter

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

    class FormViewHolder(itemBinding: RowExerciseResultBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowExerciseResultBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return questions.size
    }


    class ExerciseAdditionSubtractionResultQuestionsAdapter(
        private var questions: List<String>
    ) : RecyclerView.Adapter<ExerciseAdditionSubtractionResultQuestionsAdapter.FormViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): FormViewHolder {
            val binding = RowExerciseQuestionResultLayoutBinding.inflate(parent.context.layoutInflater,parent,false)
            return FormViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
            val data = questions[position]
            with(holder.binding){
                val context = root.context
                val que = data.replace("+","").replace("-","").replace("x","")
                if (que.length > 4){
                    tvQuestionSmall.text = que
                    tvQuestionSmall.show()
                    tvQuestion.hide()
                }else{
                    tvQuestion.text = que
                    tvQuestion.show()
                    tvQuestionSmall.hide()
                }

                if (data.contains("-")){
                    imgSymbol.setImageResource(R.drawable.cal_minus)
                    imgSymbol.show()
                }else{
                    imgSymbol.invisible()
                }
            }

        }

        class FormViewHolder(
            itemBinding: RowExerciseQuestionResultLayoutBinding
        ) :
            RecyclerView.ViewHolder(itemBinding.root) {
            var binding: RowExerciseQuestionResultLayoutBinding = itemBinding
        }

        override fun getItemCount(): Int {
            return questions.size
        }
    }
}
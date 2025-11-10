package com.jigar.me.ui.view.dashboard.fragments.home_new.category.abacuslist_temp

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.ExerciseList
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.databinding.RowAbacusListBinding
import com.jigar.me.databinding.RowExerciseQuestionResultLayoutBinding
import com.jigar.me.databinding.RowExerciseResultBinding
import com.jigar.me.utils.extensions.*

class AbacusListAdapter(
    private var questions: List<Abacus>
) : RecyclerView.Adapter<AbacusListAdapter.FormViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowAbacusListBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = questions[position]
        with(holder.binding){
            val context = root.context
            val question = data.question
            tvQuestion.text = question
        }

    }

    class FormViewHolder(itemBinding: RowAbacusListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowAbacusListBinding = itemBinding
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
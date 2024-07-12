package com.jigar.me.ui.view.dashboard.fragments.exercise.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowExerciseQuestionLayoutBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.*

class ExerciseAdditionSubtractionAdapter(
    private var questions: List<String>,private val abacusType : AbacusContent? = null
) : RecyclerView.Adapter<ExerciseAdditionSubtractionAdapter.FormViewHolder>() {
    var currentStep = 0
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowExerciseQuestionLayoutBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = questions[position]
        with(holder.binding){
            val context = root.context
            val que = data.replace("+","").replace("-","")
            val color = if (abacusType != null){
                if (abacusType.equals(AppConstants.Settings.theam_Poligon_Silver) || abacusType.equals(AppConstants.Settings.theam_Poligon_Brown)){
                    ContextCompat.getColor(context,R.color.black)
                }else{
                    CommonUtils.mixTwoColors(ContextCompat.getColor(context,abacusType.dividerColor1), ContextCompat.getColor(context,abacusType.resetBtnColor8), 0.30f)
                }
            }else{
                ContextCompat.getColor(context, R.color.red)
            }
            if (que.length > 4){
                if (currentStep == position){
                    tvQuestion1.setTextColor(color)
                    imgSymbol.setColorFilter(color)
                }else{
                    tvQuestion1.setTextColor(ContextCompat.getColor(context,R.color.black_light))
                    imgSymbol.setColorFilter(ContextCompat.getColor(context,R.color.black_light))
                }
                tvQuestion1.text = que
                tvQuestion1.show()
                tvQuestion.hide()
            }else{
                if (currentStep == position){
                    tvQuestion.setTextColor(color)
                    imgSymbol.setColorFilter(color)
                }else{
                    tvQuestion.setTextColor(ContextCompat.getColor(context,R.color.black_light))
                    imgSymbol.setColorFilter(ContextCompat.getColor(context,R.color.black_light))
                }
                tvQuestion.text = que
                tvQuestion1.hide()
                tvQuestion.show()
            }

            if (data.contains("-")){
                imgSymbol.show()
            }else{
                imgSymbol.invisible()
            }
            root.onClick {
                if (currentStep != questions.lastIndex){
                    val previousPos = currentStep
                    currentStep++
                    notifyItemChanged(currentStep)
                    notifyItemChanged(previousPos)
                }
            }
        }

    }

    class FormViewHolder(
        itemBinding: RowExerciseQuestionLayoutBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowExerciseQuestionLayoutBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}
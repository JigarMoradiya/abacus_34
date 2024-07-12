package com.jigar.me.ui.view.dashboard.fragments.reports.adapter

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.R
import com.jigar.me.data.model.data.AllExamData
import com.jigar.me.data.model.data.QuestionDataRequest
import com.jigar.me.databinding.RawReportsBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show


class ReportsListAdapter(
    private var listData: List<AllExamData>,
    private val mListener: OnItemClickListener,
) : RecyclerView.Adapter<ReportsListAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(data: AllExamData)
    }

    fun setData(listData: List<AllExamData>) {
        val previousSize = this.listData.size
        (this.listData as ArrayList<AllExamData>).addAll(listData)
        notifyItemRangeChanged(previousSize,this.listData.size)
    }
    fun clearList(){
        listData = arrayListOf()
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawReportsBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val context = holder.binding.root.context
        val data = listData[position]
        with(holder.binding){
            this.data = data
            txtType.text = if (data.type == AppConstants.ExamType.type_CCM){context.getString(R.string.custom_challenge_mode)}else{data.type}
            conCCM.hide()
            conExerciseExam.hide()
            when (data.type) {
                AppConstants.ExamType.type_CCM -> {
                    conCCM.show()
                    txtType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.report_ccm_btn_bg))
                }
                AppConstants.ExamType.type_Exam -> {
                    conExerciseExam.show()
                    txtType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.report_exam_btn_bg))
                }
                AppConstants.ExamType.type_Exercise -> {
                    conExerciseExam.show()
                    txtType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.report_exercise_btn_bg))
                }
                else -> {
                    txtType.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.report_other_btn_bg))
                }
            }
            txtExerciseCheckResult.onClick {
                mListener.onItemClick(data)
            }
        }

    }

    class FormViewHolder(itemBinding: RawReportsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawReportsBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.RawSetListBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.layoutInflater

class SetsAdapter(
    private var listData: List<Set>,
    private val mListener: (Int, Pages) -> Unit
) : RecyclerView.Adapter<SetsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawSetListBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val data: Set = listData[position]
        val context = txtTitle.context
        dataModel = data
        when (data.answer_setting) {
            AppConstants.apiParams.answerSettingStepByStep -> {
                conMain.setBackgroundColor(ContextCompat.getColor(context, R.color.step_by_step_answer))
            }
            AppConstants.apiParams.answerFinalAnswer -> {
                conMain.setBackgroundColor(ContextCompat.getColor(context, R.color.final_answer))
            }
            AppConstants.apiParams.answerFormalAnswer -> {
                conMain.setBackgroundColor(ContextCompat.getColor(context, R.color.formal_exam))
            }
            else -> {
                conMain.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_100))
            }
        }
    }

    class ViewHolder(itemBinding: RawSetListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawSetListBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
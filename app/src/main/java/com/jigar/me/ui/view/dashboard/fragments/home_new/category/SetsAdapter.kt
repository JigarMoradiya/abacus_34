package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.RawSetListBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.onLongClick
import com.jigar.me.utils.extensions.show

class SetsAdapter(
    private var listData: List<Set>,
    private val mListener: (String,Int, Set) -> Unit
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
            AppConstants.apiParams.answerStepByStep -> {
                cardMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.step_by_step_answer_light))
            }
            AppConstants.apiParams.answerFinalAnswer -> {
                cardMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.final_answer_light))
            }
            AppConstants.apiParams.answerFormalExam -> {
                cardMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.formal_exam_light))
            }
            else -> {
                cardMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey_100))
            }
        }
        if (data.is_completed_set){
            imgCompletedIndicator.show()
        }else{
            imgCompletedIndicator.hide()
        }
        if (data.is_running_set){
            CommonUtils.blinkView(imgRunningIndicator)
        }else{
            imgRunningIndicator.hide()
        }
        root.onClick {
            mListener.invoke(Constants.CLICK_TYPE_DETAIL,position,data)
        }
        root.onLongClick {
            mListener.invoke(Constants.CLICK_TYPE_LONG,position,data)
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
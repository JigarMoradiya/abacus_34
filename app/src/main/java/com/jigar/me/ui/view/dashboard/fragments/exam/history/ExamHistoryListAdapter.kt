package com.jigar.me.ui.view.dashboard.fragments.exam.history

import android.util.Log
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.databinding.RawExamHistoryListBinding
import com.jigar.me.databinding.RawObjectListBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.layoutInflater

class ExamHistoryListAdapter(
    private var listData: List<ExamHistory>,
    private val mListener: OnItemClickListener
) :
    RecyclerView.Adapter<ExamHistoryListAdapter.FormViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: ExamHistory)
    }

    fun setData(listData: List<ExamHistory>) {
        this.listData = listData
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawExamHistoryListBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(
            binding, mListener, listData
        )
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: ExamHistory = listData[position]
        val context = holder.binding.txtTitle.context
        holder.binding.txtTitle.text = DateTimeUtils.getDateString(data.addedOn,DateTimeUtils.ddMMMyyyyhhmma)
        val totalSecs = data.examTotalTime
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        val time = String.format("%02d:%02d:%02d",hours,minutes,seconds)
        holder.binding.txtDesc.text = HtmlCompat.fromHtml(context.resources.getString(R.string.TotalTakeTime) + " : <b>" +time+"</b>",HtmlCompat.FROM_HTML_MODE_COMPACT)
        val totalQuestion = if (data.examFor.isNotNullOrEmpty()){
            data.examBeginners.size
        }else if (data.examType == AppConstants.ExamType.exam_Level_Beginner){
            data.examBeginners.size
        }else{
            data.examDetails.size
        }
        holder.binding.txtTotalQue.text = totalQuestion.toString()
        val totalRight = data.getTotalRightAns()
        holder.binding.txtRightQue.text = totalRight.toString()
        val percentage : Float = ((totalRight.toFloat() * 5) / totalQuestion.toFloat())
        holder.binding.simpleRatingBar.rating = percentage
    }

    class FormViewHolder(
        itemBinding: RawExamHistoryListBinding,
        private val mListener: OnItemClickListener,
        listData: List<ExamHistory>
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawExamHistoryListBinding = itemBinding

        init {
            this.binding.txtCheckResult.setOnClickListener {
                this.mListener.onItemClick(listData[layoutPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
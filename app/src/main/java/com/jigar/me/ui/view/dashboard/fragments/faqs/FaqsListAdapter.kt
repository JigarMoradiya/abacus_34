package com.jigar.me.ui.view.dashboard.fragments.faqs

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.local.data.FAQs
import com.jigar.me.databinding.RawFaqsBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class FaqsListAdapter(
    private var listData: List<FAQs>
) :
    RecyclerView.Adapter<FaqsListAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawFaqsBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: FAQs = listData[position]
        with(holder.binding){
            faqs = data
            if (data.answer.isEmpty()){
                txtAnswer.hide()
            }
            root.onClick {
                if (data.answer.isNotEmpty()){
                    if (txtAnswer.isVisible){
                        txtAnswer.hide()
                        imgArrow.rotation = 0f
                    }else{
                        txtAnswer.show()
                        imgArrow.rotation = 180f
                    }
                }
            }
        }
    }

    class FormViewHolder(itemBinding: RawFaqsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawFaqsBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
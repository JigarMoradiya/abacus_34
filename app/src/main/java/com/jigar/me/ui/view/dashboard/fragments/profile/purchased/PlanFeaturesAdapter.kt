package com.jigar.me.ui.view.dashboard.fragments.profile.purchased

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.model.data.Plans
import com.jigar.me.databinding.RawPaidFeaturesBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.makeSpannable


class PlanFeaturesAdapter(private var listData: List<String>) :
    RecyclerView.Adapter<PlanFeaturesAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onPurchaseItemClick(selectedPlan: Plans)
    }

    fun setData(listData: List<String>) {
        this.listData = listData
        notifyItemRangeChanged(0,listData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawPaidFeaturesBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: String = listData[position]
        with(holder.binding){
            feature = data
            txtTitle.text = HtmlCompat.fromHtml(data,HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

    }

    class FormViewHolder(itemBinding: RawPaidFeaturesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPaidFeaturesBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
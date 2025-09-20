package com.jigar.me.ui.view.dashboard.fragments.purchase.newui

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.databinding.RawPurchaseInfoBinding
import com.jigar.me.utils.extensions.layoutInflater

class PurchaseInfoAdapter(
    private var listData: List<String>
) :
    RecyclerView.Adapter<PurchaseInfoAdapter.FormViewHolder>() {
    fun setData(listData: List<String>) {
        this.listData = listData
        notifyItemRangeChanged(0, listData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding =
            RawPurchaseInfoBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        with(holder.binding) {
            txtTitle.text = HtmlCompat.fromHtml(listData[position],
                HtmlCompat.FROM_HTML_MODE_COMPACT)

        }
    }

    class FormViewHolder(
        itemBinding: RawPurchaseInfoBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPurchaseInfoBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
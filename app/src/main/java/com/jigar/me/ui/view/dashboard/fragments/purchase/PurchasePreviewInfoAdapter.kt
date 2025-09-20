package com.jigar.me.ui.view.dashboard.fragments.purchase

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.databinding.RawPurchasePreviewInfoBinding
import com.jigar.me.utils.extensions.layoutInflater

class PurchasePreviewInfoAdapter(
    private var listData: List<String>
) :
    RecyclerView.Adapter<PurchasePreviewInfoAdapter.FormViewHolder>() {
    fun setData(listData: List<String>) {
        this.listData = listData
        notifyItemRangeChanged(0, listData.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding =
            RawPurchasePreviewInfoBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        with(holder.binding) {
            txtTitle.text = HtmlCompat.fromHtml(listData[position],HtmlCompat.FROM_HTML_MODE_COMPACT)

        }
    }

    class FormViewHolder(
        itemBinding: RawPurchasePreviewInfoBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPurchasePreviewInfoBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
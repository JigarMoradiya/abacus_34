package com.jigar.me.ui.view.dashboard.fragments.tables

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.ColorData
import com.jigar.me.data.local.data.ColorProvider
import com.jigar.me.data.model.pages.Pages
import com.jigar.me.databinding.RawMultiplicationTableBinding
import com.jigar.me.utils.ViewUtils
import com.jigar.me.utils.extensions.layoutInflater

class MultiplicationTablesListAdapter(
    private var table: Int
) :
    RecyclerView.Adapter<MultiplicationTablesListAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onPageItemClick(data: Pages, pageId: String)
    }

    var list: ArrayList<ColorData> = ColorProvider.getTimeTablesColorsList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding =
            RawMultiplicationTableBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val context = holder.binding.txtTable.context
        val colors = if (list.lastIndex < position){
            ColorData(R.color.deep_purple_100, R.color.deep_purple_A700, R.color.deep_purple_50)
        }else{
            list[position]
        }
        holder.binding.txtTitle.setTextColor(ContextCompat.getColor(context, colors.darkColor))
        colors.bgColor?.let { holder.binding.conMain.setCardBackgroundColor(ContextCompat.getColor(context, it)) }
        holder.binding.cardHeader.setCardBackgroundColor(ContextCompat.getColor(context, colors.color))
        holder.binding.txtTable.text = ViewUtils.getTable(context, (position + 1), -1, null, R.color.colorListingHeading,colors.darkColor)
        holder.binding.txtTitle.text = "${(position + 1)}x"
    }

    class FormViewHolder(itemBinding: RawMultiplicationTableBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawMultiplicationTableBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return table
    }
}
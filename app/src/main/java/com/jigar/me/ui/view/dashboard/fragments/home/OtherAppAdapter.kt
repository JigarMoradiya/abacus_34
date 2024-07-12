package com.jigar.me.ui.view.dashboard.fragments.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.local.data.HomeMenu
import com.jigar.me.data.local.data.OtherApps
import com.jigar.me.databinding.RawHomeMenuBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick

class OtherAppAdapter(
    private var listData: List<OtherApps>,private val mListener: OnItemClickListener,val dimension: Int
) : RecyclerView.Adapter<OtherAppAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onItemOtherAppClick(data: OtherApps)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawHomeMenuBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        holder.binding.conMain.layoutParams.width = dimension
        holder.binding.conMain.layoutParams.height = dimension
        val data = listData[position]
        holder.binding.imgMenu.setImageResource(data.image)
        holder.binding.root.onClick {
            mListener.onItemOtherAppClick(data)
        }
    }

    class FormViewHolder(itemBinding: RawHomeMenuBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawHomeMenuBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
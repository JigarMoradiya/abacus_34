package com.jigar.me.ui.view.dashboard.fragments.home

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.HomeMenu
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawHomeMenuBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class HomeMenuAdapter(
    private var listData: List<HomeMenu>,
    val prefManager: AppPreferencesHelper,
    private val mListener: OnItemClickListener,
    var dimension: Int
) : RecyclerView.Adapter<HomeMenuAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onItemHomeMenuClick(data: HomeMenu)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawHomeMenuBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val context = holder.binding.conMain.context
        if (dimension < 150){
            dimension = context.resources.getDimension(R.dimen.home_menu).toInt()
        }
        holder.binding.conMain.layoutParams.width = dimension
        holder.binding.conMain.layoutParams.height = dimension

        val data = listData[position]
        holder.binding.imgMenu.setImageResource(data.image)
        if (data.tag.isEmpty()){
            holder.binding.txtTag.hide()
        }else {
            holder.binding.txtTag.show()
            holder.binding.txtTag.text = data.tag
            if (data.tag.equals(context.getString(R.string.new_),true) || data.tag.equals(context.getString(R.string.most_liked),true)){
                holder.binding.txtTag.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.red_900))
            }else{
                holder.binding.txtTag.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.tag_bg))
            }

        }
        holder.binding.conMain.onClick {
            mListener.onItemHomeMenuClick(listData[position])
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
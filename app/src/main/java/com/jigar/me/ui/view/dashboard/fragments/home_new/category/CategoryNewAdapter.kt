package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.databinding.RawCategoryNewBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class CategoryNewAdapter(
    private var listData: List<Category>,
    private val mListener: (Int,Int, Category) -> Unit
) : RecyclerView.Adapter<CategoryNewAdapter.ViewHolder>() {
    var selectedPosition = 0

    fun setData(categoryList: List<Category>) {
        listData = categoryList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = RawCategoryNewBinding.inflate(parent.context.layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding){
        val data = listData[position]
        dataModel = data
        if (selectedPosition == position){
            imgArrow.show()
        }else{
            imgArrow.invisible()
        }
        root.onClick {
            if (selectedPosition != position){
                val previousPosition = selectedPosition
                selectedPosition = position
                imgArrow.show()
                mListener.invoke(position,previousPosition,listData[position])
            }
        }
    }

    class ViewHolder(itemBinding: RawCategoryNewBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawCategoryNewBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
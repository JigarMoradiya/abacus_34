package com.jigar.me.ui.view.dashboard.fragments.page.adapter

import android.graphics.PorterDuff
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.model.pages.CategoryPages
import com.jigar.me.databinding.RawCategoryPagelistBinding
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show


class CategoryPageListAdapter(
    private var listData: List<CategoryPages>,
    private val mListener: OnItemClickListener,
    val themeContent : AbacusContent? = null
) : RecyclerView.Adapter<CategoryPageListAdapter.FormViewHolder>() {
    var selectedPosition = 0
    interface OnItemClickListener {
        fun onCategoryItemClick(data: CategoryPages)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawCategoryPagelistBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val context = holder.binding.root.context
        with(holder.binding){
            title = listData[position].category_name
            if (selectedPosition == position){
                val typeface = ResourcesCompat.getFont(context, R.font.font_extra_bold)
                txtTitle.typeface = typeface
                themeContent?.resetBtnColor8?.let{
                    txtTitle.setTextColor(ContextCompat.getColor(context,it))
                    imgArrow.show()
                    imgArrow.setColorFilter(ContextCompat.getColor(context,it), PorterDuff.Mode.SRC_IN)
                }
            }else{
                imgArrow.invisible()
                val typeface = ResourcesCompat.getFont(context, R.font.font_medium)
                txtTitle.typeface = typeface
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorListingHeading))
            }

            if (position == listData.lastIndex){
                viewDivider.invisible()
            }else{
                viewDivider.show()
            }

            txtTitle.onClick {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
//                notifyItemRangeChanged(0,listData.size)
                mListener.onCategoryItemClick(listData[position])
            }
        }

    }

    class FormViewHolder(itemBinding: RawCategoryPagelistBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawCategoryPagelistBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

}
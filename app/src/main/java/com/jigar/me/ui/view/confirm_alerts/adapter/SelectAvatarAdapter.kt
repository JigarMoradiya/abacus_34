package com.jigar.me.ui.view.confirm_alerts.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AvatarImages
import com.jigar.me.databinding.RowAvatarBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick


class SelectAvatarAdapter(
    private var dataList: ArrayList<AvatarImages>,
    index: Int?
) : RecyclerView.Adapter<SelectAvatarAdapter.ViewHolder>() {

    var selectedPosition = -1
    init {
        if (index != null){
            selectedPosition = index
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val binding = RowAvatarBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding) {
            val context = root.context
            with(dataList[position]) {
                imgProfile.setImageResource(image)
                if (selectedPosition == position){
                    cardProfileImage.strokeWidth = context.resources.getDimension(R.dimen.card_stork).toInt()
                }else{
                    cardProfileImage.strokeWidth = 0
                }
                root.onClick {
                    val previousPos = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPos)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class ViewHolder(val binding: RowAvatarBinding) :
        RecyclerView.ViewHolder(binding.root)
}
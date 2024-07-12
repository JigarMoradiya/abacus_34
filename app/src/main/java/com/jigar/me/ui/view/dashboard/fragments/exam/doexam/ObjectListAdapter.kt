package com.jigar.me.ui.view.dashboard.fragments.exam.doexam

import android.net.Uri
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jigar.me.R
import com.jigar.me.data.local.data.DataObjectsSize
import com.jigar.me.data.local.data.ImagesDataObjects
import com.jigar.me.databinding.RawObjectListBinding
import com.jigar.me.utils.extensions.*

class ObjectListAdapter(private var totalItem: Int, private var imageData : ImagesDataObjects?, private var size : DataObjectsSize) :
    RecyclerView.Adapter<ObjectListAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawObjectListBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val options = RequestOptions()
            .centerInside()
            .error(R.drawable.animal_ant)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        val context = holder.itemView.context
        holder.binding.imgObjectSmall.hide()
        holder.binding.imgObjectMedium.hide()
        holder.binding.imgObjectLarge.hide()
        holder.binding.imgObjectExtraSmall.hide()
        if (size == DataObjectsSize.ExtraSmall){
            Glide.with(context).load(Uri.parse("file:///android_asset/objects/${imageData?.image}.webp")).apply(options).into(holder.binding.imgObjectExtraSmall)
            holder.binding.imgObjectExtraSmall.show()
        }else if (size == DataObjectsSize.Medium){
            Glide.with(context).load(Uri.parse("file:///android_asset/objects/${imageData?.image}.webp")).apply(options).into(holder.binding.imgObjectMedium)
            holder.binding.imgObjectMedium.show()
        }else if (size == DataObjectsSize.Large){
            Glide.with(context).load(Uri.parse("file:///android_asset/objects/${imageData?.image}.webp")).apply(options).into(holder.binding.imgObjectLarge)
            holder.binding.imgObjectLarge.show()
        }else {
            Glide.with(context).load(Uri.parse("file:///android_asset/objects/${imageData?.image}.webp")).apply(options).into(holder.binding.imgObjectSmall)
            holder.binding.imgObjectSmall.show()
        }

        holder.binding.imgObjectExtraSmall.onClick {
            startAnim(this)
        }
        holder.binding.imgObjectMedium.onClick {
            startAnim(this)
        }
        holder.binding.imgObjectSmall.onClick {
            startAnim(this)
        }
        holder.binding.imgObjectLarge.onClick {
            startAnim(this)
        }
    }

    private fun startAnim(imgView: AppCompatImageView) {
        // shake animation
        imgView.setExamObjectShakeAnimation()
    }

    class FormViewHolder(itemBinding: RawObjectListBinding) :RecyclerView.ViewHolder(itemBinding.root){
        var binding: RawObjectListBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return totalItem
    }

}
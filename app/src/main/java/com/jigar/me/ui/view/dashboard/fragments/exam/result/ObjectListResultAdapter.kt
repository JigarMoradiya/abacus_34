package com.jigar.me.ui.view.dashboard.fragments.exam.result

import android.net.Uri
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jigar.me.R
import com.jigar.me.data.local.data.ImagesDataObjects
import com.jigar.me.databinding.RawObjectListResultBinding
import com.jigar.me.utils.extensions.layoutInflater

class ObjectListResultAdapter(private var totalItem: Int, private var imageData : ImagesDataObjects?) :
    RecyclerView.Adapter<ObjectListResultAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawObjectListResultBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val options = RequestOptions()
            .centerInside()
            .error(R.drawable.animal_ant)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
        val context = holder.itemView.context
        Glide.with(context).load(Uri.parse("file:///android_asset/objects/${imageData?.image}.webp")).apply(options).into(holder.binding.imgObjectExtraSmall)
    }

    class FormViewHolder(itemBinding: RawObjectListResultBinding) :RecyclerView.ViewHolder(itemBinding.root){
        var binding: RawObjectListResultBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return totalItem
    }

}
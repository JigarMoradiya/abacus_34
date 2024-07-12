package com.jigar.me.ui.view.dashboard.fragments.youtubevideo

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.model.VideoData
import com.jigar.me.databinding.RawVideoDataBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick

class YoutubeVideoListAdapter(
    private var listData: List<VideoData>,
    private val mListener: OnItemClickListener
) :
    RecyclerView.Adapter<YoutubeVideoListAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onVideoItemClick(data: VideoData)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawVideoDataBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: VideoData = listData[position]
        holder.binding.title = data.txt
//        holder.binding.video = "https://img.youtube.com/vi/${data.id}/sddefault.jpg"
        holder.binding.video = "https://img.youtube.com/vi/${data.id}/maxresdefault.jpg"
//        holder.binding.video = "https://i.ytimg.com/vi_webp/${data.id}/maxresdefault.webp"

        holder.binding.root.onClick {
            mListener.onVideoItemClick(data)
        }
    }

    class FormViewHolder(itemBinding: RawVideoDataBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawVideoDataBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
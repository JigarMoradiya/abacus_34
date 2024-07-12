package com.jigar.me.ui.view.dashboard.fragments.practise_material.adater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.ImageData
import com.jigar.me.data.model.DownloadMaterialData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawDownloadmaterialChildBinding
import com.jigar.me.databinding.RawDownloadmaterialParentBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class MaterialDownloadAdapter(
    private var listData: List<DownloadMaterialData>,
    val prefManager : AppPreferencesHelper,
    private val mListener: OnItemClickListener
) : RecyclerView.Adapter<MaterialDownloadAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(parentPos: Int, position: Int)
        fun onItemDownloadClick(position: Int)
    }
    private var downloadType = ""
    fun setData(listData: List<DownloadMaterialData>, downloadType: String) {
        this.listData = listData
        this.downloadType = downloadType
        notifyItemRangeChanged(0,listData.size)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RawDownloadmaterialParentBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = listData[position]
        with(holder.binding){
            txtTitle.text = data.groupName
            holder.binding.txtTotal.text = "(${data.imagesList.size} ${holder.binding.txtTotal.context.getString(R.string.worksheet)})"

            val materialImagesListAdapter = ImageListAdapter(data.imagesList,position,mListener,data,prefManager)
            recyclerViewImages.adapter = materialImagesListAdapter

            if (downloadType == AppConstants.extras_Comman.DownloadType_Nursery) {
                holder.binding.txtDownloadNow.show()
            } else {
                holder.binding.txtDownloadNow.hide()
            }

            txtDownloadNow.setOnClickListener {
                mListener.onItemDownloadClick(position)
            }
        }
    }

    class FormViewHolder(itemBinding: RawDownloadmaterialParentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawDownloadmaterialParentBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ImageListAdapter(
        private var listData: List<ImageData>,
        private val parentPos: Int,
        private val mListener: OnItemClickListener,
        private val parentData: DownloadMaterialData,
        val prefManager : AppPreferencesHelper
    ) : RecyclerView.Adapter<ImageListAdapter.FormViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): FormViewHolder {
            val binding = RawDownloadmaterialChildBinding.inflate(parent.context.layoutInflater,parent,false)
            return FormViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
            val data = listData[position]
            holder.binding.data = data
            holder.binding.imagePath = prefManager.getCustomParam(AppConstants.AbacusProgress.iPath,"")+parentData.imagePath
            holder.binding.root.setOnClickListener {
                mListener.onItemClick(parentPos,position)
            }
        }

        class FormViewHolder(itemBinding: RawDownloadmaterialChildBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            var binding: RawDownloadmaterialChildBinding = itemBinding
        }

        override fun getItemCount(): Int {
            return listData.size
        }


    }
}
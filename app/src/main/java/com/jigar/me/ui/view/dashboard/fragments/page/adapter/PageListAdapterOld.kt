package com.jigar.me.ui.view.dashboard.fragments.page.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.pages.CategoryPages
import com.jigar.me.data.model.pages.Pages
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawPagelistChildBinding
import com.jigar.me.databinding.RawPagelistParentBinding
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.CommonUtils.getCurrentSumFromPref
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater

class PageListAdapterOld(
    private var listData: List<CategoryPages>,
    private val mListener: OnItemClickListener,
    val prefManager: AppPreferencesHelper,
    val type :Int = 0
) :
    RecyclerView.Adapter<PageListAdapterOld.FormViewHolder>() {
    interface OnItemClickListener {
        fun onPageItemClick(data: Pages,pageId : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawPagelistParentBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: CategoryPages = listData[position]
        holder.binding.title = data.category_name
        val additionSubtractionPageListAdapter = PageListChildAdapter(data.pages,mListener,prefManager,type)
        holder.binding.recyclerviewPage.adapter = additionSubtractionPageListAdapter
    }

    class FormViewHolder(itemBinding: RawPagelistParentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPagelistParentBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }


    class PageListChildAdapter(
        private var listData: List<Pages>,
        private val mListener: OnItemClickListener,
        val prefManager: AppPreferencesHelper,
        val type :Int = 0
    ) :
        RecyclerView.Adapter<PageListChildAdapter.FormViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
            val binding = RawPagelistChildBinding.inflate(parent.context.layoutInflater,parent,false)
            return FormViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
            val data: Pages = listData[position]
            val context = holder.binding.txtTitle.context
            when (type) {
                AppConstants.HomeClicks.Menu_Division -> {
                    holder.binding.desc = data.page_name
                    holder.binding.txtTitle.hide()
                }
                AppConstants.HomeClicks.Menu_Multiplication -> {
                    holder.binding.desc = data.page_name
                    holder.binding.txtTitle.hide()
                }
                AppConstants.HomeClicks.Menu_Number -> {
                    if (data.page_name == null) {
                        holder.binding.title = data.from.toString() + " " + context.resources.getString(
                            R.string.to_) + " " + data.to+" "+context.resources.getString(R.string.numbers)
                    } else {
                        holder.binding.title = data.page_name
                    }
                    holder.binding.desc = data.from.toString() + " - " + data.to
        //                if (prefManager.getCustomParam(Constants.appLanguage,"") == Constants.appLanguage_arebic){
        //                    holder.binding.desc = data.to.toString() + " - " + data.from
        //                }else{
        //                    holder.binding.desc = data.from.toString() + " - " + data.to
        //                }
                }
                else -> {
                    holder.binding.title = data.page_name
                    holder.binding.desc = data.descriptions
                }
            }
            val pageId : String = when (type) {
                AppConstants.HomeClicks.Menu_Division -> {
                    "Devide_Page ${data.page_id}"
                }
                AppConstants.HomeClicks.Menu_Multiplication -> {
                    "Multilication_Page ${data.page_id}"
                }
                AppConstants.HomeClicks.Menu_Number -> {
                    "SingleDigit_Page ${data.page_id}"
                }
                else -> {
                    data.page_id?:""
                }
            }
            if (context.getCurrentSumFromPref(pageId) != null){
                CommonUtils.blinkView(holder.binding.imgContinueIndicator)
            }else{
                holder.binding.imgContinueIndicator.hide()
            }

            holder.binding.root.setOnClickListener {
                this.mListener.onPageItemClick(data,pageId)
            }
        }

        class FormViewHolder(itemBinding: RawPagelistChildBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            var binding: RawPagelistChildBinding = itemBinding
        }

        override fun getItemCount(): Int {
            return listData.size
        }

    }
}
package com.jigar.me.ui.view.dashboard.fragments.page.adapter

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.model.AdditionSubtractionAbacus
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawAbacusTempListBinding
import com.jigar.me.utils.extensions.clipboardManager
import com.jigar.me.utils.extensions.layoutInflater


class AbacusTempListAdapter(
    private var listData: List<AdditionSubtractionAbacus>,
    private val mListener: OnItemClickListener, val prefManager : AppPreferencesHelper, val type :Int = 0
) :
    RecyclerView.Adapter<AbacusTempListAdapter.FormViewHolder>() {
    interface OnItemClickListener {
//        fun onPageItemClick(data: Pages,pageId : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = RawAbacusTempListBinding.inflate(parent.context.layoutInflater, parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data: AdditionSubtractionAbacus = listData[position]
        val context = holder.binding.txtTitle.context
        holder.binding.title = data.getQuestion()
        holder.binding.root.setOnClickListener {
            val clip = ClipData.newPlainText("Copied Text", data.getQuestion())
            context.clipboardManager.setPrimaryClip(clip)
        }

    }

    class FormViewHolder(itemBinding: RawAbacusTempListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawAbacusTempListBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
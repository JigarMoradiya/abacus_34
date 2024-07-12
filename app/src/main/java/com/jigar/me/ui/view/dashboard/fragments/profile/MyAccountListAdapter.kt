package com.jigar.me.ui.view.dashboard.fragments.profile

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.local.data.MyAccountMenu
import com.jigar.me.databinding.RowMyAccountChildBinding
import com.jigar.me.databinding.RowMyAccountParentBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick

class MyAccountListAdapter(
    private var listData: List<MyAccountMenu>,
    private val mListener: OnItemClickListener
) : RecyclerView.Adapter<MyAccountListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onMenuItemClick(tag: String)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = RowMyAccountParentBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            with(listData[position]) {
                dataModel = this
                val sideMenuListAdapter = SubMenuListAdapter(subMenu,mListener)
                rvMenu.adapter = sideMenuListAdapter
            }
        }
    }

    class ViewHolder(val binding: RowMyAccountParentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return listData.size
    }

    class SubMenuListAdapter(
        private var listData: List<MyAccountMenu>,
        private val mListener: OnItemClickListener
    ) : RecyclerView.Adapter<SubMenuListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewHolder {
            val binding = RowMyAccountChildBinding.inflate(parent.context.layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.binding) {
                with(listData[position]) {
                    dataModel = this
                    root.onClick {
                        mListener.onMenuItemClick(listData[position].tag)
                    }
                }
            }
        }

        class ViewHolder(val binding: RowMyAccountChildBinding) : RecyclerView.ViewHolder(binding.root)

        override fun getItemCount(): Int {
            return listData.size
        }

    }


}
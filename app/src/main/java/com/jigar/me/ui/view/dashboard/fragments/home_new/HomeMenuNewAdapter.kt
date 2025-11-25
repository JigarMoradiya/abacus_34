package com.jigar.me.ui.view.dashboard.fragments.home_new

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.RawHomeMenuNewBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class HomeMenuNewAdapter(
    private var listData: List<Level>,
    val prefManager: AppPreferencesHelper,
    var dimension: Int,
    private val mListener: (Int, Level) -> Unit
) : RecyclerView.Adapter<HomeMenuNewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = RawHomeMenuNewBinding.inflate(parent.context.layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding){
        val data = listData[position]
        dataModel = data

        conMain.onClick {
            mListener.invoke(position,listData[position])
        }
    }

    class ViewHolder(itemBinding: RawHomeMenuNewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawHomeMenuNewBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
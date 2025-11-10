package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.annotation.SuppressLint
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.RawPagelistNewBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class PagesNewAdapter(
    private var listData: List<DisplayPages>,
    var allSetList: List<Set>,
    private val mListener: (String,Int,Int,Set,DisplayPages) -> Unit
) : RecyclerView.Adapter<PagesNewAdapter.ViewHolder>() {
    fun setData(pagesList: List<DisplayPages>) {
        (listData as ArrayList<DisplayPages>).clear()
        (listData as ArrayList<DisplayPages>).addAll(pagesList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawPagelistNewBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val data: DisplayPages = listData[position]
        val context = txtTitle.context
//        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f) // Reset
//        txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f) // Reset
        dataModel = data

        val list = if (data.setList.isNullOrEmpty()){
            val lists = allSetList.filter { it.page_id == data.id }
            listData[position].setList = Gson().toJson(lists)
            lists
        }else{
            val lists = Gson().fromJson<List<Set>>(listData[position].setList, object : TypeToken<List<Set>>(){}.type)
            lists
        }

        if (list.isNotNullOrEmpty()){
            val spanCount = if (list.size == 1 || list.size == 2  || list.size == 3) list.size else if (list.size == 4) 2 else 3
            recyclerviewSet.layoutManager = GridLayoutManager(context,spanCount)
            val setsAdapter = SetsAdapter(list) { type, setPosition, setData ->
                mListener.invoke(type,holder.layoutPosition,setPosition,setData,data )
            }
            recyclerviewSet.adapter = setsAdapter
            recyclerviewSet.show()
        }else{
            recyclerviewSet.hide()
        }
    }

    class ViewHolder(itemBinding: RawPagelistNewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RawPagelistNewBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun updateSetDetail(clickedPagePosition: Int,setList: ArrayList<Set>) {
        allSetList = setList
        listData[clickedPagePosition].setList = null
        notifyItemChanged(clickedPagePosition)
    }
}
package com.jigar.me.ui.view.dashboard.fragments.home_new.category

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.databinding.RawPagelistNewBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.show

class PagesNewAdapter(
    private var listData: List<Pages>,
    private var allSetList: List<Set>,
    private val mListener: (Int,Int,Set,Pages) -> Unit
) : RecyclerView.Adapter<PagesNewAdapter.ViewHolder>() {
    fun setData(pagesList: List<Pages>) {
        listData = pagesList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawPagelistNewBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val data: Pages = listData[position]
        val context = txtTitle.context
        dataModel = data
        val list = allSetList.filter { it.page_id == data.id }
        if (list.isNotNullOrEmpty()){
            val spanCount = if (list.size == 1 || list.size == 2  || list.size == 3) list.size else if (list.size == 4) 2 else if (list.size == 5 || list.size == 6) 3 else if (list.size == 7 || list.size == 8) 4 else 3
            recyclerviewSet.layoutManager = GridLayoutManager(context,spanCount)
            val setsAdapter = SetsAdapter(list) { setPosition, setData ->
                mListener.invoke(position,setPosition,setData,data )
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
}
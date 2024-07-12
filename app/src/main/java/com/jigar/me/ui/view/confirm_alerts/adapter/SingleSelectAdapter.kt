package com.jigar.me.ui.view.confirm_alerts.adapter

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.model.data.KeyValuePair
import com.jigar.me.databinding.RowSingleSelectionBinding
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import java.util.Locale


class SingleSelectAdapter(
    private var dataList: ArrayList<KeyValuePair>,
    selectedIdKey: String? = null,val listener : ItemSelectInterface? = null
) : RecyclerView.Adapter<SingleSelectAdapter.ViewHolder>(), Filterable {

    interface ItemSelectInterface {
        fun onItemSelectClick(selectedData : KeyValuePair)
    }
    private var dataListUpdated: ArrayList<KeyValuePair> = dataList
    private var selectedId : String? = null
    init {
        if (selectedIdKey != null){
            this.selectedId = selectedIdKey
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        val binding = RowSingleSelectionBinding.inflate(parent.context.layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.binding) {
            val context = root.context
            val typeface = if (selectedId == dataListUpdated[position].key){
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorAccent))
                ResourcesCompat.getFont(context, R.font.font_semibold)
            }else{
                txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorEditTextBlack_33))
                ResourcesCompat.getFont(context, R.font.font_regular)
            }
            keyValuePair = dataListUpdated[position]
            txtTitle.typeface = typeface
            txtTitle.onClick {
                listener?.onItemSelectClick(dataListUpdated[position])
            }
        }

    }


    override fun getItemCount(): Int {
        return dataListUpdated.size
    }


    inner class ViewHolder(val binding: RowSingleSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                dataListUpdated = if (charString.isEmpty()) {
                    dataList
                } else {
                    val filteredList: ArrayList<KeyValuePair> = arrayListOf()
                    dataList.map {
                        if (it.name?.lowercase(Locale.getDefault())?.contains(charString.lowercase(Locale.getDefault())) == true) {
                            filteredList.add(it)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = dataListUpdated
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                dataListUpdated = filterResults.values as ArrayList<KeyValuePair>
                notifyDataSetChanged()
            }
        }
    }
}
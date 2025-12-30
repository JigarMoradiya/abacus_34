package com.jigar.me.ui.view.dashboard.fragments.settings

import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowDefaultThemeBinding
import com.jigar.me.utils.extensions.invisible
import com.jigar.me.utils.extensions.layoutInflater
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.show

class AbacusThemeSelectionsAdapter(
    private var questions: List<AbacusContent>, private val mListener: OnItemClickListener,
    private val isPaidTheme : Boolean = false,var selectedTheme : String
) : RecyclerView.Adapter<AbacusThemeSelectionsAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onThemePoligonItemClick(data: AbacusContent)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FormViewHolder {
        val binding = RowDefaultThemeBinding.inflate(parent.context.layoutInflater,parent,false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val data = questions[position]
        with(holder.binding){
            val context = root.context
            if (isPaidTheme){
                imgAbacus.layoutParams = RelativeLayout.LayoutParams((context.resources.getDimension(R.dimen.bead_column_paid).toInt()), RelativeLayout.LayoutParams.WRAP_CONTENT)
                imgAbacus.setImageResource(data.beadImage)
            }else{
                imgAbacus.apply {
                    layoutParams = RelativeLayout.LayoutParams((context.resources.getDimension(R.dimen.bead_column).toInt()), RelativeLayout.LayoutParams.WRAP_CONTENT)
                    setImageResource(data.beadImage)
                }
            }

            if (selectedTheme == data.type){
                imgTick.show()
            }else{
                imgTick.invisible()
            }
            root.onClick {
                imgTick.show()
                mListener.onThemePoligonItemClick(data)
            }
        }

    }

    class FormViewHolder(
        itemBinding: RowDefaultThemeBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: RowDefaultThemeBinding = itemBinding
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun updateSelection(selectedThemeOld : String, selectedThemeNew : String) {
        selectedTheme = selectedThemeNew
        questions.indexOfFirst { it.type == selectedThemeOld }.also {
            if (it != -1){
                notifyItemChanged(it)
            }
        }
    }
}
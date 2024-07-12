package com.jigar.me.ui.view.dashboard.fragments.settings

import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowDefaultThemeBinding
import com.jigar.me.utils.extensions.*

class AbacusThemeSelectionsAdapter(
    private var questions: List<AbacusContent>, private val mListener: OnItemClickListener, private var currentPos : Int = 0,
    private val isPaidTheme : Boolean = false
) : RecyclerView.Adapter<AbacusThemeSelectionsAdapter.FormViewHolder>() {
    interface OnItemClickListener {
        fun onThemePoligonItemClick(data: AbacusContent)
    }

    fun selectedPos(newPos : Int){
        val previousPos = currentPos
        currentPos = newPos
        notifyItemChanged(previousPos)
        notifyItemChanged(currentPos)
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
            }else{
                imgAbacus.layoutParams = RelativeLayout.LayoutParams((context.resources.getDimension(R.dimen.bead_column).toInt()), RelativeLayout.LayoutParams.WRAP_CONTENT)
            }
            imgAbacus.setImageResource(data.beadImage)
            if (currentPos == position){
                imgTick.show()
            }else{
                imgTick.invisible()
            }
            root.onClick {
                val previousPos = currentPos
                currentPos = position
                notifyItemChanged(previousPos)
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
}
package com.jigar.me.ui.view.dashboard.fragments.settings

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.databinding.RowDefaultThemeBinding
import com.jigar.me.utils.extensions.*
import androidx.core.graphics.toColorInt
import com.jigar.me.utils.ImageUtils
import com.jigar.me.utils.ImageUtils.linearGradientForAngle
import kotlin.math.cos

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
//                    val drawable = AppCompatResources.getDrawable(context, data.beadImage)
//                    drawable?.let{
//                        val bitmap = ImageUtils.drawableToBitmap(drawable,drawable.intrinsicWidth, drawable.intrinsicHeight)
//                        val shader = linearGradientForAngle(context,data, bitmap.width.toFloat(), bitmap.height.toFloat(), 90F)
//                        val paint = Paint().apply {
//                            isAntiAlias = true
//                            this.shader = shader
//                            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//                        }
//                        val canvas = Canvas(bitmap)
//                        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
//                        setImageBitmap(bitmap)
//                    }
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
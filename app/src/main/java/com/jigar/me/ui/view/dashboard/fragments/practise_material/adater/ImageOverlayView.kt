package com.jigar.me.ui.view.dashboard.fragments.practise_material.adater

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.jigar.me.R

class ImageOverlayView : RelativeLayout {
    private var tvDescription: TextView? = null
    private var tvTitle: TextView? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    fun setDescription(description: String?) {
        tvDescription!!.text = description
    }

    fun setTitleText(title: String?) {
        tvTitle!!.text = title
    }

    private fun init() {
        val view = inflate(context, R.layout.view_image_overlay, this)
        tvDescription = view.findViewById<View>(R.id.txtDesc) as TextView
        tvTitle = view.findViewById<View>(R.id.txtTitle) as TextView
    }
}
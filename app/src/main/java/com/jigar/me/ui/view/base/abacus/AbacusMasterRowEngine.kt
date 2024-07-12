package com.jigar.me.ui.view.base.abacus

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.jigar.me.R
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.AbacusContent
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import java.util.*

class AbacusMasterRowEngine(
    private val context: Context,
    private val position: Point,
    selectedPosition: Int,
    private val beadWidth: Int,
    beadHeight1: Int,
    private var beadDrawables: Array<Drawable?>,
    private val isBeadStackFromBottom: Boolean,
    private var numBeads: Int,
    private val roadDrawable: Drawable?,
    private var numColumns: Int,
    private var extraHeight : Int,
    private val beadType : AbacusBeadType,
    private val abacusContent: AbacusContent
) {

    var tempBeads: IntArray? = null
    var beadHeight: Int = 0
    var beads: IntArray

    /**
     * Total width of the row in real device pixels
     */
    private var height = 0

    /**
     * The beads' width in real device pixels
     */

    /**
     * The beads' height in real device pixels
     */

    /**
     * The number of beads on this row
     */

    /**
     * The horizontal location of the center of each bead on the row
     */
    private lateinit var tempBeads_new: IntArray

    private var beadDrawables_eyes: ArrayList<Drawable?> = arrayListOf()
    private var beadDrawables_eyes_smaile: ArrayList<Drawable?> = arrayListOf()
    private var selectedPositions: ArrayList<Int>? = null
    private var theme = AppConstants.Settings.theam_Default
    init{
        beadHeight = beadHeight1
        selectedPositions = ArrayList()
        height = (numBeads + 1) * beadHeight
        beadDrawables_eyes = ArrayList()
        beadDrawables_eyes_smaile = ArrayList()
        theme = AppPreferencesHelper(context,AppConstants.PREF_NAME)
            .getCustomParam(AppConstants.Settings.TheamTempView, AppConstants.Settings.theam_Default)
        for(i in 0..3){
            when {
                theme.contains(AppConstants.Settings.theam_Poligon_default, ignoreCase = true) -> {
                    beadDrawables_eyes.add(ContextCompat.getDrawable(context, abacusContent.topBeadClose))
                    beadDrawables_eyes_smaile.add(ContextCompat.getDrawable(context,abacusContent.topBeadOpen))
                }
                else -> {
                    beadDrawables_eyes.add(ContextCompat.getDrawable(context, abacusContent.bottomBeadClose[i]))
                    beadDrawables_eyes_smaile.add(ContextCompat.getDrawable(context,abacusContent.bottomBeadOpen[i]))
                }
            }
        }

        beads = IntArray(numBeads)
        setSelectedPosition(selectedPosition)
    }

    fun setSelectedPosition(selectedPosition: Int) {
        val heightDiff = if (isBeadStackFromBottom) height - beadHeight * numBeads else 0
        val list_abacus = ArrayList<String?>()
        tempBeads_new = IntArray(4)
        //        tempBeads_new[0] =0;
        for (i in 0 until numBeads) {
            if (i <= selectedPosition && !isBeadStackFromBottom) {
                // this is ok. for uppar portion
                beads[i] = heightDiff + i * beadHeight + beadHeight
            } else if (i <= selectedPosition && isBeadStackFromBottom) {
                beads[i] = i * beadHeight
            } else {
                beads[i] = heightDiff + i * beadHeight
            }
            if (isBeadStackFromBottom) {
                tempBeads_new[0] = 0
            }
            tempBeads_new[i] = heightDiff + i * beadHeight
            list_abacus.add("value" + beads[i])
        }
        if (AppPreferencesHelper(context, AppConstants.PREF_NAME)
                .getCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom$numColumns","").isEmpty()
        ) {
            val allIds = TextUtils.join(",", list_abacus)
            AppPreferencesHelper(context, AppConstants.PREF_NAME)
                .setCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom$numColumns", allIds)
        }
        numColumns--
    }

    fun getPosition(): Point {
        return position
    }

    fun moveBeadTo(i: Int, y: Int): Int {
        return moveBeadToInternal(i, y - position.y)
    }

    fun moveBeadToInternal(i: Int, y1: Int): Int {
        // Don't allow beads to be dragged off the ends of the row
        var y = y1
        y = if (y >= 0) y else 0
        y = if (y <= height - beadHeight) y else height - beadHeight

        // Handle collisions between beads
        if (y > beads[i]) {
            // ... when moving right:
            if (i < numBeads - 1
                && y + beadHeight > beads[i + 1]
            ) {
                y = moveBeadToInternal(i + 1, y + beadHeight) - beadHeight
            }
        } else if (y < beads[i]) {
            // ... when moving left:
            if (i > 0
                && y - beadHeight < beads[i - 1]
            ) {
                y = moveBeadToInternal(i - 1, y - beadHeight) + beadHeight
            }
        }
        return y.also { beads[i] = it }
    }


    /**
     * Get the row's current value
     *
     * @return Current value set on the row, or -1 if indeterminate
     */
    fun getValue(): Int {
        var value = 0
        if (beads[0] >= 0.5 * beadHeight) {
            value = numBeads
        } else {
            for (i in 0 until numBeads - 1) {
                if (beads[i + 1] - beads[i] >= 1.5 * beadHeight) {
                    value = numBeads - 1 - i
                    break
                }
                if (beads[numBeads - 1] <= height - 1.5 * beadHeight) {
                    value = 0
                    break
                }
            }
        }

        return if (isBeadStackFromBottom) {
            numBeads - value
        } else {
            value
        }
    }

    fun draw(canvas: Canvas?, abacusContent: AbacusContent) {
        val rowSpacing = abacusContent.beadSpace
        val road = roadDrawable!!
        // TODO
        val rowThickness = road.minimumWidth
        val divideThickness = if (beadType == AbacusBeadType.Exam || beadType == AbacusBeadType.ExamResult || beadType == AbacusBeadType.SettingPreview){
            3f
        }else{
            2.5f
        }
        val startX = position.x + beadWidth / 2 - (rowThickness.toFloat() / divideThickness).toInt()
        val endX = position.x + beadWidth / 2 + (rowThickness.toFloat() / divideThickness).toInt()
        road.setBounds(startX,position.y,endX,position.y + height + extraHeight)
        road.draw(canvas!!)


        var drawablePos = 0
        val listAbacus = ArrayList<String>()
        var tempvalue = -1
        val str: String = AppPreferencesHelper(context,AppConstants.PREF_NAME).getCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom","")
        if (str.isNotEmpty()) {
            val strAll = str.split(",".toRegex()).toTypedArray()
            val listTemp = ArrayList<String?>()
            for (i in 0 until numBeads) {
                listTemp.add("value" + beads[i])
            }
            val allIds = TextUtils.join(",", listTemp)
            for (j in strAll.indices) {
                if (!allIds.contains(strAll[j])) {
                    tempvalue = strAll[j].replace("value", "").toInt()
                    break
                }
            }
        }
        for (i in 0 until numBeads) {
            if (beadDrawables.size == drawablePos) {
                drawablePos = 0
            }

            val beadDrawable: Drawable? = if (isBeadStackFromBottom) {
                val temp = beads[i] + 2
                if (tempvalue > temp && tempvalue != -1) {
                    beadDrawables_eyes_smaile[drawablePos]!!
                } else {
                    beadDrawables_eyes[drawablePos]!!
                }
            } else {
                if (beads[i] > 0) {
                    ContextCompat.getDrawable(context,abacusContent.topBeadOpen)!!
                }else{
                    ContextCompat.getDrawable(context,abacusContent.topBeadClose)!!
                }
            }
//            var isBeadSelected = false
//            if (tempBeads != null && tempBeads!!.size > i && tempBeads!![i] != beads[i] && selectedBeadDrawable != null) {
//                /*bead is selected*/
//                when {
//                    !theme.equals(AppConstants.Settings.theam_shape, ignoreCase = true) -> {
////                        beadDrawable = selectedBeadDrawable
//                    }
//                }
//                isBeadSelected = true
//            }
//            if (isBeadSelected) {
//                beadDrawable?.setBounds(
//                    position.x + rowSpacing / 2,
//                    position.y + beads[i] + if (!isBeadStackFromBottom) extraHeight else 0,
//                    position.x + beadWidth - rowSpacing / 2,
//                    position.y + beads[i] + beadHeight + if (!isBeadStackFromBottom) extraHeight else 0
//                )
//                canvas.let { beadDrawable?.draw(it) }
//            }

            beadDrawable?.setBounds(
                position.x + rowSpacing / 2,
                position.y + beads[i] + if (!isBeadStackFromBottom) extraHeight else 0,
                position.x + beadWidth - rowSpacing / 2,
                position.y + beads[i] + beadHeight + if (!isBeadStackFromBottom) extraHeight else 0
            )
            canvas.let { beadDrawable?.draw(it) }

            // TODO Draw direction
            drawablePos++

            if (isBeadStackFromBottom && AppPreferencesHelper(context,AppConstants.PREF_NAME).getCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom","").isEmpty()) {
                listAbacus.add("value" + beads[i])
            }
        }
        if (isBeadStackFromBottom && AppPreferencesHelper(context, AppConstants.PREF_NAME).getCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom","").isEmpty()) {
            listAbacus.add("value0")
            val allIds = TextUtils.join(",", listAbacus)
            AppPreferencesHelper(context, AppConstants.PREF_NAME).setCustomParam(beadHeight.toString()+"_column$isBeadStackFromBottom",allIds)
        }
    }

    fun getBeadAt(x: Int, y: Int): Int {
        for (i in 0 until numBeads) {
            if (x >= position.x
                && x <= position.x + beadWidth
                && y >= position.y + beads[i]
                && y <= position.y + beads[i] + beadHeight
            ) {
                return i
            }
        }
        return -1
    }

    fun getBeadPosition(i: Int): Int {
        return beads[i]
    }
}
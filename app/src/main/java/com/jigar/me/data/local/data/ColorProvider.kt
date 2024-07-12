package com.jigar.me.data.local.data

import android.graphics.Color
import com.jigar.me.R

object ColorProvider {
    fun getTimeTablesColorsList() : ArrayList<ColorData> {
        val list = ArrayList<ColorData>()
        with(list) {
            add(ColorData(R.color.red_100, R.color.red_A700, R.color.red_50))
            add(ColorData(R.color.pink_100, R.color.pink_A700, R.color.pink_50))
            add(ColorData(R.color.purple_100, R.color.purple_A700, R.color.purple_50))
            add(ColorData(R.color.deep_purple_100, R.color.deep_purple_A700, R.color.deep_purple_50))
            add(ColorData(R.color.indigo_100, R.color.indigo_A700, R.color.indigo_50))
            add(ColorData(R.color.blue_100, R.color.blue_A700, R.color.blue_50))
            add(ColorData(R.color.light_blue_100, R.color.light_blue_A700, R.color.light_blue_50))
            add(ColorData(R.color.cyan_100, R.color.cyan_900, R.color.cyan_50))
            add(ColorData(R.color.teal_100, R.color.teal_900, R.color.teal_50))
            add(ColorData(R.color.green_100, R.color.green_900, R.color.green_50))
            add(ColorData(R.color.light_green_100, R.color.light_green_900, R.color.light_green_50))
            add(ColorData(R.color.lime_100, R.color.lime_900, R.color.lime_50))
            add(ColorData(R.color.yellow_100, R.color.yellow_900, R.color.yellow_50))
            add(ColorData(R.color.amber_100, R.color.amber_900, R.color.amber_50))
            add(ColorData(R.color.orange_100, R.color.orange_A700, R.color.orange_50))
            add(ColorData(R.color.deep_orange_100, R.color.deep_orange_900, R.color.deep_orange_50))
            add(ColorData(R.color.red_100, R.color.red_900, R.color.red_50))
            add(ColorData(R.color.brown_100, R.color.brown_900, R.color.brown_50))
            add(ColorData(R.color.blue_grey_100, R.color.blue_grey_900, R.color.blue_grey_50))
            add(ColorData(R.color.grey_200, R.color.grey_900, R.color.grey_100))
        }
        return list
    }
    /* purchase colors */
    fun getPurchaseColorsList() : ArrayList<ColorData>{
        val list = ArrayList<ColorData>()
        with(list) {
            add(ColorData(R.color.color3, R.color.darkColor3))
            add(ColorData(R.color.color1, R.color.darkColor1))
            add(ColorData(R.color.color2, R.color.darkColor2))
            add(ColorData(R.color.color4, R.color.darkColor4))
            add(ColorData(R.color.color5, R.color.darkColor5))
            add(ColorData(R.color.color6, R.color.darkColor6))
            add(ColorData(R.color.color9, R.color.darkColor9))
            add(ColorData(R.color.color8, R.color.darkColor8))
            add(ColorData(R.color.color10, R.color.darkColor10))
            add(ColorData(R.color.color7, R.color.darkColor7))
            add(ColorData(R.color.color11, R.color.darkColor11))
//            add(ColorData(R.color.color12,R.color.darkColor12))
            add(ColorData(R.color.color13, R.color.darkColor13))
            add(ColorData(R.color.color14, R.color.darkColor14))
            add(ColorData(R.color.color15, R.color.darkColor15))
        }
        return list
    }

    fun getColorList(): MutableList<Int>{
        val listColor: MutableList<Int> = arrayListOf()
        with(listColor) {
            add(Color.parseColor("#FFEBEE"))
            add(Color.parseColor("#EDE7F6"))
            add(Color.parseColor("#E0F2F1"))
            add(Color.parseColor("#FFF8E1"))
            add(Color.parseColor("#F9FBE7"))
            add(Color.parseColor("#F3E5F5"))
            add(Color.parseColor("#EFEBE9"))
            add(Color.parseColor("#E0F7FA"))
            add(Color.parseColor("#FFF3E0"))
            add(Color.parseColor("#FBE9E7"))
            add(Color.parseColor("#E8F5E9"))
            add(Color.parseColor("#E3F2FD"))
            add(Color.parseColor("#FCE4EC"))
            add(Color.parseColor("#E8EAF6"))
            add(Color.parseColor("#ECEFF1"))
        }
        return listColor
    }
}
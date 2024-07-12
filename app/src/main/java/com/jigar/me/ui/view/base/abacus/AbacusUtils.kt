package com.jigar.me.ui.view.base.abacus

import android.content.Context
import android.util.Log
import com.jigar.me.data.local.data.AbacusBeadType
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.ViewUtils

object AbacusUtils {
    fun setAbacusTempThemeExam(context: Context,prefManager : AppPreferencesHelper,abacusType : AbacusBeadType) : String{
        var theam : String
        with(prefManager){
            val isPurchased = (getCustomParam(AppConstants.Purchase.Purchase_All,"") == "Y")
            setCustomParam(AppConstants.Settings.TheamTempView,DataProvider.getAllAbacusThemeTypeList(context,isPurchased,abacusType,true).first().type)
            theam = getCustomParam(AppConstants.Settings.TheamTempView,AppConstants.Settings.theam_Default)
        }
        return theam
    }

    fun setAbacusColumnTheme(abacusType : AbacusBeadType, abacusTop1: AbacusMasterView, abacusBottom1: AbacusMasterView,
                             abacusTop2: AbacusMasterView? = null, abacusBottom2: AbacusMasterView? = null,column : Int = 3,column2 : Int = 3){

        val column11 = if (column < 3){3}else{column}
        val column22 = if (column2 < 3){3}else{column2}
        abacusTop1.postDelayed({
            abacusTop1.setNoOfRowAndBeads(0, column11, 1, abacusType)
        },10)
        abacusBottom1.postDelayed({
            abacusBottom1.setNoOfRowAndBeads(0, column11, 4, abacusType)
        },10)


        if (abacusTop2 != null && abacusBottom2 != null){
            abacusTop2.postDelayed({
                abacusTop2.setNoOfRowAndBeads(0, column22, 1, abacusType)
            },10)
            abacusBottom2.postDelayed({
                abacusBottom2.setNoOfRowAndBeads(0, column22, 4, abacusType)
            },10)
        }
    }

    fun setNumber(questionTemp: String,abacusTop1: AbacusMasterView,abacusBottom1: AbacusMasterView,questionTemp2: String? = null,abacusTop2: AbacusMasterView? = null,abacusBottom2: AbacusMasterView? = null,totalLength : Int = 3,totalLength1 : Int = 3) {
        val totalLengthNew = if (totalLength < 3){3}else{totalLength}
        val totalLengthNew1 = if (totalLength1 < 3){3}else{totalLength1}

        val remainLength = totalLengthNew - questionTemp.length
        var zero = ""
        for (i in 1..remainLength){
            zero += "0"
        }
        val question = zero+questionTemp


        abacusTop1.postDelayed({
            setBead(question,totalLengthNew,abacusTop1,abacusBottom1)
        },10)

        if (questionTemp2 != null && abacusTop2 != null && abacusBottom2 != null){
            val remainLength1 = totalLengthNew1 - questionTemp2.length
            var zero1 = ""
            for (i in 1..remainLength1){
                zero1 += "0"
            }
            val question1 = zero1+questionTemp2
            abacusTop2.postDelayed({
                setBead(question1,totalLengthNew1,abacusTop2,abacusBottom2)
            },10)
        }
    }

    private fun setBead(questionTemp: String,totalLength : Int, abacusTop: AbacusMasterView, abacusBottom: AbacusMasterView) {
        val topPositions = ArrayList<Int>()
        val bottomPositions = ArrayList<Int>()
        val remainLength = totalLength - questionTemp.length
        var zero = ""
        for (i in 1..remainLength){
            zero += "0"
        }

        val question = zero+questionTemp
        for (i in 0 until if (totalLength == 1) 2 else totalLength) {
            if (i < question.length) {
                val charAt = question[i] - '1' //convert char to int. minus 1 from question as in abacuse 0 item have 1 value.
                if (charAt >= 0) {
                    if (charAt >= 4) {
                        topPositions.add(i, 0)
                        bottomPositions.add(i, charAt - 5)
                    } else {
                        topPositions.add(i, -1)
                        bottomPositions.add(i, charAt)
                    }
                } else {
                    topPositions.add(i, -1)
                    bottomPositions.add(i, -1)
                }
            } else {
                topPositions.add(i, -1)
                bottomPositions.add(i, -1)
            }
        }
        val subTop: MutableList<Int> = ArrayList()
        subTop.addAll(topPositions.subList(0, question.length))
        val subBottom: MutableList<Int> = ArrayList()
        subBottom.addAll(bottomPositions.subList(0, question.length))
        for (i in question.indices) {
            topPositions.removeAt(0)
            bottomPositions.removeAt(0)
        }
        topPositions.addAll(subTop)
        bottomPositions.addAll(subBottom)

        //app was crashing if position set before update no of row count. so added this delay.
        abacusTop.post {
            abacusBottom.post {
                abacusTop.setSelectedPositions(topPositions,null)
                abacusBottom.setSelectedPositions(bottomPositions,null)
            }
        }

    }
}
package com.jigar.me.data.model.dbtable.exam

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.jigar.me.data.local.data.BeginnerExamPaper
import com.jigar.me.data.local.data.BeginnerExamQuestionType
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Calculator
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.isNotNullOrEmpty
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "ExamHistory")
data class ExamHistory(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int,
    val examTotalTime: Int, // in second
    val examType: String,
    val examDetails: List<DailyExamData> = arrayListOf(),
    val examBeginners: List<BeginnerExamPaper> = arrayListOf(),
    val addedOn: Date = Date(),
    val theme: String? = null,
    val examFor: List<String>? = null
){
    fun getTotalRightAns() : Int{
        var totalCorrectAns = 0
        val mCalculator = Calculator()

        if (examFor.isNotNullOrEmpty() || examType == AppConstants.ExamType.exam_Level_Beginner){
            examBeginners.forEach {
                val tempAns = when (it.type) {
                    BeginnerExamQuestionType.Additions -> {
                        if (it.value2.isNullOrEmpty()){
                            it.value
                        }else{
                            it.value+"+"+it.value2
                        }
                    }
                    BeginnerExamQuestionType.Subtractions -> {
                        if (it.value2.isNullOrEmpty()){
                            it.value
                        }else{
                            it.value+"-"+it.value2
                        }
                    }
                    else -> {
                        it.value
                    }
                }
                val correctAns: String = if (tempAns.contains("x")){
                    val list = tempAns.split("x")
                    (list[0].toLong()*list[1].toLong()).toString()
                }else{
                    val resultObject: String = mCalculator.getResult(tempAns,tempAns)
                    CommonUtils.removeTrailingZero(resultObject)
                }

                if (correctAns == it.userAnswer){
                    totalCorrectAns++
                }
            }
        }else{
            examDetails.forEach {
                val resultObject = mCalculator.getResult(it.questions,it.questions)
                val correctAns = CommonUtils.removeTrailingZero(resultObject)
                if (correctAns == it.userAnswer){
                    totalCorrectAns++
                }
            }
        }

        return totalCorrectAns
    }
}

data class DailyExamData(
    @SerializedName("questions")
    var questions: String = "",
    @SerializedName("userAnswer")
    var userAnswer: String = "")
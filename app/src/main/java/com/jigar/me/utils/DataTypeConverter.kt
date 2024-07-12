package com.jigar.me.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.local.data.BeginnerExamPaper
import com.jigar.me.data.model.dbtable.exam.DailyExamData
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import com.jigar.me.data.model.dbtable.inapp.InAppPurchaseDetails
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import java.util.*

class DataTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun examDetailToList(data: String): List<DailyExamData> {
        val listType = object : TypeToken<List<DailyExamData>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToExamDetail(someObjects: List<DailyExamData>): String {
        return gson.toJson(someObjects)
    }
    @TypeConverter
    fun beginnerExamDetailToList(data: String): List<BeginnerExamPaper> {
        val listType = object : TypeToken<List<BeginnerExamPaper>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToBeginnerExamDetail(someObjects: List<BeginnerExamPaper>): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToList(data: String?): List<String>? {
        if (data.isNullOrEmpty()){
            return arrayListOf()
        }
        val listType = object : TypeToken<List<String>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<String>?): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun inAppSkuDetailsToObject(data: String): InAppSkuDetails? {
        return gson.fromJson(data, InAppSkuDetails::class.java)
    }

    @TypeConverter
    fun objectToInAppSkuDetails(someObjects: InAppSkuDetails?): String {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun inAppPurchaseDetailsToObject(data: String): InAppPurchaseDetails? {
        return gson.fromJson(data, InAppPurchaseDetails::class.java)
    }

    @TypeConverter
    fun objectToInAppPurchaseDetails(someObjects: InAppPurchaseDetails?): String {
        return gson.toJson(someObjects)
    }
    @TypeConverter
    fun examHistoryToObject(data: String): ExamHistory? {
        return gson.fromJson(data, ExamHistory::class.java)
    }

    @TypeConverter
    fun objectToExamHistory(someObjects: ExamHistory?): String {
        return gson.toJson(someObjects)
    }

}
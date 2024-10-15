package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(tableName = AppConstants.DBParam.table_sets,
    foreignKeys = [ForeignKey(
        entity = Pages::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("page_id"),
        onDelete = ForeignKey.CASCADE
    )])
data class Set(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val page_id: String,
    val name: String,
    val answer_setting: String,
    val show_time_setting: Boolean = false,
    val sort_order: Int = 0,
    val created_at: String? = null,
    val is_active: Boolean = true,
    val description: String? = null,
    val hint: String? = null,
    val totals_abacus: Int = 0,
){
    fun getSetTitle() = name+" ("+totals_abacus+")"
}
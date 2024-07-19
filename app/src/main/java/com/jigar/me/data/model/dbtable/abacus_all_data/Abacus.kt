package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(tableName = AppConstants.DBParam.table_abacus,
    foreignKeys = [ForeignKey(
        entity = Set::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("set_id"),
        onDelete = ForeignKey.CASCADE
    )])
data class Abacus(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val set_id: String,
    val question: String,
    val hint: String? = null,
    val created_at: String? = null
)
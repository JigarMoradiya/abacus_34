package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(tableName = AppConstants.DBParam.table_level)
data class Level(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val icon: String,
    val sort_order: Int = 0,
    val created_at: String? = null,
    val tag: String
)
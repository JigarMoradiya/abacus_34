package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(
    tableName = AppConstants.DBParam.table_category,
    foreignKeys = [ForeignKey(
        entity = Level::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("level_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val level_id: String,
    val name: String,
    val icon: String,
    val sort_order: Int = 0,
    val created_at: String? = null,
    val is_active: Boolean = true,
)
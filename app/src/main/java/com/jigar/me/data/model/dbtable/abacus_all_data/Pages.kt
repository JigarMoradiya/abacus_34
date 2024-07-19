package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(tableName = AppConstants.DBParam.table_pages,
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Pages(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val category_id: String,
    val name: String,
    val description: String? = null,
    val sort_order: Int = 0,
    val created_at: String? = null,
    val is_active: Boolean = true,
)
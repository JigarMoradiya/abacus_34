package com.jigar.me.data.model.dbtable.abacus_all_data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jigar.me.utils.AppConstants

@Entity(tableName = AppConstants.DBParam.table_set_progress, primaryKeys = ["set_id"])
data class SetProgress(
    val set_id: String,
    var latest_abacus_id: String? = null,
    var is_set_completed: Boolean = false,
    var total_time_taken: Int = 0,
    var retry_count: Int = 1
)
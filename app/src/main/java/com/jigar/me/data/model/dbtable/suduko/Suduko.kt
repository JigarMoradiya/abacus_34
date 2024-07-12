package com.jigar.me.data.model.dbtable.suduko

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "Suduko")
data class Suduko(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int,
    val cellPosition: String,
    val cellValue: String,
    val roomID: String,
    val notes: String,
    val addedOn: Date = Date()
)
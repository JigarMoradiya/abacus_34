package com.jigar.me.data.model.dbtable.suduko

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "Suduko_Level")
data class SudukoLevel(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Int,
    val level: String,
    val status: String,
    val roomID: String,
    val playTime: String,
    val addedOn: Date = Date()
)
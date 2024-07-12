package com.jigar.me.data.local.db.exam

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jigar.me.data.model.dbtable.exam.ExamHistory

@Dao
interface  ExamHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: ExamHistory)

    @Query("SELECT * FROM ExamHistory WHERE examType = :examType ORDER BY addedOn DESC")
    fun getExamHistoryListByType(examType: String): LiveData<List<ExamHistory>>

    @Query("SELECT COUNT(*) as total FROM ExamHistory WHERE examType = :examType")
    suspend fun getExamHistoryTypeCount(examType: String): Int

    @Query("SELECT * FROM ExamHistory WHERE examType = :examType ORDER BY id ASC LIMIT 1")
    suspend fun getExamHistoryLastRecord(examType: String): ExamHistory?

    @Query("DELETE FROM ExamHistory WHERE id = :itemId")
    suspend fun deleteById(itemId: Int)

}
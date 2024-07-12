package com.jigar.me.data.local.db.exam

import androidx.lifecycle.LiveData
import com.jigar.me.data.model.dbtable.exam.ExamHistory
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import com.jigar.me.utils.Constants

class ExamHistoryDB @Inject constructor(private val dao: ExamHistoryDao) {
    suspend fun insert(data: ExamHistory) = withContext(Dispatchers.IO) {
        val count = dao.getExamHistoryTypeCount(data.examType)
        if (count >= Constants.examLevelMaxRecordHistory){
            val examData = dao.getExamHistoryLastRecord(data.examType)
            if (examData != null){
                dao.deleteById(examData.id)
            }
        }
        dao.insert(data)
    }

     fun getExamHistoryList(examType : String): LiveData<List<ExamHistory>> {
        return dao.getExamHistoryListByType(examType)
    }


}

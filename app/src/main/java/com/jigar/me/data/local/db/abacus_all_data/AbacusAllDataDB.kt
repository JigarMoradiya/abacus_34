package com.jigar.me.data.local.db.abacus_all_data

import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AbacusAllDataDB @Inject constructor(private val dao: AbacusAllDataDao) {
    suspend fun insertLevel(data : List<Level>) = withContext(Dispatchers.IO){
        dao.insertLevel(data)
    }
    suspend fun insertAllData(dataLevel: ArrayList<Level>, dataCategory: ArrayList<Category>, dataPages: ArrayList<Pages>, dataSet: ArrayList<com.jigar.me.data.model.dbtable.abacus_all_data.Set>, dataAbacus: ArrayList<Abacus>)  = withContext(Dispatchers.IO){
        dao.insertLevel(dataLevel)
        dao.insertCategory(dataCategory)
        dao.insertPages(dataPages)
        dao.insertSet(dataSet)
        dao.insertAbacus(dataAbacus)
    }
    fun getLevel() = dao.getLevel()
    suspend fun getCategory(id : String) = dao.getCategory(id)
    suspend fun getPages(id: String, isGetAllData: Boolean) = if (isGetAllData){dao.getPages(id)}else{dao.getPagesOnlyActive(id)}
    suspend fun getSet(id : String) = dao.getSet(id)
    suspend fun getSetDetail(setId : String) = dao.getSetDetail(setId)
    suspend fun getSetProgress(setId : String) = dao.getSetProgress(setId)
    suspend fun getAllSet() = dao.getAllSet()
    suspend fun getAbacus(id : String) = dao.getAbacus(id)
    suspend fun insertSetProgress(data : List<SetProgress>) = withContext(Dispatchers.IO){
        dao.insertSetProgress(data)
    }
    suspend fun deleteSetProgress(setId : String) = withContext(Dispatchers.IO){
        dao.deleteSetProgress(setId)
    }
    suspend fun deleteAllData() = withContext(Dispatchers.IO){
        dao.deleteAllLevel()
        dao.deleteAllCategory()
        dao.deleteAllPages()
        dao.deleteAllSet()
        dao.deleteAllAbacus()
        dao.deleteAllSetProgress()
    }
    suspend fun updateSetTimer(setId : String,time : Long) = dao.updateSetTimer(setId,time)
    suspend fun updateUserAnswer(abacusId : String,userAnswer : String) = dao.updateUserAnswer(abacusId,userAnswer)
    suspend fun removeUserAnswer(setId : String) = dao.removeUserAnswer(setId)

}

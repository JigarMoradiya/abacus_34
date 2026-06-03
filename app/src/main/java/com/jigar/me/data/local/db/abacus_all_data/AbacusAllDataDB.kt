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
    suspend fun insertSetProgress(data : List<SetProgress>) = withContext(Dispatchers.IO){
        dao.insertSetProgress(data)
    }
    suspend fun deleteSetProgress() = withContext(Dispatchers.IO) {
        dao.deleteSetProgress()
    }
    suspend fun removeUserAnswer(setId : String) = dao.removeUserAnswer(setId)

    suspend fun countAbacus(): Int = withContext(Dispatchers.IO) { dao.countAbacus() }

    suspend fun getParentLevelOfSet(setId: String): String = dao.getParentLevelOfSet(setId)

}

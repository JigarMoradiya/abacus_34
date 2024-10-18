package com.jigar.me.data.local.db.abacus_all_data

import javax.inject.Inject

class AbacusAllDataDB @Inject constructor(private val dao: AbacusAllDataDao) {
    fun getLevel() = dao.getLevel()
    suspend fun getCategory(id : String) = dao.getCategory(id)
    suspend fun getPages(id : String) = dao.getPages(id)
    suspend fun getSet(id : String) = dao.getSet(id)
    suspend fun getSetDetail(setId : String) = dao.getSetDetail(setId)
    suspend fun getAllSet() = dao.getAllSet()
    suspend fun getAbacus(id : String) = dao.getAbacus(id)
    suspend fun updateSetProgress(setId : String,currentAbacusId : String) = dao.updateSetProgress(setId,currentAbacusId)
    suspend fun updateSetTimer(setId : String,time : Long) = dao.updateSetTimer(setId,time)

}

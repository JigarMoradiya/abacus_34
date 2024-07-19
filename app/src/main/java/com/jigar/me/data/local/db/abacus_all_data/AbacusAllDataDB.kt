package com.jigar.me.data.local.db.abacus_all_data

import javax.inject.Inject

class AbacusAllDataDB @Inject constructor(private val dao: AbacusAllDataDao) {
    fun getLevel() = dao.getLevel()
    suspend fun getCategory(id : String) = dao.getCategory(id)
    suspend fun getPages(id : String) = dao.getPages(id)
    suspend fun getSet(id : String) = dao.getSet(id)
    suspend fun getAllSet() = dao.getAllSet()
    suspend fun getAbacus(id : String) = dao.getAbacus(id)

}

package com.jigar.me.ui.jetpack.core.repository.abacus_data

import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.model.dbtable.abacus_all_data.SetProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AbacusDataRepository @Inject constructor(
    private val dao: AbacusAllDataDao
) {
    fun getLevels(displayMenuList: List<String>): Flow<List<Level>> = flow {
        emit(dao.getLevel(displayMenuList))
    }

    fun getCategories(levelId: String): Flow<List<Category>> = flow {
        emit(dao.getCategory(levelId))
    }

    fun getPages(categoryId: String): Flow<List<DisplayPages>> = flow {
        emit(dao.getPages(categoryId))
    }
    fun getAllPages(): Flow<List<DisplayPages>> = flow {
        emit(dao.getAllPages())
    }
    fun getAllSets(): Flow<List<Set>> = dao.observeAllSets()

    fun getAbacus(setId: String): Flow<List<Abacus>> = dao.getAbacusFlow(setId)
    fun getSetDetail(setId: String): Flow<Set?> = dao.getSetDetailFlow(setId)
    fun getSetProgress(setId: String): Flow<SetProgress?> = dao.getSetProgressFLow(setId)

    suspend fun insertSetProgress(data : List<SetProgress>) = dao.insertSetProgress(data)
    suspend fun updateUserAnswer(abacusId : String, userAnswer : String) = dao.updateUserAnswer(abacusId, userAnswer)
}
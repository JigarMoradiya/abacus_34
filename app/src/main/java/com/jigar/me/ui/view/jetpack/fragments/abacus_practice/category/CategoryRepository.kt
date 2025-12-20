package com.jigar.me.ui.view.jetpack.fragments.abacus_practice.category

import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.DisplayPages
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.jigar.me.data.model.dbtable.abacus_all_data.Set

class CategoryRepository @Inject constructor(
    private val abacusDao: AbacusAllDataDao,private val inAppDap: InAppSKUDao,
) {

    fun getPurchasedSku(): Flow<List<InAppSkuDetails>> = flow {
        emit(inAppDap.getInAppSKUPurchased())
    }

    fun getCategories(levelId: String): Flow<List<Category>> = flow {
        emit(abacusDao.getCategory(levelId))
    }

    fun getPages(categoryId: String): Flow<List<DisplayPages>> = flow {
        emit(abacusDao.getPages(categoryId))
    }
    fun getAllSets(): Flow<List<Set>> = abacusDao.observeAllSets()

}

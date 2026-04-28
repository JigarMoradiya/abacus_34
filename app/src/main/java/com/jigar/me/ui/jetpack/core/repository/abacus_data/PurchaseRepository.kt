package com.jigar.me.ui.jetpack.core.repository.abacus_data

import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.model.dbtable.inapp.InAppSkuDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PurchaseRepository @Inject constructor(
     private val dao: InAppSKUDao,
) {
    fun getPurchasedSku(): Flow<List<InAppSkuDetails>> = dao.getInAppSKUPurchasedFLow()
    fun getInAppSku(idList: List<String>): Flow<List<InAppSkuDetails>> = dao.getInAppSkuFlow(idList)
}
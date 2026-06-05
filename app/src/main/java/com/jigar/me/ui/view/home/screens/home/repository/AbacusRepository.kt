package com.jigar.me.ui.view.home.screens.home.repository

import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import kotlinx.coroutines.flow.Flow

interface AbacusRepository {
    fun getAbacusData(params: FetchAbacusDataRequest): Flow<Unit>
    fun getAbacusDataPublic(params: FetchAbacusDataRequest): Flow<Unit>
    fun devicePurchaseVerify(params: PurchasedPlanCheckRequest): Flow<String>
}
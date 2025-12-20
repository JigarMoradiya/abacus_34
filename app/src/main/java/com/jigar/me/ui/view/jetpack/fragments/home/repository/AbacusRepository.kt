package com.jigar.me.ui.view.jetpack.fragments.home.repository

import com.jigar.me.data.model.data.FetchAbacusDataRequest
import kotlinx.coroutines.flow.Flow

interface AbacusRepository {
    fun getAbacusData(params: FetchAbacusDataRequest): Flow<Unit>
}
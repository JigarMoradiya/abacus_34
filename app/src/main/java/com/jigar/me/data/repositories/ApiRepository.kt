package com.jigar.me.data.repositories

import com.jigar.me.data.api.AppApi
import com.jigar.me.data.api.connections.SafeApiCall
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val api: AppApi
) : SafeApiCall {
    suspend fun getPracticeMaterial(type : String) = safeApiCall {
        api.getPracticeMaterial(type)
    }
}
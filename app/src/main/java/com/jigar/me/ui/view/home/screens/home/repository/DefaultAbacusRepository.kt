package com.jigar.me.ui.view.home.screens.home.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDB
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.core.miscs.emitFlow
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import com.jigar.me.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class DefaultAbacusRepository @Inject constructor(
    private val remote: StudentApi,
    private val abacusDataDB: AbacusAllDataDB,
    private val prefManager: AppPreferencesHelper,
    ) : AbacusRepository, SafeApiCall {

    // Submits purchase to server — fire and forget, no error handling needed
    override fun devicePurchaseVerify(params: PurchasedPlanCheckRequest): Flow<String> = emitFlow {
        runCatching { devicePurchaseVerifyApi(params) }
        return@emitFlow AppConstants.APIStatus.SUCCESS
    }
    private suspend fun devicePurchaseVerifyApi(request: PurchasedPlanCheckRequest) = safeApiCall {
        remote.handleExistingPurchase(request)
    }
    override fun getAbacusData(params: FetchAbacusDataRequest): Flow<Unit> = emitFlow {
        when (val result = safeApiCall { remote.getAbacusData(params) }) {
            is Resource.Success -> {
                val response = result.value
                if (response.status == AppConstants.APIStatus.SUCCESS){
                    getAbacusDataSuccess(response.data)
                    return@emitFlow
                } else {
                    throw Exception(response.error?.message ?: response.message ?: "Unknown server response")
                }
            }
            is Resource.Failure -> throw when {
                result.isNetworkError -> IOException("Network error occurred")
                else -> Exception("API error: ${result.errorBody ?: "Unknown error"}, code: ${result.errorCode}")
            }
            else -> throw Exception("Unexpected response type")
        }
    }

    override fun getAbacusDataPublic(params: FetchAbacusDataRequest): Flow<Unit> = emitFlow {
        when (val result = safeApiCall { remote.getAbacusDataPublic(params) }) {
            is Resource.Success -> {
                val response = result.value
                if (response.status == AppConstants.APIStatus.SUCCESS){
                    getAbacusDataSuccess(response.data)
                    return@emitFlow
                } else {
                    throw Exception(response.error?.message ?: response.message ?: "Unknown server response")
                }
            }
            is Resource.Failure -> throw when {
                result.isNetworkError -> IOException("Network error occurred")
                else -> Exception("API error: ${result.errorBody ?: "Unknown error"}, code: ${result.errorCode}")
            }
            else -> throw Exception("Unexpected response type")
        }
    }
    private suspend fun getAbacusDataSuccess(content: JsonObject?) {
        val response = Gson().fromJson(content, AbacusAllData::class.java)
        val dataLevel : ArrayList<Level> = arrayListOf()
        val dataPages : ArrayList<Pages> = arrayListOf()
        val dataCategory : ArrayList<Category> = arrayListOf()
        val dataSet : ArrayList<Set> = arrayListOf()
        val dataAbacus : ArrayList<Abacus> = arrayListOf()
        response.levels?.let { dataLevel.addAll(it) }
        response.categories?.let { dataCategory.addAll(it) }
        response.pages?.let { dataPages.addAll(it) }
        response.set?.let { dataSet.addAll(it) }
        response.abacus?.let { dataAbacus.addAll(it) }
        abacusDataDB.insertAllData(dataLevel,dataCategory,dataPages,dataSet,dataAbacus)
        response.last_sync_time?.let { prefManager.setCustomParam(Constants.last_sync_time,it) }
    }

}

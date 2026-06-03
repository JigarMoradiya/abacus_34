package com.jigar.me.ui.view.login.data

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.model.dbtable.abacus_all_data.Abacus
import com.jigar.me.data.model.dbtable.abacus_all_data.Category
import com.jigar.me.data.model.dbtable.abacus_all_data.Level
import com.jigar.me.data.model.dbtable.abacus_all_data.Pages
import com.jigar.me.data.model.dbtable.abacus_all_data.Set
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.DBRepository
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import com.jigar.me.utils.Constants
import com.jigar.me.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostLoginHandler @Inject constructor(
    private val apiRepository: StudentApiRepository,
    private val dbRepository: DBRepository,
    private val prefs: AppPreferencesHelper,
) {
    sealed class Outcome {
        object NavigateHome : Outcome()
        data class Failure(val message: String?) : Outcome()
    }

    // Called on splash when user is already logged in — syncs data + progress via auth API
    suspend fun fetchAbacusDataForAuth(): Outcome {
        val syncTime = prefs.getCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
        val request = FetchAbacusDataRequest(
            get_levels = true,
            get_categories = true,
            get_pages = true,
            get_sets = true,
            get_abacus = true,
            get_set_progress_report = true,
            last_sync_time = syncTime
        )
        when (val response = apiRepository.getAbacusData(request)) {
            is Resource.Success -> {
                if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                    insertAllAbacusData(response.value.data)
                } else {
                    return Outcome.Failure(response.value.error?.message)
                }
            }
            is Resource.Failure -> return Outcome.Failure(response.errorBody)
            else -> Unit
        }
        return Outcome.NavigateHome
    }

    // Called on splash when user is NOT logged in — syncs public data only
    suspend fun fetchPublicAbacusData(): Outcome {
        val syncTime = prefs.getCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
        val request = FetchAbacusDataRequest(
            get_levels = true,
            get_categories = true,
            get_pages = true,
            get_sets = true,
            last_sync_time = syncTime,
            public_key = CommonUtils.getPublicKey()
        )
        return when (val response = apiRepository.getAbacusDataPublic(request)) {
            is Resource.Success -> {
                if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                    insertAllAbacusData(response.value.data)
                    Outcome.NavigateHome
                } else {
                    Outcome.Failure(response.value.error?.message)
                }
            }
            is Resource.Failure -> Outcome.Failure(response.errorBody)
            else -> Outcome.Failure(null)
        }
    }

    // Called silently on home screen — fetches abacus questions only if DB is empty.
    // Always uses the default sync time so all abacus data is retrieved fresh.
    suspend fun fetchAbacusDataSilently(): Outcome {
        if (dbRepository.countAbacus() > 0) return Outcome.NavigateHome
        val isLoggedIn = prefs.isUserLoggedIn()
        val request = FetchAbacusDataRequest(
            get_abacus = true,
            last_sync_time = Constants.last_sync_default_time,
            public_key = if (!isLoggedIn) CommonUtils.getPublicKey() else null
        )
        val response = if (isLoggedIn) apiRepository.getAbacusData(request)
                       else apiRepository.getAbacusDataPublic(request)
        return when (response) {
            is Resource.Success -> {
                if (response.value.status == AppConstants.APIStatus.SUCCESS) {
                    val data = Gson().fromJson(response.value.data, AbacusAllData::class.java)
                    val abacus = ArrayList(data.abacus ?: emptyList<Abacus>())
                    dbRepository.insertAllData(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), abacus)
                    Outcome.NavigateHome
                } else Outcome.Failure(response.value.error?.message)
            }
            is Resource.Failure -> Outcome.Failure(response.errorBody)
            else -> Outcome.Failure(null)
        }
    }

    // Called after login — only restores user's progress
    suspend fun fetchAppProgressData(loginData: JsonObject?): Outcome {
        loginData?.let { persistLoginPayload(it) }

        val request = FetchAbacusDataRequest(get_set_progress_report = true)
        return when (val abacusResponse = apiRepository.getAbacusData(request)) {
            is Resource.Success -> {
                if (abacusResponse.value.status == AppConstants.APIStatus.SUCCESS) {
                    insertProgressData(abacusResponse.value.data)
                    prefs.setUserLoggedIn(true)
                    Outcome.NavigateHome
                } else {
                    Outcome.Failure(abacusResponse.value.error?.message)
                }
            }
            is Resource.Failure -> Outcome.Failure(abacusResponse.errorBody)
            else -> Outcome.Failure(null)
        }
    }

    private fun persistLoginPayload(data: JsonObject) {
        val response = Gson().fromJson(data, LoginData::class.java)
        prefs.setAccessToken(response.token)
        prefs.setLoginData(Gson().toJson(data))
    }

    private suspend fun insertAllAbacusData(data: JsonObject?) {
        val response = Gson().fromJson(data, AbacusAllData::class.java)
        val levels = ArrayList(response.levels ?: emptyList<Level>())
        val categories = ArrayList(response.categories ?: emptyList<Category>())
        val pages = ArrayList(response.pages ?: emptyList<Pages>())
        val sets = ArrayList(response.set ?: emptyList<Set>())
        val abacus = ArrayList(response.abacus ?: emptyList<Abacus>())
        dbRepository.insertAllData(levels, categories, pages, sets, abacus)
        response.last_sync_time?.let { prefs.setCustomParam(Constants.last_sync_time, it) }
    }

    private suspend fun insertProgressData(data: JsonObject?) {
        val response = Gson().fromJson(data, AbacusAllData::class.java)
        response.setProgress?.let { dbRepository.insertSetProgress(it) }
    }

    suspend fun fetchAdminAssignPlan(): Outcome {
        return when (val reviewsResponse = apiRepository.appReviewsList()) {
            is Resource.Success -> {
                if (reviewsResponse.value.status == AppConstants.APIStatus.SUCCESS) {
                    persistPurchasedPlans(reviewsResponse.value.data)
                    Outcome.NavigateHome
                } else {
                    Outcome.Failure(reviewsResponse.value.error?.message)
                }
            }
            is Resource.Failure -> Outcome.Failure(reviewsResponse.errorBody)
            else -> Outcome.Failure(null)
        }
    }
    private fun persistPurchasedPlans(data: JsonObject?) {
        if (data?.has("plans_purchased_manually") == true) {
            val arr = data.getAsJsonArray("plans_purchased_manually")
            if (arr?.isEmpty == true) {
                prefs.setCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, "")
            } else {
                val list: List<PlanAssignFromAdminData> = Gson().fromJson(
                    arr,
                    object : TypeToken<List<PlanAssignFromAdminData>>() {}.type
                )
                prefs.setCustomParam(Constants.PLAN_ASSIGN_FROM_ADMIN_DATA, Gson().toJson(list))
            }
        }
    }
}

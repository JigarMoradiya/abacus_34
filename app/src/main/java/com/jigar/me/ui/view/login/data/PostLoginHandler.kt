package com.jigar.me.ui.view.login.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.data.model.data.LoginData
import com.jigar.me.data.model.data.PlanAssignFromAdminData
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.repositories.DBRepository
import com.jigar.me.data.repositories.StudentApiRepository
import com.jigar.me.utils.AppConstants
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

    suspend fun fetchAbacusDataAndContinue(loginData: JsonObject?): Outcome {
        loginData?.let { persistLoginPayload(it) }

        val syncTime = prefs.getCustomParam(Constants.last_sync_time, Constants.last_sync_default_time)
        val request = FetchAbacusDataRequest(true, get_set_progress_report = true, last_sync_time = syncTime)
        when (val abacusResponse = apiRepository.getAbacusData(request)) {
            is Resource.Success -> {
                if (abacusResponse.value.status == AppConstants.APIStatus.SUCCESS) {
                    insertAbacusData(abacusResponse.value.data)
                } else {
                    return Outcome.Failure(abacusResponse.value.error?.message)
                }
            }
            is Resource.Failure -> return Outcome.Failure(abacusResponse.errorBody)
            else -> Unit
        }

        return fetchReviewsAndContinue(markUserLoggedIn = true)
    }

    suspend fun fetchReviewsAndContinue(markUserLoggedIn: Boolean): Outcome {
        return when (val reviewsResponse = apiRepository.appReviewsList()) {
            is Resource.Success -> {
                if (reviewsResponse.value.status == AppConstants.APIStatus.SUCCESS) {
                    persistPurchasedPlans(reviewsResponse.value.data)
                    if (markUserLoggedIn) prefs.setUserLoggedIn(true)
                    Outcome.NavigateHome
                } else {
                    Outcome.Failure(reviewsResponse.value.error?.message)
                }
            }
            is Resource.Failure -> Outcome.Failure(reviewsResponse.errorBody)
            else -> Outcome.Failure(null)
        }
    }

    private fun persistLoginPayload(data: JsonObject) {
        val response = Gson().fromJson(data, LoginData::class.java)
        prefs.setAccessToken(response.token)
        prefs.setLoginData(Gson().toJson(data))
    }

    private suspend fun insertAbacusData(data: JsonObject?) {
        val response = Gson().fromJson(data, AbacusAllData::class.java)
        response.levels?.let { dbRepository.insertLevel(it) }
        response.setProgress?.let { dbRepository.insertSetProgress(it) }
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
        if (data?.has("free_trial_remaining_days") == true) {
            val days = data.get("free_trial_remaining_days").asInt
            prefs.setCustomParamInt(Constants.free_trial_remaining_days, days)
        }
    }
}

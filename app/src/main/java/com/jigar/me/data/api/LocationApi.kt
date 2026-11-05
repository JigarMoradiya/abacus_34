package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {
    @GET("country-state-city")
    suspend fun getLocation(
        @Query("country_code") countryCode : String? = null,
        @Query("state_code") stateCode : String? = null): MainAPIResponse
}
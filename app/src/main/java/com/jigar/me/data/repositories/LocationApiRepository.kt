package com.jigar.me.data.repositories

import com.jigar.me.data.api.LocationApi
import com.jigar.me.data.api.connections.SafeApiCall
import javax.inject.Inject

class LocationApiRepository @Inject constructor(
    private val api: LocationApi
) : SafeApiCall {

    suspend fun getLocation(countryCode : String? = null, stateCode : String? = null) = safeApiCall {
        api.getLocation(countryCode, stateCode)
    }
}

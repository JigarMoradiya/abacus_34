package com.jigar.me.data.repositories

import com.jigar.me.data.api.UserApi
import com.jigar.me.data.api.connections.SafeApiCall
import com.jigar.me.data.model.data.ContactUsRequest
import javax.inject.Inject

class UserApiRepository @Inject constructor(
    private val api: UserApi
) : SafeApiCall {
    suspend fun contactUs(request : ContactUsRequest) = safeApiCall {
        api.contactUs(request)
    }
}
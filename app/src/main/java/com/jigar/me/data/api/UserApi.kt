package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.ContactUsRequest
import retrofit2.http.*

interface UserApi {
    @POST("contact-us")
    suspend fun contactUs(@Body request : ContactUsRequest): MainAPIResponse

}
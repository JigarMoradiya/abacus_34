package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.data.ChangePasswordRequest
import com.jigar.me.data.model.data.ForgotPasswordRequest
import com.jigar.me.data.model.data.LoginRequest
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.data.model.data.ResendOTPRequest
import com.jigar.me.data.model.data.ResetPasswordRequest
import com.jigar.me.data.model.data.SignupV2Request
import com.jigar.me.data.model.data.SocialLoginRequest
import com.jigar.me.data.model.data.UpdateProfileRequest
import com.jigar.me.data.model.data.VerifyEmailRequest
import retrofit2.http.*

interface LocationApi {
    @GET("country-state-city")
    suspend fun getLocation(
        @Query("country_code") countryCode : String? = null,
        @Query("state_code") stateCode : String? = null): MainAPIResponse
}
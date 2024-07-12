package com.jigar.me.data.api

import com.jigar.me.data.model.MainAPIResponseArray
import retrofit2.http.*

interface AppApi {
    @FormUrlEncoded
    @POST("getImages")
    suspend fun getPracticeMaterial(@Field("type") param : String): MainAPIResponseArray

}
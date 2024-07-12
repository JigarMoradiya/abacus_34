package com.jigar.me.data.model.data

import com.google.gson.annotations.SerializedName

data class VerifyEmailData(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("account_id") var account_id: String? = null
)
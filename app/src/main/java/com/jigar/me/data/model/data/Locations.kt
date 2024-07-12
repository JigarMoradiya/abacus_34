package com.jigar.me.data.model.data

import com.google.gson.annotations.SerializedName

data class KeyValuePair(
    @SerializedName("isoCode") var key: String? = null,
    @SerializedName("name") var name: String? = null
)
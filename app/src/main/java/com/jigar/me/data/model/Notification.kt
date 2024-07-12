package com.jigar.me.data.model

import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("type") var type: String = "",
    @SerializedName("youtube_url") var youtube_url: String = ""
)

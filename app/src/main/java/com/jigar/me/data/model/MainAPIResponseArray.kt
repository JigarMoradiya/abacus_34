package com.jigar.me.data.model

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

class MainAPIResponseArray(
    @SerializedName("message") var message: String?,
    @SerializedName("status") var status: Boolean = false,
    @SerializedName("result") var content: JsonArray?
) {
    override fun toString(): String {
        return "MainAPIResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", result=" + content +
                '}'
    }
}
package com.jigar.me.data.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class MainAPIResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: JsonObject? = null,
    @SerializedName("error") var error: ErrorData? = null,
    @SerializedName("statusCode") var statusCode: Int = 0
) {
    override fun toString(): String {
        return "MainAPIResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", result=" + data +
                ", error=" + error +
                ", statusCode=" + statusCode +
                '}'
    }
}

data class ErrorData(
    var message: String? = null,
    var error_code: String? = null,
)
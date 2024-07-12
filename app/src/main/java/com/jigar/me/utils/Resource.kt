package com.jigar.me.utils

sealed class Resource<out T> {

    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
            var isNetworkError: Boolean,
            var errorCode: Int?,
            var errorBody: String?,
            var errorType: String? = null
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}
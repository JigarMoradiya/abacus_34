package me.samlss.lighter.util

import android.text.TextUtils

object Preconditions {
    fun checkArgument(expression: Boolean, message: String) {
        require(expression) { message }
    }

    fun <T> checkNotNull(arg: T): T {
        return checkNotNull<T>(arg, "Argument must not be null")
    }

    fun <T> checkNotNull(arg: T?, message: String?): T {
        if (arg == null) {
            throw NullPointerException(message)
        }
        return arg
    }

    fun checkNotEmpty(string: String?): String? {
        require(!TextUtils.isEmpty(string)) { "Must not be null or empty" }
        return string
    }

    fun <T : Collection<Y>?, Y> checkNotEmpty(collection: T): T {
        require(!collection!!.isEmpty()) { "Must not be empty." }
        return collection
    }
}
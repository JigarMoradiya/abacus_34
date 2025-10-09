package com.jigar.me.data.pref

interface PreferencesHelper {
    // constants
    fun isUserLoggedIn(): Boolean
    fun setUserLoggedIn(value: Boolean)
    fun getAccessToken(): String?
    fun setAccessToken(accessToken: String?)
    fun getLoginData(): String?
    fun setLoginData(data: String?)

    fun getCustomParam(paramName: String,defaultValue : String): String
    fun setCustomParam(paramName: String,paramValue: String)

    fun getCustomParamInt(paramName: String,defaultValue : Int): Int
    fun setCustomParamInt(paramName: String,paramValue: Int)

    fun getCustomParamBoolean(paramName: String,defaultValue : Boolean): Boolean
    fun setCustomParamBoolean(paramName: String,paramValue: Boolean)

    fun getCustomParamFloat(paramName: String,defaultValue : Float): Float
    fun setCustomParamFloat(paramName: String,paramValue: Float)
}

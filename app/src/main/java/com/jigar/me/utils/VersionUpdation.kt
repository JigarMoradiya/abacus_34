package com.jigar.me.utils

import android.content.Context
import com.jigar.me.BuildConfig
import com.jigar.me.data.pref.AppPreferencesHelper


object VersionUpdation {
    lateinit var prefManager : AppPreferencesHelper
    fun init(context: Context) {
        prefManager = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        val versionCode = BuildConfig.VERSION_CODE
        val previousVersionCode = prefManager.getCustomParamInt(AppConstants.PREF_KEY_APP_VERSION_CODE,136)
        if (previousVersionCode > 0){
            if (versionCode != previousVersionCode){
                for (i in previousVersionCode until versionCode){
                    updateMigration((i.toString()+"_"+(i+1).toString()),context)
                }
                prefManager.setCustomParamInt(AppConstants.PREF_KEY_APP_VERSION_CODE,versionCode)
            }
        }else{
            prefManager.setCustomParamInt(AppConstants.PREF_KEY_APP_VERSION_CODE,versionCode)
        }

    }

    private fun updateMigration(versionMigration: String,context: Context) {
        if (versionMigration == "136_137"){
            val oldDB = context.getDatabasePath(AppConstants.DB_NAME)
            if (oldDB.exists()){
                oldDB.delete()
            }
            prefManager.clearPref()
        }
    }

}
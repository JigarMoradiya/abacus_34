package com.jigar.me.internal.workmanagers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.jigar.me.MyApplication
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.local.db.AppDatabase
import com.jigar.me.data.model.data.AbacusAllData
import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.DateTimeUtils
import com.jigar.me.utils.DateTimeUtils.formatTo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@HiltWorker
class FetchAbacusDataWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiStudent: StudentApi
) : CoroutineWorker(appContext, workerParams) {
    private val context = appContext
    private val appDataBase = AppDatabase.getInstance(context)

    override suspend fun doWork(): Result = coroutineScope {
        val jobs = async {
            fetchAbacusDetailApi()
        }
        jobs.await()
        Result.success()
    }

    private fun fetchAbacusDetailApi() = runBlocking{
        try {

            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR,-1)
//            val dateTime = cal.time.formatTo(DateTimeUtils.yyyy_MM_dd_T_HH_mm_ss_sssz)
            val dateTime = "2023-07-01T12:00:00.000Z"
            val request = FetchAbacusDataRequest(true,true,true,true,true,dateTime)
            Log.e("jigarWorkManager","FetchAbacusDataWorkManager request = "+Gson().toJson(request))
            val apiResponse = apiStudent.getAbacusData(request)
            if (apiResponse.status == AppConstants.APIStatus.SUCCESS){
                apiResponse.data?.let {
                    val response = Gson().fromJson(it, AbacusAllData::class.java)
                    response.levels?.let { appDataBase.abacusAllDataDao().insertLevel(it) }
                    response.categories?.let { appDataBase.abacusAllDataDao().insertCategory(it) }
                    response.pages?.let { appDataBase.abacusAllDataDao().insertPages(it) }
                    response.set?.let { appDataBase.abacusAllDataDao().insertSet(it) }
                    response.abacus?.let { appDataBase.abacusAllDataDao().insertAbacus(it) }
                }
            }
        }catch (e : IOException){
            e.printStackTrace()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    companion object{
        fun fetchAbacusDetails(){
            val data = Data.Builder()
                .build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<FetchAbacusDataWorkManager>()
                .setInputData(data)
                .build()
            val workManager = WorkManager.getInstance(MyApplication.getInstance())
            workManager.beginUniqueWork(UUID.randomUUID().toString(), ExistingWorkPolicy.APPEND_OR_REPLACE, uploadWorkRequest)
                .enqueue()
        }
    }
}
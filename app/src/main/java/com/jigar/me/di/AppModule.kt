package com.jigar.me.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jigar.me.BuildConfig
import com.jigar.me.data.api.AppApi
import com.jigar.me.data.api.ExamApi
import com.jigar.me.data.api.LocationApi
import com.jigar.me.data.api.SubscriptionsApi
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.api.UserApi
import com.jigar.me.data.api.connections.RemoteDataSource
import com.jigar.me.data.local.db.AppDatabase
import com.jigar.me.data.local.db.Migrations.MIGRATION_1_2
import com.jigar.me.data.local.db.Migrations.MIGRATION_2_3
import com.jigar.me.data.local.db.Migrations.MIGRATION_3_4
import com.jigar.me.data.local.db.Migrations.MIGRATION_4_5
import com.jigar.me.data.local.db.Migrations.MIGRATION_5_6
import com.jigar.me.data.local.db.Migrations.MIGRATION_6_7
import com.jigar.me.data.local.db.Migrations.MIGRATION_7_8
import com.jigar.me.data.local.db.Migrations.MIGRATION_8_9
import com.jigar.me.data.local.db.Migrations.MIGRATION_9_10
import com.jigar.me.data.local.db.exam.ExamHistoryDB
import com.jigar.me.data.local.db.exam.ExamHistoryDao
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDB
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDB
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.data.pref.PreferenceInfo
import com.jigar.me.data.pref.PreferencesHelper
import com.jigar.me.utils.AppConstants
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    // Preferences
    @Provides
    @PreferenceInfo
    internal fun providePreferenceName(): String = AppConstants.PREF_NAME

    @Provides
    @Singleton
    internal fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper = appPreferencesHelper

    /*
   Local Room Database
   */
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppConstants.DB_NAME)
//            .fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5,MIGRATION_5_6,MIGRATION_6_7,MIGRATION_7_8,MIGRATION_8_9,MIGRATION_9_10)
            .build()

    @Provides
    fun providesInAppSKUDao(db: AppDatabase): InAppSKUDao = db.inAppSKUDao()
    @Provides
    fun providesInAppSKUDB(dao: InAppSKUDao,preferencesHelper: AppPreferencesHelper): InAppSKUDB = InAppSKUDB(dao,preferencesHelper)

    @Provides
    fun providesInAppPurchaseDao(db: AppDatabase): InAppPurchaseDao = db.inAppPurchaseDao()
    @Provides
    fun providesInAppPurchaseDB(dao: InAppPurchaseDao): InAppPurchaseDB = InAppPurchaseDB(dao)

    @Provides
    fun providesExamHistoryDao(db: AppDatabase): ExamHistoryDao = db.examHistoryDao()
    @Provides
    fun providesExamHistoryDB(dao: ExamHistoryDao): ExamHistoryDB = ExamHistoryDB(dao)

    @Singleton
    @Provides
    fun provideAppApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): AppApi {
        val prefManager = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        return remoteDataSource.buildApi(AppApi::class.java, context, prefManager.getBaseUrl())
//        return remoteDataSource.buildApi(AppApi::class.java, context, BuildConfig.API_MODULE)
    }

    @Singleton
    @Provides
    fun provideStudentApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): StudentApi {
        return remoteDataSource.buildApi(StudentApi::class.java, context, BuildConfig.STUDENT_MODULE)
    }
    @Singleton
    @Provides
    fun provideUserApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): UserApi {
        return remoteDataSource.buildApi(UserApi::class.java, context, BuildConfig.USERS_MODULE)
    }

    @Singleton
    @Provides
    fun provideSubscriptionsApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): SubscriptionsApi {
        return remoteDataSource.buildApi(SubscriptionsApi::class.java, context, BuildConfig.SUBSCRIPTIONS_MODULE)
    }

    @Singleton
    @Provides
    fun provideLocationApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): LocationApi {
        return remoteDataSource.buildApi(LocationApi::class.java, context, BuildConfig.LOCATION_MODULE)
    }

    @Singleton
    @Provides
    fun provideExamApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): ExamApi {
        return remoteDataSource.buildApi(ExamApi::class.java, context, BuildConfig.EXAM_MODULE)
    }

}
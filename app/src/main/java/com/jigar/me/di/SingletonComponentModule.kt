package com.jigar.me.di

import android.app.Application
import android.content.Context
import com.jigar.me.BuildConfig
import com.jigar.me.data.api.ExamApi
import com.jigar.me.data.api.LocationApi
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.api.UserApi
import com.jigar.me.data.api.connections.RemoteDataSource
import com.jigar.me.data.local.db.AppDatabase
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDB
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDao
import com.jigar.me.data.local.db.exam.ExamHistoryDB
import com.jigar.me.data.local.db.exam.ExamHistoryDao
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDB
import com.jigar.me.data.local.db.inapp.purchase.InAppPurchaseDao
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDB
import com.jigar.me.data.local.db.inapp.sku.InAppSKUDao
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.fragments.game_zone.sudoku.viewmodel.SudokuRepository
import com.jigar.me.ui.view.jetpack.fragments.game_zone.target_number.viewmodel.TargetRepository
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.CommonUtils
import dagger.Module
import dagger.Provides
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

    @Singleton
    @Provides
    fun providePreferencesHelper(@ApplicationContext context: Context) = AppPreferencesHelper(context, AppConstants.PREF_NAME)

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

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

    @Provides
    fun providesAbacusAllDataDao(db: AppDatabase): AbacusAllDataDao = db.abacusAllDataDao()
    @Provides
    fun providesAbacusAllDataDB(dao: AbacusAllDataDao): AbacusAllDataDB = AbacusAllDataDB(dao)

    @Singleton
    @Provides
    fun provideStudentApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): StudentApi {
        return remoteDataSource.buildApi(StudentApi::class.java, context, CommonUtils.getApiBaseUrl()+BuildConfig.NEW_MODULE)
    }
    @Singleton
    @Provides
    fun provideUserApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): UserApi {
        return remoteDataSource.buildApi(UserApi::class.java, context, CommonUtils.getApiBaseUrl()+BuildConfig.USERS_MODULE)
    }

    @Singleton
    @Provides
    fun provideLocationApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): LocationApi {
        return remoteDataSource.buildApi(LocationApi::class.java, context, CommonUtils.getApiBaseUrl()+BuildConfig.LOCATION_MODULE)
    }

    @Singleton
    @Provides
    fun provideExamApi(@ApplicationContext context: Context,remoteDataSource: RemoteDataSource): ExamApi {
        return remoteDataSource.buildApi(ExamApi::class.java, context, CommonUtils.getApiBaseUrl()+BuildConfig.EXAM_MODULE)
    }


    @Singleton
    @Provides
    fun provideSudokuRepository(): SudokuRepository = SudokuRepository()

    @Provides
    @Singleton
    fun provideTargetRepository(): TargetRepository = TargetRepository()
}

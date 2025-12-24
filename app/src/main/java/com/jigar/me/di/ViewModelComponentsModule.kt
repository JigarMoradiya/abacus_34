package com.jigar.me.di

import com.jigar.me.data.api.ExamApi
import com.jigar.me.data.api.StudentApi
import com.jigar.me.data.local.db.abacus_all_data.AbacusAllDataDB
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.exam_base.repository.DefaultExamNewRepository
import com.jigar.me.ui.view.jetpack.exam_base.repository.ExamNewRepository
import com.jigar.me.ui.view.jetpack.fragments.home.repository.AbacusRepository
import com.jigar.me.ui.view.jetpack.fragments.home.repository.DefaultAbacusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelComponentsModule {

    @Provides
    @ViewModelScoped
    fun provideAbacusRepository(remote: StudentApi, abacusDataDB: AbacusAllDataDB, prefManager: AppPreferencesHelper): AbacusRepository =
        DefaultAbacusRepository(remote = remote, abacusDataDB = abacusDataDB, prefManager = prefManager)

    @Provides
    @ViewModelScoped
    fun provideExamNewRepository(remote: ExamApi, abacusAllDataDB : AbacusAllDataDB): ExamNewRepository = DefaultExamNewRepository(remote = remote,abacusAllDataDB = abacusAllDataDB)
}
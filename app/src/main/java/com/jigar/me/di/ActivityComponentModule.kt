package com.jigar.me.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.jetpack.utils.TextToSpeechManager


@Module
@InstallIn(ActivityComponent::class)
object ActivityComponentModule {

    @Provides
    @ActivityScoped
    fun provideTextToSpeechManager(
        @ActivityContext context: Context,
        prefs: AppPreferencesHelper
    ): TextToSpeechManager {
        return TextToSpeechManager(context, prefs)
    }
}


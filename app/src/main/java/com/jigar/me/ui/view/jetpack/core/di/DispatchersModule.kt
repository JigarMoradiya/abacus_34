package com.jigar.me.ui.view.jetpack.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Qualifier annotation to specify the type of [CoroutineDispatcher] to be injected.
 *
 * @param dispatcher The [DispatcherKey] indicating the specific dispatcher type (e.g., IO, COMPUTATION, MAIN).
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: DispatcherKey)

/**
 * Enum class defining the available dispatcher types for dependency injection.
 */
enum class DispatcherKey { IO, COMPUTATION, MAIN }

/**
 * Dagger Hilt module providing [CoroutineDispatcher] instances for dependency injection.
 *
 * This module is installed in the [SingletonComponent], ensuring that the same dispatcher instances
 * are reused throughout the application lifecycle. It provides three dispatchers:
 * - [Dispatchers.IO] for I/O-bound tasks.
 * - [Dispatchers.Default] for CPU-intensive tasks.
 * - [Dispatchers.Main] for UI-related tasks.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    /**
     * Provides a [CoroutineDispatcher] for I/O-bound tasks, such as network requests or database operations.
     *
     * This dispatcher uses [Dispatchers.IO], which is backed by a shared thread pool optimized for I/O operations.
     * It is suitable for tasks like reading/writing to a database, making API calls, or accessing the file system.
     * Using this dispatcher ensures that I/O operations do not block the main thread, keeping the UI responsive.
     *
     * @return [CoroutineDispatcher] configured as [Dispatchers.IO].
     */
    @Provides
    @Dispatcher(DispatcherKey.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides a [CoroutineDispatcher] for CPU-intensive tasks, such as complex computations or data processing.
     *
     * This dispatcher uses [Dispatchers.Default], which is backed by a thread pool designed for CPU-bound work.
     * It is suitable for tasks like parsing large JSON responses, sorting data, or performing mathematical calculations.
     * Using this dispatcher ensures that computationally intensive tasks are offloaded from the main thread.
     *
     * @return [CoroutineDispatcher] configured as [Dispatchers.Default].
     */
    @Provides
    @Dispatcher(DispatcherKey.COMPUTATION)
    fun providesComputationDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Provides a [CoroutineDispatcher] for UI-related tasks, such as updating the user interface.
     *
     * This dispatcher uses [Dispatchers.Main], which runs on the Android main thread (UI thread).
     * It is suitable for tasks like updating Compose UI, posting LiveData values, or interacting with View-based UI components.
     * Use this dispatcher when you need to perform operations that directly affect the UI.
     *
     * @return [CoroutineDispatcher] configured as [Dispatchers.Main].
     */
    @Provides
    @Dispatcher(DispatcherKey.MAIN)
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
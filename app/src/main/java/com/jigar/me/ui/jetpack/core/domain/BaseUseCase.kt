package com.jigar.me.ui.jetpack.core.domain

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<Output> {
    abstract operator fun invoke(): Flow<Output>
}

abstract class ParameterizedUseCase<Params, Output> {
    abstract operator fun invoke(params: Params): Flow<Output>
}

abstract class CallbackParameterizedUseCase<in Params, Output> {
    operator fun invoke(
        params: Params,
        onStart: (() -> Unit)? = null,
        onEachEmit: ((Output) -> Unit)? = null,
        onCompletion: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Flow<Output> = buildFlow(params, onStart, onEachEmit, onCompletion, onError)

    protected abstract fun buildFlow(
        params: Params,
        onStart: (() -> Unit)?,
        onEachEmit: ((Output) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)? = null
    ): Flow<Output>
}

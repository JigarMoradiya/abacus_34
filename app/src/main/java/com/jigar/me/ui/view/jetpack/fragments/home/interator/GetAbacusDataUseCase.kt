package com.jigar.me.ui.view.jetpack.fragments.home.interator

import com.jigar.me.data.model.data.FetchAbacusDataRequest
import com.jigar.me.ui.view.jetpack.core.di.Dispatcher
import com.jigar.me.ui.view.jetpack.core.di.DispatcherKey
import com.jigar.me.ui.view.jetpack.core.domain.CallbackParameterizedUseCase
import com.jigar.me.ui.view.jetpack.fragments.home.repository.AbacusRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAbacusDataUseCase @Inject constructor(
    private val repository: AbacusRepository,
    @Dispatcher(DispatcherKey.IO) private val dispatcher: CoroutineDispatcher
) : CallbackParameterizedUseCase<FetchAbacusDataRequest, Unit>() {

    override fun buildFlow(
        params: FetchAbacusDataRequest,
        onStart: (() -> Unit)?,
        onEachEmit: ((Unit) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)?
    ): Flow<Unit> = flow {
        try {
            onStart?.invoke()

            repository.getAbacusData(params).collect{
                onEachEmit?.invoke(it) // notify UI
                emit(it)
            }
            onCompletion?.invoke()

        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }.flowOn(dispatcher)
}

package com.jigar.me.ui.jetpack.api

import com.jigar.me.data.model.data.Statistics
import com.jigar.me.ui.jetpack.api.repository.ExamNewRepository
import com.jigar.me.ui.jetpack.core.di.Dispatcher
import com.jigar.me.ui.jetpack.core.di.DispatcherKey
import com.jigar.me.ui.jetpack.core.domain.CallbackParameterizedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: ExamNewRepository,
    @Dispatcher(DispatcherKey.IO) private val dispatcher: CoroutineDispatcher
) : CallbackParameterizedUseCase<Unit, Statistics>() {

    override fun buildFlow(
        params: Unit,
        onStart: (() -> Unit)?,
        onEachEmit: ((Statistics) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)?
    ): Flow<Statistics> = flow {
        try {
            onStart?.invoke()
            repository.getStatistics().collect{
                emit(it)
                onEachEmit?.invoke(it)
            }
            onCompletion?.invoke()

        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }.flowOn(dispatcher)
}

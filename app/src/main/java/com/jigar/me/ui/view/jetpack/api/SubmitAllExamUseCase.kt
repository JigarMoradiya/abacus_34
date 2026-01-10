package com.jigar.me.ui.view.jetpack.api

import com.jigar.me.data.model.data.SubmitAllExamDataRequest
import com.jigar.me.ui.view.jetpack.core.di.Dispatcher
import com.jigar.me.ui.view.jetpack.core.di.DispatcherKey
import com.jigar.me.ui.view.jetpack.core.domain.CallbackParameterizedUseCase
import com.jigar.me.ui.view.jetpack.api.repository.ExamNewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SubmitAllExamUseCase @Inject constructor(
    private val repository: ExamNewRepository,
    @Dispatcher(DispatcherKey.IO) private val dispatcher: CoroutineDispatcher
) : CallbackParameterizedUseCase<SubmitAllExamDataRequest, Unit>() {

    override fun buildFlow(
        params: SubmitAllExamDataRequest,
        onStart: (() -> Unit)?,
        onEachEmit: ((Unit) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)?
    ): Flow<Unit> = flow {
        try {
            onStart?.invoke()
            repository.submitExamData(params).collect()
            emit(Unit)
            onEachEmit?.invoke(Unit)
            onCompletion?.invoke()

        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }.flowOn(dispatcher)
}

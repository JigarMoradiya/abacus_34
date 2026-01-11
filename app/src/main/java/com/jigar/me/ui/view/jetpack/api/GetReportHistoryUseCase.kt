package com.jigar.me.ui.view.jetpack.api

import com.google.gson.JsonObject
import com.jigar.me.data.model.data.FetchReportHistoryRequest
import com.jigar.me.data.model.data.FetchReportHistoryResponse
import com.jigar.me.ui.view.jetpack.core.di.Dispatcher
import com.jigar.me.ui.view.jetpack.core.di.DispatcherKey
import com.jigar.me.ui.view.jetpack.core.domain.CallbackParameterizedUseCase
import com.jigar.me.ui.view.jetpack.api.repository.ExamNewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetReportHistoryUseCase @Inject constructor(
    private val repository: ExamNewRepository,
    @Dispatcher(DispatcherKey.IO) private val dispatcher: CoroutineDispatcher
) : CallbackParameterizedUseCase<FetchReportHistoryRequest, FetchReportHistoryResponse>() {

    override fun buildFlow(
        params: FetchReportHistoryRequest,
        onStart: (() -> Unit)?,
        onEachEmit: ((FetchReportHistoryResponse) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)?
    ): Flow<FetchReportHistoryResponse> = flow {
        try {
            onStart?.invoke()
            repository.getReportHistory(params).collect{
                emit(it)
                onEachEmit?.invoke(it)
            }
            onCompletion?.invoke()

        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }.flowOn(dispatcher)
}

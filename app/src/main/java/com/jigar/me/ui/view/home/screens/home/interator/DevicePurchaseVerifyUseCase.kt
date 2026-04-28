package com.jigar.me.ui.view.home.screens.home.interator

import android.util.Log
import com.google.gson.Gson
import com.jigar.me.data.model.data.PurchasedPlanCheckRequest
import com.jigar.me.ui.jetpack.core.di.Dispatcher
import com.jigar.me.ui.jetpack.core.di.DispatcherKey
import com.jigar.me.ui.jetpack.core.domain.CallbackParameterizedUseCase
import com.jigar.me.ui.view.home.screens.home.repository.AbacusRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DevicePurchaseVerifyUseCase @Inject constructor(
    private val repository: AbacusRepository,
    @Dispatcher(DispatcherKey.IO) private val dispatcher: CoroutineDispatcher
) : CallbackParameterizedUseCase<PurchasedPlanCheckRequest, String>() {

    override fun buildFlow(
        params: PurchasedPlanCheckRequest,
        onStart: (() -> Unit)?,
        onEachEmit: ((String) -> Unit)?,
        onCompletion: (() -> Unit)?,
        onError: ((Throwable) -> Unit)?
    ): Flow<String> = flow {
        try {
            onStart?.invoke()
            repository.devicePurchaseVerify(params).collect{
                onEachEmit?.invoke(it) // notify UI
                emit(it)
            }
            onCompletion?.invoke()

        } catch (e: Throwable) {
            onError?.invoke(e)
        }
    }.flowOn(dispatcher)
}

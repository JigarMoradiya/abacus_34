package com.jigar.me.ui.view.jetpack.core.miscs

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

/**
 * Converts a suspending function into a Flow that emits the result of the function.
 *
 * @param T The type of the value emitted by the Flow.
 * @param emitter A suspending function that produces a value of type T.
 * @return A Flow that emits the result of the emitter function.
 */
fun <T> emitFlow(emitter: suspend () -> T): Flow<T> {
    return emitter.asFlow()
}

/**
 * Transforms each element of the input Flow into a new Flow using the provided stream function,
 * but emits the original element instead of the transformed one. The transformation is performed
 * in a concatenated manner, processing each Flow sequentially.
 *
 * Note: This function is marked with ExperimentalCoroutinesApi as it uses flatMapConcat.
 *
 * @param T The type of the elements in the input and output Flow.
 * @param R The type of the elements in the intermediate Flow produced by the stream function.
 * @param stream A suspending function that takes an element of type T and returns a Flow of type R.
 * @return A Flow that emits the original elements of type T after applying the stream transformation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> Flow<T>.flatMapSame(stream: suspend (T) -> Flow<R>): Flow<T> =
    flatMapConcat { original -> stream(original).map { original } }
package com.jigar.me.ui.view.jetpack.core.miscs

import androidx.compose.runtime.Composable

typealias Action = () -> Unit
typealias Consumer<T> = (T) -> Unit
typealias Transformer<T, R> = (T) -> R
typealias BiConsumer<T1, T2> = (T1, T2) -> Unit
typealias ContentRenderer = @Composable () -> Unit
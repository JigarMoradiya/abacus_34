package com.jigar.me.ui.jetpack.core.domain

import com.jigar.me.ui.jetpack.core.miscs.Consumer
import kotlin.let
import kotlin.takeIf

/**
 * A wrapper for data that represents a **one-time consumable command**.
 *
 * It ensures that the underlying data can only be **consumed once** — either
 * via [consume] or [consume] with a lambda. Once consumed, any subsequent
 * calls will have no effect or return `null`.
 *
 * Typical use case:
 * - Representing **UI events** like navigation, toasts, or dialogs that
 *   should only happen once even after configuration changes.
 *
 * Example:
 * ```
 * val command = ConsumableCommand("ShowToast")
 * command.consume { println(it) } // prints "ShowToast"
 * command.consume() // returns null — already consumed
 * ```
 *
 * @param T the type of data held by this command
 * @property data the actual data being carried by this command
 * @property onConsumed optional callback invoked when the command is consumed
 */
data class ConsumableCommand<T>(
    private var data: T,
    private val onConsumed: Consumer<Long>? = null
) {
    /** Whether this command has already been consumed. */
    var isConsumed = false
        private set

    /** Timestamp of when the command was created (used for equality). */
    val updateTime: Long = System.currentTimeMillis()

    /**
     * Consumes this command if not already consumed, passing its data
     * to the given [consumer] block. If already consumed, this call has no effect.
     */
    fun consume(consumer: (T) -> Unit) {
        if (isConsumed) return
        consumer(data)
        onConsumed?.invoke(updateTime)
        isConsumed = true
    }

    /**
     * Consumes this command if not already consumed, returning its data.
     * Returns `null` if it has already been consumed.
     */
    fun consume(): T? {
        if (isConsumed) return null
        val result = data
        onConsumed?.invoke(updateTime)
        isConsumed = true
        return result
    }

    override fun equals(other: Any?): Boolean {
        return other?.takeIf { it is ConsumableCommand<*> && it.updateTime == updateTime }
            ?.let { true } ?: false
    }

    override fun hashCode(): Int {
        var result = data?.hashCode() ?: 0
        result = 31 * result + updateTime.hashCode()
        return result
    }

    companion object {
        /**
         * Creates a consumable command with no data (Unit).
         */
        fun unit(onConsumed: Consumer<Long>? = null): ConsumableCommand<Unit> =
            ConsumableCommand(Unit, onConsumed)
    }
}
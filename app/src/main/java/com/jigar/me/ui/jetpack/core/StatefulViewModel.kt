package com.jigar.me.ui.jetpack.core

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.BuildConfig
import com.jigar.me.R
import com.jigar.me.ui.jetpack.core.domain.NoLoggedInUser
import com.jigar.me.utils.CommonUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.IOException
import kotlin.also

abstract class StatefulViewModel<State>(protected open val allowLogging: Boolean = BuildConfig.DEBUG) :
    ViewModel() {

    private val _uiState by lazy { MutableStateFlow(StateWrapper(getInitialState())) }

    /**
     * A read-only StateFlow exposing the current UI state to observers.
     * Filters out silent updates and maps the StateWrapper to the raw State.
     * Initialized with the initial state from getInitialState().
     */
    val uiState: StateFlow<State> by lazy {
        _uiState.filter { !it.silentUpdate }.map { it.state }.stateIn(
            viewModelScope,
            SharingStarted.Companion.Eagerly, getInitialState()
        )
    }

    /**
     * A unique tag for logging, to be defined by subclasses.
     */
    protected abstract val TAG: String

    /**
     * Retrieves the current state from the internal StateFlow.
     *
     * @return The current state of type State.
     */
    protected fun state() = _uiState.value.state

    /**
     * Defines the initial state for the ViewModel.
     * Must be implemented by subclasses to provide the starting state.
     *
     * @return The initial state of type State.
     */
    protected abstract fun getInitialState(): State


    /**
     * Handles errors that occur during ViewModel operations by processing the given throwable.
     * Subclasses must implement this function to define how errors are handled, typically by
     * updating the UI state with an appropriate error message or side effect.
     * For example, it may use [localizeCommonFailure] to map the throwable to a localized string
     * resource and update the state with it.
     *
     * @param throwable The error to be handled.
     */
    protected abstract fun onFailure(throwable: Throwable)

    /**
     * Updates the state by applying a transformation function.
     * Optionally, the update can be silent (not propagated to uiState observers).
     * Logs state changes if allowLogging is true.
     *
     * @param silentUpdate If true, the update won't trigger uiState observers.
     * @param transform A function that takes the current state and returns a new state.
     */
    protected fun updateState(silentUpdate: Boolean = false, transform: (State) -> State) {
        val oldState = _uiState.value.state
        _uiState.updateAndGet(transformerWrapper(transform, silentUpdate)).also {
            onStateUpdated(oldState = oldState, newState = it.state)
        }
    }

    /**
     * Updates the state with a transformation, followed by a reset operation in a coroutine.
     * The transformation is applied immediately, and the reset is applied after a yield.
     *
     * @param transform A function that takes the current state and returns a new state.
     * @param reset A function that takes the current state and returns a reset state.
     */
    protected fun updateStateWithReset(transform: State.() -> State, reset: State.() -> State) {
        viewModelScope.launch {
            updateState(false, transform)
            yield()
            updateState(true, reset)
        }
    }

    /**
     * Instantly updates the state and then immediately resets it,
     * so transient states (like toast/snack-bar messages) don't persist
     * across recompositions or navigation.
     *
     * @param transform A function that takes the current state and returns a new state.
     * @param reset A function that takes the current state and returns the reset state.
     */
    protected fun updateStateInstantReset(transform: State.() -> State, reset: State.() -> State) {
        viewModelScope.launch {
            updateState(false, transform)
            yield()
            updateState(false, reset)
        }
    }


    /**
     * Updates the state using a receiver-style transformation function.
     * A convenience method for calling updateState with a lambda expression.
     *
     * @param transform A function that takes the current state and returns a new state.
     */
    fun updateState_(transform: State.() -> State) {
        updateState(transform = transform)
    }

    /**
     * Updates the state and returns the new state.
     * Applies the transformation and logs the state change if allowLogging is true.
     *
     * @param transform A function that takes the current state and returns a new state.
     * @return The new state after applying the transformation.
     */
    protected fun updateStateAndGet(transform: (State) -> State): State {
        val oldState = _uiState.value.state
        return _uiState.updateAndGet(transformerWrapper(transform)).also {
            onStateUpdated(oldState = oldState, newState = it.state)
        }.state
    }

    /**
     * Conditionally updates the state if the given condition is met.
     *
     * @param condition A function that takes the current state and returns true if the update should proceed.
     * @param transform A function that takes the current state and returns a new state.
     */
    protected fun updateStateOn(condition: (State) -> Boolean, transform: (State) -> State) {
        if (condition(state()))
            updateState(transform = transform)
    }

    /**
     * Updates the state silently, meaning the update won't trigger uiState observers.
     *
     * @param transform A function that takes the current state and returns a new state.
     */
    protected fun updateStateSilently(transform: (State) -> State) {
        updateState(true, transform)
    }

    /**
     * Wraps a transformation function into a StateWrapper transformer.
     * Applies onWillUpdateState to the transformed state and sets the silentUpdate flag.
     *
     * @param transform A function that takes the current state and returns a new state.
     * @param silentUpdate If true, the update won't trigger uiState observers.
     * @return A function that transforms a StateWrapper into a new StateWrapper.
     */
    private fun transformerWrapper(
        transform: (State) -> State,
        silentUpdate: Boolean = false
    ): (StateWrapper<State>.() -> StateWrapper<State>) = {
        copy(onWillUpdateState(transform(state)), silentUpdate = silentUpdate)
    }

    /**
     * Called after a state update to log the change or perform additional actions.
     * Can be overridden by subclasses to customize behavior.
     *
     * @param oldState The state before the update.
     * @param newState The state after the update.
     */
    protected open fun onStateUpdated(oldState: State, newState: State) {
        if (allowLogging) Log.d(TAG, "updateState:${newState}")
    }

    /**
     * Maps a throwable to a localized string resource for common failure cases.
     * Logs the error if allowLogging is true.
     *
     * @param throwable The exception to localize.
     * @return A string resource ID corresponding to the error type.
     */
    @StringRes
    protected fun localizeCommonFailure(throwable: Throwable): Int {
        if (allowLogging)
            CommonUtils.logMultilineString(TAG, "localizeCommonFailure:")
        throwable.printStackTrace()
        return when (throwable) {
            is NoLoggedInUser -> R.string.no_logged_in_user
            is IOException -> R.string.network_unreachable
            else -> R.string.unexpected_error
        }
    }

    /**
     * Called before the state is updated, allowing subclasses to modify the new state.
     * Useful for states driven by other states (e.g., enabling a submit button based on field values).
     *
     * @param state The proposed new state after applying a transformation.
     * @return The final state to be used for the update.
     */
    protected open fun onWillUpdateState(state: State): State {
        return state
    }

    /**
     * A data class that wraps the state and a flag indicating whether the update is silent.
     *
     * @param state The current state of type State.
     * @param silentUpdate If true, the state update won't trigger uiState observers.
     */
    data class StateWrapper<State>(val state: State, val silentUpdate: Boolean = false)
}
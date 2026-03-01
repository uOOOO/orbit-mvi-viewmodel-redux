package com.uoooo.orbitmvi.viewmodel.redux

import kotlinx.coroutines.flow.Flow

interface Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT> {
    fun handle(action: ACTION, state: STATE): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>
}

sealed interface MiddlewareResult<out ACTION, out CHANGE, out SIDE_EFFECT> {
    data class Action<ACTION>(
        val action: ACTION,
    ) : MiddlewareResult<ACTION, Nothing, Nothing>

    data class Change<CHANGE>(
        val change: CHANGE,
    ) : MiddlewareResult<Nothing, CHANGE, Nothing>

    data class SideEffect<SIDE_EFFECT>(
        val effect: SIDE_EFFECT,
    ) : MiddlewareResult<Nothing, Nothing, SIDE_EFFECT>
}

interface Reducer<STATE, CHANGE> {
    fun reduce(state: STATE, change: CHANGE): STATE
}

interface SideEffectHandler<SIDE_EFFECT : Any> {
    suspend fun handle(sideEffect: SIDE_EFFECT)
}

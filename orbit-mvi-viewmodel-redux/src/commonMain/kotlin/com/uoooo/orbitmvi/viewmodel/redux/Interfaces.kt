package com.uoooo.orbitmvi.viewmodel.redux

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf

fun interface Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT> {
    fun handle(action: ACTION, state: STATE): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>

    fun dispatch(action: ACTION): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>> =
        flowOf(MiddlewareResult.Action(action))

    fun change(change: CHANGE): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>> =
        flowOf(MiddlewareResult.Change(change))

    fun sideEffect(sideEffect: SIDE_EFFECT): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>> =
        flowOf(MiddlewareResult.SideEffect(sideEffect))

    suspend fun FlowCollector<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>.dispatch(action: ACTION) =
        emit(MiddlewareResult.Action(action))

    suspend fun FlowCollector<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>.change(change: CHANGE) =
        emit(MiddlewareResult.Change(change))

    suspend fun FlowCollector<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>.sideEffect(sideEffect: SIDE_EFFECT) =
        emit(MiddlewareResult.SideEffect(sideEffect))
}

fun <STATE, ACTION, CHANGE, SIDE_EFFECT> middleware(
    handler: Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT>.(action: ACTION, state: STATE) -> Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>>,
): Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT> =
    object : Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT> {
        override fun handle(
            action: ACTION,
            state: STATE,
        ): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>> =
            handler(action, state)
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

fun <STATE, CHANGE> reducer(
    handler: Reducer<STATE, CHANGE>.(state: STATE, change: CHANGE) -> STATE,
): Reducer<STATE, CHANGE> =
    object : Reducer<STATE, CHANGE> {
        override fun reduce(state: STATE, change: CHANGE): STATE = handler(state, change)
    }

interface SideEffectHandler<SIDE_EFFECT : Any> {
    suspend fun handle(sideEffect: SIDE_EFFECT)
}

fun <SIDE_EFFECT : Any> sideEffectHandler(
    handler: suspend SideEffectHandler<SIDE_EFFECT>.(sideEffect: SIDE_EFFECT) -> Unit,
): SideEffectHandler<SIDE_EFFECT> =
    object : SideEffectHandler<SIDE_EFFECT> {
        override suspend fun handle(sideEffect: SIDE_EFFECT) = handler(sideEffect)
    }

package com.uoooo.orbitmvi.viewmodel.redux

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge

internal class ReduxProcessor<STATE : Any, ACTION : Any, CHANGE : Any, SIDE_EFFECT : Any>(
    private val middlewares: List<Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT>>,
    private val reducer: Reducer<STATE, CHANGE>,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun process(
        action: ACTION,
        stateSnapshot: STATE,
    ): Flow<MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>> {
        return middlewares.asFlow()
            .flatMapMerge { middleware -> middleware.handle(action, stateSnapshot) }
    }

    fun reduce(state: STATE, change: CHANGE): STATE {
        return reducer.reduce(state, change)
    }
}

package com.uoooo.orbitmvi.viewmodel.redux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class ReduxViewModel<STATE : Any, ACTION : Any, CHANGE : Any, SIDE_EFFECT : Any>(
    initialState: STATE,
    middlewares: List<Middleware<STATE, ACTION, CHANGE, SIDE_EFFECT>>,
    reducer: Reducer<STATE, CHANGE>,
) : ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {
    private val processor: ReduxProcessor<STATE, ACTION, CHANGE, SIDE_EFFECT> =
        ReduxProcessor(middlewares, reducer)

    override val container = container<STATE, SIDE_EFFECT>(initialState) {
        onCreate(state)
    }

    private val actionChannel = Channel<ACTION>(Channel.UNLIMITED)

    init {
        observeAction()
    }

    private fun observeAction() {
        suspend fun Syntax<STATE, SIDE_EFFECT>.handleMiddlewareResult(
            result: MiddlewareResult<ACTION, CHANGE, SIDE_EFFECT>,
        ) {
            when (result) {
                is MiddlewareResult.Action -> dispatch(result.action)
                is MiddlewareResult.Change -> reduce { processor.reduce(state, result.change) }
                is MiddlewareResult.SideEffect -> postSideEffect(result.effect)
            }
        }

        actionChannel.receiveAsFlow()
            .onEach { action ->
                intent {
                    processor.process(action, state)
                        .collect(::handleMiddlewareResult)
                }
            }.launchIn(viewModelScope)
    }

    protected open suspend fun onCreate(state: STATE) {}

    fun dispatch(action: ACTION) {
        actionChannel.trySend(action)
    }
}

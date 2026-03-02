package com.uoooo.sample.orbitmvi.counter.shared

import com.uoooo.orbitmvi.viewmodel.redux.ReduxViewModel
import com.uoooo.orbitmvi.viewmodel.redux.middleware
import com.uoooo.orbitmvi.viewmodel.redux.reducer

data class CounterState(val count: Int = 0)

sealed interface CounterAction {
    data object Increment : CounterAction
    data object Decrement : CounterAction
}

sealed interface CounterChange {
    data class UpdateCount(val count: Int) : CounterChange
}

sealed interface CounterSideEffect

class CounterViewModel :
    ReduxViewModel<CounterState, CounterAction, CounterChange, CounterSideEffect>(
        initialState = CounterState(),
        middlewares = listOf(
            middleware { action, state ->
                when (action) {
                    CounterAction.Increment -> change(CounterChange.UpdateCount(state.count + 1))
                    CounterAction.Decrement -> change(CounterChange.UpdateCount(state.count - 1))
                }
            }
        ),
        reducer = reducer { state, change ->
            when (change) {
                is CounterChange.UpdateCount -> state.copy(count = change.count)
            }
        }
    )

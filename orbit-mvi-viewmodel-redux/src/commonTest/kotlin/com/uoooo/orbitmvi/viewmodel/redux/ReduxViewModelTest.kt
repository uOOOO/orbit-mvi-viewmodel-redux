package com.uoooo.orbitmvi.viewmodel.redux

import kotlinx.coroutines.test.runTest
import org.orbitmvi.orbit.test.test
import kotlin.test.Test

class ReduxViewModelTest {
    private val testReducer = object : Reducer<TestState, TestChange> {
        override fun reduce(state: TestState, change: TestChange): TestState {
            return when (change) {
                is TestChange.Increment -> state.copy(count = state.count + 1)
                is TestChange.Decrement -> state.copy(count = state.count - 1)
            }
        }
    }

    private val testMiddleware = middleware<TestState, TestAction, TestChange, TestSideEffect> { action, _ ->
        when (action) {
            TestAction.ClickPlus -> change(TestChange.Increment)
            TestAction.ClickMinus -> change(TestChange.Decrement)
            TestAction.TriggerToast -> sideEffect(TestSideEffect.ShowToast("Hello"))
            TestAction.ChainAction -> dispatch(TestAction.ClickPlus)
        }
    }

    @Test
    fun `dispatch ClickPlus action updates state correctly via Change`() = runTest {
        val viewModel = TestViewModel(listOf(testMiddleware), testReducer)

        viewModel.test(this) {
            val job = runOnCreate()

            containerHost.dispatch(TestAction.ClickPlus)

            expectState { copy(count = 1) }

            job.cancel()
        }
    }

    @Test
    fun `dispatch TriggerToast action emits SideEffect`() = runTest {
        val viewModel = TestViewModel(listOf(testMiddleware), testReducer)

        viewModel.test(this) {
            val job = runOnCreate()

            containerHost.dispatch(TestAction.TriggerToast)

            expectSideEffect(TestSideEffect.ShowToast("Hello"))

            job.cancel()
        }
    }

    @Test
    fun `dispatch ChainAction emits another Action and eventually updates state`() = runTest {
        val viewModel = TestViewModel(listOf(testMiddleware), testReducer)

        viewModel.test(this) {
            val job = runOnCreate()

            containerHost.dispatch(TestAction.ChainAction)

            expectState { copy(count = 1) }

            job.cancel()
        }
    }

    private data class TestState(val count: Int = 0)

    private sealed interface TestAction {
        object ClickPlus : TestAction
        object ClickMinus : TestAction
        object TriggerToast : TestAction
        object ChainAction : TestAction
    }

    private sealed interface TestChange {
        object Increment : TestChange
        object Decrement : TestChange
    }

    private sealed interface TestSideEffect {
        data class ShowToast(val message: String) : TestSideEffect
    }

    private class TestViewModel(
        middlewares: List<Middleware<TestState, TestAction, TestChange, TestSideEffect>>,
        reducer: Reducer<TestState, TestChange>,
    ) : ReduxViewModel<TestState, TestAction, TestChange, TestSideEffect>(
        initialState = TestState(),
        middlewares = middlewares,
        reducer = reducer
    )
}

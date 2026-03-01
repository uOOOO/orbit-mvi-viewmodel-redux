# Orbit MVI ViewModel Redux

`ReduxViewModel` is a library built on top of the [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi) framework. It integrates the Redux pattern to make state management more structured and predictable in Android ViewModels.

## Architecture Flow

The following diagram illustrates how actions flow through the system:

```mermaid
sequenceDiagram
    participant UI
    participant ViewModel
    participant Middleware
    participant Reducer
    participant SideEffectHandler
    participant State

    UI->>ViewModel: dispatch(Action)
    ViewModel->>Middleware: handle(Action, CurrentState)
    Middleware-->>ViewModel: MiddlewareResult (Action, Change, SideEffect)
    
    alt result is Action
        ViewModel->>ViewModel: dispatch(Action)
    else result is Change
        ViewModel->>Reducer: reduce(CurrentState, Change)
        Reducer-->>ViewModel: New State
        ViewModel->>State: Update State
        State-->>UI: Observe New State
    else result is SideEffect
        ViewModel-->>UI: Post SideEffect
        UI->>SideEffectHandler: handle(SideEffect)
    end
```

## Key Concepts

- **State**: An immutable data class representing the UI state.
- **Action**: A sealed interface defining user intentions or system events.
- **Change**: Internal events produced by Middleware to trigger state updates in the Reducer.
- **SideEffect**: One-time events like Toast notifications, Navigation, or Dialogs.
- **Middleware**: Handles business logic and asynchronous tasks. It processes `Action` and emits `Change`, other `Action`s, or `SideEffect`s.
- **Reducer**: A pure function that takes the current `State` and a `Change` to produce a new `State`.
- **SideEffectHandler**: An interface for handling emitted `SideEffect`s in the UI layer (Activity, Fragment, etc.).

## Getting Started

### 1. Define Components

Define the State, Action, Change, and SideEffect for your ViewModel.

```kotlin
data class MainState(val count: Int = 0, val isLoading: Boolean = false)

sealed interface MainAction {
    object ClickPlus : MainAction
    object ClickMinus : MainAction
    object LoadData : MainAction
}

sealed interface MainChange {
    object Increment : MainChange
    object Decrement : MainChange
    data class SetLoading(val isLoading: Boolean) : MainChange
}

sealed interface MainSideEffect {
    data class ShowToast(val message: String) : MainSideEffect
}
```

### 2. Implement Middleware

Implement `Middleware` to handle business logic.

```kotlin
class MainMiddleware : Middleware<MainState, MainAction, MainChange, MainSideEffect> {
    override fun handle(
        action: MainAction,
        state: MainState
    ): Flow<MiddlewareResult<MainAction, MainChange, MainSideEffect>> = flow {
        when (action) {
            MainAction.ClickPlus -> emit(MiddlewareResult.Change(MainChange.Increment))
            MainAction.ClickMinus -> emit(MiddlewareResult.Change(MainChange.Decrement))
            MainAction.LoadData -> {
                emit(MiddlewareResult.Change(MainChange.SetLoading(true)))
                // Perform async work...
                emit(MiddlewareResult.Change(MainChange.SetLoading(false)))
                emit(MiddlewareResult.SideEffect(MainSideEffect.ShowToast("Data Loaded")))
            }
        }
    }
}
```

### 3. Implement Reducer

Implement `Reducer` to define how `Change` updates the `State`.

```kotlin
class MainReducer : Reducer<MainState, MainChange> {
    override fun reduce(state: MainState, change: MainChange): MainState {
        return when (change) {
            MainChange.Increment -> state.copy(count = state.count + 1)
            MainChange.Decrement -> state.copy(count = state.count - 1)
            is MainChange.SetLoading -> state.copy(isLoading = change.isLoading)
        }
    }
}
```

### 4. Implement SideEffectHandler (Optional)

You can use `SideEffectHandler` to encapsulate side effect handling logic.

```kotlin
class MainSideEffectHandler(
    private val context: Context
) : SideEffectHandler<MainSideEffect> {
    override suspend fun handle(sideEffect: MainSideEffect) {
        when (sideEffect) {
            is MainSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

### 5. Create ViewModel

Inherit from `ReduxViewModel` and pass the initial state, middlewares, and reducer.

```kotlin
class MainViewModel(
    middlewares: List<Middleware<MainState, MainAction, MainChange, MainSideEffect>>,
    reducer: Reducer<MainState, MainChange>
) : ReduxViewModel<MainState, MainAction, MainChange, MainSideEffect>(
    initialState = MainState(),
    middlewares = middlewares,
    reducer = reducer
)
```

### 6. Use in UI

```kotlin
// In your Activity or Fragment
val sideEffectHandler = MainSideEffectHandler(this)

// Dispatch an action
viewModel.dispatch(MainAction.ClickPlus)

// Observe State
lifecycleScope.launch {
    viewModel.container.stateFlow.collect { state ->
        // Update UI
    }
}

// Observe SideEffects using SideEffectHandler
lifecycleScope.launch {
    viewModel.container.sideEffectFlow.collect { sideEffect ->
        sideEffectHandler.handle(sideEffect)
    }
}
```

## License

This project is licensed under the MIT License.

package com.uoooo.sample.orbitmvi.counter.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun App(viewModel: CounterViewModel = viewModel { CounterViewModel() }) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Count: ${state.count}",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        modifier = Modifier.size(width = 80.dp, height = 48.dp),
                        onClick = { viewModel.dispatch(CounterAction.Increment) }
                    ) {
                        Text("+", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.size(width = 80.dp, height = 48.dp),
                        onClick = { viewModel.dispatch(CounterAction.Decrement) }
                    ) {
                        Text("-", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

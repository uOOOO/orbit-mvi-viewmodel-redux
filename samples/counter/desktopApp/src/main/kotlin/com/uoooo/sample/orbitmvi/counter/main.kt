package com.uoooo.sample.orbitmvi.counter

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.uoooo.sample.orbitmvi.counter.shared.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Counter",
    ) {
        App()
    }
}
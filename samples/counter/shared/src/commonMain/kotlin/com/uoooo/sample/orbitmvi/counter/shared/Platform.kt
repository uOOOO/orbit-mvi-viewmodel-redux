package com.uoooo.sample.orbitmvi.counter.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
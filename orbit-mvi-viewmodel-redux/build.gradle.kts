import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.uoooo.orbitmvi.viewmodel.redux"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    js {
        browser()
        nodejs()
    }

    wasmJs {
        browser()
        nodejs()
    }

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosSimulatorArm64(),
        watchosX64(),
        watchosArm32(),
        watchosArm64(),
        tvosSimulatorArm64(),
        tvosX64(),
        tvosArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "orbit-mvi-viewmodel-redux"
            isStatic = true
        }
    }

    linuxX64()
    linuxArm64()

    jvm("desktop")

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(libs.androidx.lifecycle.viewmodel)
        implementation(libs.orbit.viewmodel)

        testImplementation(libs.kotlin.test)
    }
}

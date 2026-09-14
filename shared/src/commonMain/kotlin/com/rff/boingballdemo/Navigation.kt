package com.rff.boingballdemo

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.rff.boingballdemo.about.AboutScreenRoot
import com.rff.boingballdemo.calculator.CalculatorScreenRoot
import com.rff.boingballdemo.clock.ClockScreenRoot
import com.rff.boingballdemo.copper.CopperBarsScreenRoot
import com.rff.boingballdemo.main.BoingBallScreenRoot
import com.rff.boingballdemo.preferences.PreferencesScreenRoot
import com.rff.boingballdemo.shell.ShellScreenRoot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object Home : AppRoute

@Serializable
data object Prefs : AppRoute

@Serializable
data object Clock : AppRoute

@Serializable
data object About : AppRoute

@Serializable
data object Copper : AppRoute

@Serializable
data object Calculator : AppRoute

@Serializable
data object Shell : AppRoute

@OptIn(ExperimentalSerializationApi::class)
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class)
            subclass(Prefs::class)
            subclass(Clock::class)
            subclass(About::class)
            subclass(Copper::class)
            subclass(Calculator::class)
            subclass(Shell::class)
        }
    }
}

@Composable
fun NavigationRoot(
    onExitApp: () -> Unit = {},
) {
    val backStack = rememberNavBackStack(navConfig, Home)
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when(key) {
                Home -> {
                    NavEntry(key = key) {
                        BoingBallScreenRoot(
                            onPreferencesClick = {
                                backStack.add(Prefs)
                            },
                            onClockClick = {
                                backStack.add(Clock)
                            },
                            onAboutClick = {
                                backStack.add(About)
                            },
                            onCopperBarsClick = {
                                backStack.add(Copper)
                            },
                            onCalculatorClick = {
                                backStack.add(Calculator)
                            },
                            onShellClick = {
                                backStack.add(Shell)
                            },
                            onCloseClick = onExitApp
                        )
                    }
                }
                Prefs -> {
                    NavEntry(key = key) {
                        PreferencesScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                Clock -> {
                    NavEntry(key = key) {
                        ClockScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                About -> {
                    NavEntry(key = key) {
                        AboutScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                Copper -> {
                    NavEntry(key = key) {
                        CopperBarsScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                Calculator -> {
                    NavEntry(key = key) {
                        CalculatorScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                Shell -> {
                    NavEntry(key = key) {
                        ShellScreenRoot(
                            onCloseClick = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}

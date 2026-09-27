package com.rff.boingballdemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.rff.boingballdemo.screens.about.AboutScreenRoot
import com.rff.boingballdemo.screens.calculator.CalculatorScreenRoot
import com.rff.boingballdemo.screens.clock.ClockScreenRoot
import com.rff.boingballdemo.screens.copper.CopperBarsScreenRoot
import com.rff.boingballdemo.screens.boingball.BoingBallScreenRoot
import com.rff.boingballdemo.screens.workbench.WorkbenchScreenRoot
import com.rff.boingballdemo.screens.musicplayer.MusicPlayerScreenRoot
import com.rff.boingballdemo.screens.preferences.PreferencesScreenRoot
import com.rff.boingballdemo.screens.shell.ShellScreenRoot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object WorkbenchRoute : AppRoute

@Serializable
data object BoingBallRoute : AppRoute

@Serializable
data object PreferencesRoute : AppRoute

@Serializable
data object ClockRoute : AppRoute

@Serializable
data object AboutRoute : AppRoute

@Serializable
data object CopperBarsRoute : AppRoute

@Serializable
data object CalculatorRoute : AppRoute

@Serializable
data object ShellRoute : AppRoute

@Serializable
data object MusicPlayerRoute : AppRoute

@OptIn(ExperimentalSerializationApi::class)
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(WorkbenchRoute::class)
            subclass(BoingBallRoute::class)
            subclass(PreferencesRoute::class)
            subclass(ClockRoute::class)
            subclass(AboutRoute::class)
            subclass(CopperBarsRoute::class)
            subclass(CalculatorRoute::class)
            subclass(ShellRoute::class)
            subclass(MusicPlayerRoute::class)
        }
    }
}

/**
 * Pushes [route] unless it is already on the back stack. Every app window is
 * single-instance, so a double tap (or two taps landing before recomposition)
 * must not open the same window twice.
 */
internal fun MutableList<NavKey>.pushSingleInstance(route: NavKey): Boolean {
    if (route in this) return false
    add(route)
    return true
}

@Composable
fun NavigationRoot(
    onExitApp: () -> Unit = {},
) {
    // The Workbench remains below the initially displayed demo. Dismissing the
    // demo therefore reveals a desktop with no window open.
    val backStack = rememberNavBackStack(navConfig, WorkbenchRoute, BoingBallRoute)
    val popBackStack: () -> Unit = {
        // Guard against popping the last remaining entry, which would leave
        // NavDisplay with an empty backstack and crash. This can happen if
        // onCloseClick fires more than once before recomposition settles
        // (e.g. a duplicate click or a click racing the back gesture).
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                popBackStack()
            } else {
                onExitApp()
            }
        },
        // Scope each entry's ViewModels to the entry itself so they are cleared
        // when the entry is popped (e.g. the music player stops on close).
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            when(key) {
                WorkbenchRoute -> {
                    NavEntry(key = key) {
                        WorkbenchScreenRoot(
                            onBoingBallClick = {
                                backStack.pushSingleInstance(BoingBallRoute)
                            },
                            onPreferencesClick = {
                                backStack.pushSingleInstance(PreferencesRoute)
                            },
                            onClockClick = {
                                backStack.pushSingleInstance(ClockRoute)
                            },
                            onAboutClick = {
                                backStack.pushSingleInstance(AboutRoute)
                            },
                            onCopperBarsClick = {
                                backStack.pushSingleInstance(CopperBarsRoute)
                            },
                            onCalculatorClick = {
                                backStack.pushSingleInstance(CalculatorRoute)
                            },
                            onShellClick = {
                                backStack.pushSingleInstance(ShellRoute)
                            },
                            onMusicPlayerClick = {
                                backStack.pushSingleInstance(MusicPlayerRoute)
                            },
                        )
                    }
                }
                BoingBallRoute -> {
                    NavEntry(key = key) {
                        BoingBallScreenRoot(
                            onDismiss = { popBackStack() }
                        )
                    }
                }
                PreferencesRoute -> {
                    NavEntry(key = key) {
                        PreferencesScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                ClockRoute -> {
                    NavEntry(key = key) {
                        ClockScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                AboutRoute -> {
                    NavEntry(key = key) {
                        AboutScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                CopperBarsRoute -> {
                    NavEntry(key = key) {
                        CopperBarsScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                CalculatorRoute -> {
                    NavEntry(key = key) {
                        CalculatorScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                ShellRoute -> {
                    NavEntry(key = key) {
                        ShellScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                MusicPlayerRoute -> {
                    NavEntry(key = key) {
                        MusicPlayerScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}

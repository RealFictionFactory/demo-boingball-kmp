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
import com.rff.boingballdemo.boingball.BoingBallScreenRoot
import com.rff.boingballdemo.workbench.WorkbenchScreenRoot
import com.rff.boingballdemo.musicplayer.MusicPlayerScreenRoot
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
data object FullScreenBoingBall : AppRoute

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

@Serializable
data object MusicPlayer : AppRoute

@OptIn(ExperimentalSerializationApi::class)
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class)
            subclass(FullScreenBoingBall::class)
            subclass(Prefs::class)
            subclass(Clock::class)
            subclass(About::class)
            subclass(Copper::class)
            subclass(Calculator::class)
            subclass(Shell::class)
            subclass(MusicPlayer::class)
        }
    }
}

@Composable
fun NavigationRoot(
    onExitApp: () -> Unit = {},
) {
    // The Workbench remains below the initially displayed demo. Dismissing the
    // demo therefore reveals a desktop with no window open.
    val backStack = rememberNavBackStack(navConfig, Home, FullScreenBoingBall)
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
        entryProvider = { key ->
            when(key) {
                Home -> {
                    NavEntry(key = key) {
                        WorkbenchScreenRoot(
                            onBoingBallClick = {
                                backStack.add(FullScreenBoingBall)
                            },
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
                            onMusicPlayerClick = {
                                backStack.add(MusicPlayer)
                            },
                        )
                    }
                }
                FullScreenBoingBall -> {
                    NavEntry(key = key) {
                        BoingBallScreenRoot(
                            onDismiss = { popBackStack() }
                        )
                    }
                }
                Prefs -> {
                    NavEntry(key = key) {
                        PreferencesScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                Clock -> {
                    NavEntry(key = key) {
                        ClockScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                About -> {
                    NavEntry(key = key) {
                        AboutScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                Copper -> {
                    NavEntry(key = key) {
                        CopperBarsScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                Calculator -> {
                    NavEntry(key = key) {
                        CalculatorScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                Shell -> {
                    NavEntry(key = key) {
                        ShellScreenRoot(
                            onCloseClick = {
                                popBackStack()
                            }
                        )
                    }
                }
                MusicPlayer -> {
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

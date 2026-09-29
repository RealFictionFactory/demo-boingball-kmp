package com.rff.boingballdemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.rff.boingballdemo.component.LocalOsStyle
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.screens.about.AboutScreenRoot
import com.rff.boingballdemo.screens.calculator.CalculatorScreenRoot
import com.rff.boingballdemo.screens.clock.ClockScreenRoot
import com.rff.boingballdemo.screens.copper.CopperBarsScreenRoot
import com.rff.boingballdemo.screens.boingball.BoingBallScreenRoot
import com.rff.boingballdemo.screens.workbench.WorkbenchScreenRoot
import com.rff.boingballdemo.screens.musicplayer.MusicPlayerScreenRoot
import com.rff.boingballdemo.screens.plasma.PlasmaScreenRoot
import com.rff.boingballdemo.screens.preferences.PreferencesScreenRoot
import com.rff.boingballdemo.screens.shell.ShellScreenRoot
import com.rff.boingballdemo.screens.sinescroll.SineScrollScreenRoot
import com.rff.boingballdemo.screens.starfield.StarfieldScreenRoot
import com.rff.boingballdemo.ui.theme.ProvideOsStyle
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.koinInject

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

@Serializable
data object StarfieldRoute : AppRoute

@Serializable
data object SineScrollRoute : AppRoute

@Serializable
data object PlasmaRoute : AppRoute

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
            subclass(StarfieldRoute::class)
            subclass(SineScrollRoute::class)
            subclass(PlasmaRoute::class)
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
    val settings = koinInject<AppSettings>()
    // Start from the style the caller provides (Android passes the stored one after the
    // splash screen), so the first frame does not flash the default style.
    val osStyle by settings.osStyle.collectAsStateWithLifecycle(LocalOsStyle.current)
    ProvideOsStyle(osStyle) {
        AppNavDisplay(onExitApp)
    }
}

@Composable
private fun AppNavDisplay(onExitApp: () -> Unit) {
    // The Workbench remains below the initially displayed demo. Dismissing the
    // demo therefore reveals a desktop with no window open.
    val backStack = rememberNavBackStack(navConfig, WorkbenchRoute, BoingBallRoute)

    fun open(route: AppRoute) {
        backStack.pushSingleInstance(route)
    }

    // Never pops the Workbench: an empty back stack crashes NavDisplay. This also
    // absorbs a close that fires twice before recomposition settles (a duplicate
    // click, or a click racing the back gesture). Returns false if nothing was popped.
    fun close(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeLastOrNull()
        return true
    }

    val onCloseClick: () -> Unit = { close() }

    NavDisplay(
        backStack = backStack,
        onBack = { if (!close()) onExitApp() },
        // Scope each entry's ViewModels to the entry itself so they are cleared
        // when the entry is popped (e.g. the music player stops on close).
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<WorkbenchRoute> {
                WorkbenchScreenRoot(
                    onBoingBallClick = { open(BoingBallRoute) },
                    onPreferencesClick = { open(PreferencesRoute) },
                    onClockClick = { open(ClockRoute) },
                    onAboutClick = { open(AboutRoute) },
                    onCopperBarsClick = { open(CopperBarsRoute) },
                    onCalculatorClick = { open(CalculatorRoute) },
                    onShellClick = { open(ShellRoute) },
                    onMusicPlayerClick = { open(MusicPlayerRoute) },
                    onStarfieldClick = { open(StarfieldRoute) },
                    onSineScrollClick = { open(SineScrollRoute) },
                    onPlasmaClick = { open(PlasmaRoute) },
                )
            }
            entry<BoingBallRoute> { BoingBallScreenRoot(onDismiss = onCloseClick) }
            entry<PreferencesRoute> { PreferencesScreenRoot(onCloseClick = onCloseClick) }
            entry<ClockRoute> { ClockScreenRoot(onCloseClick = onCloseClick) }
            entry<AboutRoute> { AboutScreenRoot(onCloseClick = onCloseClick) }
            entry<CopperBarsRoute> { CopperBarsScreenRoot(onCloseClick = onCloseClick) }
            entry<CalculatorRoute> { CalculatorScreenRoot(onCloseClick = onCloseClick) }
            entry<ShellRoute> { ShellScreenRoot(onCloseClick = onCloseClick) }
            entry<MusicPlayerRoute> { MusicPlayerScreenRoot(onCloseClick = onCloseClick) }
            entry<StarfieldRoute> { StarfieldScreenRoot(onCloseClick = onCloseClick) }
            entry<SineScrollRoute> { SineScrollScreenRoot(onCloseClick = onCloseClick) }
            entry<PlasmaRoute> { PlasmaScreenRoot(onCloseClick = onCloseClick) }
        },
    )
}

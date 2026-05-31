package pl.chaoticorder.boingball

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import pl.chaoticorder.boingball.main.BoingBallScreenRoot
import pl.chaoticorder.boingball.preferences.PreferencesScreenRoot
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

@OptIn(ExperimentalSerializationApi::class)
private val navConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class)
            subclass(Prefs::class)
        }
    }
}

@Composable
fun NavigationRoot() {
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
                            }
                        )
                    }
                }
                Prefs -> {
                    NavEntry(key = key) {
                        PreferencesScreenRoot()
                    }
                }
                else -> throw IllegalArgumentException("Unknown key: $key")
            }
        }
    )
}

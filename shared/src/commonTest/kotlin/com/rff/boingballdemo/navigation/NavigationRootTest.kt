package com.rff.boingballdemo.navigation

import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationRootTest {
    @Test
    fun pushesRouteNotOnBackStack() {
        val backStack = mutableListOf<NavKey>(WorkbenchRoute)

        assertTrue(backStack.pushSingleInstance(ClockRoute))
        assertEquals(listOf<NavKey>(WorkbenchRoute, ClockRoute), backStack)
    }

    @Test
    fun ignoresDoubleTapOnSameRoute() {
        val backStack = mutableListOf<NavKey>(WorkbenchRoute)

        backStack.pushSingleInstance(MusicPlayerRoute)
        assertFalse(backStack.pushSingleInstance(MusicPlayerRoute))
        assertEquals(listOf<NavKey>(WorkbenchRoute, MusicPlayerRoute), backStack)
    }

    @Test
    fun ignoresRouteAlreadyDeeperInBackStack() {
        val backStack = mutableListOf<NavKey>(WorkbenchRoute, ClockRoute, AboutRoute)

        assertFalse(backStack.pushSingleInstance(ClockRoute))
        assertEquals(listOf<NavKey>(WorkbenchRoute, ClockRoute, AboutRoute), backStack)
    }

    @Test
    fun routeCanBeReopenedAfterPop() {
        val backStack = mutableListOf<NavKey>(WorkbenchRoute, ShellRoute)
        backStack.removeLastOrNull()

        assertTrue(backStack.pushSingleInstance(ShellRoute))
        assertEquals(listOf<NavKey>(WorkbenchRoute, ShellRoute), backStack)
    }
}

package com.rff.boingballdemo.screens.musicplayer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Playlist interactions. The state is hoisted into the test, which applies moves the same
 * way the player does, so the screen sees the reordered list like in the app.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MusicPlayerScreenTest {
    private val tracks = AMIGA_MUSIC_TRACKS.take(3)

    @Test
    fun playlistButtonOpensPlaylist() = runComposeUiTest {
        setContent {
            BoingBallDemoTheme { MusicPlayerScreen(state = MusicPlayerState(tracks = tracks)) }
        }

        onNodeWithContentDescription("Playlist").performClick()

        onNodeWithText(tracks[1].title).assertIsDisplayed()
        onNodeWithContentDescription("Move track down").assertIsDisplayed()
    }

    @Test
    fun movingSelectedTrackKeepsItSelected() = runComposeUiTest {
        val actions = mutableListOf<MusicPlayerAction>()
        var state by mutableStateOf(MusicPlayerState(tracks = tracks))
        setContent {
            BoingBallDemoTheme {
                MusicPlayerScreen(
                    state = state,
                    onAction = { action ->
                        actions += action
                        if (action is MusicPlayerAction.MoveTrack) {
                            val target = moveTarget(state.tracks, action.index, action.direction)
                            if (target != null) {
                                state = state.copy(tracks = state.tracks.moved(action.index, target))
                            }
                        }
                    },
                    initiallyShowPlaylist = true,
                )
            }
        }

        // Select the first track and move it down twice: the second move must still
        // apply to the same track, now at index 1.
        // The title also appears in the now-playing panel; pick the clickable playlist row.
        onNode(hasText(tracks[0].title) and hasClickAction()).performClick()
        onNodeWithContentDescription("Move track down").performClick()
        onNodeWithContentDescription("Move track down").performClick()

        assertEquals(
            listOf<MusicPlayerAction>(MusicPlayerAction.MoveTrack(0, 1), MusicPlayerAction.MoveTrack(1, 1)),
            actions,
        )
        assertEquals(tracks[0], state.tracks[2])
    }

    @Test
    fun playPauseButtonFollowsState() = runComposeUiTest {
        val actions = mutableListOf<MusicPlayerAction>()
        setContent {
            BoingBallDemoTheme {
                MusicPlayerScreen(
                    state = MusicPlayerState(tracks = tracks, isPlaying = true),
                    onAction = { actions += it },
                )
            }
        }

        onNodeWithContentDescription("Pause").performClick()

        assertEquals(listOf<MusicPlayerAction>(MusicPlayerAction.Pause), actions)
    }
}

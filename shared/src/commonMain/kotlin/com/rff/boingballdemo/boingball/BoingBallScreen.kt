package com.rff.boingballdemo.boingball

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rff.boingballdemo.component.BoingBallView
import com.rff.boingballdemo.workbench.BoingBallState
import com.rff.boingballdemo.workbench.WorkbenchViewModel
import com.rff.boingballdemo.ui.theme.backgroundColor
import org.koin.compose.viewmodel.koinViewModel

/** A chrome-free demo screen. A tap returns the user to the Workbench. */
@Composable
fun BoingBallScreenRoot(
    viewModel: WorkbenchViewModel = koinViewModel(),
    onDismiss: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BoingBallScreen(state = state, onDismiss = onDismiss)
}

@Composable
fun BoingBallScreen(
    state: BoingBallState,
    onDismiss: () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        val useFullHeight = maxWidth / maxHeight > 4f / 3f
        BoingBallView(
            modifier = if (useFullHeight) {
                Modifier.fillMaxHeight()
            } else {
                Modifier.fillMaxWidth()
            },
            themeColor = state.themeColor,
            altColor = state.altColor,
            drawBorders = state.drawBorders,
            videoSystem = state.videoSystem,
        )
    }
}

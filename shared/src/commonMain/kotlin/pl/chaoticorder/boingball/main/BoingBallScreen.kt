package pl.chaoticorder.boingball.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.app_name
import boingball.shared.generated.resources.preferences
import pl.chaoticorder.boingball.component.AmigaToolbar
import pl.chaoticorder.boingball.component.BoingBallView
import pl.chaoticorder.boingball.component.OSStyle
import pl.chaoticorder.boingball.ui.theme.BoingBallDemoTheme
import pl.chaoticorder.boingball.ui.theme.amigaOs30Blue
import pl.chaoticorder.boingball.ui.theme.backgroundColor
import pl.chaoticorder.boingball.ui.theme.blackColor
import pl.chaoticorder.boingball.ui.theme.topazFont
import pl.chaoticorder.boingball.ui.theme.whiteColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BoingBallScreenRoot(
    viewModel: BoingBallViewModel = koinViewModel(),
    onPreferencesClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BoingBallScreen(
        state = state,
        onAction = { action ->
            when (action) {
                BoingBallAction.Preferences -> {
                    onPreferencesClick()
                }
            }
        }
    )
}

@Composable
fun BoingBallScreen(
    state: BoingBallState,
    onAction: (BoingBallAction) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmigaToolbar(
                title = stringResource(Res.string.app_name),
                osStyle = state.osStyle
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .conditional(
                        condition = state.osStyle == OSStyle.AmigaOS13,
                        ifTrue = {
                            background(color = Color.White)
                            .padding(horizontal = 2.dp)
                            .padding(bottom = 2.dp)
                        },
                        ifFalse = {
                            background(color = Color.White)
                                .padding(horizontal = 1.dp)
                                .background(color = amigaOs30Blue)
                                .padding(horizontal = 2.dp)
                                .background(color = blackColor)
                                .padding(horizontal = 1.dp)
                                .background(color = blackColor)
                                .padding(bottom = 1.dp)
                                .background(color = whiteColor)
                                .padding(bottom = 1.dp)
                        }
                    )
                    .background(color = backgroundColor),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BoingBallView(
                    modifier = Modifier.padding(16.dp),
                    themeColor = state.themeColor,
                    altColor = state.altColor,
                    drawBorders = state.drawBorders,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 40.dp)
                .clickable { onAction(BoingBallAction.Preferences) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp),
                painter = painterResource(Res.drawable.preferences),
                contentDescription = stringResource(Res.string.preferences)
            )
            Text(
                text = stringResource(Res.string.preferences),
                fontFamily = topazFont()
            )
        }
    }
}

inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

private val previewState = BoingBallState(
    themeColor = Color.Red,
    altColor = Color.White,
    drawBorders = false,
    osStyle = OSStyle.AmigaOS13
)

@Preview
@Composable
private fun BoingBallScreenPreview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState)
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun BoingBallScreenLandscapePreview() {
    BoingBallDemoTheme {
        BoingBallScreen(previewState)
    }
}

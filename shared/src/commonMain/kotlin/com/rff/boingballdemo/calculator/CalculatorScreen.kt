package com.rff.boingballdemo.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.calculator
import boingball.shared.generated.resources.workbench
import com.rff.boingballdemo.component.AmigaKey
import com.rff.boingballdemo.component.AmigaScreenTitleBar
import com.rff.boingballdemo.component.AmigaToolbar
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.main.conditional
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs30Blue
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private data class CalculatorKey(
    val label: String,
    val action: CalculatorAction,
)

private val CalculatorKeypad: List<List<CalculatorKey>> = listOf(
    listOf(
        CalculatorKey("CE", CalculatorAction.ClearEntry),
        CalculatorKey("C", CalculatorAction.Clear),
        CalculatorKey("%", CalculatorAction.Percent),
        CalculatorKey("/", CalculatorAction.Operator(CalculatorOperator.Divide)),
    ),
    listOf(
        CalculatorKey("7", CalculatorAction.Digit(7)),
        CalculatorKey("8", CalculatorAction.Digit(8)),
        CalculatorKey("9", CalculatorAction.Digit(9)),
        CalculatorKey("*", CalculatorAction.Operator(CalculatorOperator.Multiply)),
    ),
    listOf(
        CalculatorKey("4", CalculatorAction.Digit(4)),
        CalculatorKey("5", CalculatorAction.Digit(5)),
        CalculatorKey("6", CalculatorAction.Digit(6)),
        CalculatorKey("-", CalculatorAction.Operator(CalculatorOperator.Subtract)),
    ),
    listOf(
        CalculatorKey("1", CalculatorAction.Digit(1)),
        CalculatorKey("2", CalculatorAction.Digit(2)),
        CalculatorKey("3", CalculatorAction.Digit(3)),
        CalculatorKey("+", CalculatorAction.Operator(CalculatorOperator.Add)),
    ),
    listOf(
        CalculatorKey("0", CalculatorAction.Digit(0)),
        CalculatorKey(".", CalculatorAction.Decimal),
        CalculatorKey("+/-", CalculatorAction.ToggleSign),
        CalculatorKey("=", CalculatorAction.Equals),
    ),
)

@Composable
fun CalculatorScreenRoot(
    viewModel: CalculatorViewModel = koinViewModel(),
    onCloseClick: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorScreen(
        state = state,
        onAction = viewModel::onAction,
        onCloseClick = onCloseClick,
    )
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    val bg = if (state.osStyle == OSStyle.AmigaOS20) backgroundColor else amigaOs13Blue

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        val isLandscape = maxWidth > maxHeight
        // Chrome + display above the keypad; the keypad itself is ~1.04 * window width.
        val chromeHeight = 165.dp
        val widthLimitedByHeight = ((maxHeight - chromeHeight) / 1.04f).coerceAtLeast(0.dp)
        val preferredWidth = if (isLandscape) maxWidth * 0.5f else maxWidth * 0.82f
        val windowWidth = minOf(preferredWidth, widthLimitedByHeight)

        Column(modifier = Modifier.fillMaxSize()) {
            AmigaScreenTitleBar(
                text = stringResource(Res.string.workbench),
                osStyle = state.osStyle,
            )
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier.width(windowWidth)) {
                    AmigaToolbar(
                        title = stringResource(Res.string.calculator),
                        osStyle = state.osStyle,
                        onCloseClick = onCloseClick,
                    )
                    CalculatorContent(state = state, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun CalculatorContent(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
                },
            )
            .background(color = if (state.osStyle == OSStyle.AmigaOS13) amigaOs13Blue else backgroundColor)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CalculatorDisplay(state = state)

        CalculatorKeypad.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { key ->
                    AmigaKey(
                        text = key.label,
                        osStyle = state.osStyle,
                        modifier = Modifier.weight(1f).aspectRatio(1.2f),
                        onClick = { onAction(key.action) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorDisplay(
    state: CalculatorState,
    modifier: Modifier = Modifier,
) {
    val isOs13 = state.osStyle == OSStyle.AmigaOS13

    Box(
        modifier = modifier
            .fillMaxWidth()
            .conditional(
                condition = isOs13,
                ifTrue = { background(color = whiteColor).padding(1.dp) },
                ifFalse = {
                    background(color = blackColor)
                        .padding(start = 1.dp, top = 1.dp)
                        .background(color = whiteColor)
                        .padding(end = 1.dp, bottom = 1.dp)
                },
            )
            .background(color = blackColor)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = state.display,
            fontFamily = if (isOs13) topazFont() else topazFont20(),
            fontSize = 24.sp,
            maxLines = 1,
            textAlign = TextAlign.End,
            color = if (isOs13) whiteColor else Color(0xFF66FF66),
        )
    }
}

private val previewState = CalculatorState(
    osStyle = OSStyle.AmigaOS13,
    display = "1234.5",
)

@Preview(device = "id:pixel_10")
@Composable
private fun CalculatorScreenOs13Preview() {
    BoingBallDemoTheme {
        CalculatorScreen(state = previewState)
    }
}

@Preview(device = "spec:parent=pixel_10,orientation=landscape")
@Composable
private fun CalculatorScreenLandscapeOs13Preview() {
    BoingBallDemoTheme {
        CalculatorScreen(state = previewState)
    }
}

@Preview(device = "id:pixel_10")
@Composable
private fun CalculatorScreenOs30Preview() {
    BoingBallDemoTheme {
        CalculatorScreen(state = previewState.copy(osStyle = OSStyle.AmigaOS20))
    }
}

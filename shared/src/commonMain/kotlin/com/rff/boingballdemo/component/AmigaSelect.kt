package com.rff.boingballdemo.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.ic_cycle
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.amigaOs13Orange
import com.rff.boingballdemo.ui.theme.amigaOs30Grey
import com.rff.boingballdemo.ui.theme.backgroundColor
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20
import com.rff.boingballdemo.ui.theme.whiteColor
import org.jetbrains.compose.resources.painterResource

private data class AmigaSelectStyle(
    val background: Color,
    val border: Color,
    val content: Color,
    val itemSelectedBackground: Color,
    val itemSelectedContent: Color,
)

internal fun notifyIfOptionChanged(
    selectedOption: String,
    tappedOption: String,
    onOptionSelected: (String) -> Unit,
) {
    if (tappedOption != selectedOption) {
        onOptionSelected(tappedOption)
    }
}

@Composable
fun AmigaSelect(
    text: String,
    options: List<String>,
    selectedOption: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        AmigaTextBox(text = text, osStyle = osStyle)
        Spacer(modifier = Modifier.height(4.dp))
        when (osStyle) {
            OSStyle.AmigaOS13 -> AmigaOs13Select(
                options = options,
                selectedOption = selectedOption,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onOptionSelected = onOptionSelected,
            )
            OSStyle.AmigaOS20 -> AmigaOs30Select(
                options = options,
                selectedOption = selectedOption,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onOptionSelected = onOptionSelected,
            )
        }
    }
}

@Composable
private fun AmigaOs13Select(
    options: List<String>,
    selectedOption: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
) {
    AmigaSelectField(
        options = options,
        selectedOption = selectedOption,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        onOptionSelected = onOptionSelected,
        osStyle = OSStyle.AmigaOS13,
        style = AmigaSelectStyle(
            background = amigaOs13Blue,
            border = whiteColor,
            content = whiteColor,
            itemSelectedBackground = amigaOs13Orange,
            itemSelectedContent = blackColor,
        ),
    )
}

@Composable
private fun AmigaOs30Select(
    options: List<String>,
    selectedOption: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
) {
    AmigaSelectField(
        options = options,
        selectedOption = selectedOption,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        onOptionSelected = onOptionSelected,
        osStyle = OSStyle.AmigaOS20,
        style = AmigaSelectStyle(
            background = whiteColor,
            border = blackColor,
            content = blackColor,
            itemSelectedBackground = blackColor,
            itemSelectedContent = whiteColor,
        ),
    )
}

@Composable
private fun AmigaSelectField(
    options: List<String>,
    selectedOption: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    osStyle: OSStyle,
    style: AmigaSelectStyle,
) {
    val density = LocalDensity.current
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    var fieldHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                fieldWidthPx = size.width
                fieldHeightPx = size.height
            }
    ) {
        SelectorRow(
            selectedOption = selectedOption,
            osStyle = osStyle,
            style = style,
            onClick = { onExpandedChange(!expanded) },
        )

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, fieldHeightPx),
                onDismissRequest = { onExpandedChange(false) },
            ) {
                Column(
                    modifier = Modifier
                        .width(with(density) { fieldWidthPx.toDp() })
                        .heightIn(max = 192.dp)
                        .background(color = style.background)
                        .border(width = 1.dp, color = style.border)
                        .verticalScroll(rememberScrollState())
                ) {
                    options.forEach { option ->
                        SelectOptionRow(
                            option = option,
                            isSelected = option == selectedOption,
                            osStyle = osStyle,
                            style = style,
                            onClick = {
                                onExpandedChange(false)
                                notifyIfOptionChanged(selectedOption, option, onOptionSelected)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorRow(
    selectedOption: String,
    osStyle: OSStyle,
    style: AmigaSelectStyle,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    var modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 28.dp)

    modifier = if (osStyle == OSStyle.AmigaOS13) {
        modifier.amigaOs13Frame()
    } else {
        modifier.amigaOs30Frame(fillColor = amigaOs30Grey)
    }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val tintColor = if (osStyle == OSStyle.AmigaOS20) {
            blackColor
        } else {
            whiteColor
        }
        Image(
            painter = painterResource(resource = Res.drawable.ic_cycle),
            contentDescription = "Checked",
            colorFilter = ColorFilter.tint(tintColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(16.dp)
                .background(color = style.border)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = selectedOption,
            style = selectTextStyle(osStyle, style.content),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SelectOptionRow(
    option: String,
    isSelected: Boolean,
    osStyle: OSStyle,
    style: AmigaSelectStyle,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val background = if (isSelected) style.itemSelectedBackground else style.background
    val content = if (isSelected) style.itemSelectedContent else style.content

    Text(
        text = option,
        style = selectTextStyle(osStyle, content),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = background)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
    )
}

@Composable
private fun selectTextStyle(osStyle: OSStyle, color: Color) =
    if (osStyle == OSStyle.AmigaOS13) {
        LocalTextStyle.current.copy(
            fontFamily = topazFont(),
            color = color,
        )
    } else {
        LocalTextStyle.current.copy(
            fontFamily = topazFont20(),
            color = color,
        )
    }

private val previewOptions = listOf("PAL", "NTSC")

@Preview
@Composable
private fun AmigaOs13SelectPreview() {
    BoingBallDemoTheme {
        Box(
            modifier = Modifier
                .background(color = amigaOs13Blue)
                .padding(16.dp)
        ) {
            var selected by remember { mutableStateOf("PAL") }
            AmigaSelect(
                text = "Video system",
                options = previewOptions,
                selectedOption = selected,
                osStyle = OSStyle.AmigaOS13,
                onOptionSelected = { selected = it },
            )
        }
    }
}

@Preview
@Composable
private fun AmigaOs30SelectPreview() {
    BoingBallDemoTheme {
        Box(
            modifier = Modifier
                .background(color = backgroundColor)
                .padding(16.dp)
        ) {
            var selected by remember { mutableStateOf("PAL") }
            AmigaSelect(
                text = "Video system",
                options = previewOptions,
                selectedOption = selected,
                osStyle = OSStyle.AmigaOS20,
                onOptionSelected = { selected = it },
            )
        }
    }
}

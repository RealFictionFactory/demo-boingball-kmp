package com.rff.boingballdemo.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.rff.boingballdemo.ui.theme.amigaOs13Blue
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.redColor
import com.rff.boingballdemo.ui.theme.whiteColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20

@Composable
fun AmigaTextBox(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    val textStyle = if (osStyle == OSStyle.AmigaOS13)
        LocalTextStyle.current.copy(
            fontFamily = topazFont(),
            color = whiteColor
        )
    else
        LocalTextStyle.current.copy(
            fontFamily = topazFont20(),
            color = blackColor
        )

    Text(
        modifier = modifier,
        text = text,
        style = textStyle.copy(textAlign = textAlign ?: textStyle.textAlign),
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun AmigaMenuText(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier
) {
    val textStyle = if (osStyle == OSStyle.AmigaOS13)
        LocalTextStyle.current.copy(
            fontFamily = topazFont(),
            color = amigaOs13Blue
        )
    else
        LocalTextStyle.current.copy(
            fontFamily = topazFont20(),
            color = blackColor
        )

    Text(
        modifier = modifier,
        text = text,
        style = textStyle
    )
}

@Composable
fun AmigaGuruText(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier
) {
    val textStyle = if (osStyle == OSStyle.AmigaOS13)
        LocalTextStyle.current.copy(
            fontFamily = topazFont(),
            textAlign = TextAlign.Center,
            color = redColor
        )
    else
        LocalTextStyle.current.copy(
            fontFamily = topazFont20(),
            textAlign = TextAlign.Center,
            color = redColor
        )

    Text(
        modifier = modifier,
        text = text,
        style = textStyle
    )
}

package com.rff.boingballdemo.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rff.boingballdemo.ui.theme.blackColor
import com.rff.boingballdemo.ui.theme.whiteColor
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.ui.theme.topazFont20

@Composable
fun AmigaTextBox(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier
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
        style = textStyle
    )
}

package pl.chaoticorder.boingball.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pl.chaoticorder.boingball.ui.theme.blackColor
import pl.chaoticorder.boingball.ui.theme.topazFont
import pl.chaoticorder.boingball.ui.theme.topazFont20

@Composable
fun AmigaTextBox(
    text: String,
    osStyle: OSStyle,
    modifier: Modifier = Modifier
) {
    val textStyle = if (osStyle == OSStyle.AmigaOS13)
        LocalTextStyle.current.copy(fontFamily = topazFont())
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

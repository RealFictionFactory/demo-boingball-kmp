package com.rff.boingballdemo.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import boingball.shared.generated.resources.Res
import boingball.shared.generated.resources.guru_meditation_code
import boingball.shared.generated.resources.guru_meditation_header
import com.rff.boingballdemo.ui.theme.redColor
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

@Composable
fun GuruMeditationOverlay(
    osStyle: OSStyle,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val guruNumber = remember {
        val hex = "0123456789ABCDEF"
        val p1 = List(8) { hex[Random.nextInt(16)] }.joinToString("")
        val p2 = List(8) { hex[Random.nextInt(16)] }.joinToString("")
        "#$p1.$p2"
    }

    val transition = rememberInfiniteTransition(label = "guru")
    val borderAlpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "borderBlink",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .drawBehind {
                    drawRect(Color.Black)
                    drawRect(
                        color = redColor.copy(alpha = borderAlpha),
                        style = Stroke(width = 10.dp.toPx()),
                    )
                }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmigaGuruText(
                text = stringResource(Res.string.guru_meditation_header),
                osStyle = osStyle
            )
            Spacer(modifier = Modifier.height(24.dp))
            AmigaGuruText(
                text = stringResource(Res.string.guru_meditation_code, guruNumber),
                osStyle = osStyle
            )
        }
    }
}

@Preview
@Composable
private fun GuruMeditationOverlayPreview() {
    GuruMeditationOverlay(
        osStyle = OSStyle.AmigaOS20,
        onDismiss = {}
    )
}

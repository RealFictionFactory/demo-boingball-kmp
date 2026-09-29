package com.rff.boingballdemo.screens.sinescroll.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.rff.boingballdemo.screens.copper.ui.drawCopperBar
import com.rff.boingballdemo.ui.theme.BoingBallDemoTheme
import com.rff.boingballdemo.ui.theme.topazFont
import com.rff.boingballdemo.utils.markImmutable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/** Size of one "Amiga pixel" relative to the view width (a 320 px wide screen). */
private const val SINE_AMIGA_PIXELS_ACROSS = 320f

/** Font size of the scroll text, in Amiga pixels. */
private const val SINE_FONT_SIZE = 32f

/** Scroll speed in Amiga pixels per second (2 pixels per PAL frame). */
private const val SINE_SCROLL_SPEED = 100f

/** Height of the copper raster bars framing the scroller, and their distance from the edges, in Amiga pixels. */
private const val RASTER_BAR_HEIGHT = 10f
private const val RASTER_BAR_MARGIN = 8f

/** Space kept between the raster bars and the wave, in Amiga pixels. */
private const val WAVE_MARGIN = 4f

/** Highest wave amplitude, in Amiga pixels. Steeper waves shear the letters beyond reading. */
private const val MAX_WAVE_AMPLITUDE = 44f

/** Colours of the text, from the top to the bottom of the wave, like a copper colour list. */
private val SineTextColors = listOf(
    Color(0xFFFFEE00),
    Color(0xFFFF8800),
    Color(0xFFFF2200),
    Color(0xFFDD22CC),
    Color(0xFF00AAFF),
    Color(0xFF00CC33),
    Color(0xFFFFEE00),
)

private val SineRasterBarColor = Color(0xFF00AAFF)

/** Every glyph of a [SineScrollText] drawn once, side by side, in cells of the same size. */
private class GlyphAtlas(val image: ImageBitmap, val cellWidth: Int, val cellHeight: Int)

/**
 * The classic demo scroller: a message scrolling right to left along a moving sine wave,
 * framed by two copper raster bars. The text is cut into thin vertical slices, each one
 * moved to its own height, so letters bend along the wave instead of just bobbing.
 * Animates only while the screen is resumed.
 */
@Composable
fun SineScrollView(
    modifier: Modifier = Modifier,
    message: String = SineScrollMessage,
) {
    val text = remember(message) { SineScrollText(message) }
    val fontFamily = topazFont()
    val textMeasurer = rememberTextMeasurer()
    // Seconds of animation so far; only the draw phase reads it.
    val clock = remember { FloatArray(1) }
    // Bumped every frame; reading it while drawing redraws without recomposing.
    var frameNanos by remember { mutableLongStateOf(0L) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Restart timing after a pause so the text does not jump.
            var lastFrame = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                // Cap long frames (e.g. after a hitch) so the scroll stays smooth.
                clock[0] += ((now - lastFrame) / 1_000_000_000f).coerceAtMost(0.1f)
                lastFrame = now
                frameNanos = now
            }
        }
    }

    Spacer(
        modifier = modifier
            .background(Color.Black)
            .drawBehind {
                val pixel = size.width / SINE_AMIGA_PIXELS_ACROSS
                drawCopperBar(RASTER_BAR_MARGIN * pixel, RASTER_BAR_HEIGHT * pixel, SineRasterBarColor)
                drawCopperBar(
                    size.height - (RASTER_BAR_MARGIN + RASTER_BAR_HEIGHT) * pixel,
                    RASTER_BAR_HEIGHT * pixel,
                    SineRasterBarColor,
                )
            }
            // The text is drawn white, then tinted by the colour list; the offscreen layer
            // keeps the tint from touching the background and raster bars.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithCache {
                val pixel = size.width / SINE_AMIGA_PIXELS_ACROSS
                val atlas = buildGlyphAtlas(textMeasurer, fontFamily, text.glyphs, SINE_FONT_SIZE * pixel)
                val spaceCell = text.glyphs.indexOf(' ')
                val textWidth = atlas.cellWidth.toFloat() * text.length
                // Slices one Amiga pixel wide: fine enough for smooth bends, few enough draw calls.
                val sliceWidth = pixel.roundToInt().coerceAtLeast(1)

                val bandTop = (RASTER_BAR_MARGIN + RASTER_BAR_HEIGHT + WAVE_MARGIN) * pixel
                val bandBottom = size.height - bandTop
                val amplitude = ((bandBottom - bandTop - atlas.cellHeight) / 2f).coerceIn(0f, MAX_WAVE_AMPLITUDE * pixel)
                val center = size.height / 2f
                val tint = Brush.verticalGradient(SineTextColors, startY = bandTop, endY = bandBottom)

                onDrawBehind {
                    frameNanos // read to redraw on every frame
                    val time = clock[0]
                    val textLeft = sineScrollTextLeft(time * SINE_SCROLL_SPEED * pixel, textWidth, size.width)
                    val first = floor(-textLeft / atlas.cellWidth).toInt().coerceAtLeast(0)
                    val last = ceil((size.width - textLeft) / atlas.cellWidth).toInt().coerceAtMost(text.length - 1)

                    for (index in first..last) {
                        val cell = text.cells[index]
                        if (cell == spaceCell) continue
                        val charLeft = textLeft + index * atlas.cellWidth
                        var column = 0
                        while (column < atlas.cellWidth) {
                            val width = minOf(sliceWidth, atlas.cellWidth - column)
                            val x = charLeft + column
                            val wave = sineScrollWave((x + width / 2f) / pixel, time)
                            val top = center + wave * amplitude - atlas.cellHeight / 2f
                            drawImage(
                                image = atlas.image,
                                srcOffset = IntOffset(cell * atlas.cellWidth + column, 0),
                                srcSize = IntSize(width, atlas.cellHeight),
                                dstOffset = IntOffset(x.roundToInt(), top.roundToInt()),
                                dstSize = IntSize(width, atlas.cellHeight),
                            )
                            column += width
                        }
                    }
                    drawRect(brush = tint, blendMode = BlendMode.SrcIn)
                }
            },
    )
}

/**
 * Draws every glyph once, white, into its own cell of an atlas image at [fontSizePx], so
 * the scroller can copy slices of glyphs instead of laying out text on every frame.
 */
private fun buildGlyphAtlas(
    textMeasurer: TextMeasurer,
    fontFamily: FontFamily,
    glyphs: String,
    fontSizePx: Float,
): GlyphAtlas {
    // A density of 1 makes sp equal to pixels.
    val density = Density(1f)
    val style = TextStyle(fontFamily = fontFamily, fontSize = fontSizePx.sp, color = Color.White)
    val layouts = glyphs.map { glyph ->
        textMeasurer.measure(glyph.toString(), style, softWrap = false, maxLines = 1, density = density)
    }
    val cellWidth = layouts.maxOf { it.size.width }.coerceAtLeast(1)
    val cellHeight = layouts.maxOf { it.size.height }.coerceAtLeast(1)
    val image = ImageBitmap(cellWidth * glyphs.length, cellHeight)
    CanvasDrawScope().draw(
        density = density,
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(image),
        size = Size(image.width.toFloat(), image.height.toFloat()),
    ) {
        layouts.forEachIndexed { cell, layout ->
            drawText(layout, topLeft = Offset(cell * cellWidth.toFloat(), 0f))
        }
    }
    // The scroller draws hundreds of slices of the atlas per frame.
    image.markImmutable()
    return GlyphAtlas(image, cellWidth, cellHeight)
}

@Preview
@Composable
private fun SineScrollViewPreview() {
    BoingBallDemoTheme {
        SineScrollView(modifier = Modifier.size(320.dp).aspectRatio(4f / 3f))
    }
}

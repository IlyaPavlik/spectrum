package ru.magflayer.spectrum.presentation.pages.main.camera.holder

import androidx.annotation.ColorInt
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.palette.graphics.Palette
import java.util.concurrent.TimeUnit

class ColorAnalyzer : ImageAnalysis.Analyzer {

    companion object {
        private const val ANALYZE_DELAY = 200L // ms
        private const val MAX_SWATCHES_COLORS = 16
    }

    var analyzerType: Type = Type.CENTER
    var analyzeResultListener: (ColorAnalyzerResult) -> Unit = {}

    private val paletteFilter: Palette.Filter = buildPaletteFilter()
    private var lastAnalyzedTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.MILLISECONDS.toMillis(ANALYZE_DELAY)) {
            val analyzeResult = calculateCenterRGBFromYUV(image, analyzerType)
            analyzeResultListener.invoke(analyzeResult)
            lastAnalyzedTimestamp = currentTimestamp
        }
        image.close()
    }

    private fun calculateCenterRGBFromYUV(
        image: ImageProxy,
        analyzerType: Type,
    ): ColorAnalyzerResult {
        val height = image.height
        val width = image.width
        val rgbPlane = image.planes[0]

        return when (analyzerType) {
            Type.CENTER -> {
                val centerIndex = ((width / 2) + (height / 2) * width) * rgbPlane.pixelStride

                // for some reason Red and Blue bytes are swapped
                val b = rgbPlane.buffer[centerIndex].toInt() and 0xff
                val g = rgbPlane.buffer[centerIndex + 1].toInt() and 0xff
                val r = rgbPlane.buffer[centerIndex + 2].toInt() and 0xff
                val a = rgbPlane.buffer[centerIndex + 3].toInt() and 0xff

                CenterColorResult(rgbaToColor(r, g, b, a))
            }
            Type.SWATCHES -> {
                val pixels = IntArray(width * height)
                var j = 0

                for (i in pixels.indices) {
                    val b = rgbPlane.buffer[j++].toInt() and 0xff
                    val g = rgbPlane.buffer[j++].toInt() and 0xff
                    val r = rgbPlane.buffer[j++].toInt() and 0xff
                    val a = rgbPlane.buffer[j++].toInt() and 0xff

                    pixels[i] = rgbaToColor(r, g, b, a)
                }

                val quantizer = ColorCutQuantizer(
                    pixels,
                    MAX_SWATCHES_COLORS,
                    arrayOf(paletteFilter),
                )

                val swatches = quantizer.quantizedColors?.map {
                    AnalyzerSwatch(it.rgb, it.population)
                } ?: emptyList()

                SwatchesResult(swatches)
            }
        }
    }

    /**
     * see [Bitmap.Config.ARGB_8888]
     */
    @ColorInt
    private fun rgbaToColor(r: Int, g: Int, b: Int, a: Int): Int =
        (a shl 24) or (b shl 16) or (g shl 8) or (r)

    enum class Type {
        CENTER,
        SWATCHES,
    }

    private fun buildPaletteFilter(): Palette.Filter = object : Palette.Filter {

        private val BLACK_MAX_LIGHTNESS = 0.05f
        private val WHITE_MIN_LIGHTNESS = 0.95f

        override fun isAllowed(rgb: Int, hsl: FloatArray): Boolean {
            return !isWhite(hsl) && !isBlack(hsl) && !isNearRedILine(hsl)
        }

        /**
         * @return true if the color represents a color which is close to black.
         */
        private fun isBlack(hslColor: FloatArray): Boolean {
            return hslColor[2] <= BLACK_MAX_LIGHTNESS
        }

        /**
         * @return true if the color represents a color which is close to white.
         */
        private fun isWhite(hslColor: FloatArray): Boolean {
            return hslColor[2] >= WHITE_MIN_LIGHTNESS
        }

        /**
         * @return true if the color lies close to the red side of the I line.
         */
        private fun isNearRedILine(hslColor: FloatArray): Boolean {
            return hslColor[0] in 10f..37f && hslColor[1] <= 0.82f
        }
    }
}

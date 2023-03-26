package ru.magflayer.spectrum.presentation.pages.main.camera.holder

import android.graphics.Color
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.palette.graphics.Palette
import java.nio.ByteBuffer
import java.nio.IntBuffer
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
        val planes = image.planes

        val height = image.height
        val width = image.width

        // Y
        val yArr = planes[0].buffer
        val yArrByteArray = yArr.toByteArray()
        val yPixelStride = planes[0].pixelStride
        val yRowStride = planes[0].rowStride

        // U
        val uArr = planes[1].buffer
        val uArrByteArray = uArr.toByteArray()
        val uPixelStride = planes[1].pixelStride
        val uRowStride = planes[1].rowStride

        // V
        val vArr = planes[2].buffer
        val vArrByteArray = vArr.toByteArray()
        val vPixelStride = planes[2].pixelStride
        val vRowStride = planes[2].rowStride

        return when (analyzerType) {
            Type.CENTER -> {
                val yIndex = (height * yRowStride + width * yPixelStride) / 2
                val uIndex = (height * uRowStride + width * uPixelStride) / 4
                val vIndex = (height * vRowStride + width * vPixelStride) / 4

                val y = yArrByteArray[yIndex].toInt() and 255
                val u = (uArrByteArray[uIndex].toInt() and 255) - 128
                val v = (vArrByteArray[vIndex].toInt() and 255) - 128

                val (r, g, b) = convertYUVToRGB(y, u, v)

                CenterColorResult(Color.rgb(r, g, b))
            }
            Type.SWATCHES -> {
                val pixels = IntBuffer.allocate(width * height).apply {
                    position(0)
                }
                for (xCord in 0 until width) {
                    for (yCord in 0 until height) {
                        val uvx = xCord / 2
                        val uvy = yCord / 2

                        val yIndex = yCord * yRowStride + xCord * yPixelStride
                        val uIndex = uvy * uRowStride + uvx * uPixelStride
                        val vIndex = uvy * vRowStride + uvx * vPixelStride

                        val y = yArrByteArray[yIndex].toInt() and 255
                        val u = (uArrByteArray[uIndex].toInt() and 255) - 128
                        val v = (vArrByteArray[vIndex].toInt() and 255) - 128

                        val (r, g, b) = convertYUVToRGB(y, u, v)

                        pixels.put(Color.rgb(r, g, b))
                    }
                }

                pixels.flip()

                val quantizer = ColorCutQuantizer(
                    pixels.array(),
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

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind() // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data) // Copy the buffer into a byte array
        return data // Return the byte array
    }

    private fun convertYUVToRGB(y: Int, u: Int, v: Int): Triple<Int, Int, Int> =
        Triple(
            (y + (1.370705 * v)).toInt(), // red
            (y - (0.698001 * v) - (0.337633 * u)).toInt(), // green
            (y + (1.732446 * u)).toInt(), // blue
        )

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

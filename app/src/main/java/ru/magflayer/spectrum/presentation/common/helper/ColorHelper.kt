package ru.magflayer.spectrum.presentation.common.helper

import android.graphics.Color
import android.text.TextUtils
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Size
import ru.magflayer.spectrum.domain.entity.NcsColorEntity
import java.math.BigDecimal

object ColorHelper {

    private const val COLOR_ERROR = 5

    fun parseHex2Dec(colorString: String): Int {
        if (colorString[0] == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            var color = java.lang.Long.parseLong(colorString.substring(1), 16)
            if (colorString.length == 7) {
                // Set the alpha value
                color = color or -0x1000000
            } else if (colorString.length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return color.toInt()
        }
        throw IllegalArgumentException("Unknown color")
    }

    fun inverseColor(color: Int): Int {
        return 0x00FFFFFF - (color or -0x1000000) or (color and -0x1000000)
    }

    fun isSameColor(previousColor: Int, newColor: Int): Boolean {
        val previousRgb = intArrayOf(
            Color.red(previousColor),
            Color.green(previousColor),
            Color.blue(previousColor),
        )
        val newRgb = intArrayOf(Color.red(newColor), Color.green(newColor), Color.blue(newColor))

        return (
            Math.abs(previousRgb[0] - newRgb[0]) >= COLOR_ERROR ||
                Math.abs(previousRgb[1] - newRgb[1]) >= COLOR_ERROR ||
                Math.abs(previousRgb[2] - newRgb[2]) >= COLOR_ERROR
            )
    }

    fun dec2Rgb(color: Int): IntArray {
        val rgba = dec2Rgba(color)

        return intArrayOf(rgba[0], rgba[1], rgba[2])
    }

    fun dec2Rgba(color: Int): IntArray {
        val rgba = IntArray(4)
        rgba[0] = color shr 16 and 0xFF // red
        rgba[1] = color shr 8 and 0xFF // green
        rgba[2] = color and 0xFF // blue
        rgba[3] = color.ushr(24) // alpha
        return rgba
    }

    fun dec2Hsl(@ColorInt color: Int): FloatArray {
        val rgb = dec2Rgb(color)
        return rgb2Hsl(rgb[0], rgb[1], rgb[2])
    }

    fun rgb2Hsl(
        @IntRange(from = 0x0, to = 0xFF) r: Int,
        @IntRange(from = 0x0, to = 0xFF) g: Int,
        @IntRange(from = 0x0, to = 0xFF) b: Int,
    ): FloatArray {
        val outHsl = FloatArray(3)

        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f

        val max = Math.max(rf, Math.max(gf, bf))
        val min = Math.min(rf, Math.min(gf, bf))
        val deltaMaxMin = max - min

        var h: Float
        val s: Float
        val l = (max + min) / 2f

        if (max == min) {
            // Monochromatic
            s = 0f
            h = s
        } else {
            h = when (max) {
                rf -> (gf - bf) / deltaMaxMin % 6f
                gf -> (bf - rf) / deltaMaxMin + 2f
                else -> (rf - gf) / deltaMaxMin + 4f
            }

            s = deltaMaxMin / (1f - Math.abs(2f * l - 1f))
        }

        h = h * 60f % 360f
        if (h < 0) {
            h += 360f
        }

        outHsl[0] = constrain(h, 0f, 360f)
        outHsl[1] = constrain(s, 0f, 1f)
        outHsl[2] = constrain(l, 0f, 1f)
        return outHsl
    }

    fun dec2Hex(@ColorInt color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    fun hex2Dec(hex: String?): Int {
        return if (!TextUtils.isEmpty(hex)) {
            Integer.parseInt(hex!!.substring(1), 16)
        } else {
            Color.TRANSPARENT
        }
    }

    fun dec2Cmyk(color: Int): IntArray {
        val rgb = dec2Rgb(color)
        val cmyk = rgb2Cmyk(*rgb)
        return intArrayOf(
            Math.round(cmyk[0]) * 100,
            Math.round(cmyk[1]) * 100,
            Math.round(cmyk[2]) * 100,
            Math.round(cmyk[3]) * 100,
        )
    }

    fun rgb2Cmyk(@Size(3) vararg rgb: Int): FloatArray {
        var c: Float
        var m: Float
        var y: Float
        val k: Float

        val r = rgb[0].toFloat()
        val g = rgb[1].toFloat()
        val b = rgb[2].toFloat()

        // BLACK
        if (r == 0f && g == 0f && b == 0f) {
            return floatArrayOf(0f, 0f, 0f, 1f)
        }

        c = 1 - r / 255f
        m = 1 - g / 255f
        y = 1 - b / 255f

        val minCMY = Math.min(c, Math.min(m, y))

        if (1 - minCMY != 0f) {
            c = (c - minCMY) / (1 - minCMY)
            m = (m - minCMY) / (1 - minCMY)
            y = (y - minCMY) / (1 - minCMY)
        }
        k = minCMY

        return floatArrayOf(c, m, y, k)
    }

    fun dec2Xyz(@ColorInt color: Int): DoubleArray {
        val xyz = DoubleArray(3)
        val rgba = dec2Rgba(color)
        val coef = floatArrayOf(
            0.4124f,
            0.3576f,
            0.1805f,
            0.2126f,
            0.7152f,
            0.0722f,
            0.0193f,
            0.1192f,
            0.9505f,
        )

        var r = rgba[0] / 255.0
        var g = rgba[1] / 255.0
        var b = rgba[2] / 255.0

        r = if (r > 0.04045) {
            Math.pow((r + 0.055) / 1.055, 2.4)
        } else {
            r / 12.92
        }

        g = if (g > 0.04045) {
            Math.pow((g + 0.055) / 1.055, 2.4)
        } else {
            g / 12.92
        }

        b = if (b > 0.04045) {
            Math.pow((b + 0.055) / 1.055, 2.4)
        } else {
            b / 12.92
        }

        r *= 100.0
        g *= 100.0
        b *= 100.0

        xyz[0] = coef[0] * r + coef[1] * g + coef[2] * b
        xyz[1] = coef[3] * r + coef[4] * g + coef[5] * b
        xyz[2] = coef[6] * r + coef[7] * g + coef[8] * b

        // round values
        xyz[0] = BigDecimal(xyz[0]).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()
        xyz[1] = BigDecimal(xyz[1]).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()
        xyz[2] = BigDecimal(xyz[2]).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()

        return xyz
    }

    fun dec2Lab(@ColorInt color: Int): DoubleArray {
        val xyz = dec2Xyz(color)
        var xr: Double
        var yr: Double
        var zr: Double
        val eps = 0.008856
        val k = 7.787

        // D65
        val Xr = 95.047
        val Yr = 100.0
        val Zr = 108.883

        xr = xyz[0] / Xr
        yr = xyz[1] / Yr
        zr = xyz[2] / Zr

        xr = if (xr > eps) {
            Math.pow(xr, 1 / 3.0)
        } else {
            k * xr + 16 / 116.0
        }

        yr = if (yr > eps) {
            Math.pow(yr, 1 / 3.0)
        } else {
            k * yr + 16 / 116.0
        }

        zr = if (zr > eps) {
            Math.pow(zr, 1 / 3.0)
        } else {
            k * zr + 16 / 116.0
        }

        val lab = DoubleArray(3)

        lab[0] = BigDecimal(116 * yr - 16).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()
        lab[1] = BigDecimal(500 * (xr - yr)).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()
        lab[2] = BigDecimal(200 * (yr - zr)).setScale(4, BigDecimal.ROUND_HALF_UP).toDouble()

        return lab
    }

    fun dec2Ncs(colors: List<NcsColorEntity>?, @ColorInt color: Int): String? {
        val rgb = dec2Rgb(color)

        var name: String? = null
        var minError = java.lang.Double.MAX_VALUE
        if (colors != null) {
            for ((name1, value) in colors) {
                val ncsRgb = dec2Rgb(hex2Dec(value))
                var error = (
                    Math.pow((ncsRgb[0] - rgb[0]).toDouble(), 2.0) +
                        Math.pow((ncsRgb[1] - rgb[1]).toDouble(), 2.0) +
                        Math.pow((ncsRgb[2] - rgb[2]).toDouble(), 2.0)
                    )
                error = Math.sqrt(error)
                if (error < minError) {
                    minError = error
                    name = name1
                }
            }
        }

        return name
    }

    fun dec2Ryb(@ColorInt color: Int): IntArray {
        val rgb = dec2Rgb(color)

        var r = rgb[0].toFloat()
        var g = rgb[1].toFloat()
        var b = rgb[2].toFloat()

        val w = Math.min(Math.min(r, g), b)

        r -= w
        g -= w
        b -= w

        val mg = Math.max(Math.max(r, g), b)

        var y = Math.min(r, g)
        r -= y
        g -= y

        // If this unfortunate conversion combines blue and green, then cut each in
        // half to preserve the value's maximum range.
        if (b != 0f && g != 0f) {
            b /= 2.0f
            g /= 2.0f
        }

        // Redistribute the remaining green.
        y += g
        b += g

        // Normalize to values.
        val my = Math.max(Math.max(r, y), b)
        if (my != 0f) {
            val n = mg / my
            r *= n
            y *= n
            b *= n
        }

        // Add the white back in.
        r += w
        y += w
        b += w

        return intArrayOf(Math.round(r), Math.round(y), Math.round(b))
    }

    private fun constrain(amount: Float, low: Float, high: Float): Float {
        return if (amount < low) low else if (amount > high) high else amount
    }
}

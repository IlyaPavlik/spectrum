@file:Suppress("unused")

package ru.magflayer.spectrum.presentation.common.extension

import android.graphics.*
import android.support.v7.graphics.Palette
import java.io.ByteArrayOutputStream
import java.io.IOException

/*
     * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
     * are your width and height respectively
     */
fun Bitmap.averageColor(x0: Int, y0: Int, w: Int, h: Int): Int {
    val x1 = x0 + w
    val y1 = y0 + h
    var sumr = 0
    var sumg = 0
    var sumb = 0
    for (x in x0 until x1) {
        for (y in y0 until y1) {
            val pixelColor = getPixel(x, y)
            sumr += Color.red(pixelColor)
            sumg += Color.green(pixelColor)
            sumb += Color.blue(pixelColor)
        }
    }
    val num = w * h
    return Color.rgb(sumr / num, sumg / num, sumb / num)
}

fun Bitmap.mostPopularColor(x0: Int, y0: Int, w: Int, h: Int): Palette.Swatch? {
    val bitmap = Bitmap.createBitmap(this, x0, y0, w, h)
    val colors = Palette.from(bitmap).generate().swatches
    var popularColor: Palette.Swatch? = null
    val countPopularColors = 0

    for (swatch in colors) {
        if (countPopularColors < swatch.population) {
            popularColor = swatch
        }
    }

    return popularColor
}

fun createMultiColorHorizontalBitmap(width: Int, height: Int, colors: List<Int>): Bitmap {
    val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)

    val canvas = Canvas(resultBitmap)
    val paint = Paint()
    val colorSize = if (colors.isNotEmpty()) colors.size else 1
    val colorWidth = width / colorSize
    var startX = 0

    for (color in colors) {
        paint.color = color
        val rect = RectF(startX.toFloat(), 0f, (startX + colorWidth).toFloat(), height.toFloat())
        canvas.drawRect(rect, paint)
        startX += colorWidth
    }

    return resultBitmap
}

@Throws(IOException::class)
fun Bitmap.convertBitmapToBytes(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}
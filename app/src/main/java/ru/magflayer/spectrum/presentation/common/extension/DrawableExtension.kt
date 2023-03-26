package ru.magflayer.spectrum.presentation.common.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import androidx.annotation.ColorInt

fun Drawable.setColor(@ColorInt color: Int) {
    if (this is ShapeDrawable) {
        this.paint.color = color
    } else if (this is GradientDrawable) {
        this.setColor(color)
    } else if (this is ColorDrawable) {
        this.color = color
    } else if (this is ClipDrawable) {
        this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this is RippleDrawable) {
        this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

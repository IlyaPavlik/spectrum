package ru.magflayer.spectrum.presentation.common.extension

import android.animation.Animator
import android.view.View
import android.view.animation.LinearInterpolator
import ru.magflayer.spectrum.presentation.common.listeners.SimpleAnimatorListener

fun View.rotate(degree: Int) {
    if (rotation == degree.toFloat()) return

    animate()
        .rotationBy(degree - rotation)
        .setDuration(200)
        .setInterpolator(LinearInterpolator())
        .setListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
                rotation = degree.toFloat()
            }
        })
        .start()
}

fun View.show() = visible(true)

fun View.hide() = visible(false)

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
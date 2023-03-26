package ru.magflayer.spectrum.presentation.common.listeners

import android.animation.Animator

abstract class SimpleAnimatorListener : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator) {
        // do nothing
    }

    override fun onAnimationEnd(animation: Animator) {
        // do nothing
    }

    override fun onAnimationCancel(animation: Animator) {
        // do nothing
    }

    override fun onAnimationRepeat(animation: Animator) {
        // do nothing
    }
}

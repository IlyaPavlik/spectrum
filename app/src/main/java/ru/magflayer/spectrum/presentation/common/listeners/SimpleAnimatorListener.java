package ru.magflayer.spectrum.presentation.common.listeners;

import android.animation.Animator;


public abstract class SimpleAnimatorListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {
        //do nothing
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //do nothing
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        //do nothing
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        //do nothing
    }
}

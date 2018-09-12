package ru.magflayer.spectrum.presentation.common.utils;

import android.animation.Animator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import ru.magflayer.spectrum.presentation.common.listeners.SimpleAnimatorListener;

public class ViewUtils {

    private ViewUtils() {
    }

    public static void changeViewVisibility(boolean visible, View... views) {
        for (View view : views) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static void rotateView(final View view, final int toDegrees) {
        if (view.getRotation() == toDegrees) return;

        view.animate()
                .rotationBy(toDegrees - view.getRotation())
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setRotation(toDegrees);
                    }
                })
                .start();
    }

}
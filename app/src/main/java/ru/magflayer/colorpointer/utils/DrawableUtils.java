package ru.magflayer.colorpointer.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;

public class DrawableUtils {

    private DrawableUtils() {

    }

    public static void setColor(Drawable drawable, @ColorInt int color) {
        if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(color);
        } else if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color);
        } else if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(color);
        } else if (drawable instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rippleDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        } else if (drawable instanceof ClipDrawable) {
            ClipDrawable clipDrawable = (ClipDrawable) drawable;
            clipDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

}

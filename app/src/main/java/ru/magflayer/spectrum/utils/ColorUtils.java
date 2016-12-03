package ru.magflayer.spectrum.utils;

import android.graphics.Color;

public class ColorUtils {

    private ColorUtils() {
    }

    public static int inverseColor(int color) {
        return (0x00FFFFFF - (color | 0xFF000000)) | (color & 0xFF000000);
    }

    public static int[] hexToRgb(String hex) {
        int[] rgb = new int[3];
        int color = Color.parseColor(hex);
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);
        return rgb;
    }

    public static float[] hexToHsl(String hex) {
        float[] hsl = new float[3];
        int color = Color.parseColor(hex);
        android.support.v4.graphics.ColorUtils.colorToHSL(color, hsl);
        return hsl;
    }
}

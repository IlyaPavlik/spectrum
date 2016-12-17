package ru.magflayer.spectrum.utils;

import android.graphics.Color;

import java.io.IOException;

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

    public static String colorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static float[] rgbToCmyk(float... rgb) {
        float c = 0;
        float m = 0;
        float y = 0;
        float k = 0;

        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        // BLACK
        if (r == 0 && g == 0 && b == 0) {
            return new float[]{0, 0, 0, 1};
        }

        c = 1 - (r / 255f);
        m = 1 - (g / 255f);
        y = 1 - (b / 255f);

        float minCMY = Math.min(c, Math.min(m, y));

        if (1 - minCMY != 0) {
            c = (c - minCMY) / (1 - minCMY);
            m = (m - minCMY) / (1 - minCMY);
            y = (y - minCMY) / (1 - minCMY);
        }
        k = minCMY;

        return new float[]{c, m, y, k};
    }
}

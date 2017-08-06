package ru.magflayer.spectrum.utils;

import android.graphics.Color;

public class ColorUtils {

    private ColorUtils() {
    }

    private static final int COLOR_ERROR = 5;

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

    public static boolean isSameColor(int previousColor, int newColor) {
        int[] previousRgb = {Color.red(previousColor), Color.green(previousColor), Color.blue(previousColor)};
        int[] newRgb = {Color.red(newColor), Color.green(newColor), Color.blue(newColor)};

        return Math.abs(previousRgb[0] - newRgb[0]) >= COLOR_ERROR
                || Math.abs(previousRgb[1] - newRgb[1]) >= COLOR_ERROR
                || Math.abs(previousRgb[2] - newRgb[2]) >= COLOR_ERROR;
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

    public static double[] rgbToXYZ(int R, int G, int B) {

        double r, g, b, X, Y, Z, xr, yr, zr;

        // D65/2°
        double Xr = 95.047;
        double Yr = 100.0;
        double Zr = 108.883;

        // --------- RGB to XYZ ---------//

        r = R / 255.0;
        g = G / 255.0;
        b = B / 255.0;

        if (r > 0.04045)
            r = Math.pow((r + 0.055) / 1.055, 2.4);
        else
            r = r / 12.92;

        if (g > 0.04045)
            g = Math.pow((g + 0.055) / 1.055, 2.4);
        else
            g = g / 12.92;

        if (b > 0.04045)
            b = Math.pow((b + 0.055) / 1.055, 2.4);
        else
            b = b / 12.92;

        r *= 100;
        g *= 100;
        b *= 100;

        X = 0.4124 * r + 0.3576 * g + 0.1805 * b;
        Y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        Z = 0.0193 * r + 0.1192 * g + 0.9505 * b;

        // --------- XYZ to Lab --------- //

        xr = X / Xr;
        yr = Y / Yr;
        zr = Z / Zr;

        if (xr > 0.008856)
            xr = (float) Math.pow(xr, 1 / 3.);
        else
            xr = (float) ((7.787 * xr) + 16 / 116.0);

        if (yr > 0.008856)
            yr = (float) Math.pow(yr, 1 / 3.);
        else
            yr = (float) ((7.787 * yr) + 16 / 116.0);

        if (zr > 0.008856)
            zr = (float) Math.pow(zr, 1 / 3.);
        else
            zr = (float) ((7.787 * zr) + 16 / 116.0);

        double[] lab = new double[3];

        lab[0] = (116 * yr) - 16;
        lab[1] = 500 * (xr - yr);
        lab[2] = 200 * (yr - zr);

        return lab;
    }
}

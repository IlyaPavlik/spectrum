package ru.magflayer.spectrum.presentation.common.utils;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.List;

import ru.magflayer.spectrum.domain.entity.NcsColorEntity;

@SuppressWarnings("WeakerAccess")
public class ColorUtils {

    private ColorUtils() {
    }

    private static final int COLOR_ERROR = 5;

    public static int parseHex2Dec(final String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static int inverseColor(int color) {
        return (0x00FFFFFF - (color | 0xFF000000)) | (color & 0xFF000000);
    }

    public static boolean isSameColor(int previousColor, int newColor) {
        int[] previousRgb = {Color.red(previousColor), Color.green(previousColor), Color.blue(previousColor)};
        int[] newRgb = {Color.red(newColor), Color.green(newColor), Color.blue(newColor)};

        return Math.abs(previousRgb[0] - newRgb[0]) >= COLOR_ERROR
                || Math.abs(previousRgb[1] - newRgb[1]) >= COLOR_ERROR
                || Math.abs(previousRgb[2] - newRgb[2]) >= COLOR_ERROR;
    }

    public static int[] dec2Rgb(final int color) {
        int[] rgba = dec2Rgba(color);

        return new int[]{rgba[0], rgba[1], rgba[2]};
    }

    public static int[] dec2Rgba(final int color) {
        int[] rgba = new int[4];
        rgba[0] = (color >> 16) & 0xFF; //red
        rgba[1] = (color >> 8) & 0xFF; //green
        rgba[2] = color & 0xFF; //blue
        rgba[3] = color >>> 24; //alpha
        return rgba;
    }

    public static float[] dec2Hsl(@ColorInt final int color) {
        int rgb[] = dec2Rgb(color);
        return rgb2Hsl(rgb[0], rgb[1], rgb[2]);
    }

    public static float[] rgb2Hsl(@IntRange(from = 0x0, to = 0xFF) int r,
                                  @IntRange(from = 0x0, to = 0xFF) int g,
                                  @IntRange(from = 0x0, to = 0xFF) int b) {
        float[] outHsl = new float[3];

        final float rf = r / 255f;
        final float gf = g / 255f;
        final float bf = b / 255f;

        final float max = Math.max(rf, Math.max(gf, bf));
        final float min = Math.min(rf, Math.min(gf, bf));
        final float deltaMaxMin = max - min;

        float h, s;
        float l = (max + min) / 2f;

        if (max == min) {
            // Monochromatic
            h = s = 0f;
        } else {
            if (max == rf) {
                h = ((gf - bf) / deltaMaxMin) % 6f;
            } else if (max == gf) {
                h = ((bf - rf) / deltaMaxMin) + 2f;
            } else {
                h = ((rf - gf) / deltaMaxMin) + 4f;
            }

            s = deltaMaxMin / (1f - Math.abs(2f * l - 1f));
        }

        h = (h * 60f) % 360f;
        if (h < 0) {
            h += 360f;
        }

        outHsl[0] = constrain(h, 0f, 360f);
        outHsl[1] = constrain(s, 0f, 1f);
        outHsl[2] = constrain(l, 0f, 1f);
        return outHsl;
    }

    public static String dec2Hex(@ColorInt final int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int hex2Dec(@Nullable final String hex) {
        if (!TextUtils.isEmpty(hex)) {
            return Integer.parseInt(hex.substring(1), 16);
        }
        return Color.TRANSPARENT;
    }

    public static int[] dec2Cmyk(final int color) {
        int[] rgb = dec2Rgb(color);
        float[] cmyk = rgb2Cmyk(rgb);
        return new int[]{Math.round(cmyk[0]) * 100, Math.round(cmyk[1]) * 100,
                Math.round(cmyk[2]) * 100, Math.round(cmyk[3]) * 100};
    }

    public static float[] rgb2Cmyk(@Size(3) final int... rgb) {
        float c;
        float m;
        float y;
        float k;

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

    public static double[] dec2Xyz(@ColorInt final int color) {
        double[] xyz = new double[3];
        int[] rgba = dec2Rgba(color);
        float[] coef = new float[]{
                0.4124f, 0.3576f, 0.1805f,
                0.2126f, 0.7152f, 0.0722f,
                0.0193f, 0.1192f, 0.9505f,
        };

        double r = rgba[0] / 255.0D;
        double g = rgba[1] / 255.0D;
        double b = rgba[2] / 255.0D;

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

        xyz[0] = coef[0] * r + coef[1] * g + coef[2] * b;
        xyz[1] = coef[3] * r + coef[4] * g + coef[5] * b;
        xyz[2] = coef[6] * r + coef[7] * g + coef[8] * b;

        //round values
        xyz[0] = new BigDecimal(xyz[0]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        xyz[1] = new BigDecimal(xyz[1]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        xyz[2] = new BigDecimal(xyz[2]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

        return xyz;
    }

    public static double[] dec2Lab(@ColorInt final int color) {
        double[] xyz = dec2Xyz(color);
        double xr, yr, zr;
        double eps = 0.008856;
        double k = 7.787D;

        // D65
        double Xr = 95.047;
        double Yr = 100.0;
        double Zr = 108.883;

        xr = xyz[0] / Xr;
        yr = xyz[1] / Yr;
        zr = xyz[2] / Zr;

        if (xr > eps)
            xr = Math.pow(xr, 1 / 3.);
        else
            xr = ((k * xr) + 16 / 116.0);

        if (yr > eps)
            yr = Math.pow(yr, 1 / 3.);
        else
            yr = ((k * yr) + 16 / 116.0);

        if (zr > eps)
            zr = Math.pow(zr, 1 / 3.);
        else
            zr = ((k * zr) + 16 / 116.0);

        double[] lab = new double[3];

        lab[0] = new BigDecimal((116 * yr) - 16).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        lab[1] = new BigDecimal(500 * (xr - yr)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        lab[2] = new BigDecimal(200 * (yr - zr)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

        return lab;
    }

    public static String dec2Ncs(final List<NcsColorEntity> colors, @ColorInt final int color) {
        final int[] rgb = dec2Rgb(color);

        String name = null;
        double minError = Double.MAX_VALUE;
        if (colors != null) {
            for (NcsColorEntity ncsColor : colors) {
                final int[] ncsRgb = dec2Rgb(hex2Dec(ncsColor.getValue()));
                double error = Math.pow(ncsRgb[0] - rgb[0], 2)
                        + Math.pow(ncsRgb[1] - rgb[1], 2)
                        + Math.pow(ncsRgb[2] - rgb[2], 2);
                error = Math.sqrt(error);
                if (error < minError) {
                    minError = error;
                    name = ncsColor.getName();
                }
            }
        }

        return name;
    }

    public static int[] dec2Ryb(@ColorInt final int color) {
        final int[] rgb = dec2Rgb(color);

        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        float w = Math.min(Math.min(r, g), b);

        r -= w;
        g -= w;
        b -= w;

        float mg = Math.max(Math.max(r, g), b);

        float y = Math.min(r, g);
        r -= y;
        g -= y;

        // If this unfortunate conversion combines blue and green, then cut each in
        // half to preserve the value's maximum range.
        if (b != 0 && g != 0) {
            b /= 2.0;
            g /= 2.0;
        }

        // Redistribute the remaining green.
        y += g;
        b += g;

        // Normalize to values.
        float my = Math.max(Math.max(r, y), b);
        if (my != 0) {
            float n = mg / my;
            r *= n;
            y *= n;
            b *= n;
        }

        // Add the white back in.
        r += w;
        y += w;
        b += w;

        return new int[]{Math.round(r), Math.round(y), Math.round(b)};
    }

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
}

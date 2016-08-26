package ru.magflayer.colorpointer.utils;

public class ColorUtils {

    private ColorUtils() {
    }

    public static int inverseColor(int color) {
        return (0x00FFFFFF - (color | 0xFF000000)) | (color & 0xFF000000);
    }
}

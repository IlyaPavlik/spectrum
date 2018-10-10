package ru.magflayer.spectrum.domain.common.utils;

public final class Utils {

    private Utils() {
    }

    public static boolean isEmpty(final String text) {
        return text == null || text.length() == 0;
    }

}

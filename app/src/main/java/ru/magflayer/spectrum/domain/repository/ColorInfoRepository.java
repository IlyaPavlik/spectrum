package ru.magflayer.spectrum.domain.repository;

import android.database.Cursor;

import java.util.Map;

public interface ColorInfoRepository {

    boolean uploadColorNames(Map<String, String> hexName);

    boolean isColorNamesUploaded();

    Cursor loadColorNames();

    String loadColorNameByHex(String hex);

    boolean uploadNcsColors(Map<String, String> hexName);

    boolean isNcsColorUploaded();

    Cursor loadNcsColors();

    String loadNcsColorByHex(String hex);

}

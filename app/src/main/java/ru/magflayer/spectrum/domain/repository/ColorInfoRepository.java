package ru.magflayer.spectrum.domain.repository;

import java.util.List;
import java.util.Map;

import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;

public interface ColorInfoRepository {

    boolean uploadColorNames(Map<String, String> hexName);

    boolean isColorNamesUploaded();

    List<ColorInfoEntity> loadColorNames();

    String loadColorNameByHex(String hex);

    boolean uploadNcsColors(Map<String, String> hexName);

    boolean isNcsColorUploaded();

    List<ColorInfoEntity> loadNcsColors();

    String loadNcsColorByHex(String hex);

}

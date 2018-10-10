package ru.magflayer.spectrum.data.entity.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.magflayer.spectrum.data.entity.ColorName;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;

public class ColorNameConverter {

    public List<ColorInfoEntity> convertToColorInfos(final List<ColorName> colorNames) {
        List<ColorInfoEntity> colorInfoEntities = new ArrayList<>();
        for (ColorName colorName : colorNames) {
            colorInfoEntities.add(convertToColorInfo(colorName));
        }
        return colorInfoEntities;
    }

    public List<ColorName> convertToNcsColors(final Collection<ColorInfoEntity> colorInfoEntities) {
        List<ColorName> colorNames = new ArrayList<>();
        for (ColorInfoEntity colorInfoEntity : colorInfoEntities) {
            colorNames.add(convertToColorName(colorInfoEntity));
        }
        return colorNames;
    }

    public ColorInfoEntity convertToColorInfo(final ColorName colorName) {
        return new ColorInfoEntity(colorName.getHex(), colorName.getName());
    }

    public ColorName convertToColorName(final ColorInfoEntity colorInfoEntity) {
        return new ColorName(colorInfoEntity.getId(), colorInfoEntity.getName());
    }

}

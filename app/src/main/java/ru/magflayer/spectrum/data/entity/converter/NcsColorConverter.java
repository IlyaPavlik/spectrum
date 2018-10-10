package ru.magflayer.spectrum.data.entity.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.magflayer.spectrum.data.entity.NcsColor;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;

public class NcsColorConverter {

    public List<ColorInfoEntity> convertToColorInfos(final List<NcsColor> ncsColors) {
        List<ColorInfoEntity> colorInfoEntities = new ArrayList<>();
        for (NcsColor ncsColor : ncsColors) {
            colorInfoEntities.add(convertToColorInfo(ncsColor));
        }
        return colorInfoEntities;
    }

    public List<NcsColor> convertToNcsColors(final Collection<ColorInfoEntity> colorInfoEntities) {
        List<NcsColor> ncsColors = new ArrayList<>();
        for (ColorInfoEntity colorInfoEntity : colorInfoEntities) {
            ncsColors.add(convertToNcsColor(colorInfoEntity));
        }
        return ncsColors;
    }

    public ColorInfoEntity convertToColorInfo(final NcsColor ncsColor) {
        return new ColorInfoEntity(ncsColor.getHex(), ncsColor.getName());
    }

    public NcsColor convertToNcsColor(final ColorInfoEntity colorInfoEntity) {
        return new NcsColor(colorInfoEntity.getId(), colorInfoEntity.getName());
    }

}

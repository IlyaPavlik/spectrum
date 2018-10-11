package ru.magflayer.spectrum.data.entity.converter;

import ru.magflayer.spectrum.data.entity.ColorPhoto;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;

public class ColorPhotoConverter {

    public ColorPhoto convertToColorPhoto(final ColorPhotoEntity entity) {
        return new ColorPhoto(entity.getMillis(), entity.getFilePath(), entity.getRgbColors(),
                entity.getType().ordinal());
    }

    public ColorPhotoEntity convertToColorPhotoEntity(final ColorPhoto colorPhoto) {
        ColorPhotoEntity.Type type = ColorPhotoEntity.Type.values()[colorPhoto.getType()];
        return new ColorPhotoEntity(type, colorPhoto.getFilePath(), colorPhoto.getRgbColors(),
                colorPhoto.getId());
    }

}

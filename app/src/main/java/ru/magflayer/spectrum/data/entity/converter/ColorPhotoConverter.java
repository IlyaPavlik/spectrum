package ru.magflayer.spectrum.data.entity.converter;

import ru.magflayer.spectrum.data.entity.ColorPhoto;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;

public class ColorPhotoConverter {

    public ColorPhoto convertToColorPhoto(final ColorPhotoEntity entity) {
        return new ColorPhoto(entity.getMillis(), entity.getFilePath(), entity.getRgbColors());
    }

    public ColorPhotoEntity convertToColorPhotoEntity(final ColorPhoto colorPhoto) {
        return new ColorPhotoEntity(colorPhoto.getFilePath(), colorPhoto.getRgbColors(), colorPhoto.getId());
    }

}

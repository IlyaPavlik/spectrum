package ru.magflayer.spectrum.data.entity.converter;

import ru.magflayer.spectrum.data.database.AppDatabase;
import ru.magflayer.spectrum.data.entity.ColorPhoto;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;

public class ColorPhotoConverter {

    public ColorPhoto convertToColorPhoto(final ColorPhotoEntity entity) {
        int id = AppDatabase.IdGenerator.generateId(entity.getFilePath());
        return new ColorPhoto(id, entity.getFilePath(), entity.getRgbColors());
    }

    public ColorPhotoEntity convertToColorPhotoEntity(final ColorPhoto colorPhoto) {
        return new ColorPhotoEntity(colorPhoto.getFilePath(), colorPhoto.getRgbColors());
    }

}

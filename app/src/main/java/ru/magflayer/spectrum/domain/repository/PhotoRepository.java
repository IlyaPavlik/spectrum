package ru.magflayer.spectrum.domain.repository;

import java.util.List;

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import rx.Observable;

public interface PhotoRepository {

    Observable<Boolean> savePhoto(ColorPhotoEntity colorPhoto);

    Observable<List<ColorPhotoEntity>> loadPhotos();

    Observable<ColorPhotoEntity> loadPhoto(String filePath);

    Observable<Boolean> removePhoto(ColorPhotoEntity entity);

}

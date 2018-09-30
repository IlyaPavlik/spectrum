package ru.magflayer.spectrum.domain.interactor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.repository.PhotoRepository;
import rx.Observable;

public class ColorPhotoInteractor {

    private final PhotoRepository photoRepository;

    @Inject
    ColorPhotoInteractor(final PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Observable<Boolean> saveColorPhoto(final ColorPhotoEntity entity) {
        return photoRepository.savePhoto(entity);
    }

    public Observable<List<ColorPhotoEntity>> loadColorPhotos() {
        return photoRepository.loadPhotos()
                .map(entities -> {
                    List<ColorPhotoEntity> photoEntities = new ArrayList<>();

                    for (ColorPhotoEntity entity : entities) {
                        if (new File(entity.getFilePath()).exists()) {
                            photoEntities.add(entity);
                        } else {
                            photoRepository.removePhoto(entity);
                        }
                    }

                    return photoEntities;
                });
    }

    public Observable<Boolean> removeColorPhoto(final ColorPhotoEntity entity) {
        return photoRepository.removePhoto(entity)
                .map(success -> new File(entity.getFilePath()).delete());
    }

    public Observable<ColorPhotoEntity> loadColorPhoto(final String filePath) {
        return photoRepository.loadPhoto(filePath);
    }

}

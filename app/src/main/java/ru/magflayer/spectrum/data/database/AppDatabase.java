package ru.magflayer.spectrum.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import java.util.List;

import ru.magflayer.spectrum.data.database.dao.ColorPhotoDao;
import ru.magflayer.spectrum.data.entity.ColorPhoto;
import ru.magflayer.spectrum.data.entity.converter.ColorPhotoConverter;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.repository.PhotoRepository;
import rx.Observable;

@Database(entities = {ColorPhoto.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase implements PhotoRepository {

    public static final String DATABASE_NAME = "spectre-database";

    private final ColorPhotoConverter photoConverter = new ColorPhotoConverter();

    public abstract ColorPhotoDao colorPhotoDao();

    @Override
    public Observable<Boolean> savePhoto(final ColorPhotoEntity colorPhoto) {
        return Observable.just(colorPhoto)
                .map(photoConverter::convertToColorPhoto)
                .map(photo -> {
                    ColorPhotoDao dao = colorPhotoDao();
                    return dao.savePhoto(photo) > 0;
                });
    }

    @Override
    public Observable<List<ColorPhotoEntity>> loadPhotos() {
        return Observable.fromCallable(() -> {
            ColorPhotoDao dao = colorPhotoDao();
            return dao.loadPhotos();
        })
                .flatMap(Observable::from)
                .map(photoConverter::convertToColorPhotoEntity)
                .toList();
    }

    @Override
    public Observable<ColorPhotoEntity> loadPhoto(final String filePath) {
        return Observable.fromCallable(() -> {
            ColorPhotoDao dao = colorPhotoDao();
            return dao.loadPhoto(filePath);
        })
                .map(photoConverter::convertToColorPhotoEntity);
    }

    @Override
    public Observable<Boolean> removePhoto(final ColorPhotoEntity entity) {
        return Observable.just(entity)
                .map(photoConverter::convertToColorPhoto)
                .map(colorPhoto -> {
                    ColorPhotoDao dao = colorPhotoDao();
                    return dao.deletePhoto(colorPhoto) > 0;
                });
    }

    public static class IdGenerator {
        public static int generateId(final String path) {
            return path.hashCode();
        }
    }
}

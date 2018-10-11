package ru.magflayer.spectrum.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.magflayer.spectrum.data.database.dao.ColorNameDao
import ru.magflayer.spectrum.data.database.dao.ColorPhotoDao
import ru.magflayer.spectrum.data.database.dao.NcsColorDao
import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.data.entity.ColorPhoto
import ru.magflayer.spectrum.data.entity.NcsColor
import ru.magflayer.spectrum.data.entity.converter.ColorPhotoConverter
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import rx.Observable

@Database(entities = [ColorPhoto::class, ColorName::class, NcsColor::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase(), PhotoRepository {

    companion object {
        const val DATABASE_NAME = "spectre-database"
    }

    private val photoConverter = ColorPhotoConverter()

    abstract fun colorPhotoDao(): ColorPhotoDao

    abstract fun colorNameDao(): ColorNameDao

    abstract fun ncsColorDao(): NcsColorDao

    override fun savePhoto(colorPhoto: ColorPhotoEntity): Observable<Boolean> {
        return Observable.just(colorPhoto)
                .map<ColorPhoto> { photoConverter.convertToColorPhoto(it) }
                .map { photo -> colorPhotoDao().savePhoto(photo) > 0 }
    }

    override fun loadPhotos(): Observable<List<ColorPhotoEntity>> {
        return Observable.fromCallable { colorPhotoDao().loadPhotos() }
                .flatMap<ColorPhoto> { Observable.from(it) }
                .map<ColorPhotoEntity> { photoConverter.convertToColorPhotoEntity(it) }
                .toList()
    }

    override fun loadPhoto(filePath: String): Observable<ColorPhotoEntity> {
        return Observable.fromCallable { colorPhotoDao().loadPhoto(filePath) }
                .map { photoConverter.convertToColorPhotoEntity(it) }
    }

    override fun removePhoto(entity: ColorPhotoEntity): Observable<Boolean> {
        return Observable.just(entity)
                .map<ColorPhoto> { photoConverter.convertToColorPhoto(it) }
                .map { colorPhoto -> colorPhotoDao().deletePhoto(colorPhoto) > 0 }
    }
}

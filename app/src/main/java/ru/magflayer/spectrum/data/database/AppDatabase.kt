package ru.magflayer.spectrum.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.magflayer.spectrum.data.database.dao.ColorNameDao
import ru.magflayer.spectrum.data.database.dao.ColorPhotoDao
import ru.magflayer.spectrum.data.database.dao.NcsColorDao
import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.data.entity.ColorPhoto
import ru.magflayer.spectrum.data.entity.NcsColor
import ru.magflayer.spectrum.data.entity.converter.ColorPhotoConverter
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.PhotoRepository

@Database(
    entities = [ColorPhoto::class, ColorName::class, NcsColor::class],
    version = 3,
    exportSchema = false
)
//TODO move photo repository to different class
abstract class AppDatabase : RoomDatabase(), PhotoRepository {

    companion object {
        const val DATABASE_NAME = "spectre-database"
    }

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val photoConverter = ColorPhotoConverter()

    abstract fun colorPhotoDao(): ColorPhotoDao

    abstract fun colorNameDao(): ColorNameDao

    abstract fun ncsColorDao(): NcsColorDao

    override suspend fun savePhoto(colorPhoto: ColorPhotoEntity): Boolean =
        withContext(dispatcher) {
            val colorPhotoDto = photoConverter.convertToDto(colorPhoto)
            colorPhotoDao().savePhoto(colorPhotoDto) > 0
        }

    override suspend fun loadPhotos(): List<ColorPhotoEntity> = withContext(dispatcher) {
        val colorPhotos = colorPhotoDao().loadPhotos()
        colorPhotos.map { photoConverter.convertToEntity(it) }
    }

    override suspend fun loadPhoto(filePath: String): ColorPhotoEntity = withContext(dispatcher) {
        val colorPhoto = colorPhotoDao().loadPhoto(filePath)
        photoConverter.convertToEntity(colorPhoto)
    }

    override suspend fun removePhoto(entity: ColorPhotoEntity): Boolean = withContext(dispatcher) {
        val colorPhotoDto = photoConverter.convertToDto(entity)
        colorPhotoDao().deletePhoto(colorPhotoDto) > 0
    }
}

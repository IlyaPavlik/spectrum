package ru.magflayer.spectrum.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.magflayer.spectrum.data.database.AppDatabase
import ru.magflayer.spectrum.data.entity.converter.ColorPhotoConverter
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.PhotoRepository

class PhotoRepositoryImpl(private val appDatabase: AppDatabase) : PhotoRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val photoConverter = ColorPhotoConverter()

    override suspend fun savePhoto(colorPhoto: ColorPhotoEntity): Boolean =
        withContext(dispatcher) {
            val colorPhotoDto = photoConverter.convertToDto(colorPhoto)
            appDatabase.colorPhotoDao().savePhoto(colorPhotoDto) > 0
        }

    override suspend fun loadPhotos(): List<ColorPhotoEntity> = withContext(dispatcher) {
        val colorPhotos = appDatabase.colorPhotoDao().loadPhotos()
        colorPhotos.map { photoConverter.convertToEntity(it) }
    }

    override suspend fun loadPhoto(filePath: String): ColorPhotoEntity = withContext(dispatcher) {
        val colorPhoto = appDatabase.colorPhotoDao().loadPhoto(filePath)
        photoConverter.convertToEntity(colorPhoto)
    }

    override suspend fun removePhoto(entity: ColorPhotoEntity): Boolean = withContext(dispatcher) {
        val colorPhotoDto = photoConverter.convertToDto(entity)
        appDatabase.colorPhotoDao().deletePhoto(colorPhotoDto) > 0
    }

}
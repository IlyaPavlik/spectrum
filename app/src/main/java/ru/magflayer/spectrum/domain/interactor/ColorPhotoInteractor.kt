package ru.magflayer.spectrum.domain.interactor

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import java.io.File
import javax.inject.Inject

class ColorPhotoInteractor @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val fileManagerRepository: FileManagerRepository
) {

    suspend fun saveColorPhoto(entity: ColorPhotoEntity): Boolean {
        return photoRepository.savePhoto(entity)
    }

    suspend fun loadColorPhotos(): List<ColorPhotoEntity> {
        return photoRepository.loadPhotos()
            .map { colorPhoto ->
                colorPhoto.also {
                    val fileExists = fileManagerRepository.isFileExists(colorPhoto.filePath)
                    if (!fileExists) {
                        photoRepository.removePhoto(colorPhoto)
                    }
                }
            }
            .sortedByDescending { colorPhoto -> colorPhoto.millis }
    }

    suspend fun removeColorPhoto(entity: ColorPhotoEntity): Boolean {
        val success = photoRepository.removePhoto(entity)
        if (entity.type === ColorPhotoEntity.Type.INTERNAL) {
            File(entity.filePath).delete()
        }
        return success
    }

    suspend fun loadColorPhoto(filePath: String): ColorPhotoEntity {
        return photoRepository.loadPhoto(filePath)
    }

}

package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity

interface PhotoRepository {

    suspend fun savePhoto(colorPhoto: ColorPhotoEntity): Boolean

    suspend fun loadPhotos(): List<ColorPhotoEntity>

    suspend fun loadPhoto(filePath: String): ColorPhotoEntity

    suspend fun removePhoto(entity: ColorPhotoEntity): Boolean
}
